package org.szesmaker.ordermeal

import android.app.*
import android.content.*
import android.os.*
import android.widget.*
import java.util.*

class ProhibitedList : Activity() {
    private var ordered: CheckBox? = null
    private var list: ListView? = null
    @Override
    fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_holder)
        ordered = this.findViewById(R.id.ordered) as CheckBox
        list = this.findViewById(R.id.list) as ListView
        val remote = getIntent()
        val flag = remote.getIntExtra("flag", -1)
        val order = remote.getIntExtra("ordered", 0)
        var caidan: String? = null
        when (flag) {
            1 -> caidan = remote.getStringExtra("zao")
            2 -> caidan = remote.getStringExtra("wuu")
            3 -> caidan = remote.getStringExtra("wan")
        }
        when (order) {
            0 -> ordered!!.setChecked(false)
            2 -> if (flag == 1)
                ordered!!.setChecked(true)
            else
                ordered!!.setChecked(false)
            4 -> if (flag == 2)
                ordered!!.setChecked(true)
            else
                ordered!!.setChecked(false)
            6 -> if (flag == 1 || flag == 2)
                ordered!!.setChecked(true)
            else
                ordered!!.setChecked(false)
            8 -> if (flag == 3)
                ordered!!.setChecked(true)
            else
                ordered!!.setChecked(false)
            10 -> if (flag == 1 || flag == 3)
                ordered!!.setChecked(true)
            else
                ordered!!.setChecked(false)
            12 -> if (flag == 2 || flag == 3)
                ordered!!.setChecked(true)
            else
                ordered!!.setChecked(false)
            14 -> ordered!!.setChecked(true)
        }
        ordered!!.setClickable(false)
        val ol = wcd(caidan)
        val sap = SimpleAdapter(this, ol, R.layout.listitem_prohibited, arrayOf("bh", "lb", "cm", "dj", "dg"), intArrayOf(R.id.bh, R.id.lb, R.id.cm, R.id.dj, R.id.fs))
        list!!.setAdapter(sap)
    }

    fun wcd(caidan: String?): ArrayList<HashMap<String, Object>> {
        val cd = ArrayList<HashMap<String, Object>>()
        var map: HashMap<String, Object>
        val key = arrayOf("bh", "lb", "cm", "", "", "dj", "zd", "dg", "")
        map = HashMap<String, Object>()
        val str = arrayOf("编号", "类别", "菜名", "", "", "单价", "最大份数", "份数", "")
        for (l in 0..8)
            if (l == 0 || l == 1 || l == 2 || l == 5 || l == 7)
                map.put(key[l], str[l])
        cd.add(map)
        var temp = caidan!!.substring(caidan.indexOf("0"))
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
                if (t == 1 || t == 2 || t == 5 || t == 7)
                    map.put(key[t], item)
            }
            cd.add(map)
            temp = temp.substring(k + 1)
        }
        return cd
    }
}
