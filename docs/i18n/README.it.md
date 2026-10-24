<div align="center">

<img src="../../docs/assets/logo.png" width="128" alt="AEmulator logo"/>

# AEmulator — Honor MagicOS 12 fork

*Fork of [uxazu/AEmulator](https://github.com/uxazu/AEmulator); the engine it grew from is [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*

**Firmware Android classici — HTC Sense, TouchWiz, MIUI, AOSP — su un telefono moderno. Senza root, senza PC.**

[![Version](https://img.shields.io/badge/version-0.0.0.3-3D5AFE?style=for-the-badge)](https://github.com/oopnv70-lab/AEmulator/releases) [![License](https://img.shields.io/badge/license-GPL--3.0-3D5AFE?style=for-the-badge)](../../LICENSE) [![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/oopnv70-lab/AEmulator) [![Fork of](https://img.shields.io/badge/fork%20of-uxazu%2FAEmulator-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/uxazu/AEmulator)

[🇬🇧 English](../../README.md) · [🇷🇺 Русский](README.ru.md) · [🇺🇦 Українська](README.uk.md) · [🇩🇪 Deutsch](README.de.md) · [🇫🇷 Français](README.fr.md) · [🇪🇸 Español](README.es.md) · [🇧🇷 Português](README.pt-BR.md) · **🇮🇹 Italiano** · [🇵🇱 Polski](README.pl.md) · [🇹🇷 Türkçe](README.tr.md) · [🇸🇦 العربية](README.ar.md) · [🇮🇷 فارسی](README.fa.md) · [🇮🇳 हिन्दी](README.hi.md) · [🇮🇩 Indonesia](README.id.md) · [🇻🇳 Tiếng Việt](README.vi.md) · [🇨🇳 简体中文](README.zh-CN.md) · [🇯🇵 日本語](README.ja.md) · [🇰🇷 한국어](README.ko.md)

</div>

---

AEmulator avvia un vero sistema Android 2.3–17 direttamente da un file firmware: ZIP per recovery, archivio Odin o immagine di fabbrica Google o GSI. I vecchi firmware a 32 bit (2.3–4.4) passano da un QEMU modificato, Android 5.0–17 gira in modo nativo sui core arm64 del telefono; il binder del kernel è emulato, la grafica usa la GPU del telefono e l’audio passa per lo stack audio di Android — tutto in una normale app.

## ✨ Funzionalità

- Importa quasi ogni formato: ZIP CWM/TWRP, Odin `.tar.md5` di Samsung, immagine di fabbrica Google `.tgz`, `system.img`, OTA `system.new.dat.br`
- Le interfacce dei produttori funzionano così come sono: HTC Sense, Samsung TouchWiz, MIUI, AOSP
- Grafica hardware tramite il ponte GL, audio, tocco e multitouch, rete con proxy TLS moderno
- Cartella condivisa della scheda di memoria per APK, musica e foto
- Interfaccia Material 3 Expressive in 18 lingue
- Gratuito e open source (GPL-3.0)

## 📱 Elenco firmware

Testato su Xiaomi 15 (Snapdragon 8 Elite, Android 16). Vale la pena provare qualsiasi firmware ARM a 32 bit per Android 2.3–4.4; l’elenco contiene solo immagini verificate.

## 🚀 Avvio rapido

1. Scarica l’APK da [Releases](https://github.com/oopnv70-lab/AEmulator/releases) e installalo.
2. Scarica sul telefono un firmware dalla tabella sopra.
3. Apri AEmulator → **Aggiungi firmware** e scegli il file. L’importazione richiede qualche minuto.
4. Premi **Avvia**. Il primo avvio è più lento: il sistema ottimizza le app.
5. Menu ⋮ per volume, tasto di accensione e registro; ⚙️ apre impostazioni e lingua.

## 📋 Requisiti

- Android 8.0+ su telefono ARM a 64 bit (arm64-v8a)
- Circa 1–3 GB liberi per firmware
- Consigliato: Snapdragon / Dimensity / Tensor recente

## ⚙️ Come funziona

Su Android 2.3–4.4 ogni processo ospite gira sotto un QEMU user-mode modificato; su 5.0–17 i programmi arm64 del firmware girano in modo nativo e un kernel del motore dedicato a ogni versione di Android intercetta le loro chiamate di sistema. Un demone binder sostituisce il driver del kernel, un ponte GL inoltra le chiamate OpenGL ES alla GPU e piccole librerie ospiti (HAL audio, wrapper audio policy, shim LD_PRELOAD) adattano il codice dei produttori all’emulatore. L’importatore legge il firmware, trova gli script init nell’immagine boot e crea il piano di avvio dei servizi.

## 🛠️ Compilare dai sorgenti

Servono JDK 17, Android SDK 36 e NDK r28. Le librerie ospiti si compilano con `native/*/build.sh`.

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

## 🙏 Ringraziamenti

The architecture and the vast majority of the code come from upstream:
- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — the direct upstream, GPL-3.0
- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — the earlier engine it grew from (SENSES Emulator), GPL-2.0
- Original engine author: [t.me/istratii_tech1](https://t.me/istratii_tech1)
Third-party components, binary provenance and the licence gaps are listed in [NOTICE.md](../../NOTICE.md) and [docs/COMPLIANCE.md](../../docs/COMPLIANCE.md).

## 📄 Licenza

GPL-3.0. Android, i marchi e i firmware appartengono ai rispettivi proprietari.
