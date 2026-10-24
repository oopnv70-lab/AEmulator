<div dir="rtl">

<div align="center">

<img src="../../docs/assets/logo.png" width="128" alt="AEmulator logo"/>

# AEmulator — Honor MagicOS 12 fork

*Fork of [uxazu/AEmulator](https://github.com/uxazu/AEmulator); the engine it grew from is [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*

**شغّل برامج أندرويد الكلاسيكية — HTC Sense وTouchWiz وMIUI وAOSP — على هاتف حديث. بلا روت وبلا حاسوب.**

[![Version](https://img.shields.io/badge/version-0.0.0.3-3D5AFE?style=for-the-badge)](https://github.com/oopnv70-lab/AEmulator/releases) [![License](https://img.shields.io/badge/license-GPL--3.0-3D5AFE?style=for-the-badge)](../../LICENSE) [![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/oopnv70-lab/AEmulator) [![Fork of](https://img.shields.io/badge/fork%20of-uxazu%2FAEmulator-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/uxazu/AEmulator)

[🇬🇧 English](../../README.md) · [🇷🇺 Русский](README.ru.md) · [🇺🇦 Українська](README.uk.md) · [🇩🇪 Deutsch](README.de.md) · [🇫🇷 Français](README.fr.md) · [🇪🇸 Español](README.es.md) · [🇧🇷 Português](README.pt-BR.md) · [🇮🇹 Italiano](README.it.md) · [🇵🇱 Polski](README.pl.md) · [🇹🇷 Türkçe](README.tr.md) · **🇸🇦 العربية** · [🇮🇷 فارسی](README.fa.md) · [🇮🇳 हिन्दी](README.hi.md) · [🇮🇩 Indonesia](README.id.md) · [🇻🇳 Tiếng Việt](README.vi.md) · [🇨🇳 简体中文](README.zh-CN.md) · [🇯🇵 日本語](README.ja.md) · [🇰🇷 한국어](README.ko.md)

</div>

---

يقلع AEmulator نظام أندرويد 2.3–17 حقيقيًا مباشرة من ملف البرنامج الثابت: ملف ZIP للريكفري أو أرشيف Odin أو صورة مصنع Google أو GSI. تعمل البرامج الثابتة القديمة بمعمارية 32 بت (2.3–4.4) عبر QEMU معدّل، ويعمل أندرويد 5.0–17 مباشرة على أنوية arm64 في الهاتف؛ ويُحاكى binder النواة، وتُرسم الواجهة بمعالج رسومات الهاتف ويمر الصوت عبر نظام صوت أندرويد — كل ذلك داخل تطبيق عادي.

## ✨ المزايا

- استيراد كل الصيغ تقريبًا: ‏ZIP لـ CWM/TWRP، و‏Odin ‏`.tar.md5` من سامسونج، وصورة مصنع Google ‏`.tgz`، و`system.img`، وOTA ‏`system.new.dat.br`
- واجهات الشركات تعمل كما هي: HTC Sense وSamsung TouchWiz وMIUI وAOSP
- رسوميات عتادية عبر جسر GL، وصوت، ولمس ولمس متعدد، وشبكة مع وكيل TLS حديث
- مجلد بطاقة ذاكرة مشترك لملفات APK والموسيقى والصور
- واجهة Material 3 Expressive بـ 18 لغة
- مجاني ومفتوح المصدر (GPL-3.0)

## 📱 قائمة البرامج الثابتة

اختُبر على Xiaomi 15 ‏(Snapdragon 8 Elite، أندرويد 16). تستحق أي برامج ARM بمعمارية 32 بت لأندرويد 2.3–4.4 التجربة؛ القائمة تضم ما تحققنا منه فقط.

## 🚀 البدء السريع

1. نزّل ملف APK من [Releases](https://github.com/oopnv70-lab/AEmulator/releases) وثبّته.
2. نزّل برنامجًا ثابتًا من الجدول أعلاه إلى هاتفك.
3. افتح AEmulator ← **إضافة برنامج ثابت** واختر الملف. يستغرق الاستيراد بضع دقائق.
4. اضغط **تشغيل**. الإقلاع الأول أبطأ: النظام يحسّن التطبيقات.
5. القائمة ⋮ للصوت وزر التشغيل والسجل؛ وزر ⚙️ للإعدادات واللغة.

## 📋 المتطلبات

- أندرويد 8.0+ على هاتف ARM بمعمارية 64 بت (arm64-v8a)
- نحو 1–3 غيغابايت لكل برنامج ثابت
- يُنصح بمعالج Snapdragon / Dimensity / Tensor حديث

## ⚙️ كيف يعمل

في أندرويد 2.3–4.4 تعمل كل عملية ضيف تحت QEMU معدّل بوضع المستخدم؛ وفي 5.0–17 تعمل برامج arm64 الخاصة بالبرنامج الثابت مباشرة، وتلتقط نواة محرك مستقلة لكل إصدار أندرويد استدعاءات النظام. يحل خادم binder محل برنامج تشغيل النواة، وينقل جسر GL استدعاءات OpenGL ES إلى معالج الرسومات، وتكيّف مكتبات صغيرة (HAL للصوت، وغلاف audio policy، وطبقة LD_PRELOAD) شفرة الشركات مع المحاكي. يقرأ المستورد البرنامج الثابت ويجد سكربتات init في صورة boot ويبني خطة تشغيل الخدمات.

## 🛠️ البناء من المصدر

تحتاج JDK 17 وAndroid SDK 36 وNDK r28. تُبنى مكتبات الضيف عبر `native/*/build.sh`.

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

## 🙏 شكر وتقدير

The architecture and the vast majority of the code come from upstream:
- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — the direct upstream, GPL-3.0
- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — the earlier engine it grew from (SENSES Emulator), GPL-2.0
- Original engine author: [t.me/istratii_tech1](https://t.me/istratii_tech1)
Third-party components, binary provenance and the licence gaps are listed in [NOTICE.md](../../NOTICE.md) and [docs/COMPLIANCE.md](../../docs/COMPLIANCE.md).

## 📄 الترخيص

GPL-3.0. أندرويد والعلامات التجارية والبرامج الثابتة ملك لأصحابها.

</div>
