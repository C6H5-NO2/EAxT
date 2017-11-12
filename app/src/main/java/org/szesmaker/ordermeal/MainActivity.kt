package org.szesmaker.ordermeal

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import android.view.View.*
import android.view.inputmethod.*
import android.widget.*
import java.io.*
import java.net.*
import java.util.*
import org.jsoup.*
import org.jsoup.nodes.*

class MainActivity : Activity() {
    internal var submit: Button
    internal var name: EditText
    internal var pass: EditText
    internal var szsy: ImageView
    internal var sp: SharedPreferences
    internal var editor: SharedPreferences.Editor
    @Override
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        submit = this.findViewById(R.id.sign_in) as Button
        name = this.findViewById(R.id.xuehao) as EditText
        pass = this.findViewById(R.id.mima) as EditText
        szsy = this.findViewById(R.id.szsy) as ImageView
        sp = getSharedPreferences("code", MODE_PRIVATE)
        editor = sp.edit()
        name.setText(sp.getString("name", "").toString())
        if (sp.getBoolean("savepass", true))
            pass.setText(sp.getString("pass", "").toString())
        szsy.setOnClickListener(object : OnClickListener() {
            @Override
            fun onClick(v: View) {
                val settingjump = Intent()
                settingjump.setClass(this@MainActivity, Settings::class.java)
                startActivity(settingjump)
            }
        })
        submit.setOnClickListener(object : OnClickListener() {
            @Override
            fun onClick(v: View) {
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(this@MainActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
                editor.putString("name", name.getText().toString())
                editor.commit()
                login().execute()
            }
        })
    }

    internal inner class login : AsyncTask<Void, Void, Integer>() {
        var respond = ""
        var window = ProgressDialog(this@MainActivity, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT)
        @Override
        protected fun onPreExecute() {
            window.setCancelable(false)
            window.setProgress(ProgressDialog.STYLE_SPINNER)
            window.setMessage("       正在登录")
            window.show()
        }

        @Override
        protected fun doInBackground(p1: Array<Void>): Integer {
            val cookie = CookieManager()
            CookieHandler.setDefault(cookie)
            val doc: Document
            try {
                doc = Jsoup.connect("http://passport-yun.szsy.cn/login?service=http://gzb.szsy.cn/card/Default.aspx").followRedirects(true).timeout(5000).get()
            } catch (e: IOException) {
                return 0
            }

            respond = post("http://passport-yun.szsy.cn/login?service=http://gzb.szsy.cn/card/Default.aspx",
                    doc.select("input[name=execution]").first().attr("value"),
                    doc.select("input[name=lt]").first().attr("value"),
                    pass.getText().toString(),
                    name.getText().toString())
            val cs = cookie.getCookieStore()
            return if (respond.indexOf("深圳实验学校一卡通管理系统") !== -1) 1 else 0
        }

        @Override
        protected fun onPostExecute(flag: Integer) {
            window.hide()
            if (flag == 1) {
                editor.putString("pass", pass.getText().toString())
                editor.commit()
                val loginjump = Intent()
                loginjump.putExtra("out", respond)
                loginjump.putExtra("id", name.getText().toString())
                loginjump.setClass(this@MainActivity, Order::class.java)
                startActivity(loginjump)
                overridePendingTransition(R.anim.slide_out_bottom, 0)
            } else {
                editor.putString("pass", "")
                editor.commit()
                Toast.makeText(this@MainActivity, "登录失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun post(url: String, execution: String, lt: String, password: String, username: String): String {
        val params = HashMap<String, String>(8)
        params.put("_eventId", "submit")
        params.put("captcha", "null")
        params.put("code", "")
        params.put("execution", execution)
        params.put("lt", lt)
        params.put("password", password)
        params.put("phone", "")
        params.put("username", username)
        return sendHttpRequest(url, params, "utf-8")
    }

    fun sendHttpRequest(url: String, params: Map<String, String>, encode: String): String {
        var html = ""
        val buffer = StringBuffer()
        val data = encapsulate(params, encode)!!.toString().getBytes()
        try {
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.setRequestMethod("POST")
            connection.setDoInput(true)
            connection.setDoOutput(true)
            connection.setConnectTimeout(5000)
            connection.connect()
            connection.getOutputStream().write(data)
            val stream = connection.getInputStream()
            val b = ByteArray(4096)
            while (stream.read(b) !== -1)
                buffer.append(String(b))
            html = buffer.toString()
            connection.disconnect()
        } catch (e: Exception) {
            html = ""
        }

        return html
    }

    fun encapsulate(params: Map<String, String>, encode: String): StringBuffer? {
        val buffer = StringBuffer()
        try {
            for (entry in params.entrySet()) {
                buffer
                        .append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&")
            }
            buffer.deleteCharAt(buffer.length() - 1)
        } catch (e: Exception) {
            return null
        }

        return buffer
    }
}
