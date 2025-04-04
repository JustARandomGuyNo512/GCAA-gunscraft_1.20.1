# GCAA-gunscraft_1.20.1 项目结构

这是一个Minecraft 1.20.1版本的枪械模组项目，提供了多种枪械、弹药和附件系统。以下是项目的目录结构，以便于进行二次开发。

## 目录

- [源代码概览](#源代码概览)
- [模块详细说明](#模块详细说明)
  - [1. 附加组件系统 (addon)](#1-附加组件系统-addon)
  - [2. 附件系统核心 (attachmentSys)](#2-附件系统核心-attachmentsys)
  - [3. 方块系统 (blocks)](#3-方块系统-blocks)
  - [4. 能力系统 (capability)](#4-能力系统-capability)
  - [5. 客户端系统 (client)](#5-客户端系统-client)
  - [6. 通用代码 (common)](#6-通用代码-common)
  - [7. 数据系统 (data)](#7-数据系统-data)
  - [8. 实体系统 (entities)](#8-实体系统-entities)
  - [9. 工业系统 (industrial)](#9-工业系统-industrial)
  - [10. 物品系统 (items)](#10-物品系统-items)
    - [10.1 弹药系统 (ammunition)](#101-弹药系统-ammunition)
    - [10.2 附件系统 (attachments)](#102-附件系统-attachments)
    - [10.3 枪械系统 (gun)](#103-枪械系统-gun)
  - [11. 库系统 (lib)](#11-库系统-lib)
  - [12. Mixin系统 (mixin)](#12-mixin系统-mixin)
  - [13. 网络系统 (network)](#13-网络系统-network)
  - [14. 服务系统 (service)](#14-服务系统-service)
  - [15. 音效系统 (sounds)](#15-音效系统-sounds)
  - [16. 工具系统 (utils)](#16-工具系统-utils)

## 源代码概览

以下是`src/main/java/sheridan/gcaa/`目录下的主要Java源码结构概览：

```
└── src/main/java/sheridan/gcaa/
    ├── Clients.java      # 客户端相关代码，处理客户端初始化和管理
    ├── Commons.java      # 通用工具类，提供全局使用的工具方法
    ├── GCAA.java         # 模组主类，包含模组初始化、事件注册和生命周期管理
    ├── ModTabs.java      # 创造模式物品栏分类，定义模组在创造模式中的物品分类
    ├── addon/            # 附加组件系统
    ├── attachmentSys/    # 附件系统核心
    ├── blocks/           # 方块相关
    ├── capability/       # 能力系统
    ├── client/           # 客户端代码
    ├── common/           # 通用代码
    ├── data/             # 数据生成
    ├── entities/         # 实体相关
    ├── industrial/       # 工业相关内容
    ├── items/            # 物品相关
    ├── lib/              # 库系统，提供API和工具方法
    └── mixin/            # Mixin系统，用于修改Minecraft原版代码
    ├── network/         # 网络系统，处理客户端和服务器之间的通信
    ├── service/         # 服务系统，提供产品交易和注册功能
    ├── sounds/          # 音效系统，管理模组中的所有音效
    └── utils/           # 工具系统，提供各种实用工具方法
```

## 模块详细说明

### 1. 附加组件系统 (addon)

附加组件系统提供了模组的扩展功能，允许添加新的功能模块。

```
└── addon/
    ├── Addon.java        # 附加组件基类，定义附加组件的基本结构
    └── AddonHandler.java # 附加组件处理器，管理所有附加组件
```

[返回目录](#目录)

### 2. 附件系统核心 (attachmentSys)

附件系统是本模组的核心功能之一，负责管理枪械附件的安装、卸载和功能实现。

```
└── attachmentSys/
    ├── AttachmentSlot.java            # 附件槽位定义
    ├── AttachmentSlotProxy.java       # 附件槽位代理，处理不同枪械的附件兼容性
    ├── IAttachmentSlotProxyCreator.java # 附件槽位代理创建接口
    ├── Rail.java                      # 导轨系统，用于安装附件
    ├── common/                         # 附件系统通用组件
    │   ├── AttachmentsHandler.java     # 附件处理器，管理附件的安装和卸载
    │   └── AttachmentsRegister.java    # 附件注册器，注册所有附件类型
    └── proxies/                        # 特定枪械的附件代理实现
        ├── AkmAttachmentProxy.java     # AKM枪械的附件代理
        ├── HkG28AttachmentProxy.java   # HK G28枪械的附件代理
        ├── M4a1AttachmentProxy.java    # M4A1枪械的附件代理
        └── Mk47AttachmentProxy.java    # MK47枪械的附件代理
```

[返回目录](#目录)

### 3. 方块系统 (blocks)

方块系统包含模组中的各种功能性方块，如弹药处理器、自动售货机等。

```
└── blocks/
    ├── AirLightBlock.java         # 空气光源方块，用于枪口闪光效果
    ├── AmmunitionProcessor.java   # 弹药处理器方块，用于制作和修改弹药
    ├── ModBlocks.java             # 方块注册类，注册所有模组方块
    ├── VendingMachine.java        # 自动售货机方块，用于购买枪械和弹药
    └── industrial/                # 工业相关方块
        └── BulletCrafting.java    # 子弹制作方块，用于制作子弹
```

[返回目录](#目录)

### 4. 能力系统 (capability)

能力系统用于管理玩家的枪械相关状态和数据。

```
└── capability/
    ├── PlayerStatus.java          # 玩家状态能力，存储玩家的枪械相关状态
    ├── PlayerStatusEvents.java    # 玩家状态事件，处理玩家状态变化
    └── PlayerStatusProvider.java  # 玩家状态提供者，提供玩家状态能力
```

[返回目录](#目录)

### 5. 客户端系统 (client)

客户端系统负责处理模组的客户端渲染、动画、界面等功能。

```
└── client/
    ├── KeyBinds.java              # 按键绑定，定义模组使用的按键
    ├── animation/                 # 动画系统
    │   ├── AnimationHandler.java  # 动画处理器，管理所有动画
    │   ├── AnimationSequence.java # 动画序列，定义动画的播放顺序
    │   ├── CameraAnimationHandler.java # 相机动画处理器，处理视角动画
    │   ├── frameAnimation/        # 帧动画系统
    │   ├── io/                    # 动画输入输出
    │   └── recoilAnimation/       # 后坐力动画系统
    ├── model/                     # 模型系统
    ├── render/                    # 渲染相关
    ├── screens/                   # 游戏界面
    └── 其他客户端功能类...
```

[返回目录](#目录)

### 6. 通用代码 (common)

通用代码包含服务端和客户端共用的功能和工具。

```
└── common/
    ├── HeadBox.java              # 头部碰撞箱，用于头部射击判定
    ├── config/                   # 通用配置
    │   └── CommonConfig.java     # 通用配置类，存储服务端和客户端共用的设置
    ├── damageTypes/              # 伤害类型
    │   ├── DamageTypes.java      # 伤害类型定义
    │   └── ProjectileDamage.java # 投射物伤害
    ├── events/                   # 通用事件
    │   ├── CommonEvents.java     # 通用事件处理器
    │   └── TestEvents.java       # 测试事件
    └── server/                   # 服务端代码
        └── projetile/            # 服务端投射物处理
```

[返回目录](#目录)

### 7. 数据系统 (data)

数据系统负责生成和管理模组的各种数据，如枪械属性、自动售货机产品等。

```
└── data/
    ├── IJsonSyncable.java        # JSON同步接口
    ├── Utils.java                # 数据工具类
    ├── gun/                      # 枪械数据
    │   ├── GunPropertiesHandler.java  # 枪械属性处理器
    │   └── GunPropertiesProvider.java # 枪械属性提供者
    ├── industrial/               # 工业数据
    │   └── bulletCrafting/       # 子弹制作数据
    └── vendingMachineProducts/   # 自动售货机产品数据
        ├── VendingMachineProductsHandler.java  # 自动售货机产品处理器
        └── VendingMachineProductsProvider.java # 自动售货机产品提供者
```

[返回目录](#目录)

### 8. 实体系统 (entities)

实体系统包含模组中的各种实体，如投射物、方块实体等。

```
└── entities/
    ├── ModEntities.java          # 实体注册类，注册所有模组实体
    ├── industrial/               # 工业实体
    │   └── BulletCraftingBlockEntity.java # 子弹制作方块实体
    └── projectiles/              # 投射物实体
        └── Grenade.java          # 手榴弹实体
```

[返回目录](#目录)

### 9. 工业系统 (industrial)

工业系统负责处理模组中的工业相关内容，如配方、制作等。

```
└── industrial/
    ├── AmmunitionRecipe.java     # 弹药配方
    ├── Recipe.java               # 配方基类
    └── RecipeRegister.java       # 配方注册器
```

[返回目录](#目录)

### 10. 物品系统 (items)

物品系统是模组的核心部分，包含枪械、弹药、附件等物品的定义和实现。

#### 10.1 弹药系统 (ammunition)

弹药系统负责管理各种类型的弹药及其修改器。

```
└── ammunition/
    ├── Ammunition.java           # 弹药基类，定义弹药的基本属性和行为
    ├── AmmunitionHandler.java    # 弹药处理器，管理弹药的加载和使用
    ├── AmmunitionMod.java        # 弹药修改器基类，定义弹药修改器的基本结构
    ├── AmmunitionModRegister.java # 弹药修改器注册器
    ├── IAmmunition.java          # 弹药接口，定义弹药必须实现的方法
    ├── IAmmunitionMod.java       # 弹药修改器接口
    ├── ammunitionMods/           # 弹药修改器
    │   ├── AmmunitionMods.java   # 弹药修改器注册
    │   ├── ArmorPiercing.java    # 穿甲弹修改器，增加穿透能力
    │   ├── EfficientPropellant.java # 高效推进剂，增加射程和速度
    │   └── 其他弹药修改器...
    └── ammunitions/              # 弹药类型
        ├── Ammo12Gauge.java      # 12号口径霰弹
        ├── Ammo357Magnum.java    # .357马格南弹药
        └── 其他弹药类型...
```

[返回目录](#目录)

#### 10.2 附件系统 (attachments)

附件系统包含各种枪械附件的定义和实现。

```
└── attachments/
    ├── Attachment.java           # 附件基类，定义附件的基本属性和行为
    ├── Compensator.java          # 补偿器基类，减少后坐力
    ├── Grip.java                 # 握把基类，提高稳定性
    ├── Handguard.java            # 护木基类，提供附件安装点
    ├── 接口和基类...
    ├── akStuff/                  # AK系列附件
    ├── arStuff/                  # AR系列附件
    ├── functional/               # 功能性附件
    ├── grip/                     # 握把
    ├── handguard/                # 护木
    ├── mag/                      # 弹匣
    ├── muzzle/                   # 枪口装置
    ├── other/                    # 其他附件
    ├── replaceableParts/         # 可替换部件
    ├── scope/                    # 瞄准镜
    ├── sight/                    # 瞄具
    └── stock/                    # 枪托
```

[返回目录](#目录)

#### 10.3 枪械系统 (gun)

枪械系统是模组的核心，包含各种枪械的定义和实现，以及射击模式、口径系统等。

```
└── gun/
    ├── ArmPoseHandler.java       # 手臂姿势处理器，管理持枪姿势
    ├── AutoShotgun.java          # 自动霰弹枪类，实现自动霰弹枪功能
    ├── EditableAttributeModifier.java # 可编辑属性修改器，用于动态修改枪械属性
    ├── Gun.java                  # 枪械基类，定义枪械的基本属性和行为，管理射击、装弹、卸弹等核心功能
    ├── GunProperties.java        # 枪械属性类，定义枪械的各种属性
    ├── HandActionGun.java        # 手动操作枪械类，如泵动式霰弹枪
    ├── IGun.java                 # 枪械接口，定义枪械必须实现的方法
    ├── IGunFireMode.java         # 枪械射击模式接口
    ├── MG.java                   # 机枪类，实现机枪特有功能
    ├── Pistol.java               # 手枪类，实现手枪特有功能
    ├── ProjectileData.java       # 投射物数据类，定义子弹的飞行数据
    ├── PropertyExtension.java    # 属性扩展类，用于扩展枪械属性
    ├── PumpActionShotgun.java    # 泵动式霰弹枪类
    ├── SMG.java                  # 冲锋枪类
    ├── Sniper.java               # 狙击枪类
    ├── calibers/                 # 口径系统
    │   ├── Caliber.java          # 口径基类，定义口径的基本属性
    │   └── CaliberGauge12.java   # 12号口径实现
    ├── fireModes/                # 射击模式系统
    │   ├── Auto.java             # 全自动射击模式
    │   ├── Burst.java            # 点射模式
    │   ├── Charge.java           # 蓄力射击模式
    │   ├── HandAction.java       # 手动操作射击模式
    │   └── Semi.java             # 半自动射击模式
    ├── guns/                     # 具体枪械实现
    │   ├── Ak12.java             # AK-12突击步枪
    │   ├── Akm.java              # AKM突击步枪
    │   ├── Annihilator.java      # Annihilator枪械
    │   ├── AugA3.java            # AUG A3突击步枪
    │   ├── Awp.java              # AWP狙击步枪
    │   ├── Beretta686.java       # Beretta 686霰弹枪
    │   ├── Fn57.java             # FN Five-Seven手枪
    │   ├── FnBallista.java       # FN Ballista狙击步枪
    │   ├── G19.java              # Glock 19手枪
    │   ├── HkG28.java            # HK G28狙击步枪
    │   ├── M249.java             # M249轻机枪
    │   ├── M4a1.java             # M4A1突击步枪
    │   ├── M60E4.java            # M60E4通用机枪
    │   ├── M870.java             # Remington 870霰弹枪
    │   ├── MCXSpear.java         # MCX Spear突击步枪
    │   ├── Mk47.java             # MK47突击步枪
    │   ├── Mp5.java              # MP5冲锋枪
    │   ├── Python357.java        # Python .357左轮手枪
    │   ├── Vector45.java         # Vector .45冲锋枪
    │   └── Xm1014.java           # XM1014自动霰弹枪
    └── propertyExtensions/       # 属性扩展系统
        ├── AutoShotgunExtension.java # 自动霰弹枪属性扩展
        ├── HandActionExtension.java  # 手动操作枪械属性扩展
        └── SingleReloadExtension.java # 单发装填属性扩展
```

[返回目录](#目录)

### 11. 库系统 (lib)

库系统提供了模组的API和工具方法，允许其他模组或扩展与本模组进行交互。

```
└── lib/
    ├── ArsenalLib.java          # 库主类，提供模组API和工具方法
    └── events/                  # 事件系统
        └── server/              # 服务端事件
            └── VendingMachineTradeEvent.java # 自动售货机交易事件
```

库系统的ArsenalLib.java提供了一系列方法，用于加载模型、动画，注册枪械模型和附件，以及获取客户端武器状态等功能，是模组与外部交互的主要接口。

[返回目录](#目录)

### 12. Mixin系统 (mixin)

Mixin系统用于修改Minecraft原版代码，实现一些无法通过常规方式实现的功能。

```
└── mixin/
    ├── BlockLightEngineMixin.java    # 方块光照引擎Mixin，用于修改光照系统
    ├── EntityRenderDispatcherMixin.java # 实体渲染调度器Mixin
    ├── GameRendererMixin.java        # 游戏渲染器Mixin，用于修改相机和视角
    ├── HumanoidModelMixin.java       # 人形模型Mixin，用于修改玩家模型姿势
    ├── PenetrationMixin.java         # 穿透Mixin，实现子弹穿透功能
    └── RenderItemMixin.java          # 物品渲染Mixin，修改物品渲染方式
```

Mixin系统通过注入代码到Minecraft原版类中，实现了枪械的后坐力效果、子弹穿透、自定义持枪姿势等功能，是模组实现高级功能的重要组成部分。

[返回目录](#目录)

### 13. 网络系统 (network)

网络系统负责处理客户端和服务器之间的通信，包括枪械射击、装弹、附件安装等操作的同步。

```
└── network/
    ├── IPacket.java             # 数据包接口，定义数据包的基本结构和方法
    ├── PacketHandler.java       # 数据包处理器，注册和管理所有网络数据包
    └── packets/                 # 数据包实现
        ├── c2s/                 # 客户端到服务器的数据包
        │   ├── GunFirePacket.java           # 枪械射击数据包
        │   ├── GunReloadPacket.java         # 枪械装弹数据包
        │   ├── InstallAttachmentsPacket.java # 安装附件数据包
        │   ├── SwitchFireModePacket.java    # 切换射击模式数据包
        │   └── 其他客户端到服务器数据包...
        └── s2c/                 # 服务器到客户端的数据包
            ├── BroadcastPlayerStatusPacket.java # 广播玩家状态数据包
            ├── ClientSoundPacket.java           # 客户端音效数据包
            ├── HeadShotFeedBackPacket.java      # 爆头反馈数据包
            ├── UpdateGunPropertiesPacket.java   # 更新枪械属性数据包
            └── 其他服务器到客户端数据包...
```

网络系统是模组中客户端和服务器通信的核心，确保了多人游戏中枪械操作的同步和一致性。所有的网络数据包都实现了IPacket接口，并通过PacketHandler进行注册和管理。

[返回目录](#目录)

### 14. 服务系统 (service)

服务系统提供了产品交易和注册功能，主要用于自动售货机和物品回收系统。

```
└── service/
    ├── ProductTradingHandler.java  # 产品交易处理器，管理产品的购买和交换
    ├── ProductsRegister.java       # 产品注册器，注册所有可交易的产品
    └── product/                    # 产品实现
        ├── AmmunitionProduct.java  # 弹药产品
        ├── AttachmentProduct.java  # 附件产品
        ├── CommonProduct.java      # 通用产品
        ├── GunProduct.java         # 枪械产品
        ├── IProduct.java           # 产品接口，定义产品的基本属性和方法
        ├── IRecycleProduct.java    # 可回收产品接口
        └── 其他产品实现...
```

服务系统负责管理模组中的经济系统，包括产品的注册、定价、购买和回收。ProductsRegister类注册了所有可交易的产品，而ProductTradingHandler类则处理产品的交易逻辑。

[返回目录](#目录)

### 15. 音效系统 (sounds)

音效系统管理模组中的所有音效，包括枪械射击、装弹、附件安装等音效。

```
└── sounds/
    └── ModSounds.java           # 音效注册类，注册所有模组音效
```

音效系统通过ModSounds类注册和管理所有模组音效，包括通用音效和特定枪械的音效。这些音效文件存储在`src/main/resources/assets/gcaa/sounds/`目录下，按照枪械类型进行分类，如ak12、m4a1、awp等。

[返回目录](#目录)

### 16. 工具系统 (utils)

工具系统提供了各种实用工具方法，用于渲染、数学计算、字体处理等。

```
└── utils/
    ├── FontUtils.java           # 字体工具类，提供字体渲染和颜色处理方法
    └── RenderAndMathUtils.java  # 渲染和数学工具类，提供渲染和数学计算方法
```

工具系统提供了模组中常用的工具方法，FontUtils类用于处理字体渲染和颜色，而RenderAndMathUtils类则提供了各种渲染和数学计算方法，如随机数生成、矩阵变换等。

[返回目录](#目录)