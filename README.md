# Dual Hotbar（双快捷栏）

**Minecraft 1.21.1 / NeoForge** 模组。**客户端与服务端都需要安装**（扩展快捷栏槽位是玩家物品栏的一部分，两端配置需一致）。
*A Minecraft 1.21.1 / NeoForge mod. Install on **both client and server** (the extra hotbar slots are part of the player inventory; configs must match on both sides).*

---

## 功能 / Features

- **底部快捷栏格子数可配置（1-18）**：1-9 格为原版快捷栏，10-18 格为扩展槽位。HUD 使用原版半透明贴图平铺渲染，任意格数都保持原版风格。
  *Configurable bottom hotbar (1-18 slots): 1-9 are the vanilla hotbar, 10-18 are extended slots. The HUD tiles the vanilla translucent sprite for any slot count.*
- **屏幕左右两侧各一个竖排快捷栏**（1-18 格可配置），垂直居中，原版半透明风格（hotbar 贴图旋转 90°）。
  *Vertical side hotbars on the left and right of the screen (1-18 slots each), vertically centered, vanilla translucent style (hotbar sprite rotated 90°).*
- **侧边栏是独立存储槽位，不是背包映射**：按 **E** 打开背包，原版物品栏下方会出现扩展行——底部扩展格、左侧栏、右侧栏。像原版快捷栏一样**把物品从背包拖入**即可使用。
  *Side bars are independent storage slots (not a backpack mapping): press **E** to open the inventory and you'll see extra rows below the vanilla grid (bottom extended slots, left bar, right bar). Drag items in from the backpack like the vanilla hotbar.*
- **选中即用（不换物品）**：按下侧边栏数字键/滚轮选中某格后，主手直接持有并使用该格物品（攻击、放置、食用、投掷等），**不会**把物品交换到底部快捷栏，与原版快捷栏交互完全一致。
  *Select-and-use (no item swapping): select a side-bar slot with number keys/scroll and the main hand directly holds and uses that item (attack, place, eat, throw, etc.) — nothing is swapped with the bottom hotbar, exactly like vanilla.*
- **切换时像原版一样显示物品名称**（复用原版 `Gui` 高亮机制）。
  *Item name tooltip on switch, same as vanilla (reuses the vanilla `Gui` highlight mechanism).*
- **鼠标中键**（默认）显示/隐藏两侧快捷栏，按键可在设置中改绑。
  *Middle mouse button (default) toggles the side bars; rebindable in key settings.*
- **模组菜单内配置**（Mods 列表 → Dual Hotbar → Config）。
  *Config screen inside the mod menu (Mods list → Dual Hotbar → Config).*

---

## 操作 / Controls

| 操作 / Action | 效果 / Effect |
|---|---|
| `1~9` | 选择底部快捷栏 1-9 格（原版） / Select bottom hotbar slots 1-9 (vanilla) |
| `0` | 选择底部第 10 格（需底部格数 > 9） / Select bottom slot 10 (requires bottom slots > 9) |
| 鼠标滚轮 / Mouse scroll | 在底部快捷栏（全部 N 格）中循环 / Cycle through all bottom hotbar slots |
| `Shift + 1~9` / `Shift + 滚轮` | 切换左侧栏 / Select left bar (keys up to 9, scroll cycles all) |
| `Ctrl + 1~9` / `Ctrl + 滚轮` | 切换右侧栏 / Select right bar |
| `Shift/Ctrl + 0` | 左侧/右侧第 10 格（需对应格数 > 9） / Left/right bar slot 10 (requires > 9 slots) |
| 鼠标中键 / Middle mouse | 显示/隐藏两侧快捷栏 / Toggle side bars visibility |

---

## 配置 / Configuration

配置文件：`.minecraft/config/dualhotbar-common.toml`（**客户端与服务端需一致**）。
*Config file: `.minecraft/config/dualhotbar-common.toml` (**must match on client and server**).*

| 配置 / Key | 说明 / Description | 默认 / Default |
|---|---|---|
| `hotbar.hotbar_slots` | 底部快捷栏格数（1-18，重启生效）/ Bottom hotbar slots (1-18, requires restart) | 9 |
| `side_bars.left_slots` | 左侧竖栏格数（1-18，重启生效）/ Left bar slots (1-18, restart) | 9 |
| `side_bars.right_slots` | 右侧竖栏格数（1-18，重启生效）/ Right bar slots (1-18, restart) | 9 |
| `side_bars.left_enabled` | 显示左侧竖栏（实时生效）/ Show left bar (live) | true |
| `side_bars.right_enabled` | 显示右侧竖栏（实时生效）/ Show right bar (live) | true |
| `side_bars.show_by_default` | 进入世界时竖栏默认显示（实时生效）/ Side bars visible by default (live) | true |

> ⚠️ 修改格数需重启游戏；`left/right_slots` 决定扩展槽位的偏移量，服务端与客户端必须一致，否则物品同步会出错。
> ⚠️ Changing slot counts requires a game restart; `left/right_slots` determine the extended-slot offset and must match between server and client, otherwise item sync will break.
>
> ⚠️ 默认绑定占用鼠标中键（原版"选取方块"），可在按键设置中改绑。
> ⚠️ The default binding occupies the middle mouse button (vanilla "pick block"); rebind it in key settings if needed.

---

## 槽位布局 / Slot Layout

```
0-8    原版快捷栏 / Vanilla hotbar
9-35   主背包 / Main inventory
36+    左侧栏（left_slots 格） / Left bar (left_slots)
…      右侧栏（right_slots 格） / Right bar (right_slots)
…      底部扩展格（hotbar_slots - 9 格） / Bottom extended slots (hotbar_slots - 9)
```

扩展槽位随玩家物品栏自动保存/加载（NBT），服务端同步，死亡掉落与原版一致。
*Extended slots are saved/loaded with the player inventory (NBT), synced with the server, and dropped on death like vanilla.*

> 创造模式物品栏界面不显示扩展槽（在生存背包界面填充）；创造模式下仍可通过按键/滚轮使用已放入的扩展槽物品。
> The creative inventory screen hides the extended slots (fill them in the survival inventory screen); in creative mode you can still use items already placed in extended slots via keys/scroll.

---

## 构建 / Building

需要 JDK 21（Gradle 自动下载 Toolchain）与网络。
*Requires JDK 21 (Gradle downloads the toolchain automatically) and network access.*

```bash
./gradlew build
```

产物：`build/libs/dualhotbar-1.0.0.jar`，放入 `.minecraft/mods/`（客户端与服务端都放）。
*Output: `build/libs/dualhotbar-1.0.0.jar`, drop it into `.minecraft/mods/` (both client and server).*

开发运行：`./gradlew runClient`。 *Development run: `./gradlew runClient`.*

---


## 版本 / Versions

Minecraft 1.21.1 / NeoForge 21.1.248 / ModDevGradle 2.0.143 / Java 21 / Gradle 9.2.1 / Mixin 0.8.7

## 下载 / Download

从 [Releases](../../releases) 页面下载最新的 `dualhotbar-1.0.0.jar`。
*Download the latest `dualhotbar-1.0.0.jar` from the [Releases](../../releases) page.*
