# Dual Hotbar（双快捷栏）

Minecraft **1.21.1** / **NeoForge 21.1.248** 模组。**客户端与服务端都需要安装**（扩展快捷栏槽位是玩家物品栏的一部分，两端配置需一致）。

## 功能

- **底部快捷栏格子数可配置（1-18）**：1-9 格为原版快捷栏，10-18 格为扩展槽位。HUD 使用原版半透明贴图平铺渲染，任意格数都保持原版风格。
- **屏幕左右两侧各一个竖排快捷栏**（1-18 格可配置），垂直居中，原版半透明风格（hotbar 贴图旋转 90°）。
- **侧边栏是独立存储槽位，不是背包映射**：按 **E** 打开背包，原版物品栏下方会出现扩展行——底部扩展格、左侧栏、右侧栏。像原版快捷栏一样**把物品从背包拖入**即可使用。
- **选中即用（不换物品）**：按下侧边栏数字键/滚轮选中某格后，主手直接持有并使用该格物品（攻击、放置、食用、投掷等），**不会**把物品交换到底部快捷栏，与原版快捷栏交互完全一致。
- **切换时像原版一样显示物品名称**（复用原版 `Gui` 高亮机制）。
- **鼠标中键**（默认）显示/隐藏两侧快捷栏，按键可在设置中改绑。
- **模组菜单内配置**（Mods 列表 -> Dual Hotbar -> Config）。

## 操作

| 操作 | 效果 |
|---|---|
| `1~9` | 选择底部快捷栏 1-9 格（原版） |
| `0` | 选择底部第 10 格（需底部格数 > 9） |
| 鼠标滚轮 | 在底部快捷栏（全部 N 格）中循环 |
| `Shift + 1~9` / `Shift + 滚轮` | 切换左侧栏（数字键到第 9 格，滚轮循环全部） |
| `Ctrl + 1~9` / `Ctrl + 滚轮` | 切换右侧栏 |
| `Shift/Ctrl + 0` | 左侧/右侧第 10 格（需对应格数 > 9） |
| 鼠标中键 | 显示/隐藏两侧快捷栏 |

## 配置

配置文件：`.minecraft/config/dualhotbar-common.toml`（**客户端与服务端需一致**）。

| 配置 | 说明 | 默认 |
|---|---|---|
| `hotbar.hotbar_slots` | 底部快捷栏格数（1-18，重启生效） | 9 |
| `side_bars.left_slots` | 左侧竖栏格数（1-18，重启生效） | 9 |
| `side_bars.right_slots` | 右侧竖栏格数（1-18，重启生效） | 9 |
| `side_bars.left_enabled` | 显示左侧竖栏（实时生效） | true |
| `side_bars.right_enabled` | 显示右侧竖栏（实时生效） | true |
| `side_bars.show_by_default` | 进入世界时竖栏默认显示（实时生效） | true |

> ⚠️ 修改格数需重启游戏；`left/right_slots` 决定扩展槽位的偏移量，服务端与客户端必须一致，否则物品同步会出错。
> ⚠️ 默认绑定占用鼠标中键（原版"选取方块"），可在按键设置中改绑。

## 槽位布局

```
0-8    原版快捷栏
9-35   主背包
36+    左侧栏（left_slots 格）
…      右侧栏（right_slots 格）
…      底部扩展格（hotbar_slots - 9 格）
```

扩展槽位随玩家物品栏自动保存/加载（NBT），服务端同步，死亡掉落与原版一致。

> 创造模式物品栏界面不显示扩展槽（在生存背包界面填充）；创造模式下仍可通过按键/滚轮使用已放入的扩展槽物品。

## 构建

需要 JDK 21（Gradle 自动下载 Toolchain）与网络。

```bash
./gradlew build
```

产物：`build/libs/dualhotbar-1.0.0.jar`，放入 `.minecraft/mods/`（客户端与服务端都放）。

开发运行：`./gradlew runClient`。

## 技术要点

