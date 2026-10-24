#!/usr/bin/env python3
"""Собирает README.md (английский) и docs/i18n/README.<код>.md из data.py + texts.py.
Запуск из корня репозитория: python docs/readme/gen.py"""
import os, sys
from urllib.parse import quote
HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.abspath(os.path.join(HERE, "..", ".."))
sys.path.insert(0, HERE)
from data import *  # noqa
from texts import T  # noqa

RTL = {"ar", "fa"}

# 本分支的进展说明。只有中文与英文两份手写；其余语言回退到英文，
# 避免给 18 个语言都塞一份没人维护的机器翻译。
BRANCH_ZH = [
    "## 📌 本分支做了什么\n",
    "本仓库不是 AEmulator 的原版，而是**针对 Honor MagicOS 12（Android 12 / API 31）固件**的适配分支。"
    "上游代码基线为 `0.0.0.3`（根提交 `3b97ed5`），其后的提交全部为本分支改动。\n",
    "**目标**：让官方 Honor MagicOS 12 固件在 AEmulator 上完成开机、进入桌面、能装软件、能正常使用。\n",
    "### 已解决\n",
    "- **VINTF 清单缺失导致 system_server 反复重启**：补声明框架侧 HAL 服务（hidl.manager 1.0/1.2、"
    "hidl.token、frameworks.sensorservice、frameworks.displayservice、system.net.netd）；"
    "把 13+ 才支持的 `<fqname>` 写法改成 12 能读的 `<interface>/<instance>`；"
    "并把同一 HAL 的两个版本合并进单个 `<hal>` 块——否则 12 的解析器会把整份框架清单判为冲突，报 NULL VINTF MANIFEST\n",
    "- **厂商 HAL 拖死启动**：InitPlan 增加 vendor 硬件 HAL 跳过名单，并提供 `run/skip-services` 运行时跳过机制\n",
    "- **开机向导卡住**：Honor 语言选择页本身是一个 setup wizard。新增逐级重试窗口（4s/8s/15s/30s/60s）"
    "轮询 `dumpsys activity`，发现向导在前台就 `pm disable-user` 并切回 HOME；"
    "同时修掉 `skipWizardNow()` 因 `bootCount > 0` 提前返回、导致向导永远跳不过的问题\n",
    "- **安装软件报内存不足**：根因是存储路径映射。引擎的 `sd[]` 重定向表只认 8 个路径，"
    "而该固件 `storage_list.xml` 把主存储声明为 `/storage/sdcard`——不在表里，于是在镜像内只是个空目录。"
    "现在为固件自有存储点建符号链接指向引擎可映射的点，并让 `/proc/mounts` 为所有存储点写入 vfat 条目\n",
    "### 已知未解决\n",
    "- **设置里「可用空间」仍显示 0 B**。装软件已经正常，但存储页面的数字不对。"
    "已核对 AOSP 12 源码：存储页走 `StorageStatsService.getFreeBytes()`，其实现是 `path.getUsableSpace()`，"
    "也就是直接 `statfs(2)`，**并不读 `/proc/mounts`**。因此往镜像的 `/proc/mounts` 里加 FUSE 条目"
    "只会让文件「看起来」挂上了，对 `statfs` 的返回值没有任何影响。真正原因尚未定位，"
    "需要 guest 内部的 `df` / `statfs` 一手数据\n",
    "- **设置锁屏密码提示存储不足**。可能与上一条同源，也可能是独立问题："
    "`run/gatekeeper-1-0.log` 里有 `Unable to open GateKeeper HAL`，以及 "
    "`android.hardware.gatekeeper@1.0-service-qti` 的 fatal signal 6。尚未确认\n",
    "### 已知限制\n",
    "`app.aemu.SHELL` 广播调试通道受 `BuildConfig.DEBUG` 限制。装机版本虽然是 DEBUGGABLE 的，"
    "但实测广播从未送达 receiver（`dumpsys` 显示 `dispatchClockTime=1970`），"
    "因此无法直接进 guest 执行 `df`。后续取证需要改用应用内写文件的方式。\n",
]

