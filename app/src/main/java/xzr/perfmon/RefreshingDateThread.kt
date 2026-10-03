package xzr.perfmon

class RefreshingDateThread : Thread() {
    override fun run() {
        delay = SharedPreferencesUtil.sharedPreferences.getInt(
            SharedPreferencesUtil.delay,
            SharedPreferencesUtil.default_delay
        )
        reverse_current_now = SharedPreferencesUtil.sharedPreferences.getBoolean(
            SharedPreferencesUtil.reverse_current,
            SharedPreferencesUtil.reverse_current_default
        )
        val freq = IntArray(cpunum)
        val load = IntArray(cpunum)
        val online = IntArray(cpunum)
        cpufreq = freq
        cpuload = load
        cpuonline = online
        while (!FloatingWindow.do_exit) {
            for (i in 0 until cpunum) {
                online[i] = JniTools.getcpuonlinestatus(i)
                if (online[i] == 1 && FloatingWindow.show_cpufreq_now) {
                    freq[i] = JniTools.getcpufreq(i)
                }
            }
            if (FloatingWindow.show_gpufreq_now && Support.support_adrenofreq) {
                adrenofreq = JniTools.getadrenofreq()
            }
            if (FloatingWindow.show_gpuload_now && Support.support_adrenofreq) {
                adrenoload = JniTools.getadrenoload()
            }
            if (FloatingWindow.show_mincpubw_now && Support.support_mincpubw) {
                mincpubw = JniTools.getmincpubw()
            }
            if (FloatingWindow.show_cpubw_now && Support.support_cpubw) {
                cpubw = JniTools.getcpubw()
            }
            if (FloatingWindow.show_m4m_now && Support.support_m4m) {
                m4m = JniTools.getm4m()
            }
            if (FloatingWindow.show_thermal_now && Support.support_temp) {
                maxtemp = JniTools.getmaxtemp()
            }
            if (FloatingWindow.show_mem_now && Support.support_mem) {
                memusage = JniTools.getmemusage()
            }
            if (FloatingWindow.show_current_now && Support.support_current) {
                current = JniTools.getcurrent()
            }
            if (FloatingWindow.show_gpubw_now && Support.support_gpubw) {
                gpubw = JniTools.getgpubw()
            }
            if (FloatingWindow.show_llcbw_now && Support.support_llcbw) {
                llcbw = JniTools.getllcbw()
            }
            if (FloatingWindow.show_fps_now && Support.support_fps) {
                fps = JniTools.getfps()
            }
            if (reverse_current_now) {
                current = -current
            }
            FloatingWindow.ui_refresher.sendEmptyMessage(0)
            for (i in 0 until cpunum) {
                if (online[i] == 1 && FloatingWindow.show_cpuload_now && Support.support_cpuload) {
                    Thread { load[i] = JniTools.getcpuload(i) }.start()
                }
            }
            try {
                sleep(delay.toLong())
            } catch (e: Exception) {
                // paritas perilaku Java: exception sleep diabaikan
            }
        }
    }

    companion object {
        @JvmField var cpunum = 0
        @JvmField var cpufreq: IntArray? = null
        @JvmField var cpuload: IntArray? = null
        @JvmField var cpuonline: IntArray? = null
        @JvmField var adrenoload = 0
        @JvmField var adrenofreq = 0
        @JvmField var mincpubw = 0
        @JvmField var cpubw = 0
        @JvmField var m4m = 0
        @JvmField var maxtemp = 0
        @JvmField var memusage = 0
        @JvmField var current = 0
        @JvmField var gpubw = 0
        @JvmField var llcbw = 0
        @JvmField var fps: String? = null

        @JvmField var delay = 0
        @JvmField var reverse_current_now = false
    }
}
