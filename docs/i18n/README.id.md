<div align="center">

<img src="../../docs/assets/logo.png" width="128" alt="AEmulator logo"/>

# AEmulator — Honor MagicOS 12 fork

*Fork of [uxazu/AEmulator](https://github.com/uxazu/AEmulator); the engine it grew from is [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*

**Firmware Android klasik — HTC Sense, TouchWiz, MIUI, AOSP — di ponsel modern. Tanpa root, tanpa PC.**

[![Version](https://img.shields.io/badge/version-0.0.0.3-3D5AFE?style=for-the-badge)](https://github.com/oopnv70-lab/AEmulator/releases) [![License](https://img.shields.io/badge/license-GPL--3.0-3D5AFE?style=for-the-badge)](../../LICENSE) [![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/oopnv70-lab/AEmulator) [![Fork of](https://img.shields.io/badge/fork%20of-uxazu%2FAEmulator-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/uxazu/AEmulator)

[🇬🇧 English](../../README.md) · [🇷🇺 Русский](README.ru.md) · [🇺🇦 Українська](README.uk.md) · [🇩🇪 Deutsch](README.de.md) · [🇫🇷 Français](README.fr.md) · [🇪🇸 Español](README.es.md) · [🇧🇷 Português](README.pt-BR.md) · [🇮🇹 Italiano](README.it.md) · [🇵🇱 Polski](README.pl.md) · [🇹🇷 Türkçe](README.tr.md) · [🇸🇦 العربية](README.ar.md) · [🇮🇷 فارسی](README.fa.md) · [🇮🇳 हिन्दी](README.hi.md) · **🇮🇩 Indonesia** · [🇻🇳 Tiếng Việt](README.vi.md) · [🇨🇳 简体中文](README.zh-CN.md) · [🇯🇵 日本語](README.ja.md) · [🇰🇷 한국어](README.ko.md)

</div>

---

AEmulator mem-boot sistem Android 2.3–17 asli langsung dari file firmware: ZIP recovery, arsip Odin, factory image Google, atau GSI. Firmware 32-bit lama (2.3–4.4) berjalan lewat QEMU yang dimodifikasi, Android 5.0–17 berjalan langsung di inti arm64 ponsel; binder kernel diemulasikan, grafis memakai GPU ponsel, dan suara lewat sistem audio Android — semuanya di dalam aplikasi biasa.

## ✨ Fitur

- Impor hampir semua format: ZIP CWM/TWRP, Odin `.tar.md5` Samsung, factory image Google `.tgz`, `system.img`, OTA `system.new.dat.br`
- Tampilan pabrikan berjalan apa adanya: HTC Sense, Samsung TouchWiz, MIUI, AOSP
- Grafis hardware lewat jembatan GL, suara, sentuh dan multisentuh, jaringan dengan proxy TLS modern
- Folder kartu memori bersama untuk APK, musik, dan foto
- Antarmuka Material 3 Expressive dalam 18 bahasa
- Gratis dan sumber terbuka (GPL-3.0)

## 📱 Daftar firmware

Diuji di Xiaomi 15 (Snapdragon 8 Elite, Android 16). Firmware ARM 32-bit apa pun untuk Android 2.3–4.4 layak dicoba; daftar ini hanya memuat image yang sudah kami periksa.

## 🚀 Mulai cepat

1. Unduh APK dari [Releases](https://github.com/oopnv70-lab/AEmulator/releases) lalu pasang.
2. Unduh firmware dari tabel di atas ke ponsel.
3. Buka AEmulator → **Tambah firmware** lalu pilih file. Impor butuh beberapa menit.
4. Tekan **Mulai**. Boot pertama lebih lambat: sistem mengoptimalkan aplikasi.
5. Menu ⋮ untuk volume, tombol daya, dan log; ⚙️ membuka setelan dan bahasa.

## 📋 Persyaratan

- Android 8.0+ di ponsel ARM 64-bit (arm64-v8a)
- Sekitar 1–3 GB ruang kosong per firmware
- Disarankan Snapdragon / Dimensity / Tensor terbaru

## ⚙️ Cara kerja

Di Android 2.3–4.4 setiap proses tamu berjalan di QEMU mode pengguna yang dimodifikasi; di 5.0–17 program arm64 milik firmware berjalan langsung, dan kernel mesin tersendiri untuk tiap versi Android menangkap panggilan sistemnya. Daemon binder menggantikan driver kernel, jembatan GL meneruskan panggilan OpenGL ES ke GPU, dan pustaka tamu kecil (HAL audio, pembungkus audio policy, shim LD_PRELOAD) menyesuaikan kode pabrikan dengan emulator. Importir membaca firmware, menemukan skrip init di image boot, dan menyusun rencana mulai layanan.

## 🛠️ Membangun dari sumber

Butuh JDK 17, Android SDK 36, dan NDK r28. Pustaka tamu dibangun dengan `native/*/build.sh`.

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

## 🙏 Kredit

The architecture and the vast majority of the code come from upstream:
- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — the direct upstream, GPL-3.0
- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — the earlier engine it grew from (SENSES Emulator), GPL-2.0
- Original engine author: [t.me/istratii_tech1](https://t.me/istratii_tech1)
Third-party components, binary provenance and the licence gaps are listed in [NOTICE.md](../../NOTICE.md) and [docs/COMPLIANCE.md](../../docs/COMPLIANCE.md).

## 📄 Lisensi

GPL-3.0. Android, merek dagang, dan firmware milik pemiliknya.