BRANCH_EN = [
    "## 📌 What this branch does\n",
    "This is **not** upstream AEmulator: it is a fork aimed at running official **Honor MagicOS 12 "
    "(Android 12 / API 31)** firmware. The upstream baseline is `0.0.0.3` (root commit `3b97ed5`); "
    "every commit after it is ours.\n",
    "**Goal**: get official Honor MagicOS 12 firmware to boot to the home screen, install apps and be usable "
    "inside AEmulator.\n",
    "### Fixed\n",
    "- **system_server restarting in a loop over a missing VINTF manifest**: declare the framework-side HAL "
    "services the firmware leaves out (hidl.manager 1.0/1.2, hidl.token, frameworks.sensorservice, "
    "frameworks.displayservice, system.net.netd); rewrite the `<fqname>` form (13+) as the "
    "`<interface>/<instance>` form that 12 can parse; and merge the two versions of one HAL into a single "
    "`<hal>` block — two fragments make the parser of 12 reject the whole framework manifest as a conflict "
    "and report NULL VINTF MANIFEST\n",
    "- **Vendor HALs stalling the boot**: a skip list for vendor hardware HALs in the start plan, plus a "
    "`run/skip-services` mechanism that skips a service at runtime\n",
    "- **A setup wizard that could not be passed**: the Honor language picker is itself a setup wizard. A "
    "retry window (4 s / 8 s / 15 s / 30 s / 60 s) polls `dumpsys activity`; when the wizard is in front it "
    "is disabled with `pm disable-user` and HOME is brought up. This also fixes `skipWizardNow()` returning "
    "early on `bootCount > 0`, which meant the wizard was never skipped on images that had already booted\n",
    "- **\"Not enough space\" when installing an app**: the root cause was storage path mapping. The engine's "
    "`sd[]` redirect table knows only eight paths, and this firmware declares its primary storage as "
    "`/storage/sdcard` — not one of them, so it stayed a plain empty directory inside the image. Firmware-"
    "owned storage points are now symlinked to a point the engine does map, and `/proc/mounts` gets a vfat "
    "entry for every storage point\n",
    "### Known open issues\n",
    "- **The storage screen still reads 0 B of free space.** Installing apps works, but the number is wrong. "
    "Checked against the AOSP 12 sources: the screen goes through `StorageStatsService.getFreeBytes()`, whose "
    "body is `path.getUsableSpace()`, i.e. a plain `statfs(2)` — it **never reads `/proc/mounts`**. Adding "
    "FUSE entries to the image's `/proc/mounts` therefore only makes the file *look* mounted; it cannot "
    "change what `statfs` returns. The real cause is not located yet and needs first-hand `df` / `statfs` "
    "data from inside the guest\n",
    "- **Setting a lock-screen password reports \"not enough storage\".** It may share the cause above, or be "
    "unrelated: `run/gatekeeper-1-0.log` shows `Unable to open GateKeeper HAL` and a fatal signal 6 in "
    "`android.hardware.gatekeeper@1.0-service-qti`. Not confirmed\n",
    "### Known limitation\n",
    "The `app.aemu.SHELL` broadcast debug channel is gated on `BuildConfig.DEBUG`. The installed build is "
    "DEBUGGABLE, yet the broadcast never reached the receiver in testing (`dumpsys` shows "
    "`dispatchClockTime=1970`), so a guest `df` cannot be run this way. Diagnosis has to go through a file "
    "written by the app itself instead.\n",
]


def branch_section(code, up):
    """本分支进展：中英文手写，其余语言回退英文。"""
    return BRANCH_ZH if code.startswith("zh") else BRANCH_EN


CREDITS = {
    "en": (
        "The architecture and the vast majority of the code come from upstream:\n"
        "- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — the direct upstream, GPL-3.0\n"
        "- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — the earlier engine it grew from "
        "(SENSES Emulator), GPL-2.0\n"
        "- Original engine author: [t.me/istratii_tech1]({orig})\n"
        "Third-party components, binary provenance and the licence gaps are listed in "
        "[NOTICE.md]({up}NOTICE.md) and [docs/COMPLIANCE.md]({up}docs/COMPLIANCE.md)."
    ),
    "zh-CN": (
        "本项目的**全部原始架构与绝大部分代码**来自上游：\n"
        "- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — 直接上游，GPL-3.0\n"
        "- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — 更早的引擎源头（SENSES Emulator），GPL-2.0\n"
        "- 原始引擎作者：[t.me/istratii_tech1]({orig})\n"
        "第三方组件、二进制来源与许可证缺口详见 [NOTICE.md]({up}NOTICE.md) 与 [docs/COMPLIANCE.md]({up}docs/COMPLIANCE.md)。"
    ),
}


