package org.szesmaker.ordermeal

import android.app.*
import android.content.*
import android.os.*
import android.provider.*
import android.view.*
import android.view.View.*
import android.widget.*
import android.graphics.*

class Settings : Activity() {
    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)
        val savePass = this.findViewById(R.id.savePass) as Switch
        val checkupdate = this.findViewById(R.id.checkupdate) as TextView
        val about = this.findViewById(R.id.about) as TextView
        val sp = getSharedPreferences("code", MODE_PRIVATE)
        val editor = sp.edit()
        this.setTitle("设置")
        savePass.setChecked(sp.getBoolean("savepass", true))
        savePass.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener() {
            @Override
            fun onCheckedChanged(button: CompoundButton, isChecked: Boolean) {
                editor.putBoolean("savepass", isChecked)
                editor.putString("pass", "")
                editor.commit()
            }
        })
        checkupdate.setOnClickListener(object : OnClickListener() {
            @Override
            fun onClick(v: View) {
                //new checkUpdate().execute();
                val build = AlertDialog.Builder(this@Settings, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                build.setTitle("检查更新")
                build.setMessage("我不想写了，你们自己去下载罢\nszesmaker.org")
                build.setPositiveButton("复制", object : DialogInterface.OnClickListener() {
                    @Override
                    fun onClick(p1: DialogInterface, p2: Int) {
                        (getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(ClipData.newPlainText("text", "https://szesmaker.org/"))
                    }
                })
                build.create().show()
            }
        })
        about.setOnClickListener(object : OnClickListener() {
            @Override
            fun onClick(v: View) {
                val build = AlertDialog.Builder(this@Settings, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                build.setTitle("Developer: C6H5-NO2")
                build.setMessage("License: AGPL-3.0\nFollow us on Github: szes-maker")
                build.create().show()
            }
        })
    }
}