- **扩展物品栏**：Mixin `Inventory` 在构造时扩展 `items` 列表（`getContainerSize`/`save`/`load` 自动适配）；`getSelected()` 允许 selected 指向任意扩展槽（"选中即用"的核心）；`swapPaint` 滚轮循环范围扩展到全部底部格。
- **菜单**：Mixin `InventoryMenu` 在热键行后追加扩展槽行；`InventoryScreen` 加高窗口并绘制半透明面板 + 槽位框；创造模式屏幕（`selectTab`）跳过扩展槽避免重叠。
- **服务端/客户端同步**：放宽 `ServerGamePacketListenerImpl`/`ClientPacketListener` 的 carried-item 槽位校验（0..getContainerSize()）。
- **HUD**：底部栏按格裁剪原版 `hud/hotbar` 贴图（首格/中间格/末格三种裁剪实现无缝平铺，任意格数）；侧栏把同一贴图旋转 90°；选中框 `hud/hotbar_selection` 跟随 selected 槽。
- **输入**：`handleKeybinds` Mixin 拦截 Shift/Ctrl+数字（消费点击防干扰原版）；`0` 键与 Shift/Ctrl 滚轮走 NeoForge 输入事件；切换后立即发包同步 selected。

## 版本

Minecraft 1.21.1 / NeoForge 21.1.248 / ModDevGradle 2.0.143 / Java 21 / Gradle 9.2.1 / Mixin 0.8.7

## 已知问题与修复（v1.0.1 ~ v1.0.4）

- **启动崩溃 `@Redirect handler method dualhotbar$mutableWithSize has an invalid signature`**：@Redirect 的 handler 方法参数类型必须与目标方法**擦除后**的签名一致——`NonNullList.withSize(int, E)` 擦除为 `(int, Object)`，handler 写 `(int, ItemStack)` 会报 InvalidInjectionException。已改为 `(int, Object)` + raw `NonNullList` 返回。
- **加载存档被踢出"无效的玩家数据"（Invalid player data）**：`Inventory.items` 由 `NonNullList.withSize()` 创建，其 backing 是 `Arrays.asList`（**定长列表，`add()` 抛 `UnsupportedOperationException`**），扩展槽 `items.addAll(...)` 在服务端创建 `ServerPlayer` 时崩溃。已用 `@Redirect` 把 `Inventory.<init>` 中的 `withSize` 替换为 ArrayList-backed 的可变列表（反射调用 protected 构造器，defaultValue 语义保留），`compartments` 引用同一实例自动同步，扩展槽可正常追加。
- **启动崩溃 `@Shadow method addSlot ... was not located in the target class`**：Mixin 0.8.7 的 `@Shadow` 只解析目标类**自身声明**的成员，定义在父类（`AbstractContainerMenu.addSlot`、`AbstractContainerScreen.imageHeight/leftPos/topPos`、`ClientCommonPacketListenerImpl.minecraft`）的成员在 NeoForge 1.21 官方映射（无 remapper）运行时会直接崩溃。已改用**反射**（`MixinReflect` 工具类）替代这些父类成员的 `@Shadow`。
- **启动崩溃 `Redirector dualhotbar$skipExtraSlots ... failed injection check, (0/1) succeeded`**：`@Redirect` 的 `At(target="Ljava/util/List;add...")` 只能匹配 owner 为 `java/util/List` 的调用，而 `AbstractContainerMenu.slots/items` 字段类型是 **`NonNullList`**（`slots.add` 编译为 `invokevirtual NonNullList.add`），目标扫描为 0。已改为 `@Inject(method="selectTab", at=@At("TAIL"))` 直接对 `menu.slots` 做 `removeIf` 移除扩展槽。
- `dualhotbar.mixins.json` 已声明 `refmap`，消除 "No refMap loaded" 提示。

> ⚠️ 拾取物品时若原版 36 格已满，新拾取物会进入扩展槽（底部扩展格/侧栏）——如需限制可后续调整 `Inventory.getFreeSlot`。
