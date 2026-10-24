# NOTICE — 来源声明、第三方组件与许可证

本文件回答三个问题：**这份代码从哪来、里面哪些东西不是我们的、各自是什么许可证。**

---

## 1. 上游来源（本仓库不是原创项目）

本仓库是 **AEmulator 的分支副本（fork）**，所有原始代码与架构设计均来自上游，不由本仓库作者创作。

| | |
|---|---|
| 直接上游 | [uxazu/AEmulator](https://github.com/uxazu/AEmulator) |
| 上游版本基线 | `0.0.0.3`（本仓库根提交 `3b97ed5`，原样导入上游快照） |
| 更早的上游 | [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — *SENSES Emulator* |
| 上游许可证 | GNU GPL v3.0（AEmulator）／ GNU GPL v2.0（SENSES，见其 `COPYING`） |
| 本仓库许可证 | GNU GPL v3.0（见 [LICENSE](LICENSE)） |

上游 README 中「Based on the original [SENSES emulator](https://github.com/Istratii-tech/SENSES)」一句即来源于此。
原始引擎的最早作者为 **Istratii-tech**（联系方式见其仓库 `NOTICE`）。

### 1.1 本仓库作者的实际改动范围

本仓库除根提交以外的提交，全部由 `oopnv70-lab` 提交，**集中在「让 Honor MagicOS 12（API 31）固件跑起来」这一目标上**，不涉及对上游架构的重写。详见 [README.md](README.md) 的「本项目进展」一节。

> **诚实说明**：本项目目前是**上游代码的一个适配分支**，尚未形成独立可维护的分叉。若上游有更新，本仓库的改动需要重新评估是否可以合并。

---

## 2. 打包进 APK 的二进制组件（重点，此前缺失声明）

仓库内 `app/src/main/assets/` 与 `app/src/main/jniLibs/` 共有 **1186 个已跟踪的 `.so` 文件**，它们是**预编译产物**，绝大部分不是本仓库编译生成，也不是上游以源码形式提供的。

| 位置 | 数量 | 说明 |
|---|---|---|
| `app/src/main/jniLibs/arm64-v8a/libaemu64_21.so` … `_37.so` | 17 | 本项目的引擎内核（`native/guest64/*/run64.c` 等编译产物） |
| `app/src/main/jniLibs/arm64-v8a/libqemu_gb.so`、`libqemu_kk.so`、`libqemu_arm.so` | 3 | **修改版 QEMU**，见 §3 |
| `app/src/main/assets/engines/**` | 1166 | 各 Android 版本（API 21–37）的 guest 侧库：SwiftShader、HAL 实现、shim 等 |

### 2.1 没有源码的二进制 —— 已知风险

上游的 `NOTICE`（SENSES）明确说明其**源码分发包含 `qemu/`、`licenses/` 与根目录文件**，而 `guest/`、`art/`、`tools/` 等目录属于「application's own source tree, which is not part of this source distribution」。

**本仓库同样没有包含这些目录的源码**，因此以下内容目前**无法从本仓库构建**：

- `libqemu_*.so`（QEMU 修改版）— 其源码与补丁**不在本仓库**
- `app/src/main/assets/engines/**` 下的多数 `.so`

这是 GPL 合规上**最需要处理的一项**：以二进制形式分发 GPL 组件时，必须能提供对应的完整源码。**当前状态：未满足。** 处理方式见 [docs/COMPLIANCE.md](docs/COMPLIANCE.md)。

---

## 3. QEMU — GNU GPL v2

APK 内的 `libqemu_gb.so`、`libqemu_kk.so`、`libqemu_arm.so` 是**修改过的 QEMU**。

| | |
|---|---|
| 上游版本 | QEMU 8.2.4（据上游 SENSES `NOTICE` 记载） |
| 上游来源 | `https://download.qemu.org/qemu-8.2.4.tar.xz` |
| 许可证 | GNU GPL v2 |
| 上游补丁与构建脚本 | 见 [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) 的 `qemu/patches/`、`qemu/src/`、`qemu/build-android.sh` |
| **本仓库是否含源码** | **否** |

> GPLv2 §3 要求以二进制分发时提供对应源码。本仓库目前直接沿用了上游编译好的二进制，**没有**附带 QEMU 源码或补丁，因此**分发前必须补齐**，否则构成许可证违规。

---

## 4. SwiftShader — Apache License 2.0

`app/src/main/assets/engines/*/system#lib#*#egl#lib*_swiftshader.so` 等文件为 Google 的 SwiftShader（软件 OpenGL ES 实现）。

- 上游：https://swiftshader.googlesource.com/SwiftShader
- 许可证：Apache License 2.0
- 本仓库提供的是预编译二进制，未附带 `LICENSE`/`NOTICE` 文本。**Apache-2.0 §4(d) 要求随分发附带 NOTICE**，当前缺失。

---

## 5. Android Open Source Project 头文件与代码 — Apache License 2.0

`app/src/main/java/` 中部分文件包含 AOSP 版权头（例如 `native/gueststubs/src/app/aemu/stub/VoldStub.java` 等处的 `android.os.*` 用法与签名说明）。这些内容遵循 Apache License 2.0。

---

## 6. 固件与商标

本项目**不分发任何固件镜像**。用户导入的 recovery ZIP、Odin 包、factory image、GSI 等，其版权归各自厂商所有，使用需遵守当地法律与厂商条款。

Android、HTC Sense、TouchWiz、MIUI、Honor MagicOS 等名称与商标归各自所有者。本项目与上述厂商**无任何关联、未获其授权或背书**。

---

## 7. 生成式 AI 参与说明

本仓库中标注为 `oopnv70-lab` 的提交，有相当比例是在 **AI 辅助（大语言模型）** 下完成的：问题定位、代码编写、提交信息均由 AI 生成，经人工确认后提交。

- AI 工具**不持有**这些代码的版权，其版权仍按 GPL-3.0 归本项目贡献者所有。
- 上游根提交 `3b97ed5` 的提交信息中带有 `Co-Authored-By: Claude Opus 5.5` 标记，说明上游同样使用了 AI 辅助。
- **提醒后续维护者**：AI 生成的补丁可能存在未被察觉的错误。本项目已发生过数起「推理正确但实测不符」的情况（记录于 [README.md](README.md) 的「已知问题」），合入前应尽量以实机日志验证，而非仅凭代码阅读。

---

## 8. 已知合规缺口汇总

| # | 缺口 | 严重度 | 状态 |
|---|---|---|---|
| 1 | QEMU 修改版源码缺失（GPLv2 §3） | **高** | 未处理 |
| 2 | `assets/engines/**` 1166 个 `.so` 无来源与许可证说明 | **高** | 未处理 |
| 3 | SwiftShader 未附 Apache-2.0 NOTICE | 中 | 未处理 |
| 4 | 仓库无 `docs/COMPLIANCE.md` 等合规流程说明 | 低 | 本次新增 |

处理建议与具体步骤见 [docs/COMPLIANCE.md](docs/COMPLIANCE.md)。

---

## 9. 联系方式

- 本仓库：https://github.com/oopnv70-lab/AEmulator
- 上游项目：https://github.com/uxazu/AEmulator
- 原始引擎：https://github.com/Istratii-tech/SENSES

发现许可证问题请开 issue，会优先处理。
