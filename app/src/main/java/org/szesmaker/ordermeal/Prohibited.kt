package org.szesmaker.ordermeal

import android.app.*
import android.content.*
import android.os.*
import android.widget.*

class Prohibited : TabActivity() {
    @Override
    fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        val date: String
        val zao: String
        val wuu: String
        val wan: String
        var ordered = 0
        val dca = getIntent()
        zao = dca.getStringExtra("zao")
        wuu = dca.getStringExtra("wuu")
        wan = dca.getStringExtra("wan")
        date = dca.getStringExtra("date")
        ordered = dca.getIntExtra("ordered", 0)
        this.setTitle(date.substring(0, 4) + "年" + date.substring(5, 7) + "月" + date.substring(8) + "日菜单")
        val tab = getTabHost()
        val tab1 = tab.newTabSpec("tab1")
        val tab2 = tab.newTabSpec("tab2")
        val tab3 = tab.newTabSpec("tab3")
        var remote: Intent
        if (!zao.equals("")) {
            remote = Intent()
            remote.setClass(this, ProhibitedList::class.java)
            remote.putExtra("flag", 1)
            remote.putExtra("zao", zao)
            remote.putExtra("ordered", ordered)
            tab1.setContent(remote)
            tab1.setIndicator("早餐菜单")
            tab.addTab(tab1)
        }
        if (!wuu.equals("")) {
            remote = Intent()
            remote.setClass(this, ProhibitedList::class.java)
            remote.putExtra("flag", 2)
            remote.putExtra("wuu", wuu)
            remote.putExtra("ordered", ordered)
            tab2.setContent(remote)
            tab2.setIndicator("午餐菜单")
            tab.addTab(tab2)
        }
        if (!wan.equals("")) {
            remote = Intent()
            remote.setClass(this, ProhibitedList::class.java)
            remote.putExtra("flag", 3)
            remote.putExtra("wan", wan)
            remote.putExtra("ordered", ordered)
            tab3.setContent(remote)
            tab3.setIndicator("晚餐菜单")
            tab.addTab(tab3)
        }
    }
}
