# 许可证合规与来源追溯

本文件是 [NOTICE.md](../NOTICE.md) 的操作版：把「发现了什么缺口」变成「怎么补」。

---

## 一、当前合规状态（一句话）

**可以私下编译自用；以二进制形式公开发布 APK 之前，必须先补齐 §二 第 1、2 项。**

---

## 二、必须处理的缺口

### 缺口 1 — QEMU 修改版源码（GPLv2 §3）

**问题**：`libqemu_gb.so` / `libqemu_kk.so` / `libqemu_arm.so` 是修改过的 QEMU（GPLv2）。本仓库只有编译好的 `.so`，没有源码、没有补丁、没有构建脚本。

GPLv2 §3 规定：以目标代码形式分发时，必须附上对应完整源码，或提供有效期至少三年的书面承诺。

**三种合法做法（选一）**：

| 做法 | 具体步骤 | 代价 |
|---|---|---|
| A. 引入上游 QEMU 源码目录 | 从 [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) 取 `qemu/patches/`、`qemu/src/`、`qemu/build-android.sh`、`licenses/`，原样放进本仓库 `qemu/` | 仓库体积增大，但最省事、最合规 |
| B. 改用未修改的 QEMU | 自己从 qemu.org 取 8.2.4，重新打补丁并记录 | 工作量大，需重跑验证 |
| C. 书面承诺 + 单独源码仓库 | 另建仓库放源码，NOTICE 里写明地址与三年承诺 | 需长期维护两个仓库 |

> **建议：选 A。** 上游已经准备好了完整补丁集与构建脚本，直接搬过来即可，且能保证与本仓库二进制一致。

### 缺口 2 — `assets/engines/**` 的 1166 个 `.so`

**问题**：这些是各 Android 版本的 guest 侧库（SwiftShader、HAL 实现、shim）。来源混杂，部分来自 AOSP、部分来自厂商固件提取、部分是上游自制。

**处理步骤**：

1. **先分类，别一锅端**。建议在 `docs/ENGINES.md` 建一张表，逐个目录标注来源：

   | 目录 | 内容 | 来源 | 许可证 | 是否有源码 |
   |---|---|---|---|---|
   | `engines/common/` | `libaemushim.so`、`camera.default.so` 等 | 本项目 `native/*` 编译 | GPL-3.0 | ✅ `native/` 内有 |
   | `engines/gb/` | `libGLES_dhd.so`、`gralloc.default.so` | 上游自制／QEMU 相关 | GPL | 部分 |
   | `engines/n64-*/system#lib*#egl#*_swiftshader.so` | SwiftShader | Google | Apache-2.0 | ❌ 需附 NOTICE |
   | `engines/n64-*/` 其余 | 版本适配库 | 上游自制 | GPL-3.0 | 部分 |

2. **能删的就删**。本仓库只需要支持 Honor MagicOS 12（API 31）。API 21–30、33–37 的 engine 目录如果暂时用不到，可以从构建里排除，既减小体积也减小合规面。

3. **剩下必须留的，逐个补来源说明**。

### 缺口 3 — SwiftShader 的 Apache-2.0 NOTICE

**问题**：SwiftShader 是 Apache-2.0，§4(d) 要求分发时附带其 NOTICE 文本。

**处理**：在 `app/src/main/assets/` 下新建 `licenses/SwiftShader-NOTICE.txt`，内容从 [SwiftShader 仓库](https://swiftshader.googlesource.com/SwiftShader) 的 `LICENSE.txt` 与 `NOTICE` 取。同时确保 APK 内可见（应用「关于」页增加许可证查看入口最佳）。

### 缺口 4 — 应用内许可证界面（建议）

GPL-3.0 与 Apache-2.0 都要求用户能获得许可证信息。建议在设置页加「开源许可」条目，列出本项目许可证全文与第三方组件清单。上游 `AppSettingsActivity.kt` 已有 `Links` 常量区，可扩展。

---

## 三、GPL-3.0 对本分支的额外要求

本仓库是 GPL-3.0 项目的 fork，因此：

1. **必须保持 GPL-3.0**。不能改成闭源，也不能改成与 GPL-3.0 不兼容的许可证。
2. **必须保留原始版权声明**。各源文件头部的版权信息不得删除。
3. **必须标注修改**。GPL-3.0 §5(a) 要求修改过的文件「带有显著通知，说明你修改了它及修改日期」。本仓库的实践是通过 git 提交历史体现，这对源码分发是可接受的；但若发布二进制，最好在「关于」页注明「本程序基于 AEmulator，含修改」。
4. **分发二进制时须提供源码**。这是本文档缺口 1、2 的根源。

---

## 四、如果你只是自己用（不分发）

那么：

- **不需要**补齐 QEMU 源码 —— GPL 的源码提供义务由**分发行为**触发，自己编译自己装不构成分发。
- **建议**仍保留 `NOTICE.md`，方便日后要发布时不用重新梳理。
- **注意**：把 APK 发到群里、论坛、网盘，**都算分发**。

---

## 五、AI 辅助开发的合规补充

本项目大量提交由 AI 辅助完成。就此说明：

- AI 工具（Claude、GPT 等）**不对生成代码主张版权**，因此不构成额外的许可证冲突。
- 但 AI 可能**复现训练数据中的代码片段**。若某段代码与已知 GPL 项目高度相似，应主动标注来源。本项目采用 GPL-3.0，与 GPL 系代码兼容；风险主要在于**与 Apache-2.0／MIT 代码混用时的声明缺失**，见缺口 3。
- 提交信息中的 `Co-Authored-By` 标记建议保留，作为真实性的追溯线索。

---

## 六、待办清单（按优先级）

- [ ] 从上游取 `qemu/` 源码目录（补丁＋自制文件＋构建脚本＋licenses）—— **发布前必须**
- [ ] 建立 `docs/ENGINES.md`，逐目录标注 `assets/engines/**` 来源 —— **发布前必须**
- [ ] 补 SwiftShader NOTICE 文本
- [ ] 精简用不到的 engine 目录（API 21–30、33–37）
- [ ] 应用内加「开源许可」查看入口
- [ ] 「关于」页注明「基于 AEmulator，含修改」

---

最后更新：本次提交