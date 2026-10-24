<div align="center">

<img src="../../docs/assets/logo.png" width="128" alt="AEmulator logo"/>

# AEmulator — Honor MagicOS 12 fork

*Fork of [uxazu/AEmulator](https://github.com/uxazu/AEmulator); the engine it grew from is [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES)*

**Firmware Android cổ điển — HTC Sense, TouchWiz, MIUI, AOSP — trên điện thoại hiện đại. Không root, không cần PC.**

[![Version](https://img.shields.io/badge/version-0.0.0.3-3D5AFE?style=for-the-badge)](https://github.com/oopnv70-lab/AEmulator/releases) [![License](https://img.shields.io/badge/license-GPL--3.0-3D5AFE?style=for-the-badge)](../../LICENSE) [![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/oopnv70-lab/AEmulator) [![Fork of](https://img.shields.io/badge/fork%20of-uxazu%2FAEmulator-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/uxazu/AEmulator)

[🇬🇧 English](README.en.md) · [🇷🇺 Русский](README.ru.md) · [🇺🇦 Українська](README.uk.md) · [🇩🇪 Deutsch](README.de.md) · [🇫🇷 Français](README.fr.md) · [🇪🇸 Español](README.es.md) · [🇧🇷 Português](README.pt-BR.md) · [🇮🇹 Italiano](README.it.md) · [🇵🇱 Polski](README.pl.md) · [🇹🇷 Türkçe](README.tr.md) · [🇸🇦 العربية](README.ar.md) · [🇮🇷 فارسی](README.fa.md) · [🇮🇳 हिन्दी](README.hi.md) · [🇮🇩 Indonesia](README.id.md) · **🇻🇳 Tiếng Việt** · [🇨🇳 简体中文](../../README.md) · [🇯🇵 日本語](README.ja.md) · [🇰🇷 한국어](README.ko.md)

</div>

---

AEmulator khởi động một hệ thống Android 2.3–17 thật trực tiếp từ tệp firmware: ZIP recovery, gói Odin factory image của Google hoặc GSI. Firmware 32-bit cũ (2.3–4.4) chạy qua QEMU đã chỉnh sửa, Android 5.0–17 chạy trực tiếp trên các nhân arm64 của điện thoại; binder của nhân được giả lập, đồ họa dùng GPU điện thoại và âm thanh đi qua hệ thống âm thanh Android — tất cả trong một ứng dụng bình thường.

## ✨ Tính năng

- Nhập gần như mọi định dạng: ZIP CWM/TWRP, Odin `.tar.md5` của Samsung, factory `.tgz` của Google, `system.img`, OTA `system.new.dat.br`
- Giao diện hãng chạy nguyên bản: HTC Sense, Samsung TouchWiz, MIUI, AOSP
- Đồ họa phần cứng qua cầu GL, âm thanh, cảm ứng và đa điểm, mạng với proxy TLS hiện đại
- Thư mục thẻ nhớ dùng chung cho APK, nhạc và ảnh
- Giao diện Material 3 Expressive với 18 ngôn ngữ
- Miễn phí và mã nguồn mở (GPL-3.0)

## 📱 Danh sách firmware

Đã thử trên Xiaomi 15 (Snapdragon 8 Elite, Android 16). Mọi firmware ARM 32-bit cho Android 2.3–4.4 đều đáng thử; danh sách chỉ gồm các image đã kiểm tra.

## 🚀 Bắt đầu nhanh

1. Tải APK từ [Releases](https://github.com/oopnv70-lab/AEmulator/releases) và cài đặt.
2. Tải một firmware trong bảng trên về điện thoại.
3. Mở AEmulator → **Thêm firmware** và chọn tệp. Việc nhập mất vài phút.
4. Nhấn **Chạy**. Lần khởi động đầu chậm hơn: hệ thống tối ưu ứng dụng.
5. Menu ⋮ để chỉnh âm lượng, nút nguồn và nhật ký; ⚙️ mở cài đặt và ngôn ngữ.

## 📋 Yêu cầu

- Android 8.0+ trên điện thoại ARM 64-bit (arm64-v8a)
- Khoảng 1–3 GB trống cho mỗi firmware
- Nên dùng Snapdragon / Dimensity / Tensor đời mới

## ⚙️ Cách hoạt động

Trên Android 2.3–4.4 mỗi tiến trình khách chạy dưới QEMU chế độ người dùng đã chỉnh sửa; trên 5.0–17 các chương trình arm64 của firmware chạy trực tiếp, và một nhân động cơ riêng cho từng phiên bản Android bắt các lời gọi hệ thống của chúng. Một daemon binder thay cho driver nhân, cầu GL chuyển lệnh OpenGL ES tới GPU và các thư viện khách nhỏ (HAL âm thanh, lớp bọc audio policy, shim LD_PRELOAD) điều chỉnh mã của hãng cho hợp với trình giả lập. Trình nhập đọc firmware, tìm script init trong image boot và lập kế hoạch khởi động dịch vụ.

## 🛠️ Biên dịch từ mã nguồn

Cần JDK 17, Android SDK 36 và NDK r28. Thư viện khách được dựng bằng `native/*/build.sh`.

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

## 🙏 Ghi công

The architecture and the vast majority of the code come from upstream:
- [uxazu/AEmulator](https://github.com/uxazu/AEmulator) — the direct upstream, GPL-3.0
- [Istratii-tech/SENSES](https://github.com/Istratii-tech/SENSES) — the earlier engine it grew from (SENSES Emulator), GPL-2.0
- Original engine author: [t.me/istratii_tech1](https://t.me/istratii_tech1)
Third-party components, binary provenance and the licence gaps are listed in [NOTICE.md](../../NOTICE.md) and [docs/COMPLIANCE.md](../../docs/COMPLIANCE.md).

## 📄 Giấy phép

GPL-3.0. Android, nhãn hiệu và firmware thuộc về chủ sở hữu.
