<div align="center">

<img src="../../docs/assets/logo.png" width="128" alt="AEmulator logo"/>

# AEmulator — Honor MagicOS 12 fork

*Fork of [uxazu/AEmulator](https://github.com/uxazu/AEmulator); the engine it grew from is [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*

**懐かしの Android ファームウェア — HTC Sense、TouchWiz、MIUI、AOSP — を最新スマホで。root も PC も不要。**

[![Version](https://img.shields.io/badge/version-0.0.0.3-3D5AFE?style=for-the-badge)](https://github.com/oopnv70-lab/AEmulator/releases) [![License](https://img.shields.io/badge/license-GPL--3.0-3D5AFE?style=for-the-badge)](../../LICENSE) [![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/oopnv70-lab/AEmulator) [![Fork of](https://img.shields.io/badge/fork%20of-uxazu%2FAEmulator-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/uxazu/AEmulator)

[🇬🇧 English](README.en.md) · [🇷🇺 Русский](README.ru.md) · [🇺🇦 Українська](README.uk.md) · [🇩🇪 Deutsch](README.de.md) · [🇫🇷 Français](README.fr.md) · [🇪🇸 Español](README.es.md) · [🇧🇷 Português](README.pt-BR.md) · [🇮🇹 Italiano](README.it.md) · [🇵🇱 Polski](README.pl.md) · [🇹🇷 Türkçe](README.tr.md) · [🇸🇦 العربية](README.ar.md) · [🇮🇷 فارسی](README.fa.md) · [🇮🇳 हिन्दी](README.hi.md) · [🇮🇩 Indonesia](README.id.md) · [🇻🇳 Tiếng Việt](README.vi.md) · [🇨🇳 简体中文](../../README.md) · **🇯🇵 日本語** · [🇰🇷 한국어](README.ko.md)

</div>

---

AEmulator はファームウェアファイルから本物の Android 2.3〜17 を直接起動します：リカバリー ZIP、Odin アーカイブ、Google ファクトリーイメージ、GSI。古い 32 ビットのファームウェア（2.3〜4.4）は改良版 QEMU で動かし、Android 5.0〜17 はスマホの arm64 コアでネイティブに動かします。カーネルの binder をエミュレートし、描画はスマホの GPU、音声は Android のオーディオを使います。すべて普通のアプリの中で動きます。

## ✨ 特長

- ほぼすべての形式をインポート：CWM/TWRP ZIP、Samsung Odin `.tar.md5`、Google ファクトリー `.tgz`、`system.img`、OTA `system.new.dat.br`
- メーカー独自 UI がそのまま動作：HTC Sense、Samsung TouchWiz、MIUI、AOSP
- GL ブリッジによるハードウェア描画、音声、タッチとマルチタッチ、最新 TLS プロキシ付きネットワーク
- APK・音楽・写真用の共有メモリーカードフォルダー
- 18 言語対応の Material 3 Expressive UI
- 無料・オープンソース（GPL-3.0）

## 📱 ファームウェア一覧

Xiaomi 15（Snapdragon 8 Elite、Android 16）で検証。Android 2.3〜4.4 の 32 ビット ARM ファームウェアなら試す価値があります。一覧は検証済みのイメージのみです。

## 🚀 クイックスタート

1. [Releases](https://github.com/oopnv70-lab/AEmulator/releases) から APK をダウンロードしてインストール。
2. 上の表からファームウェアをスマホにダウンロード。
3. AEmulator を開き →**ファームウェアを追加**でファイルを選択。インポートには数分かかります。
4. **起動**をタップ。初回はアプリ最適化のため時間がかかります。
5. ⋮ メニューで音量・電源ボタン・ログ、⚙️ で設定と言語。

## 📋 動作要件

- 64 ビット ARM 端末（arm64-v8a）の Android 8.0+
- ファームウェア 1 つにつき約 1〜3 GB の空き
- 最新の Snapdragon / Dimensity / Tensor を推奨

## ⚙️ 仕組み

Android 2.3〜4.4 では各ゲストプロセスが改良版ユーザーモード QEMU で動き、5.0〜17 ではファームウェア自身の arm64 プログラムがネイティブに動き、Android のバージョンごとのエンジンカーネルがシステムコールを受け持ちます。binder デーモンがカーネルドライバーの代わりを務め、GL ブリッジが OpenGL ES 呼び出しを GPU に転送し、小さなゲストライブラリ（オーディオ HAL、audio policy ラッパー、LD_PRELOAD シム）がメーカーのコードをエミュレーターに合わせます。インポーターはファームウェアを読み、boot イメージの init スクリプトからサービスの起動計画を作ります。

## 🛠️ ソースからビルド

JDK 17、Android SDK 36、NDK r28 が必要です。ゲスト用ライブラリは `native/*/build.sh` でビルドします。

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

## 🙏 クレジット

The architecture and the vast majority of the code come from upstream:
- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — the direct upstream, GPL-3.0
- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — the earlier engine it grew from (SENSES Emulator), GPL-2.0
- Original engine author: [t.me/istratii_tech1](https://t.me/istratii_tech1)
Third-party components, binary provenance and the licence gaps are listed in [NOTICE.md](../../NOTICE.md) and [docs/COMPLIANCE.md](../../docs/COMPLIANCE.md).

## 📄 ライセンス

GPL-3.0。Android、商標、ファームウェアは各所有者に帰属します。
