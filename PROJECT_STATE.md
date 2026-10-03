# PROJECT_STATE

## Placeholder (sumber tunggal untuk skrip Termux)
- `[BRANDING_NAME]`: PerfMon-Plus
- `[TERMUX_ROOT]`: perfmon-plus

## Identitas Proyek (terbaca dari source)
- Aplikasi: PerfMon+ (monitor performa Android; floating window)
- Package / applicationId: `xzr.perfmon` (JANGAN diubah tanpa persetujuan user)
- Versi: versionCode 16, versionName 1.7.1
- Bahasa: Java + JNI (C, ndkBuild)
- Toolchain: AGP 4.0.1, Gradle 6.1.1, compileSdk/targetSdk 30, minSdk 21, NDK 21.3.6528147
- Repositori Gradle: `google()` + `jcenter()` (jcenter sudah deprecated)
- Signing: `signingConfigs.release` di `app/build.gradle`, aktif HANYA jika env `KEYSTORE_PATH` ada (env lain: `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`). Dipakai oleh buildType `release` DAN `debug` (satu signature untuk semua APK CI). Release tetap `minifyEnabled false`.
- CI: `.github/workflows/build.yml` (push ke main/PR/manual; JDK 11, install SDK platform 30 + build-tools 29.0.2 + NDK 21.3.6528147, `assembleDebug assembleRelease`, upload artifact `PerfMon-Plus-apk`). Tanpa secret -> release unsigned, debug pakai key debug runner (berubah tiap run).
- Secrets repo (dari BOX B): `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`.
- Belum ada: konfigurasi lint/detekt, pre-commit hook
- Izin manifest: SYSTEM_ALERT_WINDOW, SYSTEM_OVERLAY_WINDOW
- Source utama: `app/src/main/java/xzr/perfmon/` (FloatingWindow, JniTools, MainActivity, RefreshingDateThread, Settings, SharedPreferencesUtil, Support, Tools) dan `app/src/main/jni/` (functions.c, interfaces.c, perfmon.h, xzr_perfmon_JniTools.h, Android.mk)

## GitHub Release
- File: `.github/workflows/release.yml`. Pemicu: push tag semver `vMAJOR.MINOR.PATCH[-prerelease]` (contoh `v1.7.1`, `v1.8.0-beta.1`).
- Gate (job `build`): format tag valid; tag minus `v` == `versionName` di `app/build.gradle`; commit tag ada di `origin/main`; 4 secret signing wajib ada (rilis unsigned DITOLAK); `apksigner verify` lolos.
- Aset rilis: `PerfMon-Plus-v<versi>.apk` + `PerfMon-Plus-v<versi>.apk.sha256`.
- Release notes: header (unduh, SHA-256, catatan instal) + daftar commit sejak tag `v*` sebelumnya (`git log`) + link Full Changelog. Alur kerja repo = push langsung ke main (tanpa PR), jadi auto-notes GitHub berbasis PR tidak dipakai.
- Tag berisi `-` otomatis ditandai prerelease.
- Job `publish` terpisah: hanya dia yang `contents: write`, tidak menjalankan build dan tidak membaca secret signing; memakai `gh release create --verify-tag`.
- Cara rilis: pastikan `versionName` = versi yang diinginkan, push ke main, tunggu CI hijau, lalu buat dan push tag `v<versionName>` (BOX RELEASE TAG).
- Versi app (`versionName`) berbeda dari nomor batch ZIP/CHANGELOG pipeline (v1, v2, ...).

## Status Verifikasi
- Build/Lint/test BELUM dijalankan oleh pembuat ZIP (tanpa Android SDK/jaringan). Tidak ada klaim build hijau.
- User melaporkan error install di perangkat ("paket ini bentrok dengan paket yang sudah ada"), jadi APK dari CI diduga sudah terbentuk; log run Actions belum dilihat.
- Perbaikan signature (v3) BELUM diverifikasi di perangkat.
- `release.yml` BELUM pernah dijalankan di GitHub. Terverifikasi lokal: parse YAML, `bash -n` semua blok run, dan simulasi di repo git sandbox untuk gate tag/versi/branch, deteksi prerelease, packaging, checksum, dan release notes. Belum terverifikasi: langkah yang butuh runner GitHub (install SDK/NDK, Gradle build, apksigner, `gh release create`).
- Integritas ZIP dicek (daftar isi + exec bit `gradlew`).

## [RESUME POINT]
[GITHUB_RELEASE_v4] -> [`.github/workflows/release.yml` ditambahkan (gate tag/versionName/main, signed-only, apksigner verify, SHA-256, notes dari git log, job publish terpisah); belum pernah dijalankan di GitHub, hanya disimulasikan lokal] -> [Setelah DAILY UPDATE ter-push dan build CI hijau, jalankan BOX RELEASE TAG (`v1.7.1` = versionName saat ini); cek tab Actions "Release" dan halaman Releases. Jika gagal, baca log step yang merah dan perbaiki di `release.yml` saja; kandidat: install NDK/build-tools, path `apksigner` 29.0.2, atau izin `contents: write`]
