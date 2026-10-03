# PROJECT_STATE

## Placeholder (sumber tunggal untuk skrip Termux)
- `[BRANDING_NAME]`: PerfMon-Plus
- `[TERMUX_ROOT]`: perfmon-plus

## Tujuan Proyek (dari user)
1. Migrasi TOTAL ke Kotlin demi keandalan.
2. Tanam fitur overclock CPU dengan preset siap pakai ("siap tempur").

## Identitas Proyek
- Aplikasi: PerfMon+ (monitor performa Android; floating window)
- Package / applicationId: `xzr.perfmon` (JANGAN diubah tanpa persetujuan user)
- Versi: versionCode 16, versionName 1.7.1
- Bahasa: Java + Kotlin (migrasi bertahap) + JNI (C, ndkBuild)
- Toolchain: AGP 4.0.1, Gradle 6.1.1, Kotlin 1.4.32 (`kotlin-android`, jvmTarget 1.8), compileSdk/targetSdk 30, minSdk 21, NDK 21.3.6528147
- Repositori Gradle: `google()`, `mavenCentral()`, `jcenter()` (jcenter deprecated; dipertahankan sebagai fallback)
- Signing: `signingConfigs.release` di `app/build.gradle`, aktif HANYA jika env `KEYSTORE_PATH` ada (env lain: `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`). Dipakai buildType `release` DAN `debug`. Release `minifyEnabled false`.
- Secrets repo (dari BOX B): `KEYSTORE_BASE64`, `KEYSTORE_PASSWORD`, `KEY_ALIAS`, `KEY_PASSWORD`. Password keystore hanya ada di GitHub Secrets.
- Izin manifest: SYSTEM_ALERT_WINDOW, SYSTEM_OVERLAY_WINDOW
- JNI: `JniTools` semua method `public static native` (C: `(JNIEnv*, jclass)`), symbol `Java_xzr_perfmon_JniTools_*`. Saat dikonversi WAJIB tetap static (`object` + `@JvmStatic external fun`) dan nama symbol tidak berubah.

## CI & Release
- `.github/workflows/build.yml`: push main/PR/manual; JDK 11; SDK platform 30 + build-tools 29.0.2 + NDK 21.3.6528147; `assembleDebug assembleRelease`; artifact `PerfMon-Plus-apk`.
- `.github/workflows/release.yml`: push tag `vX.Y.Z[-pre]`; gate (tag == versionName, commit di main, 4 secret wajib, `apksigner verify`); aset `PerfMon-Plus-v<versi>.apk` + `.sha256`; notes dari `git log`; job `publish` terpisah (`contents: write`).
- Rilis: naikkan `versionName`, push ke main, tunggu CI hijau, buat dan push tag `v<versionName>`.
- `versionName` app berbeda dari nomor batch ZIP/CHANGELOG pipeline.

## Peta Migrasi Kotlin (target: semua .java -> .kt, maks 3-5 file per batch)
- Batch 5 (ZIP v5): toolchain Kotlin + Tools, Support, SharedPreferencesUtil -> .kt. [SELESAI ditulis, menunggu CI]
- Batch 6: JniTools + RefreshingDateThread.
- Batch 7: FloatingWindow.
- Batch 8: MainActivity.
- Batch 9: Settings (file terbesar, sendiri).
- Batch 10: ExampleInstrumentedTest + ExampleUnitTest (opsional).
- Aturan kompatibilitas selama campuran Java/Kotlin: class Kotlin yang diakses Java memakai `object` + `@JvmStatic`/`@JvmField`/`const val` agar nama static tidak berubah; hindari `internal` (nama JVM di-mangle). `lateinit var` tidak boleh digabung `@JvmField`.

## Rencana Fitur Overclock CPU (BELUM dimulai, belum disetujui detailnya)
- Syarat: perangkat root. Jawaban user soal status root/kernel/SoC masih ditunggu.
- Batas teknis: aplikasi hanya bisa memilih frekuensi yang diekspos kernel (`scaling_available_frequencies`); melampaui batas stok butuh kernel custom.
- Rancangan awal: preset berbasis persentase dari rentang frekuensi tersedia (bukan angka hardcode), snapshot nilai asli untuk tombol kembali ke stock, eksekusi root di `Dispatchers.IO` dengan path/nilai tervalidasi (whitelist), tanpa watchdog/foreground service abadi (guard), apply-on-boot hanya jika opt-in.
- Dikerjakan SETELAH migrasi cukup maju, ditulis langsung dalam Kotlin.

## Status Verifikasi
- Terbukti (dilaporkan user): build di runner GitHub dan `release.yml` berjalan sampai publish; APK rilis berhasil diinstal di perangkat (masalah "paket bentrok" selesai).
- BELUM terverifikasi: perilaku fitur aplikasi di perangkat (user belum melaporkan).
- Batch 5 BELUM dikompilasi (kotlinc/Gradle tidak ada di lingkungan pembuat ZIP); verifikasi lewat CI setelah push. Tidak ada klaim build hijau untuk Batch 5.
- Lint/detekt/pre-commit belum ada (detekt hanya untuk Kotlin; relevan setelah migrasi).

## [RESUME POINT]
[KOTLIN_MIGRATION_BATCH_5] -> [Toolchain Kotlin 1.4.32 + `mavenCentral()` ditambahkan; Tools, Support, SharedPreferencesUtil dikonversi ke .kt (paritas perilaku, API static Java dipertahankan); belum dikompilasi, belum dibuild CI] -> [Setelah DAILY UPDATE ter-push, cek CI build.yml. Jika merah: baca error step Gradle dan perbaiki hanya di file Batch 5 (kandidat: akses Java ke field/fungsi object Kotlin, versi KGP vs Gradle 6.1.1). Jika hijau: Batch 6 = JniTools (`@JvmStatic external`) + RefreshingDateThread. Overclock menunggu jawaban user: apakah HP sudah root]
