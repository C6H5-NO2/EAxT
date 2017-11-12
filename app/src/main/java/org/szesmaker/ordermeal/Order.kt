package org.szesmaker.ordermeal

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import android.view.View.*
import android.widget.*
import android.widget.DatePicker.*
import java.util.*
import org.jsoup.*
import org.jsoup.nodes.*
import org.jsoup.select.*
import android.view.View.OnClickListener

class Order : Activity() {
    @Override
    private var dp: DatePicker? = null
    private var datey: TextView? = null
    private var ksdc: Button? = null
    private var name: String? = null
    private var cid: String? = null
    fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order)
        dp = this.findViewById(R.id.dp) as DatePicker
        datey = this.findViewById(R.id.datey) as TextView
        ksdc = this.findViewById(R.id.ksdc) as Button
        val dl = getIntent()
        cid = dl.getStringExtra("id")
        val nm = Jsoup.parse(dl.getStringExtra("out"))
        name = nm.select("span#LblUserName").first().text()
        name = name!!.substring(name!!.indexOf("：") + 1)
        this.setTitle("欢迎, " + name + "同学")
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        var month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        datey!!.setText("")
        datey!!.setText(year + "-")
        month++
        if (month < 10)
            datey!!.setText(datey!!.getText() + "0")
        datey!!.setText(datey!!.getText() + "" + month + "-")
        month--
        if (day < 10)
            datey!!.setText(datey!!.getText() + "0")
        datey!!.setText(datey!!.getText() + "" + day)
        dp!!.init(year, month, day, object : OnDateChangedListener() {
            @Override
            fun onDateChanged(view: DatePicker, year: Int, month: Int, day: Int) {
                var month = month
                datey!!.setText("")
                datey!!.setText(year.toString() + "-")
                month++
                if (month < 10)
                    datey!!.setText(datey!!.getText() + "0")
                datey!!.setText(datey!!.getText() + "" + month + "-")
                if (day < 10)
                    datey!!.setText(datey!!.getText() + "0")
                datey!!.setText(datey!!.getText() + "" + day)
            }
        })
        ksdc!!.setOnClickListener(object : OnClickListener() {
            @Override
            fun onClick(v: View) {
                val k = ks()
                k.execute()
            }
        })
    }

    @Override
    fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(1, 1, 0, "查询余额")
        menu.add(1, 2, 0, "设置")
        menu.add(1, 3, 0, "登出")
        return true
    }

    @Override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            1 -> cx().execute()
            2 -> {
                val settingjump = Intent()
                settingjump.setClass(this, Settings::class.java)
                startActivity(settingjump)
            }
            3 -> {
                this.finish()
                overridePendingTransition(0, R.anim.slide_in_bottom)
            }
        }
        return true
    }

    internal inner class cx : AsyncTask<Void, Void, Void>() {
        var dd = true
        var resp = ""
        var winlod = ProgressDialog(this@Order, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT)
        @Override
        protected fun onPreExecute() {
            winlod.setCancelable(false)
            winlod.setProgress(ProgressDialog.STYLE_SPINNER)
            winlod.setMessage("       正在查询")
            winlod.show()
        }

        @Override
        protected fun doInBackground(p1: Array<Void>): Void? {
            try {
                val doc = Jsoup.connect("http://gzb.szsy.cn/card/Default.aspx").get()
                resp = doc.toString()
                if (resp.equals(""))
                    dd = false
                else {
                    resp = doc.select("span#LblBalance").first().text()
                    resp = resp.substring(resp.indexOf("：") + 1)
                }
            } catch (e: Exception) {
                dd = false
            }

            return null
        }

        @Override
        protected fun onPostExecute(result: Void) {
            winlod.hide()
            if (dd)
                Toast.makeText(this@Order, "当前余额" + resp, Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this@Order, "查询失败", Toast.LENGTH_SHORT).show()
        }
    }

    internal inner class ks : AsyncTask<Void, Void, Void>() {
        var dd = true
        var date = ""
        var zao = ""
        var wuu = ""
        var wan = ""
        var resp = ""
        var view = ""
        var gen = ""
        var event = ""
        var flag = 2
        var ordered = 0
        var sp = getSharedPreferences("orderlist", MODE_PRIVATE)
        var editor = sp.edit()
        var winlod = ProgressDialog(this@Order, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT)
        @Override
        protected fun onPreExecute() {
            date = datey!!.getText().toString()
            winlod.setCancelable(false)
            winlod.setProgress(ProgressDialog.STYLE_SPINNER)
            winlod.setMessage("       正在加载")
            winlod.show()
        }

        @Override
        protected fun doInBackground(p1: Array<Void>): Void? {
            try {
                val url = "http://gzb.szsy.cn/card/Restaurant/RestaurantUserMenu/RestaurantUserMenu.aspx?Date=" + date
                val doc = Jsoup.connect(url).followRedirects(true).timeout(5000).get()
                resp = doc.toString()
                var temp = 0
                if (resp !== "") {
                    flag = resp.indexOf("Repeater1_Label1_0")
                    if (flag != -1) {
                        flag = resp.indexOf("value=\"+\"")
                        if (flag != -1) {
                            flag = 1
                            view = doc.select("input#__VIEWSTATE").first().attr("value")
                            gen = doc.select("input#__VIEWSTATEGENERATOR").first().attr("value")
                            event = doc.select("input#__EVENTVALIDATION").first().attr("value")
                        } else
                            flag = 0
                        val zaoc = doc.select("table#Repeater1_GvReport_0")
                        val wuuc = doc.select("table#Repeater1_GvReport_1")
                        val wanc = doc.select("table#Repeater1_GvReport_2")
                        val zaoo = doc.select("input#Repeater1_CbkMealtimes_0")
                        val wuuo = doc.select("input#Repeater1_CbkMealtimes_1")
                        val wano = doc.select("input#Repeater1_CbkMealtimes_2")
                        zao = zaoc.text()
                        wuu = wuuc.text()
                        wan = wanc.text()
                        editor.clear()
                        editor.commit()
                        temp = zaoo.toString().indexOf("checked")
                        if (temp != -1) {
                            ordered += 2
                            editor.putBoolean("0", true)
                            editor.putBoolean("y0", true)
                            editor.commit()
                        }
                        temp = wuuo.toString().indexOf("checked")
                        if (temp != -1) {
                            ordered += 4
                            editor.putBoolean("1", true)
                            editor.putBoolean("y1", true)
                            editor.commit()
                        }
                        temp = wano.toString().indexOf("checked")
                        if (temp != -1) {
                            ordered += 8
                            editor.putBoolean("2", true)
                            editor.putBoolean("y2", true)
                            editor.commit()
                        }
                    }
                }
            } catch (e: Exception) {
                dd = false
            }

            return null
        }

        @Override
        protected fun onPostExecute(result: Void) {
            winlod.hide()
            val dca = Intent()
            if (dd) {
                dca.putExtra("date", date)
                dca.putExtra("zao", zao)
                dca.putExtra("wuu", wuu)
                dca.putExtra("wan", wan)
                dca.putExtra("ordered", ordered)
                when (flag) {
                    -1 -> Toast.makeText(this@Order, "该日无菜单", Toast.LENGTH_SHORT).show()
                    0 -> {
                        dca.setClass(this@Order, Prohibited::class.java)
                        startActivity(dca)
                    }
                    1 -> {
                        dca.putExtra("view", view)
                        dca.putExtra("gen", gen)
                        dca.putExtra("event", event)
                        dca.setClass(this@Order, Allowed::class.java)
                        startActivity(dca)
                    }
                    else -> Toast.makeText(this@Order, "加载失败", Toast.LENGTH_SHORT).show()
                }
            } else
                Toast.makeText(this@Order, "加载失败", Toast.LENGTH_SHORT).show()
        }
    }

    @Override
    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
            return true
        }
        return false
    }
}
