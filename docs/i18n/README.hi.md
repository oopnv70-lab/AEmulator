<div align="center">

<img src="../../docs/assets/logo.png" width="128" alt="AEmulator logo"/>

# AEmulator — Honor MagicOS 12 fork

*Fork of [uxazu/AEmulator](https://github.com/uxazu/AEmulator); the engine it grew from is [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*

**पुराने Android फ़र्मवेयर — HTC Sense, TouchWiz, MIUI, AOSP — आधुनिक फ़ोन पर। बिना root, बिना PC।**

[![Version](https://img.shields.io/badge/version-0.0.0.3-3D5AFE?style=for-the-badge)](https://github.com/oopnv70-lab/AEmulator/releases) [![License](https://img.shields.io/badge/license-GPL--3.0-3D5AFE?style=for-the-badge)](../../LICENSE) [![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/oopnv70-lab/AEmulator) [![Fork of](https://img.shields.io/badge/fork%20of-uxazu%2FAEmulator-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/uxazu/AEmulator)

[🇬🇧 English](../../README.md) · [🇷🇺 Русский](README.ru.md) · [🇺🇦 Українська](README.uk.md) · [🇩🇪 Deutsch](README.de.md) · [🇫🇷 Français](README.fr.md) · [🇪🇸 Español](README.es.md) · [🇧🇷 Português](README.pt-BR.md) · [🇮🇹 Italiano](README.it.md) · [🇵🇱 Polski](README.pl.md) · [🇹🇷 Türkçe](README.tr.md) · [🇸🇦 العربية](README.ar.md) · [🇮🇷 فارسی](README.fa.md) · **🇮🇳 हिन्दी** · [🇮🇩 Indonesia](README.id.md) · [🇻🇳 Tiếng Việt](README.vi.md) · [🇨🇳 简体中文](README.zh-CN.md) · [🇯🇵 日本語](README.ja.md) · [🇰🇷 한국어](README.ko.md)

</div>

---

AEmulator सीधे फ़र्मवेयर फ़ाइल से असली Android 2.3–17 सिस्टम बूट करता है: रिकवरी ZIP, Odin आर्काइव या Google फ़ैक्टरी इमेज या GSI। पुराने 32-बिट फ़र्मवेयर (2.3–4.4) संशोधित QEMU से चलते हैं, Android 5.0–17 सीधे फ़ोन के arm64 कोर पर चलता है; कर्नेल का binder एमुलेट होता है, ग्राफ़िक्स फ़ोन के GPU से और आवाज़ Android के ऑडियो सिस्टम से चलती है — सब कुछ एक सामान्य ऐप के भीतर।

## ✨ विशेषताएँ

- लगभग हर फ़ॉर्मेट आयात करें: CWM/TWRP ZIP, Samsung Odin `.tar.md5`, Google फ़ैक्टरी `.tgz`, `system.img`, OTA `system.new.dat.br`
- कंपनियों के स्किन जैसे हैं वैसे चलते हैं: HTC Sense, Samsung TouchWiz, MIUI, AOSP
- GL ब्रिज से हार्डवेयर ग्राफ़िक्स, आवाज़, टच और मल्टीटच, आधुनिक TLS प्रॉक्सी के साथ नेटवर्क
- APK, संगीत और फ़ोटो के लिए साझा मेमोरी कार्ड फ़ोल्डर
- 18 भाषाओं में Material 3 Expressive इंटरफ़ेस
- मुफ़्त और ओपन सोर्स (GPL-3.0)

## 📱 फ़र्मवेयर सूची

Xiaomi 15 (Snapdragon 8 Elite, Android 16) पर परखा गया। Android 2.3–4.4 का कोई भी 32-बिट ARM फ़र्मवेयर आज़माने लायक है; सूची में केवल जाँची गई इमेज हैं।

## 🚀 जल्दी शुरुआत

1. [Releases](https://github.com/oopnv70-lab/AEmulator/releases) से APK डाउनलोड करके इंस्टॉल करें।
2. ऊपर की तालिका से कोई फ़र्मवेयर फ़ोन पर डाउनलोड करें।
3. AEmulator खोलें → **फ़र्मवेयर जोड़ें** और फ़ाइल चुनें। आयात में कुछ मिनट लगते हैं।
4. **चलाएँ** दबाएँ। पहला बूट धीमा होता है: सिस्टम ऐप्स ऑप्टिमाइज़ करता है।
5. ⋮ मेनू में वॉल्यूम, पावर बटन और लॉग; ⚙️ से सेटिंग्स और भाषा।

## 📋 आवश्यकताएँ

- 64-बिट ARM फ़ोन (arm64-v8a) पर Android 8.0+
- प्रति फ़र्मवेयर लगभग 1–3 GB खाली जगह
- नया Snapdragon / Dimensity / Tensor सुझाया जाता है

## ⚙️ यह कैसे काम करता है

Android 2.3–4.4 पर हर गेस्ट प्रोसेस संशोधित यूज़र-मोड QEMU में चलता है; 5.0–17 पर फ़र्मवेयर के अपने arm64 प्रोग्राम सीधे चलते हैं, और हर Android संस्करण के लिए अलग इंजन कर्नेल उनके सिस्टम कॉल पकड़ता है। एक binder डेमन कर्नेल ड्राइवर की जगह लेता है, GL ब्रिज OpenGL ES कॉल GPU तक भेजता है, और छोटी गेस्ट लाइब्रेरी (ऑडियो HAL, audio policy रैपर, LD_PRELOAD शिम) निर्माताओं के कोड को एमुलेटर के अनुकूल बनाती हैं। इम्पोर्टर फ़र्मवेयर पढ़ता है, boot इमेज में init स्क्रिप्ट ढूँढता है और सेवाओं की शुरुआत की योजना बनाता है।

## 🛠️ सोर्स से बनाएँ

JDK 17, Android SDK 36 और NDK r28 चाहिए। गेस्ट लाइब्रेरी `native/*/build.sh` से बनती हैं।

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

## 🙏 आभार

The architecture and the vast majority of the code come from upstream:
- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — the direct upstream, GPL-3.0
- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — the earlier engine it grew from (SENSES Emulator), GPL-2.0
- Original engine author: [t.me/istratii_tech1](https://t.me/istratii_tech1)
Third-party components, binary provenance and the licence gaps are listed in [NOTICE.md](../../NOTICE.md) and [docs/COMPLIANCE.md](../../docs/COMPLIANCE.md).

## 📄 लाइसेंस

GPL-3.0। Android, ट्रेडमार्क और फ़र्मवेयर उनके स्वामियों के हैं।
