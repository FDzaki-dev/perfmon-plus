# Changelog

## v2 — 2026-10-03
- Tambah GitHub Actions `.github/workflows/build.yml`: build debug + release, upload APK sebagai artifact.
- Tambah `signingConfigs.release` di `app/build.gradle` (env-based, aktif hanya jika `KEYSTORE_PATH` ada; tanpa secret hardcode).
- Source aplikasi dan package identity (`xzr.perfmon`) tidak diubah.

## v1 — 2026-10-03
- Initial setup pipeline: ZIP sumber (PerfMon-Plus 1.7.1) diratakan tanpa folder induk.
- Tambah `PROJECT_STATE.md` (placeholder + RESUME POINT) dan `CHANGELOG.md`.
- `gradlew` diberi exec bit.
- Source aplikasi, package identity (`xzr.perfmon`), dan konfigurasi Gradle tidak diubah.
