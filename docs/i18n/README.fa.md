<div dir="rtl">

<div align="center">

<img src="../../docs/assets/logo.png" width="128" alt="AEmulator logo"/>

# AEmulator — Honor MagicOS 12 fork

*Fork of [uxazu/AEmulator](https://github.com/uxazu/AEmulator); the engine it grew from is [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*

**فرم‌ویرهای کلاسیک اندروید — HTC Sense، TouchWiz، MIUI، AOSP — روی گوشی مدرن. بدون روت و بدون رایانه.**

[![Version](https://img.shields.io/badge/version-0.0.0.3-3D5AFE?style=for-the-badge)](https://github.com/oopnv70-lab/AEmulator/releases) [![License](https://img.shields.io/badge/license-GPL--3.0-3D5AFE?style=for-the-badge)](../../LICENSE) [![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/oopnv70-lab/AEmulator) [![Fork of](https://img.shields.io/badge/fork%20of-uxazu%2FAEmulator-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/uxazu/AEmulator)

[🇬🇧 English](README.en.md) · [🇷🇺 Русский](README.ru.md) · [🇺🇦 Українська](README.uk.md) · [🇩🇪 Deutsch](README.de.md) · [🇫🇷 Français](README.fr.md) · [🇪🇸 Español](README.es.md) · [🇧🇷 Português](README.pt-BR.md) · [🇮🇹 Italiano](README.it.md) · [🇵🇱 Polski](README.pl.md) · [🇹🇷 Türkçe](README.tr.md) · [🇸🇦 العربية](README.ar.md) · **🇮🇷 فارسی** · [🇮🇳 हिन्दी](README.hi.md) · [🇮🇩 Indonesia](README.id.md) · [🇻🇳 Tiếng Việt](README.vi.md) · [🇨🇳 简体中文](../../README.md) · [🇯🇵 日本語](README.ja.md) · [🇰🇷 한국어](README.ko.md)

</div>

---

AEmulator یک سیستم واقعی اندروید 2.3 تا 17 را مستقیم از فایل فرم‌ویر بوت می‌کند: ZIP ریکاوری، آرشیو Odin یا ایمیج کارخانهٔ گوگل یا GSI. فرم‌ویرهای قدیمی ۳۲ بیتی (2.3 تا 4.4) با QEMU اصلاح‌شده اجرا می‌شوند و اندروید 5.0 تا 17 مستقیم روی هسته‌های arm64 گوشی اجرا می‌شود؛ binder هسته شبیه‌سازی می‌شود، گرافیک با GPU گوشی و صدا با سامانهٔ صوتی اندروید پخش می‌شود — همه درون یک برنامهٔ معمولی.

## ✨ ویژگی‌ها

- درون‌ریزی تقریباً همهٔ قالب‌ها: ‏ZIP ‏CWM/TWRP، ‏Odin ‏`.tar.md5` سامسونگ، ایمیج کارخانهٔ گوگل ‏`.tgz`، ‏`system.img`، ‏OTA ‏`system.new.dat.br`
- رابط‌های سازندگان همان‌طور که هستند کار می‌کنند: HTC Sense، Samsung TouchWiz، MIUI، AOSP
- گرافیک سخت‌افزاری از طریق پل GL، صدا، لمس و چندلمسی، شبکه با پراکسی TLS مدرن
- پوشهٔ مشترک کارت حافظه برای APK، موسیقی و عکس
- رابط Material 3 Expressive به 18 زبان
- رایگان و متن‌باز (GPL-3.0)

## 📱 فهرست فرم‌ویرها

روی Xiaomi 15 ‏(Snapdragon 8 Elite، اندروید 16) آزمایش شده است. هر فرم‌ویر ARM ‏32 بیتی اندروید 2.3 تا 4.4 ارزش امتحان دارد؛ فهرست فقط ایمیج‌های بررسی‌شده را دارد.

## 🚀 شروع سریع

1. فایل APK را از [Releases](https://github.com/oopnv70-lab/AEmulator/releases) دانلود و نصب کنید.
2. یک فرم‌ویر از جدول بالا روی گوشی دانلود کنید.
3. AEmulator را باز کنید ← **افزودن فرم‌ویر** و فایل را انتخاب کنید. درون‌ریزی چند دقیقه طول می‌کشد.
4. **اجرا** را بزنید. بوت اول کندتر است: سیستم برنامه‌ها را بهینه می‌کند.
5. منوی ⋮ برای صدا، دکمهٔ پاور و گزارش؛ دکمهٔ ⚙️ برای تنظیمات و زبان.

## 📋 نیازمندی‌ها

- اندروید 8.0+ روی گوشی ARM ‏64 بیتی (arm64-v8a)
- حدود 1 تا 3 گیگابایت فضای آزاد برای هر فرم‌ویر
- پیشنهاد: Snapdragon / Dimensity / Tensor جدید

## ⚙️ چگونه کار می‌کند

در اندروید 2.3 تا 4.4 هر فرایند مهمان زیر یک QEMU اصلاح‌شده در حالت کاربر اجرا می‌شود؛ در 5.0 تا 17 برنامه‌های arm64 خود فرم‌ویر مستقیم اجرا می‌شوند و یک هستهٔ موتور جداگانه برای هر نسخهٔ اندروید فراخوانی‌های سیستمی آن‌ها را می‌گیرد. سرویس binder جای درایور هسته را می‌گیرد، پل GL فراخوانی‌های OpenGL ES را به GPU می‌فرستد و کتابخانه‌های کوچک مهمان (HAL صوتی، پوشش audio policy، لایهٔ LD_PRELOAD) کد سازندگان را با شبیه‌ساز سازگار می‌کنند. درون‌ریز فرم‌ویر را می‌خواند، اسکریپت‌های init را در ایمیج boot پیدا می‌کند و برنامهٔ اجرای سرویس‌ها را می‌سازد.

## 🛠️ ساخت از کد منبع

به JDK 17، Android SDK 36 و NDK r28 نیاز دارید. کتابخانه‌های مهمان با `native/*/build.sh` ساخته می‌شوند.

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

## 🙏 سپاس

The architecture and the vast majority of the code come from upstream:
- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — the direct upstream, GPL-3.0
- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — the earlier engine it grew from (SENSES Emulator), GPL-2.0
- Original engine author: [t.me/istratii_tech1](https://t.me/istratii_tech1)
Third-party components, binary provenance and the licence gaps are listed in [NOTICE.md](../../NOTICE.md) and [docs/COMPLIANCE.md](../../docs/COMPLIANCE.md).

## 📄 مجوز

GPL-3.0. اندروید، نشان‌های تجاری و فرم‌ویرها متعلق به صاحبانشان‌اند.

</div>
