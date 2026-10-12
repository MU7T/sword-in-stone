# 誓约胜利之剑 (Excalibur)

适用于 **Minecraft Java 版 1.21.1 / NeoForge** 的模组。

在世界上会随机生成一座高耸的石砖废墟塔，塔顶的**剑之底座**上插着一把**誓约胜利之剑**。
它是一把可以前期就靠运气拿到的中后期武器 —— 只要你找得到它，并且攒够经验。

---

## 一、内容一览

### 1. 誓约胜利之剑废墟塔（结构）

* 生成生物群系：平原、森林、白桦林、黑森林、针叶林、雪原、热带草原、丛林、沼泽、恶地、石岸、沙漠等主世界陆地群系。
* 生成频率：**约为村庄的一半**（结构集 `spacing = 68`，村庄为 34，即单位面积上的刷新密度减半）。
* 结构形态（全部由代码生成，不含二进制 `.nbt` 模板）：

```
        ▲  +8  剑之底座（誓约胜利之剑插在这里）
       ▓▓▓  +7  3×3 塔顶平台 + 四角城垛
       ▓▓▓  +5~+6  3×3 塔冠
       ▓▓▓  +4  3×3 顶层楼板
      ▓▓▓▓▓  +1~+3  5×5 上层房间（内含战利品箱）
    ▓▓▓▓▓▓▓▓▓  -3~+0  9×9 塔身（南北各有一道门洞与窗口）
    ▓▓▓▓▓▓▓▓▓  -7~-4  9×9 地基（埋入地下，随地形平滑衔接）
```

* 材质为石砖 / 苔石砖 / 裂纹石砖 / 普通石头随机混合，塔身有门洞、窗口、雕纹石砖装饰。
* 塔顶上层房间里有一个战利品箱（使用原版地牢战利品表）。

### 2. 剑之底座（`sword_in_stone:sword_pedestal`）

* 只有**塔顶**那一个是插着剑的（`has_sword=true`）。
* 空手或手持物品右键点击插着剑的底座即可尝试拔剑（无需潜行、无需特定物品）。
* 拔剑条件：**玩家经验等级 ≥ 10 级**。
  * 不足 10 级：剑纹丝不动，只会提示当前需要的等级，不会扣经验。
  * 满足条件：扣除 **10 级经验**，誓约胜利之剑直接进入背包（背包满了会掉在脚下），底座变为空底座。
* **剑可以插回去**：手持誓约胜利之剑右键点击**空底座**，剑就会重新插回石中，底座回到 `has_sword=true`。
  这样你可以把它当成一个可反复开关的"祭坛"来用（注意：插回去不返还经验）。
* 底座造型为三级石阶 + 四角石柱 + 中央石柱与顶盘，插着剑时会显示剑身。
* 空底座也可以正常放置、破坏，行为与普通装饰方块一致。

### 3. 誓约胜利之剑（`sword_in_stone:stone_sword`）

* 贴图为深蓝剑身（带中央亮脊与暗色刀锋倒角）、金色护手、棕色握柄、金色球首的设计。
* 稀有度 RARE、1800 耐久、抗火（掉进岩浆不会烧掉）。
* 攻击力：+7（合计 8 点伤害，与钻石剑同级），攻速 1.6。
* 自带技能：**庇护（Sanctuary）**

| 项目 | 数值 |
| --- | --- |
| 触发方式 | 手持誓约胜利之剑，**按住右键蓄力 1.5 秒**后松开 |
| 蓄力不足 | 松开时提示“蓄力被打断”，不消耗、不进冷却 |
| 庇护效果 | **抗性提升 III** + **力量 II** + **迅捷 II** |
| 持续时间 | **10 分钟** |
| 冷却时间 | **12 分钟**（物品栏中会显示冷却读条） |

* 三个效果都是本模组注册的自定义效果，数值与原版同等级药水完全一致：
  * 抗性提升 III → 击退抗性 +0.9，免疫大部分伤害减免（每级 +0.3 击退抗性 + 原版伤害减免逻辑）
  * 力量 II → 近战伤害 +3
  * 迅捷 II → 移动速度 +20%
* 三个效果都有**专属像素图标**：抵抗＝蓝盾圣剑、力量＝金边盾牌、速度＝金环圣光环，HUD 与物品栏正常显示倒计时，等级显示为 III / II / II。
* 蓄力期间会有拉弓动作、进度音效，蓄力完成后有提示音与“庇护已就绪”提示。

