<div align="center">

<img src="../../docs/assets/logo.png" width="128" alt="AEmulator logo"/>

# AEmulator — Honor MagicOS 12 fork

*Fork of [uxazu/AEmulator](https://github.com/uxazu/AEmulator); the engine it grew from is [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*

**클래식 Android 펌웨어 — HTC Sense, TouchWiz, MIUI, AOSP — 를 최신 휴대폰에서. 루팅도 PC도 필요 없습니다.**

[![Version](https://img.shields.io/badge/version-0.0.0.3-3D5AFE?style=for-the-badge)](https://github.com/oopnv70-lab/AEmulator/releases) [![License](https://img.shields.io/badge/license-GPL--3.0-3D5AFE?style=for-the-badge)](../../LICENSE) [![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/oopnv70-lab/AEmulator) [![Fork of](https://img.shields.io/badge/fork%20of-uxazu%2FAEmulator-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/uxazu/AEmulator)

[🇬🇧 English](../../README.md) · [🇷🇺 Русский](README.ru.md) · [🇺🇦 Українська](README.uk.md) · [🇩🇪 Deutsch](README.de.md) · [🇫🇷 Français](README.fr.md) · [🇪🇸 Español](README.es.md) · [🇧🇷 Português](README.pt-BR.md) · [🇮🇹 Italiano](README.it.md) · [🇵🇱 Polski](README.pl.md) · [🇹🇷 Türkçe](README.tr.md) · [🇸🇦 العربية](README.ar.md) · [🇮🇷 فارسی](README.fa.md) · [🇮🇳 हिन्दी](README.hi.md) · [🇮🇩 Indonesia](README.id.md) · [🇻🇳 Tiếng Việt](README.vi.md) · [🇨🇳 简体中文](README.zh-CN.md) · [🇯🇵 日本語](README.ja.md) · **🇰🇷 한국어**

</div>

---

AEmulator는 펌웨어 파일에서 진짜 Android 2.3–17 시스템을 바로 부팅합니다: 리커버리 ZIP, Odin 아카이브, Google 팩토리 이미지, GSI. 오래된 32비트 펌웨어(2.3–4.4)는 수정된 QEMU로 실행하고, Android 5.0–17은 휴대폰의 arm64 코어에서 네이티브로 실행하며, 커널 binder를 에뮬레이션하며, 그래픽은 휴대폰 GPU로, 소리는 Android 오디오로 처리합니다. 모두 평범한 앱 안에서 동작합니다.

## ✨ 기능

- 거의 모든 형식 가져오기: CWM/TWRP ZIP, 삼성 Odin `.tar.md5`, Google 팩토리 `.tgz`, `system.img`, OTA `system.new.dat.br`
- 제조사 UI가 그대로 동작: HTC Sense, Samsung TouchWiz, MIUI, AOSP
- GL 브리지 하드웨어 그래픽, 소리, 터치와 멀티터치, 최신 TLS 프록시 네트워크
- APK·음악·사진을 위한 공유 메모리 카드 폴더
- 18개 언어의 Material 3 Expressive 인터페이스
- 무료 오픈 소스(GPL-3.0)

## 📱 펌웨어 목록

Xiaomi 15(Snapdragon 8 Elite, Android 16)에서 테스트했습니다. Android 2.3–4.4용 32비트 ARM 펌웨어라면 시도해 볼 만합니다. 목록은 확인된 이미지만 담고 있습니다.

## 🚀 빠른 시작

1. [Releases](https://github.com/oopnv70-lab/AEmulator/releases)에서 APK를 받아 설치합니다.
2. 위 표에서 펌웨어를 휴대폰으로 받습니다.
3. AEmulator → **펌웨어 추가**에서 파일을 고릅니다. 가져오기는 몇 분 걸립니다.
4. **시작**을 누릅니다. 첫 부팅은 앱 최적화로 더 오래 걸립니다.
5. ⋮ 메뉴에서 볼륨·전원 버튼·로그, ⚙️에서 설정과 언어.

## 📋 요구 사항

- 64비트 ARM 휴대폰(arm64-v8a)의 Android 8.0+
- 펌웨어당 약 1–3GB 여유 공간
- 최신 Snapdragon / Dimensity / Tensor 권장

## ⚙️ 작동 방식

Android 2.3–4.4에서는 각 게스트 프로세스가 수정된 사용자 모드 QEMU에서 실행되고, 5.0–17에서는 펌웨어 자체의 arm64 프로그램이 네이티브로 실행되며 Android 버전마다 별도의 엔진 커널이 시스템 호출을 처리합니다. binder 데몬이 커널 드라이버를 대신하고, GL 브리지가 OpenGL ES 호출을 GPU로 전달하며, 작은 게스트 라이브러리(오디오 HAL, audio policy 래퍼, LD_PRELOAD 심)가 제조사 코드를 에뮬레이터에 맞춥니다. 가져오기 도구는 펌웨어를 읽고 boot 이미지의 init 스크립트로 서비스 시작 계획을 만듭니다.

## 🛠️ 소스에서 빌드

JDK 17, Android SDK 36, NDK r28이 필요합니다. 게스트 라이브러리는 `native/*/build.sh`로 빌드합니다.

```bash
git clone https://github.com/oopnv70-lab/AEmulator.git
cd AEmulator
./gradlew :app:assembleV0003Debug
```

## 📌 What this branch does

This is **not** upstream AEmulator: it is a fork aimed at running official **Honor MagicOS 12 (Android 12 / API 31)** firmware. The upstream baseline is `0.0.0.3` (root commit `3b97ed5`); every commit after it is ours.

**Goal**: get official Honor MagicOS 12 firmware to boot to the home screen, install apps and be usable inside AEmulator.

### Fixed

- **system_server restarting in a loop over a missing VINTF manifest**: declare the framework-side HAL services the firmware leaves out (hidl.manager 1.0/1.2, hidl.token, frameworks.sensorservice, frameworks.displayservice, system.net.netd); rewrite the `<fqname>` form (13+) as the `<interface>/<instance>` form that 12 can parse; and merge the two versions of one HAL into a single `<hal>` block — two fragments make the parser of 12 reject the whole framework manifest as a conflict and report NULL VINTF MANIFEST

- **Vendor HALs stalling the boot**: a skip list for vendor hardware HALs in the start plan, plus a `run/skip-services` mechanism that skips a service at runtime

- **A setup wizard that could not be passed**: the Honor language picker is itself a setup wizard. A retry window (4 s / 8 s / 15 s / 30 s / 60 s) polls `dumpsys activity`; when the wizard is in front it is disabled with `pm disable-user` and HOME is brought up. This also fixes `skipWizardNow()` returning early on `bootCount > 0`, which meant the wizard was never skipped on images that had already booted

- **"Not enough space" when installing an app**: the root cause was storage path mapping. The engine's `sd[]` redirect table knows only eight paths, and this firmware declares its primary storage as `/storage/sdcard` — not one of them, so it stayed a plain empty directory inside the image. Firmware-owned storage points are now symlinked to a point the engine does map, and `/proc/mounts` gets a vfat entry for every storage point

### Known open issues

- **The storage screen still reads 0 B of free space.** Installing apps works, but the number is wrong. Checked against the AOSP 12 sources: the screen goes through `StorageStatsService.getFreeBytes()`, whose body is `path.getUsableSpace()`, i.e. a plain `statfs(2)` — it **never reads `/proc/mounts`**. Adding FUSE entries to the image's `/proc/mounts` therefore only makes the file *look* mounted; it cannot change what `statfs` returns. The real cause is not located yet and needs first-hand `df` / `statfs` data from inside the guest

- **Setting a lock-screen password reports "not enough storage".** It may share the cause above, or be unrelated: `run/gatekeeper-1-0.log` shows `Unable to open GateKeeper HAL` and a fatal signal 6 in `android.hardware.gatekeeper@1.0-service-qti`. Not confirmed

### Known limitation

The `app.aemu.SHELL` broadcast debug channel is gated on `BuildConfig.DEBUG`. The installed build is DEBUGGABLE, yet the broadcast never reached the receiver in testing (`dumpsys` shows `dispatchClockTime=1970`), so a guest `df` cannot be run this way. Diagnosis has to go through a file written by the app itself instead.

## 🙏 감사의 말

The architecture and the vast majority of the code come from upstream:
- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — the direct upstream, GPL-3.0
- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — the earlier engine it grew from (SENSES Emulator), GPL-2.0
- Original engine author: [t.me/istratii_tech1](https://t.me/istratii_tech1)
Third-party components, binary provenance and the licence gaps are listed in [NOTICE.md](../../NOTICE.md) and [docs/COMPLIANCE.md](../../docs/COMPLIANCE.md).

## 📄 라이선스

GPL-3.0. Android, 상표, 펌웨어는 각 소유자에게 있습니다.
