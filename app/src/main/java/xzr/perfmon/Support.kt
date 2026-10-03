package xzr.perfmon

object Support {
    @JvmField var support_cpufreq = false
    @JvmField var support_cpuload = false
    @JvmField var support_adrenofreq = false
    @JvmField var support_mincpubw = false
    @JvmField var support_cpubw = false
    @JvmField var support_m4m = false
    @JvmField var support_temp = false
    @JvmField var support_mem = false
    @JvmField var support_current = false
    @JvmField var support_gpubw = false
    @JvmField var support_llcbw = false
    @JvmField var support_fps = false

    const val UNSUPPORTED = -1

    @JvmStatic
    fun CheckSupport(): Int {
        var linen = 0

        support_cpufreq = JniTools.getcpufreq(0) != UNSUPPORTED
        if (support_cpufreq) linen += RefreshingDateThread.cpunum

        support_cpuload = JniTools.checkcpuload()

        support_adrenofreq = JniTools.getadrenofreq() != UNSUPPORTED
        if (support_adrenofreq) linen++

        support_mincpubw = JniTools.getmincpubw() != UNSUPPORTED
        if (support_mincpubw) linen++

        support_cpubw = JniTools.getcpubw() != UNSUPPORTED
        if (support_cpubw) linen++

        support_m4m = JniTools.getm4m() != UNSUPPORTED
        if (support_m4m) linen++

        support_temp = JniTools.getmaxtemp() != UNSUPPORTED
        if (support_temp) linen++

        support_mem = JniTools.getmemusage() != UNSUPPORTED
        if (support_mem) linen++

        support_current = JniTools.getcurrent() != UNSUPPORTED
        if (support_current) linen++

        support_gpubw = JniTools.getgpubw() != UNSUPPORTED
        if (support_gpubw) linen++

        support_llcbw = JniTools.getllcbw() != UNSUPPORTED
        if (support_llcbw) linen++

        support_fps = JniTools.getfps() != ""
        if (support_fps) linen++

        return linen
    }
}
