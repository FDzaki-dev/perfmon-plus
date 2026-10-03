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
- Batch 5 (ZIP v5): toolchain Kotlin + Tools, Support, SharedPreferencesUtil -> .kt. [CI build hijau, dilaporkan user]
- Batch 6 (ZIP v6): JniTools (`object` + `@JvmStatic external`) + RefreshingDateThread (class + `companion object` dengan `@JvmField`, agar `import static` di FloatingWindow.java tetap valid). [Ditulis; user lanjut dengan "Next", hasil CI v6 tidak dilaporkan eksplisit]
- Batch 7 (ZIP v7): FloatingWindow (Service; state static -> `companion object` `@JvmField` supaya `FloatingWindow.do_exit`/`linen` dari MainActivity.java tetap valid) + hardening `Thread.sleep` di RefreshingDateThread.kt. [SELESAI ditulis, menunggu CI]
- Batch 8: MainActivity.
- Batch 9: Settings (file terbesar, sendiri).
- Batch 10: ExampleInstrumentedTest + ExampleUnitTest (opsional).
- Aturan kompatibilitas selama campuran Java/Kotlin: class Kotlin yang diakses Java memakai `object` + `@JvmStatic`/`@JvmField`/`const val` agar nama static tidak berubah; hindari `internal` (nama JVM di-mangle). `lateinit var` tidak boleh digabung `@JvmField`.

## Rencana Fitur Overclock CPU (BELUM dimulai; menunggu pilihan user)
- Status perangkat (dari user): non-root murni; user berharap overclock bisa lewat Shizuku.
- Temuan teknis (analisis, BELUM diuji di perangkat user): Shizuku memberi UID shell (2000), bukan root. Node `scaling_max_freq`/`scaling_min_freq`/`scaling_governor` di sysfs cpufreq pada perangkat stok umumnya milik root (0644) dan dilindungi SELinux, jadi tidak writable oleh shell. Maka mengubah/menaikkan frekuensi CPU via Shizuku diperkirakan GAGAL. Batas frekuensi di atas tabel OPP kernel (overclock sungguhan) tetap butuh kernel custom, dengan atau tanpa root.
- Uji verifikasi (read-only, aman): jalankan `rish -c 'ls -l /sys/devices/system/cpu/cpu0/cpufreq/scaling_max_freq'`; owner root + tanpa izin tulis untuk shell = terkonfirmasi tidak bisa.
- Opsi yang realistis (pilih salah satu; jangan menulis kode Shizuku sebelum dipilih): (A) tombol "boost" level-hint via Shizuku (Game Mode / fixed performance mode; efek bergantung vendor, BUKAN overclock); (B) Shizuku untuk MONITOR (baca node sysfs yang diblokir SELinux untuk aplikasi biasa); (C) tunda fitur overclock, fokus migrasi Kotlin.
- Dampak integrasi Shizuku bila dipilih: Shizuku-API menurut dokumentasinya butuh minSdk 23 (proyek saat ini 21; verifikasi saat dikerjakan; perubahan minSdk butuh persetujuan user). Kompatibilitas dengan AGP 4.0.1 / Kotlin 1.4.32 belum diverifikasi.
- Rancangan umum bila lanjut: preset berbasis persentase dari frekuensi tersedia, snapshot nilai asli untuk tombol kembali ke stock, eksekusi di `Dispatchers.IO`, whitelist path/nilai, tanpa watchdog/foreground service abadi, apply-on-boot hanya opt-in. Ditulis langsung dalam Kotlin.

## Status Verifikasi
- Terbukti (dilaporkan user): build di runner GitHub dan `release.yml` berjalan sampai publish; APK rilis berhasil diinstal di perangkat (masalah "paket bentrok" selesai). CI build.yml hijau untuk Batch 5.
- BELUM terverifikasi: perilaku fitur aplikasi di perangkat (user belum melaporkan).
- Batch 6 dan 7 BELUM dikompilasi di lingkungan pembuat ZIP (kotlinc/Gradle tidak ada); verifikasi lewat CI setelah push. Tidak ada klaim build hijau untuk Batch 6 maupun 7.
- Lint/detekt/pre-commit belum ada (detekt hanya untuk Kotlin; relevan setelah migrasi).

## [RESUME POINT]
[KOTLIN_MIGRATION_BATCH_7 + OVERCLOCK_VIA_SHIZUKU] -> [Batch 7: FloatingWindow.kt menggantikan FloatingWindow.java (paritas perilaku; companion @JvmField untuk do_exit, linen, show_*_now; fungsi init/monitor_init dinamai ulang initWindow/initMonitor; Handler pakai Looper.getMainLooper(); refresh teks dipisah ke updateLines()). RefreshingDateThread.kt: sleep -> Thread.sleep. Belum dikompilasi, belum dibuild CI; CI v6 tidak dilaporkan eksplisit. Overclock: user non-root, analisis teknis menyatakan shell UID tidak bisa menulis sysfs cpufreq (belum diuji di perangkat); fitur overclock BELUM dimulai, tidak ada kode Shizuku] -> [Setelah DAILY UPDATE ter-push, cek CI build.yml. Jika merah: perbaiki hanya di FloatingWindow.kt/RefreshingDateThread.kt/JniTools.kt (kandidat: akses Java ke companion field dari MainActivity.java, lateinit companion, nullability array cpufreq/cpuload/cpuonline). Jika hijau: Batch 8 = MainActivity. Overclock: tunggu pilihan user (A boost level-hint, B Shizuku untuk monitor, C tunda); jangan menulis kode Shizuku sebelum dipilih]
