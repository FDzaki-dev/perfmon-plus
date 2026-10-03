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
- CI: `.github/workflows/build.yml` (JDK 11, install SDK platform 30 + build-tools 29.0.2 + NDK 21.3.6528147, `assembleDebug assembleRelease`, upload artifact `PerfMon-Plus-apk`). Secrets repo (dari BOX B): `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`. Tanpa secret -> release unsigned, debug pakai key debug runner (berubah tiap run).
- Belum ada: konfigurasi lint/detekt, pre-commit hook
- Izin manifest: SYSTEM_ALERT_WINDOW, SYSTEM_OVERLAY_WINDOW
- Source utama: `app/src/main/java/xzr/perfmon/` (FloatingWindow, JniTools, MainActivity, RefreshingDateThread, Settings, SharedPreferencesUtil, Support, Tools) dan `app/src/main/jni/` (functions.c, interfaces.c, perfmon.h, xzr_perfmon_JniTools.h, Android.mk)

## Status Verifikasi
- Build/Lint/test BELUM dijalankan oleh pembuat ZIP (tanpa Android SDK/jaringan). Tidak ada klaim build hijau.
- User melaporkan error install di perangkat ("paket ini bentrok dengan paket yang sudah ada"), jadi APK dari CI diduga sudah terbentuk; log run Actions belum dilihat.
- Perbaikan signature (v3) BELUM diverifikasi di perangkat.
- Integritas ZIP dicek (daftar isi + exec bit `gradlew`).

## [RESUME POINT]
[BUG_INSTALL_CONFLICT "paket bentrok"] -> [`buildTypes.debug` di `app/build.gradle` kini memakai `signingConfigs.release` saat `KEYSTORE_PATH` ada, supaya APK debug+release CI satu signature; penyebab diduga dari signature berbeda, belum diverifikasi di perangkat] -> [User uninstall PerfMon+ lama / APK CI lama (signature beda) lalu install APK baru dari artifact `PerfMon-Plus-apk`. Jika masih bentrok, tanya user sebelum mengubah applicationId `xzr.perfmon`]
