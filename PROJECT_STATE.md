# PROJECT_STATE

## Placeholder (sumber tunggal untuk skrip Termux)
- `[BRANDING_NAME]`: PerfMon-Plus
- `[TERMUX_ROOT]`: perfmon-plus

## Identitas Proyek (terbaca dari source ZIP awal)
- Aplikasi: PerfMon+ (monitor performa Android; floating window)
- Package / applicationId: `xzr.perfmon` (JANGAN diubah)
- Versi: versionCode 16, versionName 1.7.1
- Bahasa: Java + JNI (C, ndkBuild)
- Toolchain: AGP 4.0.1, Gradle 6.1.1, compileSdk/targetSdk 30, minSdk 21, NDK 21.3.6528147
- Repositori Gradle: `google()` + `jcenter()` (jcenter sudah deprecated)
- Release: `minifyEnabled false`, belum ada `signingConfigs`
- Belum ada: `.github/workflows`, konfigurasi detekt/lint, pre-commit hook
- Izin manifest: SYSTEM_ALERT_WINDOW, SYSTEM_OVERLAY_WINDOW
- Source utama: `app/src/main/java/xzr/perfmon/` (FloatingWindow, JniTools, MainActivity, RefreshingDateThread, Settings, SharedPreferencesUtil, Support, Tools) dan `app/src/main/jni/` (functions.c, interfaces.c, perfmon.h, xzr_perfmon_JniTools.h, Android.mk)

## Status Verifikasi
- Build/Lint/test BELUM dijalankan (lingkungan pembuat ZIP tanpa Android SDK/jaringan). Tidak ada klaim build hijau.
- Integritas ZIP v1 dicek (daftar isi + exec bit `gradlew`).

## [RESUME POINT]
[INITIAL_SETUP_v1] -> [ZIP rata (tanpa folder induk), PROJECT_STATE.md + CHANGELOG.md ditambahkan, source app tidak diubah, gradlew diberi exec bit; build belum diverifikasi] -> [Verifikasi BOX A (repo perfmon-plus ter-push) dan BOX B (4 secret ter-set); lalu tunggu task berikutnya dari user. Workflow CI release + signingConfigs BELUM ada: tambahkan hanya jika diminta]
