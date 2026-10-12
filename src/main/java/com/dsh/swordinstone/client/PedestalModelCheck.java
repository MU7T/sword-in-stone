package com.dsh.swordinstone.client;

import com.dsh.swordinstone.SwordInStone;
import com.dsh.swordinstone.registry.ModBlocks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 启动自检：确认誓约之剑底座的两个方块状态模型都真的烘焙出来了、贴图也接上了。
 *
 * <p>背景：模型 UV 越界时 MC 会<b>静默</b>丢弃模型、不报错；parent 继承失败也可能悄无声息。
 * 所以这里主动从方块状态拿烘焙结果写进日志，出问题时一眼能看出来。
 */
@EventBusSubscriber(modid = SwordInStone.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class PedestalModelCheck {

    private static boolean done;

    @SubscribeEvent
    public static void onJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        if (done) {
            return;
        }
        done = true;
        try {
            check();
        } catch (Throwable t) {
            SwordInStone.LOGGER.warn("[自检] 底座模型检查出错: {}", t.toString());
        }
    }

    private static void check() {
        var shaper = Minecraft.getInstance().getModelManager().getBlockModelShaper();
        var block = ModBlocks.SWORD_PEDESTAL.get();

        // 方块的所有状态里必然同时存在 has_sword=true 和 false，各查一次
        BlockState withSword = block.defaultBlockState();
        BlockState empty = withSword.setValue(com.dsh.swordinstone.block.SwordPedestalBlock.HAS_SWORD, false);

        report(shaper, withSword, "has_sword=true（插着剑）");
        report(shaper, empty, "has_sword=false（空台座）");
    }

    private static void report(BlockModelShaper shaper, BlockState state, String label) {
        BakedModel baked;
        try {
            baked = shaper.getBlockModel(state);
        } catch (Throwable t) {
            SwordInStone.LOGGER.warn("[自检] {} 取模型失败: {}", label, t.toString());
            return;
        }
        if (baked == null) {
            SwordInStone.LOGGER.warn("[自检] {} 没烘焙出来", label);
            return;
        }
        RandomSource rnd = RandomSource.create(0L);
        int quads = 0;
        Set<String> sprites = new LinkedHashSet<>();
        for (Direction dir : Direction.values()) {
            List<BakedQuad> list = baked.getQuads(state, dir, rnd);
            quads += list.size();
            for (BakedQuad q : list) {
                sprites.add(q.getSprite().contents().name().toString());
            }
        }
        List<BakedQuad> general = baked.getQuads(state, null, rnd);
        quads += general.size();
        for (BakedQuad q : general) {
            sprites.add(q.getSprite().contents().name().toString());
        }
        boolean missing = sprites.stream().anyMatch(s -> s.contains("missingno"));
        SwordInStone.LOGGER.info("[自检] {} -> {} 个面，贴图 {}", label, quads, sprites);
        if (quads == 0 || missing) {
            SwordInStone.LOGGER.warn("[自检] {} 有问题！面数={} 含缺失贴图={}", label, quads, missing);
        }
    }

    private PedestalModelCheck() {
    }
}
