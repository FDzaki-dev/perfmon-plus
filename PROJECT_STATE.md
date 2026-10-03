# PROJECT_STATE

## Placeholder (sumber tunggal untuk skrip Termux)
- `[BRANDING_NAME]`: PerfMon-Plus
- `[TERMUX_ROOT]`: perfmon-plus

## Identitas Proyek (terbaca dari source)
- Aplikasi: PerfMon+ (monitor performa Android; floating window)
- Package / applicationId: `xzr.perfmon` (JANGAN diubah)
- Versi: versionCode 16, versionName 1.7.1
- Bahasa: Java + JNI (C, ndkBuild)
- Toolchain: AGP 4.0.1, Gradle 6.1.1, compileSdk/targetSdk 30, minSdk 21, NDK 21.3.6528147
- Repositori Gradle: `google()` + `jcenter()` (jcenter sudah deprecated)
- Release: `minifyEnabled false`; signing release via `signingConfigs.release` di `app/build.gradle`, aktif HANYA jika env `KEYSTORE_PATH` ada (env lain: `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`)
- CI: `.github/workflows/build.yml` (JDK 11, install SDK platform 30 + build-tools 29.0.2 + NDK 21.3.6528147, `assembleDebug assembleRelease`, upload artifact `PerfMon-Plus-apk`). Secrets repo (dari BOX B): `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`. Tanpa secret -> release unsigned.
- Belum ada: konfigurasi lint/detekt, pre-commit hook
- Izin manifest: SYSTEM_ALERT_WINDOW, SYSTEM_OVERLAY_WINDOW
- Source utama: `app/src/main/java/xzr/perfmon/` (FloatingWindow, JniTools, MainActivity, RefreshingDateThread, Settings, SharedPreferencesUtil, Support, Tools) dan `app/src/main/jni/` (functions.c, interfaces.c, perfmon.h, xzr_perfmon_JniTools.h, Android.mk)

## Status Verifikasi
- Build/Lint/test BELUM dijalankan (pembuat ZIP tanpa Android SDK/jaringan). Tidak ada klaim build hijau.
- Workflow CI BELUM pernah dijalankan; sintaks YAML sudah di-parse, perilaku di runner belum terverifikasi.
- Integritas ZIP dicek (daftar isi + exec bit `gradlew`).

## [RESUME POINT]
[CI_ACTIONS_v2] -> [`.github/workflows/build.yml` + `signingConfigs` env di `app/build.gradle` ditambahkan; belum pernah dijalankan di GitHub] -> [Setelah DAILY UPDATE ter-push, cek tab Actions. Jika gagal, baca log step yang gagal dan perbaiki di file itu saja; kandidat penyebab: resolve `jcenter()`, install NDK 21.3.6528147 / build-tools 29.0.2, atau JDK 11 vs AGP 4.0.1]
