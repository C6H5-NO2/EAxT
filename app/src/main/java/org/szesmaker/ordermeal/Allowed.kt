package org.szesmaker.ordermeal

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import android.widget.*
import java.io.*
import java.lang.reflect.*
import java.net.*
import java.util.*
import org.jsoup.*
import org.jsoup.nodes.*

class Allowed : TabActivity() {
    private var sp: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    private var view: String? = null
    private var gen: String? = null
    private var event: String? = null
    private var date: String? = null
    @Override
    fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        var zao = ""
        var wuu = ""
        var wan = ""
        var ordered = 0
        sp = getSharedPreferences("orderlist", MODE_PRIVATE)
        editor = sp!!.edit()
        val dca = getIntent()
        zao = dca.getStringExtra("zao")
        wuu = dca.getStringExtra("wuu")
        wan = dca.getStringExtra("wan")
        date = dca.getStringExtra("date")
        ordered = dca.getIntExtra("ordered", 0)
        view = dca.getStringExtra("view")
        gen = dca.getStringExtra("gen")
        event = dca.getStringExtra("event")
        this.setTitle(date!!.substring(0, 4) + "年" + date!!.substring(5, 7) + "月" + date!!.substring(8) + "日菜单")
        val tab = getTabHost()
        val tab1 = tab.newTabSpec("tab1")
        val tab2 = tab.newTabSpec("tab2")
        val tab3 = tab.newTabSpec("tab3")
        var remote: Intent
        if (!zao.equals("")) {
            remote = Intent()
            remote.setClass(this, AllowedList::class.java)
            remote.putExtra("flag", 1)
            remote.putExtra("zao", zao)
            remote.putExtra("ordered", ordered)
            tab1.setContent(remote)
            tab1.setIndicator("早餐菜单")
            tab.addTab(tab1)
            prepare(zao, 0)
        }
        if (!wuu.equals("")) {
            remote = Intent()
            remote.setClass(this, AllowedList::class.java)
            remote.putExtra("flag", 2)
            remote.putExtra("wuu", wuu)
            remote.putExtra("ordered", ordered)
            tab2.setContent(remote)
            tab2.setIndicator("午餐菜单")
            tab.addTab(tab2)
            prepare(wuu, 1)
        }
        if (!wan.equals("")) {
            remote = Intent()
            remote.setClass(this, AllowedList::class.java)
            remote.putExtra("flag", 3)
            remote.putExtra("wan", wan)
            remote.putExtra("ordered", ordered)
            tab3.setContent(remote)
            tab3.setIndicator("晚餐菜单")
            tab.addTab(tab3)
            prepare(wan, 2)
        }
        getOverflowMenu()
    }

    fun prepare(caidan: String, meal_num: Int) {
        val ol = wcd(caidan)
        for (position in 1 until ol.size())
            editor!!.putString("Repeater1_GvReport_" + meal_num + "_TxtNum_" + (position - 1) + "@", ol.get(position).get("fs").toString() + "|")
        editor!!.commit()
    }

    fun wcd(caidan: String): ArrayList<HashMap<String, Object>> {
        val cd = ArrayList<HashMap<String, Object>>()
        var map: HashMap<String, Object>
        val key = arrayOf("bh", "lb", "cm", "", "", "dj", "zd", "fs", "")
        map = HashMap<String, Object>()
        val str = arrayOf("编号", "类别", "菜名", "", "", "单价", "最大份数", "份数", "")
        for (l in 0..8)
            if (l == 0 || l == 1 || l == 2 || l == 5 || l == 6 || l == 7)
                map.put(key[l], str[l])
        cd.add(map)
        var temp = caidan.substring(caidan.indexOf("0"))
        for (i in 0..9) {
            if (!temp.substring(0, 1).equals(i.toString() + ""))
                continue
            map = HashMap<String, Object>()
            map.put(key[0], i)
            var j = 0
            var k = 0
            for (t in 1..8) {
                j = temp.indexOf(" ", k)
                k = temp.indexOf(" ", j + 2)
                val item = temp.substring(j + 1, k)
                if (t == 1 || t == 2 || t == 5 || t == 6 || t == 7)
                    map.put(key[t], item)
            }
            cd.add(map)
            temp = temp.substring(k + 1)
        }
        return cd
    }

    @Override
    fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.add(1, 1, 1, "提交订单")
        return true
    }

    @Override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.getItemId() === 1) {
            val om = ordermeal()
            om.execute()
        }
        return false
    }

    private fun getOverflowMenu() {
        try {
            val config = ViewConfiguration.get(this)
            val menuKeyField = ViewConfiguration::class.java!!.getDeclaredField("sHasPermanentMenuKey")
            if (menuKeyField != null) {
                menuKeyField!!.setAccessible(true)
                menuKeyField!!.setBoolean(config, false)
            }
        } catch (e: Exception) {
            Toast.makeText(this@Allowed, "不支持该版本", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    internal inner class ordermeal : AsyncTask<Void, Void, Void>() {
        var dd = true
        var resp = ""
        var url = ""
        var winlod = ProgressDialog(this@Allowed, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT)
        @Override
        protected fun onPreExecute() {
            winlod.setCancelable(false)
            winlod.setProgress(ProgressDialog.STYLE_SPINNER)
            winlod.setMessage("       正在提交")
            winlod.show()
        }

        @Override
        protected fun doInBackground(p1: Array<Void>): Void? {
            try {
                url = "http://gzb.szsy.cn/card/Restaurant/RestaurantUserMenu/RestaurantUserMenu.aspx?Date=" + date!!
                for (i in 0..2) {
                    val bol = fix(url, i)
                    if (!bol) {
                        dd = false
                        break
                    }
                }
                if (dd)
                    resp = sendHttpRequest(url, makemap())
            } catch (e: Exception) {
                dd = false
            }

            return null
        }

        @Override
        protected fun onPostExecute(result: Void) {
            winlod.hide()
            if (resp.equals(""))
                dd = false
            if (dd) {
                Toast.makeText(this@Allowed, "提交成功", Toast.LENGTH_SHORT).show()
                editor!!.clear()
                editor!!.commit()
                this@Allowed.finish()
            } else
                Toast.makeText(this@Allowed, "提交失败", Toast.LENGTH_SHORT).show()
        }
    }

    fun fix(url: String, meal_num: Int): Boolean {
        val temp: String
        val yun: Boolean
        val xin: Boolean
        yun = sp!!.getBoolean(("y" + meal_num).toString(), false)
        xin = sp!!.getBoolean((meal_num.toString() + "").toString(), false)
        var i: Int
        if (yun == false && xin == true) {
            val encode = "utf-8"
            val stringBuffer = StringBuffer()
            val params = HashMap<String, String>()
            i = 0
            while (i < meal_num) {
                if (sp!!.getBoolean((i.toString() + "").toString(), false))
                    params.put("Repeater1\$ctl0$i\$CbkMealtimes", "on")
                i++
            }
            params.put("__EVENTARGUMENT", "")
            params.put("__EVENTTARGET", "Repeater1\$ctl0$meal_num\$CbkMealtimes")
            params.put("__EVENTVALIDATION", event)
            params.put("__LASTFOCUS", "")
            params.put("__VIEWSTATEENCRYPTED", "")
            params.put("__VIEWSTATEGENERATOR", gen)
            params.put("DrplstRestaurantBasis1\$DrplstControl", "4d05282b-b96f-4a3f-ba54-fc218266a524")
            params.put("Repeater1\$ctl0$meal_num\$CbkMealtimes", "on")
            try {
                for (entry in params.entrySet()) {
                    stringBuffer
                            .append(URLEncoder.encode(entry.getKey(), encode))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&")
                }
                stringBuffer.append(URLEncoder.encode("__VIEWSTATE", encode)).append("=").append(URLEncoder.encode(view, encode))
            } catch (e: Exception) {
                return false
            }

            temp = sendHttpRequest(url, stringBuffer)
            try {
                val doc = Jsoup.connect(url).get()
                view = doc.select("input#__VIEWSTATE").first().attr("value")
                gen = doc.select("input#__VIEWSTATEGENERATOR").first().attr("value")
                event = doc.select("input#__EVENTVALIDATION").first().attr("value")
            } catch (e: Exception) {
                return false
            }

        } else if (yun == true && xin == false) {
            val encode = "utf-8"
            val stringBuffer = StringBuffer()
            val params = HashMap<String, String>()
            i = 0
            while (i < meal_num) {
                if (sp!!.getBoolean((i.toString() + "").toString(), false))
                    params.put("Repeater1\$ctl0$i\$CbkMealtimes", "on")
                i++
            }
            params.put("__EVENTARGUMENT", "")
            params.put("__EVENTTARGET", "Repeater1\$ctl0$meal_num\$CbkMealtimes")
            params.put("__EVENTVALIDATION", event)
            params.put("__LASTFOCUS", "")
            params.put("__VIEWSTATEENCRYPTED", "")
            params.put("__VIEWSTATEGENERATOR", gen)
            params.put("DrplstRestaurantBasis1\$DrplstControl", "4d05282b-b96f-4a3f-ba54-fc218266a524")
            try {
                for (entry in params.entrySet()) {
                    stringBuffer
                            .append(URLEncoder.encode(entry.getKey(), encode))
                            .append("=")
                            .append(URLEncoder.encode(entry.getValue(), encode))
                            .append("&")
                }
                stringBuffer.append(URLEncoder.encode("__VIEWSTATE", encode)).append("=").append(URLEncoder.encode(view, encode))
            } catch (e: Exception) {
                return false
            }

            temp = sendHttpRequest(url, stringBuffer)
            try {
                val doc = Jsoup.connect(url).get()
                view = doc.select("input#__VIEWSTATE").first().attr("value")
                gen = doc.select("input#__VIEWSTATEGENERATOR").first().attr("value")
                event = doc.select("input#__EVENTVALIDATION").first().attr("value")
            } catch (e: Exception) {
                return false
            }

        }
        return true
    }

    fun makemap(): StringBuffer? {
        val encode = "utf-8"
        val stringBuffer = StringBuffer()
        val params = HashMap<String, String>()
        params.put("__CALLBACKID", "__Page")
        var temp = ""
        var bd = ""
        var i = 0
        var j = 0
        i = 0
        while (i < 3) {
            if (sp!!.getBoolean((i.toString() + "").toString(), false)) {
                params.put("Repeater1\$ctl0$i\$CbkMealtimes", "on")
                j = 0
                while (j < 9) {
                    temp = sp!!.getString("Repeater1_GvReport_" + i + "_TxtNum_" + j + "@", "")
                    if (!temp.equals(""))
                        bd = bd + "Repeater1_GvReport_" + i + "_TxtNum_" + j + "@0|"
                    j++
                }
            } else {
                j = 0
                while (j < 9) {
                    temp = sp!!.getString("Repeater1_GvReport_" + i + "_TxtNum_" + j + "@", "")
                    if (!temp.equals(""))
                        bd = bd + "Repeater1_GvReport_" + i + "_TxtNum_" + j + "@" + temp
                    j++
                }
            }
            i++
        }
        params.put("__CALLBACKPARAM", bd)
        params.put("__EVENTARGUMENT", "")
        params.put("__EVENTTARGET", "")
        params.put("__EVENTVALIDATION", event)
        params.put("__LASTFOCUS", "")
        params.put("__VIEWSTATEENCRYPTED", "")
        params.put("__VIEWSTATEGENERATOR", gen)
        params.put("DrplstRestaurantBasis1\$DrplstControl", "4d05282b-b96f-4a3f-ba54-fc218266a524")
        try {
            for (entry in params.entrySet()) {
                stringBuffer
                        .append(URLEncoder.encode(entry.getKey(), encode))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&")
            }
            stringBuffer.append(URLEncoder.encode("__VIEWSTATE", encode)).append("=").append(URLEncoder.encode(view, encode))
        } catch (e: Exception) {
            return null
        }

        return stringBuffer
    }

    fun sendHttpRequest(url: String, upload: StringBuffer?): String {
        var ret = ""
        val buffer = StringBuffer()
        val data = upload!!.toString().getBytes()
        try {
            val con = URL(url).openConnection() as HttpURLConnection
            con.setRequestMethod("POST")
            con.setDoInput(true)
            con.setDoOutput(true)
            con.setConnectTimeout(5000)
            con.connect()
            con.getOutputStream().write(data)
            val `is` = con.getInputStream()
            val b = ByteArray(1024)
            while (`is`.read(b) !== -1)
                buffer.append(String(b))
            ret = buffer.toString()
            con.disconnect()
        } catch (e: Exception) {
            ret = ""
        }

        return ret
    }
}
