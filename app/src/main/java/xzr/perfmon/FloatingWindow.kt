package xzr.perfmon

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class FloatingWindow : Service() {
    private lateinit var main: LinearLayout

    @SuppressLint("ClickableViewAccessibility")
    @Suppress("DEPRECATION")
    private fun initWindow() {
        val prefs = SharedPreferencesUtil.sharedPreferences
        size_multiple_now = prefs.getFloat(
            SharedPreferencesUtil.size_multiple,
            SharedPreferencesUtil.size_multiple_default
        )

        show_cpufreq_now = prefs.getBoolean(SharedPreferencesUtil.show_cpufreq, SharedPreferencesUtil.show_cpufreq_default)
        if (!show_cpufreq_now && Support.support_cpufreq) linen -= JniTools.getcpunum()
        show_cpuload_now = prefs.getBoolean(SharedPreferencesUtil.show_cpuload, SharedPreferencesUtil.show_cpuload_default)

        show_gpufreq_now = prefs.getBoolean(SharedPreferencesUtil.show_gpufreq, SharedPreferencesUtil.show_gpufreq_default)
        if (!show_gpufreq_now && Support.support_adrenofreq) linen--
        show_gpuload_now = prefs.getBoolean(SharedPreferencesUtil.show_gpuload, SharedPreferencesUtil.show_gpuload_default)

        show_cpubw_now = prefs.getBoolean(SharedPreferencesUtil.show_cpubw, SharedPreferencesUtil.show_cpubw_default)
        if (!show_cpubw_now && Support.support_cpubw) linen--

        show_mincpubw_now = prefs.getBoolean(SharedPreferencesUtil.show_mincpubw, SharedPreferencesUtil.show_mincpubw_default)
        if (!show_mincpubw_now && Support.support_mincpubw) linen--

        show_m4m_now = prefs.getBoolean(SharedPreferencesUtil.show_m4m, SharedPreferencesUtil.show_m4m_default)
        if (!show_m4m_now && Support.support_m4m) linen--

        show_thermal_now = prefs.getBoolean(SharedPreferencesUtil.show_thermal, SharedPreferencesUtil.show_thermal_default)
        if (!show_thermal_now && Support.support_temp) linen--

        show_mem_now = prefs.getBoolean(SharedPreferencesUtil.show_mem, SharedPreferencesUtil.show_mem_default)
        if (!show_mem_now && Support.support_mem) linen--

        show_current_now = prefs.getBoolean(SharedPreferencesUtil.show_current, SharedPreferencesUtil.show_current_default)
        if (!show_current_now && Support.support_current) linen--

        show_gpubw_now = prefs.getBoolean(SharedPreferencesUtil.show_gpubw, SharedPreferencesUtil.show_gpubw_default)
        if (!show_gpubw_now && Support.support_gpubw) linen--

        show_llcbw_now = prefs.getBoolean(SharedPreferencesUtil.show_llcbw, SharedPreferencesUtil.show_llcbw_default)
        if (!show_llcbw_now && Support.support_llcbw) linen--

        show_fps_now = prefs.getBoolean(SharedPreferencesUtil.show_fps, SharedPreferencesUtil.show_fps_default)
        if (!show_fps_now && Support.support_fps) linen--

        params = WindowManager.LayoutParams()
        windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        params.type = if (Build.VERSION.SDK_INT >= 26) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }

        params.format = PixelFormat.RGBA_8888
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        params.gravity = Gravity.LEFT or Gravity.TOP
        params.x = 0
        params.y = 0
        val metrics = resources.displayMetrics
        val savedWidth = prefs.getInt(SharedPreferencesUtil.width, SharedPreferencesUtil.default_width)
        if (savedWidth != SharedPreferencesUtil.default_width) {
            params.width = savedWidth
        } else if ((Support.support_cpuload && show_cpuload_now) || (Support.support_adrenofreq && show_gpuload_now)) {
            params.width = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 145f, metrics).toInt() * size_multiple_now).toInt()
        } else {
            params.width = (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 120f, metrics).toInt() * size_multiple_now).toInt()
        }
        params.height = 300
        main = LinearLayout(this)
        main.orientation = LinearLayout.VERTICAL
        main.setBackgroundColor(resources.getColor(R.color.floating_window_backgrouns))
        main.setPadding(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 5f, metrics).toInt(), 0, 0, 0)
        val close = TextView(this)
        close.setText(R.string.close)
        close.setTextSize(TypedValue.COMPLEX_UNIT_PX, close.textSize * size_multiple_now)
        close.setTextColor(resources.getColor(R.color.white))
        main.addView(close)
        close.setOnClickListener { stopSelf() }
        close.setOnLongClickListener {
            SharedPreferencesUtil.sharedPreferences.edit()
                .putBoolean(SharedPreferencesUtil.skip_first_screen, false)
                .commit()
            Toast.makeText(this@FloatingWindow, R.string.skip_first_screen_str_disabled, Toast.LENGTH_LONG).show()
            false
        }
        main.setOnTouchListener(object : View.OnTouchListener {
            private var x = 0
            private var y = 0

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = event.rawX.toInt()
                        y = event.rawY.toInt()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val nowX = event.rawX.toInt()
                        val nowY = event.rawY.toInt()
                        val movedX = nowX - x
                        val movedY = nowY - y
                        x = nowX
                        y = nowY
                        params.x = params.x + movedX
                        params.y = params.y + movedY
                        windowManager.updateViewLayout(main, params)
                    }
                    else -> Unit
                }
                return false
            }
        })
        windowManager.addView(main, params)

        main.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = resources.getDimensionPixelSize(resourceId)
        }
    }

    @Suppress("DEPRECATION")
    private fun initMonitor() {
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        line = Array(linen) { TextView(this) }

        val prefs = SharedPreferencesUtil.sharedPreferences
        val savedHeight = prefs.getInt(SharedPreferencesUtil.height, SharedPreferencesUtil.default_height)
        if (savedHeight != SharedPreferencesUtil.default_height) {
            params.height = savedHeight
        } else {
            params.height = (linen + 1) *
                (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20f, resources.displayMetrics) * size_multiple_now).toInt()
        }

        windowManager.updateViewLayout(main, params)
        ui_refresher = Handler(Looper.getMainLooper(), Handler.Callback {
            updateLines()
            false
        })

        for (i in 0 until linen) {
            val view = line[i]
            view.setTextColor(resources.getColor(R.color.white))
            view.layoutParams = layoutParams
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, view.textSize * size_multiple_now)
            main.addView(view)
        }
        windowManager.updateViewLayout(main, params)
        RefreshingDateThread().start()
    }

    private fun updateLines() {
        var i = 0
        if (Support.support_cpufreq && show_cpufreq_now) {
            val online = RefreshingDateThread.cpuonline ?: return
            val freq = RefreshingDateThread.cpufreq ?: return
            val load = RefreshingDateThread.cpuload ?: return
            for (c in 0 until RefreshingDateThread.cpunum) {
                var text = "cpu$c "
                if (online[c] == 1) {
                    text = text + freq[c] + " Mhz"
                    if (Support.support_cpuload && show_cpuload_now) {
                        text = text + Tools.format_ify_add_blank(freq[c].toString()) + load[c] + "%"
                    }
                } else {
                    text += resources.getString(R.string.offline)
                }
                line[c].text = text
            }
            i = RefreshingDateThread.cpunum
        }
        if (Support.support_adrenofreq && show_gpufreq_now) {
            val adrenofreq = RefreshingDateThread.adrenofreq
            if (show_gpuload_now) {
                line[i].text = "gpu0 " + adrenofreq + " Mhz" +
                    Tools.format_ify_add_blank(adrenofreq.toString()) + RefreshingDateThread.adrenoload + "%"
            } else {
                line[i].text = "gpu0 " + adrenofreq + " Mhz" + Tools.format_ify_add_blank(adrenofreq.toString())
            }
            i++
        }
        if (Support.support_mincpubw && show_mincpubw_now) {
            line[i].text = "mincpubw " + RefreshingDateThread.mincpubw
            i++
        }
        if (Support.support_cpubw && show_cpubw_now) {
            line[i].text = "cpubw " + RefreshingDateThread.cpubw
            i++
        }
        if (Support.support_gpubw && show_gpubw_now) {
            line[i].text = "gpubw " + RefreshingDateThread.gpubw
            i++
        }
        if (Support.support_llcbw && show_llcbw_now) {
            line[i].text = "llccbw " + RefreshingDateThread.llcbw
            i++
        }
        if (Support.support_m4m and show_m4m_now) {
            line[i].text = "m4m " + RefreshingDateThread.m4m + " Mhz"
            i++
        }
        if (Support.support_temp && show_thermal_now) {
            line[i].text = resources.getString(R.string.temp) + RefreshingDateThread.maxtemp + " ℃"
            i++
        }
        if (Support.support_mem && show_mem_now) {
            line[i].text = resources.getString(R.string.mem) + RefreshingDateThread.memusage + "%"
            i++
        }
        if (Support.support_current && show_current_now) {
            line[i].text = resources.getString(R.string.current) + RefreshingDateThread.current + " mA"
            i++
        }
        if (Support.support_fps && show_fps_now) {
            line[i].text = "fps " + RefreshingDateThread.fps
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        do_exit = false
        initWindow()
        initMonitor()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "Calling destory service")
        do_exit = true
        try {
            windowManager.removeView(main)
        } catch (e: Exception) {
            // paritas perilaku Java: exception removeView diabaikan
        }
        super.onDestroy()
    }

    companion object {
        private const val TAG = "FloatingWindow"

        @JvmField var do_exit = true
        lateinit var params: WindowManager.LayoutParams
        lateinit var windowManager: WindowManager
        @JvmField var statusBarHeight = -1
        lateinit var line: Array<TextView>
        @JvmField var linen = 0
        lateinit var ui_refresher: Handler
        @JvmField var size_multiple_now = 0f

        @JvmField var show_cpufreq_now = false
        @JvmField var show_cpuload_now = false
        @JvmField var show_gpufreq_now = false
        @JvmField var show_gpuload_now = false
        @JvmField var show_cpubw_now = false
        @JvmField var show_mincpubw_now = false
        @JvmField var show_m4m_now = false
        @JvmField var show_thermal_now = false
        @JvmField var show_mem_now = false
        @JvmField var show_current_now = false
        @JvmField var show_gpubw_now = false
        @JvmField var show_llcbw_now = false
        @JvmField var show_fps_now = false
    }
}
