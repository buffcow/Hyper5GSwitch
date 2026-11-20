# 5G开关（Hyper5GSwitch）
在移动网络面板中添加 5G 开关，仅适用于支持5G网络并搭载 HyperOS 的设备。

Add a 5G switch to the mobile network panel, only for devices that support 5G net and equipped with HyperOS.

## 特点
1. 无后台服务
2. 无图标，无配置，即装即用

## 使用与作用域

1. 在 LSPosed 管理器中激活模块。
2. 勾选 **系统界面**（`com.android.systemui`），在控制中心的移动网络面板中添加 5G 开关。
3. 如果 HyperOS 4 会在重启或切卡后自动开启 5G，再勾选 **电话服务**（`com.android.phone`）。从旧版升级的用户也请检查这一项。
4. **重启手机**，使模块在所选作用域中生效。

## HyperOS 4 强制开启 5G

在部分 HyperOS 4 设备上，即使手动关闭了 5G，系统电话服务也可能在切换默认数据 SIM 卡或重启手机时重新开启 5G。启用「电话服务」作用域后，模块会让电话服务将 5G 开关视为可见，从而跳过因隐藏开关而强制开启 5G 的逻辑。

## 下载
[LSPosed 仓库](https://github.com/Xposed-Modules-Repo/cn.buffcow.hyper5g/releases)

[蓝奏云](https://qyma.lanzout.com/b051np9gf) 密码:`miui`

## 无效
请先检查模块是否正常激活，并且作用域是否勾选。<br>如果排查后仍有错误，请提交issue。或联系酷安[@buffcow](http://www.coolapk.com/u/1188320)(qingyu)

## 致谢
- [libxposed](https://github.com/libxposed)
- [~~customiuizer~~](https://github.com/MonwF/customiuizer)