---

## 二、安装方法

### 方式一：直接使用构建好的 jar（推荐）

1. 安装 **NeoForge 21.1.250**（或任意 21.1.x）对应的 Minecraft 1.21.1 客户端/服务端。
2. 把 `sword_in_stone-1.0.0.jar` 放进 `.minecraft/mods/` 目录。
3. 启动游戏即可，无需其他前置模组。

### 方式二：从源码构建

需要 **JDK 21**（Minecraft 1.21.1 要求 Java 21）。

```bash
# Windows
gradlew.bat build

# macOS / Linux
./gradlew build
```

产物位于 `build/libs/sword_in_stone-1.0.0.jar`。

开发环境常用任务：

```bash
gradlew.bat runClient     # 启动带模组的客户端
gradlew.bat runServer     # 启动带模组的服务端
gradlew.bat runData       # 运行数据生成器
```

---

## 三、游戏内指令（调试 / 服主用）

```mcfunction
# 立即在当前位置生成一座石中剑塔
/place structure sword_in_stone:sword_tower ~ ~ ~

# 直接获得石中剑 / 底座
/give @s sword_in_stone:stone_sword
/give @s sword_in_stone:sword_pedestal

# 直接给自己上庇护的三个效果（测试用，各 10 分钟）
/effect give @s sword_in_stone:sanctuary_resistance 600 2
/effect give @s sword_in_stone:sanctuary_strength 600 1
/effect give @s sword_in_stone:sanctuary_speed 600 1
```

> 提示：`/place structure` 会以当前位置为结构原点，底座在原点上方约 9 格处，请在空旷处执行。

---

## 四、可调参数

想改数值的话，所有关键数字都集中在这两个文件里：

| 想改什么 | 文件 | 常量 |
| --- | --- | --- |
| 蓄力时长 / 持续时间 / 冷却 | `src/main/java/com/dsh/swordinstone/item/StoneSwordItem.java` | `CHARGE_TICKS` / `SANCTUARY_DURATION_TICKS` / `COOLDOWN_TICKS` |
| 拔剑所需等级 | `src/main/java/com/dsh/swordinstone/block/SwordPedestalBlock.java` | `REQUIRED_LEVELS` |
| 效果等级 | 同上 `StoneSwordItem.java` | `castSanctuary()` 里的 amplifier（2 = III 级，1 = II 级） |
| 生成频率 | `src/main/resources/data/sword_in_stone/worldgen/structure_set/sword_towers.json` | `spacing` / `separation`（调大 = 更稀有） |
| 生成群系 | `src/main/resources/data/sword_in_stone/tags/worldgen/biome/has_structure/sword_tower.json` | `values` 列表 |
| 塔的战利品 | `SwordTowerStructure.java` | `decorate()` 中的箱子战利品表 |

---

## 五、已验证内容

本项目在真实的 NeoForge 21.1.250 专用服务器上跑过以下验证（非仅编译通过）：

| 项目 | 结果 |
| --- | --- |
| 编译 / 打包 | `gradle build` 成功，产出 `sword_in_stone-1.0.0.jar` |
| 服务端加载 | 启动无报错，`neoforge.mods.toml`、物品/方块/效果/结构全部注册成功 |
| 结构放置 | `/place structure sword_in_stone:sword_tower` 成功，塔身、门洞、城垛、底座齐备 |
| 塔顶底座 | 塔顶正中确认为 `sword_in_stone:sword_pedestal[has_sword=true]` |
| 自定义效果 | `/effect give` 成功施加，`active_effects` 中 amplifier 正确（抗性 2 / 力量 1 / 迅捷 1） |
| 自然生成 | 随机种子下主世界能自然刷出本结构（`/locate structure` 命中，且只落在陆地群系） |
| 资源完整性 | 全部 JSON（结构、结构集、群系标签、战利品表、模型、语言文件）解析通过 |
| 模型烘焙（游戏内） | 进世界时自检确认 `has_sword=true` 与 `false` 两个状态模型均烘焙成功、贴图接上（见下方"踩过的坑"） |
| 剑的外观与朝向 | 游戏内截图确认：金色球首朝上、蓝色剑刃朝下插入底座 |

> 说明：受限于纯命令行验证环境（无图形客户端、无真实玩家账号），**“右键蓄力 1.5 秒 → 庇护 → 12 分钟冷却”这条技能路径没有用真人账号点过**。
> 该路径的代码已逐行核对过 1.21.1 的 API 签名，但请在正式游玩前自行进游戏确认一次手感。

