package xzr.perfmon

import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesUtil {
    lateinit var sharedPreferences: SharedPreferences

    const val skip_first_screen = "skip_first_screen"
    const val default_skip_first_screen = false

    const val delay = "refreshing_delay"
    const val default_delay = 1000

    const val width = "window_width"
    @JvmField var default_width = -1

    const val height = "window_height"
    const val default_height = -1

    const val reverse_current = "reverse_current"
    const val reverse_current_default = false

    const val size_multiple = "size_multiple"
    const val size_multiple_default = 0.8f

    const val show_cpufreq = "show_cpufreq"
    const val show_cpufreq_default = true

    const val show_cpuload = "show_cpuload"
    const val show_cpuload_default = true

    const val show_gpufreq = "show_gpufreq"
    const val show_gpufreq_default = true

    const val show_gpuload = "show_gpuload"
    const val show_gpuload_default = true

    const val show_cpubw = "show_cpubw"
    const val show_cpubw_default = true

    const val show_mincpubw = "show_mincpubw"
    const val show_mincpubw_default = false

    const val show_m4m = "show_m4m"
    const val show_m4m_default = true

    const val show_thermal = "show_thermal"
    const val show_thermal_default = true

    const val show_mem = "show_mem"
    const val show_mem_default = true

    const val show_current = "show_current"
    const val show_current_default = true

    const val show_gpubw = "show_gpubw"
    const val show_gpubw_default = true

    const val show_llcbw = "show_llcbw"
    const val show_llcbw_default = true

    const val show_fps = "show_fps"
    const val show_fps_default = true

    @JvmStatic
    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences("main", 0)
    }
}