def path_of(code, from_code):
    """Относительная ссылка на README языка code из файла языка from_code."""
    if code == "en":
        return "README.md" if from_code == "en" else "../../README.md"
    return f"docs/i18n/README.{code}.md" if from_code == "en" else f"README.{code}.md"


def render(code):
    t = T[code]
    up = "" if code == "en" else "../../"
    L = []
    if code in RTL:
        L.append('<div dir="rtl">\n')
    L.append('<div align="center">\n')
    L.append(f'<img src="{up}docs/assets/logo.png" width="128" alt="AEmulator logo"/>\n')
    L.append("# AEmulator — Honor MagicOS 12 fork\n" if not code.startswith("zh") else "# AEmulator — Honor MagicOS 12 适配分支\n")
    L.append("*Fork of [uxazu/AEmulator](https://github.com/uxazu/AEmulator); the engine it grew from is "
             "[Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*\n" if not code.startswith("zh") else
             "*基于 [uxazu/AEmulator](https://github.com/uxazu/AEmulator) 的分支，更早的源头是 "
             "[Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*\n")
    L.append(f"**{t['tagline']}**\n")
    L.append(
        f'[![Version](https://img.shields.io/badge/version-{VERSION}-3D5AFE?style=for-the-badge)]({REPO}/releases) '
        f'[![License](https://img.shields.io/badge/license-GPL--3.0-3D5AFE?style=for-the-badge)]({up}LICENSE) '
        f'[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)]({REPO}) '
        f'[![Fork of](https://img.shields.io/badge/fork%20of-uxazu%2FAEmulator-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/uxazu/AEmulator)\n')
    L.append(" · ".join(
        (f"**{flag} {name}**" if c == code else f"[{flag} {name}]({path_of(c, code)})") for c, name, flag in LANGS) + "\n")
    L.append("</div>\n")
    L.append("---\n")
    L.append(t["about"] + "\n")
    L.append(f"## ✨ {t['feat_title']}\n")
    L += [f"- {f}" for f in t["feats"]]
    L.append("")
    L.append(f"## 📱 {t['fw_title']}\n")
    L.append(t["fw_intro"] + "\n")
    # 上游把「已验证固件列表」放在它自己的论坛上。本分支不维护那份列表，也不指向它，
    # 只说明本分支的目标固件，其余交给上游文档。
    L.append(f"## 🚀 {t['start_title']}\n")
    L += [f"{i + 1}. {s.format(repo=REPO)}" for i, s in enumerate(t["steps"])]
    L.append("")
    L.append(f"## 📋 {t['req_title']}\n")
    L += [f"- {r}" for r in t["reqs"]]
    L.append("")
    L.append(f"## ⚙️ {t['how_title']}\n")
    L.append(t["how_text"] + "\n")
    L.append(f"## 🛠️ {t['build_title']}\n")
    L.append(t["build_text"] + "\n")
    L.append("```bash\ngit clone https://github.com/oopnv70-lab/AEmulator.git\ncd AEmulator\n./gradlew :app:assembleV0003Debug\n```\n")
    L += branch_section(code, up)
    L.append(f"## 🙏 {t['credits_title']}\n")
    L.append(CREDITS.get(code, CREDITS["en"]).format(up=up, orig=ORIGINAL) + "\n")
    L.append(f"## 📄 {t['license_title']}\n")
    L.append(t["license_text"] + "\n")
    if code in RTL:
        L.append("</div>\n")
    return "\n".join(L)


def main():
    for code, _, _ in LANGS:
        assert code in T, code
        out = os.path.join(ROOT, "README.md") if code == "en" else os.path.join(ROOT, "docs", "i18n", f"README.{code}.md")
        os.makedirs(os.path.dirname(out), exist_ok=True)
        with open(out, "w", encoding="utf-8", newline="\n") as f:
            f.write(render(code))
    print(f"README: {len(LANGS)} языков")


if __name__ == "__main__":
    main()