### 踩过的坑（供改模型的人参考）

这两个问题都是"MC 静默失败、不报任何错"，排查花了很久，记在这里免得重踩：

1. **UV 空间恒为 `0..16`。** MC 的 `BlockFaceUV` 只做 `convertToFloat`，**不存在 `texture_size` 字段**（原版和 NeoForge 都不解析它）。
   如果按 `0..64` 写 UV，会被当成 `0..16` 解读、全部指到贴图外，MC 会**静默丢掉整个模型**——
   表现就是"方块放下去什么都不显示"。
2. **`parent` 跨命名空间继承不可靠。** 曾把插剑状态的模型写成 `"parent": "sword_in_stone:block/sword_pedestal_empty"`，
   只保留剑的元素，结果游戏里插着剑的状态**只有剑、台座整块不见了**。
   现改为把台座元素**内联**进 `sword_pedestal_with_sword.json`（两个模型都只挂原版 `block/block`），彻底消除该失败点。
   代价：改动台座形状时要同时改两个文件。

`client/PedestalModelCheck.java` 会在进世界时把两个状态模型的烘焙面数与所用贴图打进日志，出问题时先看那里。

---

## 六、工程结构

```
src/main/java/com/dsh/swordinstone/
├── SwordInStone.java                     模组主类，注册所有内容
├── block/SwordPedestalBlock.java         底座方块：拔剑交互 + 含水状态 + 碰撞箱
├── client/PedestalModelCheck.java        启动自检：把底座模型烘焙结果写进日志
├── client/SanctuaryClientEffects.java    三个效果的像素图标绘制
├── effect/SanctuaryEffect.java           庇护效果（无逐帧行为）
├── item/StoneSwordItem.java              石中剑：蓄力、庇护、冷却、悬浮提示
├── item/SwordFeedback.java               动作栏提示小工具
├── registry/ModBlocks.java              方块注册
├── registry/ModItems.java               物品注册 + 创造模式物品栏
├── registry/ModEffects.java             三个自定义效果 + 玩家属性加成
├── registry/ModDataComponents.java      蓄力起始时间数据组件
├── registry/ModStructures.java          结构类型 / 结构碎片类型注册
└── worldgen/SwordTowerStructure.java    废墟塔的代码化生成逻辑

src/main/resources/
├── assets/sword_in_stone/               模型、方块状态、贴图、中英文语言文件
└── data/sword_in_stone/                 结构 JSON、结构集、群系标签、战利品表
```

---

## 七、兼容性

* Minecraft **1.21.1**，NeoForge **21.1.250**（`versionRange="[21.1.250,)"`）。
* 客户端与服务端**都需要**安装。
* 与其它模组无已知冲突；纯服务端逻辑（世界生成、技能结算）均在服务端执行。

---

## 八、构建

需要 **JDK 21**（Minecraft 1.21.1 要求）。仓库自带 Gradle wrapper，直接：

```bash
# Windows
gradlew.bat build

# macOS / Linux
./gradlew build
```

产物位于 `build/libs/sword_in_stone-1.0.0.jar`。

> 首次构建需要联网下载 NeoForge / Minecraft 依赖。若你的环境无法联网，可加 `--offline`
> 并使用已预热过的 `GRADLE_USER_HOME`。
>
> 国内网络下 Gradle 下载较慢时，可自行配置镜像后重试。

---

## 九、许可证

本项目采用 **MIT License**，全文见 [LICENSE](LICENSE)。

简单说，**你可以自由地**：

* 使用这个模组，包括放进整合包、服务器
* 阅读、修改源码，改成你自己想要的样子
* 再分发原版或你修改后的版本，甚至可以商用

**唯一的条件是**：在你分发的时候，保留原有的版权声明和这份 MIT 许可证文本。

软件按"原样"提供，不附带任何担保。

> 想给这个项目提交改动？欢迎直接开 Issue 或 Pull Request。
> Fork 之后改完提 PR 即可，不需要事先征求同意。

### English

This project is licensed under the **MIT License** — see [LICENSE](LICENSE) for the full text.

You are free to use, copy, modify, merge, publish, distribute, sublicense and/or sell
copies of this software. The only requirement is that you keep the original copyright
notice and the MIT license text in all copies or substantial portions of the software.

The software is provided "as is", without warranty of any kind.

Contributions are welcome — feel free to open an issue or send a pull request.

