package xzr.perfmon

object JniTools {
    init {
        System.loadLibrary("tools")
    }

    @JvmStatic external fun getcpufreq(cpu: Int): Int
    @JvmStatic external fun getadrenofreq(): Int
    @JvmStatic external fun getadrenoload(): Int
    @JvmStatic external fun getmincpubw(): Int
    @JvmStatic external fun getcpubw(): Int
    @JvmStatic external fun getllcbw(): Int
    @JvmStatic external fun getgpubw(): Int
    @JvmStatic external fun getm4m(): Int
    @JvmStatic external fun getcpuload(cpu: Int): Int
    @JvmStatic external fun checkcpuload(): Boolean
    @JvmStatic external fun getcpuonlinestatus(cpu: Int): Int
    @JvmStatic external fun getmaxtemp(): Int
    @JvmStatic external fun getmemusage(): Int
    @JvmStatic external fun getcurrent(): Int
    @JvmStatic external fun getcpunum(): Int
    @JvmStatic external fun getfps(): String?
}
