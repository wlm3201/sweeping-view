# Sweeping View（横扫视图）

一个显示Minecraft横扫攻击判定范围的Fabric客户端模组。

当玩家手持剑并瞄准实体时，模组会在世界中绘制：

- **横扫范围** —— 攻击目标碰撞箱水平扩大1格、垂直扩大0.25格的方框；
- **攻击范围** —— 攻击者碰撞箱底面中心为球心的半径 3 格球体。

## 配置

通过ModMenu进入YACL配置：

| 选项     | 默认值    |
| -------- | --------- |
| 线框开关 | 开启      |
| 线框颜色 | `#FF5050` |
| 面框开关 | 开启      |
| 面框颜色 | `#FF5050` |
| 球体开关 | 开启      |
| 球体颜色 | `#FF5050` |

## 构建

一份源码同时面向多个 Minecraft 版本，通过 `settings.gradle` 中的两个子项目共享根目录的 `src/`：

```powershell
# 构建全部版本，产物在 v26_1_2/build/libs 与 v26_2/build/libs
.\gradlew build

# 启动指定版本的开发客户端
.\gradlew :v26_1_2:runClient
.\gradlew :v26_2:runClient
```

产物命名形如 `sweeping-view-1.0.0+26.2.jar`。

## 许可

[CC0 1.0](LICENSE)。
