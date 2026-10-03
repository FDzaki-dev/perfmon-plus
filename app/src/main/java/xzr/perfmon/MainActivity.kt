package xzr.perfmon

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MainActivity : Activity() {
    private lateinit var mainView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainView = ScrollView(this)
        setContentView(mainView)

        if (!FloatingWindow.do_exit) {
            Toast.makeText(this, resources.getString(R.string.please_close_app_first), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Dipindah dari service:onCreate agar daftar dukungan bisa ditampilkan
        RefreshingDateThread.cpunum = JniTools.getcpunum()
        FloatingWindow.linen = Support.CheckSupport()
        SharedPreferencesUtil.init(this)

        if (SharedPreferencesUtil.sharedPreferences.getBoolean(
                SharedPreferencesUtil.skip_first_screen,
                SharedPreferencesUtil.default_skip_first_screen
            )
        ) {
            permissioncheck()
            finish()
            return
        }

        addView()
    }

    private fun addSupportRow(parent: LinearLayout, labelRes: Int, supported: Boolean) {
        val textView = TextView(this)
        textView.text = resources.getString(labelRes) + Tools.bool2text(supported, this)
        parent.addView(textView)
    }

    private fun addView() {
        val main = LinearLayout(this)
        main.orientation = LinearLayout.VERTICAL
        mainView.addView(main)

        addSupportRow(main, R.string.support_cpufreq_mo, Support.support_cpufreq)
        addSupportRow(main, R.string.support_cpuload_mo, Support.support_cpuload)
        addSupportRow(main, R.string.support_gpufreq_mo, Support.support_adrenofreq)
        addSupportRow(main, R.string.support_gpuload_mo, Support.support_adrenofreq)
        addSupportRow(main, R.string.support_cpubw_mo, Support.support_cpubw)
        addSupportRow(main, R.string.support_gpubw_mo, Support.support_gpubw)
        addSupportRow(main, R.string.support_llcbw_mo, Support.support_llcbw)
        addSupportRow(main, R.string.support_m4mfreq_mo, Support.support_m4m)
        addSupportRow(main, R.string.support_thermal_mo, Support.support_temp)
        addSupportRow(main, R.string.support_mem_mo, Support.support_mem)
        addSupportRow(main, R.string.support_current_mo, Support.support_current)
        addSupportRow(main, R.string.support_fps_mo, Support.support_fps)

        val reason = TextView(this)
        reason.setText(R.string.Unsupport_reason)
        main.addView(reason)

        val showButton = Button(this)
        showButton.setText(R.string.show_floatingwindow)
        main.addView(showButton)
        showButton.setOnClickListener {
            permissioncheck()
            finish()
        }

        val settingsButton = Button(this)
        settingsButton.setText(R.string.settings)
        main.addView(settingsButton)
        settingsButton.setOnClickListener { Settings.creatDialog(this) }

        val selinuxButton = Button(this)
        selinuxButton.setText(R.string.permissive_selinux)
        main.addView(selinuxButton)
        selinuxButton.setOnClickListener {
            runSu("setenforce 0\nexit\n", R.string.permissive_done)
        }
        selinuxButton.setOnLongClickListener {
            runSu("setenforce 1\nexit\n", R.string.enforce_done)
            true
        }

        val description = TextView(this)
        description.setText(R.string.permissive_selinux_description)
        main.addView(description)

        val links = LinearLayout(this)
        main.addView(links)
        addLink(links, R.string.visit_github, "https://github.com/xzr467706992/PerfMon-Plus")
        addLink(links, R.string.visit_coolapk, "https://www.coolapk.com/apk/xzr.perfmon")
    }

    private fun addLink(parent: LinearLayout, labelRes: Int, url: String) {
        val textView = TextView(this)
        textView.setText(labelRes)
        parent.addView(textView)
        textView.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

    // Paritas perilaku Java: eksekusi su tetap di thread utama (utang teknis, belum diubah)
    private fun runSu(command: String, doneRes: Int) {
        try {
            val process = ProcessBuilder("su").redirectErrorStream(true).start()
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            val writer = OutputStreamWriter(process.outputStream)
            writer.write(command)
            writer.flush()
            val log = reader.readLines().joinToString(separator = "") { it + "\n" }
            if (log == "") {
                Toast.makeText(this, doneRes, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, log, Toast.LENGTH_SHORT).show()
            }
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    private fun permissioncheck() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (android.provider.Settings.canDrawOverlays(this)) {
                startService(Intent(this, FloatingWindow::class.java))
            } else {
                try {
                    val field = android.provider.Settings::class.java
                        .getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION")
                    val intent = Intent(field.get(null)!!.toString())
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                } catch (e: Exception) {
                    // paritas perilaku Java: exception diabaikan
                }
            }
        } else {
            // SDK di bawah 23: tidak perlu izin overlay
            startService(Intent(this, FloatingWindow::class.java))
        }
    }
}
