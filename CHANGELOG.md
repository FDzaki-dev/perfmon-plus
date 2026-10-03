# Changelog

## v5 — 2026-10-03
- Migrasi Kotlin Batch 1: tambah toolchain Kotlin 1.4.32 (`kotlin-android`, stdlib, jvmTarget 1.8, Java 8 compileOptions) dan `mavenCentral()`.
- Konversi `Tools`, `Support`, `SharedPreferencesUtil` dari Java ke Kotlin (`object`, API static untuk caller Java dipertahankan).
- File Java lain, JNI, workflow, dan package identity (`xzr.perfmon`) tidak diubah.
- Belum dikompilasi lokal; verifikasi lewat CI.

## v4 — 2026-10-03
- Tambah `.github/workflows/release.yml`: rilis GitHub otomatis dari tag semver (`v1.7.1`, `v1.8.0-beta.1`).
- Gate rilis: tag harus sama dengan `versionName`, commit harus ada di `main`, semua secret signing wajib ada, signature APK diverifikasi dengan `apksigner`.
- Aset rilis `PerfMon-Plus-v<versi>.apk` + checksum SHA-256; release notes dari riwayat commit; tag dengan `-` ditandai prerelease.
- Job `publish` dipisah (satu-satunya yang punya `contents: write`, tanpa akses secret signing).
- Source aplikasi, `build.yml`, dan package identity (`xzr.perfmon`) tidak diubah.

## v3 — 2026-10-03
- Fix install "paket bentrok": buildType `debug` ikut ditandatangani keystore CI (env `KEYSTORE_PATH`) agar APK debug dan release satu signature dan kompatibel antar-run.
- Source aplikasi, workflow, dan package identity (`xzr.perfmon`) tidak diubah.

## v2 — 2026-10-03
- Tambah GitHub Actions `.github/workflows/build.yml`: build debug + release, upload APK sebagai artifact.
- Tambah `signingConfigs.release` di `app/build.gradle` (env-based, aktif hanya jika `KEYSTORE_PATH` ada; tanpa secret hardcode).
- Source aplikasi dan package identity (`xzr.perfmon`) tidak diubah.

## v1 — 2026-10-03
- Initial setup pipeline: ZIP sumber (PerfMon-Plus 1.7.1) diratakan tanpa folder induk.
- Tambah `PROJECT_STATE.md` (placeholder + RESUME POINT) dan `CHANGELOG.md`.
- `gradlew` diberi exec bit.
- Source aplikasi, package identity (`xzr.perfmon`), dan konfigurasi Gradle tidak diubah.
