<div align="center">

<img src="../../docs/assets/logo.png" width="128" alt="AEmulator logo"/>

# AEmulator — Honor MagicOS 12 适配分支

*基于 [uxazu/AEmulator](https://github.com/uxazu/AEmulator) 的分支，更早的源头是 [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*

**在现代手机上运行经典 Android 固件——HTC Sense、TouchWiz、MIUI、AOSP。无需 root，无需电脑。**

[![Version](https://img.shields.io/badge/version-0.0.0.3-3D5AFE?style=for-the-badge)](https://github.com/oopnv70-lab/AEmulator/releases) [![License](https://img.shields.io/badge/license-GPL--3.0-3D5AFE?style=for-the-badge)](../../LICENSE) [![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/oopnv70-lab/AEmulator) [![Fork of](https://img.shields.io/badge/fork%20of-uxazu%2FAEmulator-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/uxazu/AEmulator)

[🇬🇧 English](../../README.md) · [🇷🇺 Русский](README.ru.md) · [🇺🇦 Українська](README.uk.md) · [🇩🇪 Deutsch](README.de.md) · [🇫🇷 Français](README.fr.md) · [🇪🇸 Español](README.es.md) · [🇧🇷 Português](README.pt-BR.md) · [🇮🇹 Italiano](README.it.md) · [🇵🇱 Polski](README.pl.md) · [🇹🇷 Türkçe](README.tr.md) · [🇸🇦 العربية](README.ar.md) · [🇮🇷 فارسی](README.fa.md) · [🇮🇳 हिन्दी](README.hi.md) · [🇮🇩 Indonesia](README.id.md) · [🇻🇳 Tiếng Việt](README.vi.md) · **🇨🇳 简体中文** · [🇯🇵 日本語](README.ja.md) · [🇰🇷 한국어](README.ko.md)

</div>

---

AEmulator 直接从固件文件启动真实的 Android 2.3–17 系统：Recovery 卡刷包、Odin 包Google 出厂镜像或 GSI。旧的 32 位固件（2.3–4.4）通过改进的 QEMU 运行，Android 5.0–17 直接在手机的 arm64 核心上原生运行；内核 binder 被模拟，图形使用手机 GPU 绘制，声音经由 Android 音频系统播放——一切都在一个普通应用里完成。

## ✨ 功能

- 导入几乎所有格式：CWM/TWRP 卡刷 ZIP、三星 Odin `.tar.md5`、Google 出厂 `.tgz`、`system.img`、OTA `system.new.dat.br`
- 厂商系统原样运行：HTC Sense、三星 TouchWiz、MIUI、AOSP
- 经 GL 桥的硬件图形、声音、触控与多点触控，带现代 TLS 代理的网络
- 共享存储卡文件夹，方便放 APK、音乐和照片
- Material 3 Expressive 界面，支持 18 种语言
- 免费开源（GPL-3.0）

## 📱 固件列表

在 Xiaomi 15（骁龙 8 Elite，Android 16）上测试。任何适用于 Android 2.3–4.4 的 32 位 ARM 固件都值得一试；列表仅包含我们验证过的镜像。

## 🚀 快速开始

1. 从 [Releases](https://github.com/oopnv70-lab/AEmulator/releases) 下载并安装 APK。
2. 把上表中的固件下载到手机。
3. 打开 AEmulator →**添加固件**并选择文件。导入需要几分钟。
4. 点击**启动**。首次启动较慢：系统正在优化应用。
5. ⋮ 菜单可调音量、电源键和日志；⚙️ 打开设置与语言。

## 📋 要求

- 64 位 ARM 手机（arm64-v8a）上的 Android 8.0+
- 每个固件约需 1–3 GB 空间
- 推荐较新的骁龙 / 天玑 / Tensor 芯片

## ⚙️ 工作原理

在 Android 2.3–4.4 上，每个客户机进程运行在改进的用户态 QEMU 中；在 5.0–17 上，固件自带的 arm64 程序原生运行，由每个 Android 版本各自的引擎内核拦截其系统调用。binder 守护进程取代内核驱动，GL 桥把 OpenGL ES 调用转发给 GPU，小型客户机库（音频 HAL、audio policy 包装、LD_PRELOAD 垫片）让厂商代码适配模拟器。导入器读取固件，在 boot 镜像中找到 init 脚本，并生成系统服务的启动计划。

## 🛠️ 从源码构建

需要 JDK 17、Android SDK 36 和 NDK r28。客户机库通过 `native/*/build.sh` 构建。

```bash
git clone https://github.com/oopnv70-lab/AEmulator.git
cd AEmulator
./gradlew :app:assembleV0003Debug
```

## 📌 本分支做了什么

本仓库不是 AEmulator 的原版，而是**针对 Honor MagicOS 12（Android 12 / API 31）固件**的适配分支。上游代码基线为 `0.0.0.3`（根提交 `3b97ed5`），其后的提交全部为本分支改动。

**目标**：让官方 Honor MagicOS 12 固件在 AEmulator 上完成开机、进入桌面、能装软件、能正常使用。

### 已解决

- **VINTF 清单缺失导致 system_server 反复重启**：补声明框架侧 HAL 服务（hidl.manager 1.0/1.2、hidl.token、frameworks.sensorservice、frameworks.displayservice、system.net.netd）；把 13+ 才支持的 `<fqname>` 写法改成 12 能读的 `<interface>/<instance>`；并把同一 HAL 的两个版本合并进单个 `<hal>` 块——否则 12 的解析器会把整份框架清单判为冲突，报 NULL VINTF MANIFEST

- **厂商 HAL 拖死启动**：InitPlan 增加 vendor 硬件 HAL 跳过名单，并提供 `run/skip-services` 运行时跳过机制

- **开机向导卡住**：Honor 语言选择页本身是一个 setup wizard。新增逐级重试窗口（4s/8s/15s/30s/60s）轮询 `dumpsys activity`，发现向导在前台就 `pm disable-user` 并切回 HOME；同时修掉 `skipWizardNow()` 因 `bootCount > 0` 提前返回、导致向导永远跳不过的问题

- **安装软件报内存不足**：根因是存储路径映射。引擎的 `sd[]` 重定向表只认 8 个路径，而该固件 `storage_list.xml` 把主存储声明为 `/storage/sdcard`——不在表里，于是在镜像内只是个空目录。现在为固件自有存储点建符号链接指向引擎可映射的点，并让 `/proc/mounts` 为所有存储点写入 vfat 条目

### 已知未解决

- **设置里「可用空间」仍显示 0 B**。装软件已经正常，但存储页面的数字不对。已核对 AOSP 12 源码：存储页走 `StorageStatsService.getFreeBytes()`，其实现是 `path.getUsableSpace()`，也就是直接 `statfs(2)`，**并不读 `/proc/mounts`**。因此往镜像的 `/proc/mounts` 里加 FUSE 条目只会让文件「看起来」挂上了，对 `statfs` 的返回值没有任何影响。真正原因尚未定位，需要 guest 内部的 `df` / `statfs` 一手数据

- **设置锁屏密码提示存储不足**。可能与上一条同源，也可能是独立问题：`run/gatekeeper-1-0.log` 里有 `Unable to open GateKeeper HAL`，以及 `android.hardware.gatekeeper@1.0-service-qti` 的 fatal signal 6。尚未确认

### 已知限制

`app.aemu.SHELL` 广播调试通道受 `BuildConfig.DEBUG` 限制。装机版本虽然是 DEBUGGABLE 的，但实测广播从未送达 receiver（`dumpsys` 显示 `dispatchClockTime=1970`），因此无法直接进 guest 执行 `df`。后续取证需要改用应用内写文件的方式。

## 🙏 致谢

本项目的**全部原始架构与绝大部分代码**来自上游：
- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — 直接上游，GPL-3.0
- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — 更早的引擎源头（SENSES Emulator），GPL-2.0
- 原始引擎作者：[t.me/istratii_tech1](https://t.me/istratii_tech1)
第三方组件、二进制来源与许可证缺口详见 [NOTICE.md](../../NOTICE.md) 与 [docs/COMPLIANCE.md](../../docs/COMPLIANCE.md)。

## 📄 许可证

GPL-3.0。本仓库是 AEmulator 的非官方分支，上游作者未参与本项目。Android、商标及固件归其所有者所有。第三方组件与许可证清单见 NOTICE.md。
