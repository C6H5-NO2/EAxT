package org.szesmaker.ordermeal

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import android.view.View.*
import android.widget.*
import android.widget.RadioGroup.*
import java.util.*
import android.view.View.OnClickListener

class AllowedList : Activity() {
    @Override
    private var ordered: CheckBox? = null
    private var list: ListView? = null
    private var meal_num = -1
    private var order_num = -1
    private var sp: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null
    @Override
    private var exittime: Long = -2001

    fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_holder)
        ordered = this.findViewById(R.id.ordered) as CheckBox
        list = this.findViewById(R.id.list) as ListView
        sp = getSharedPreferences("orderlist", MODE_PRIVATE)
        editor = sp!!.edit()
        val remote = getIntent()
        val flag = remote.getIntExtra("flag", -1)
        meal_num = flag - 1
        val order = remote.getIntExtra("ordered", 0)
        var caidan: String? = null
        when (flag) {
            1 -> caidan = remote.getStringExtra("zao")
            2 -> caidan = remote.getStringExtra("wuu")
            3 -> caidan = remote.getStringExtra("wan")
        }
        //Fake hash. To be improved.
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
        val ol = wcd(caidan)
        val adapter = Adpa(this, ol)
        list!!.setAdapter(adapter)
        ordered!!.setOnClickListener(object : OnClickListener() {
            @Override
            fun onClick(v: View) {
                val cb = v as CheckBox
                val check = cb.isChecked()
                editor!!.putBoolean((meal_num.toString() + "").toString(), check)
                editor!!.commit()
                //Toast.makeText(ab_list.this, (meal_num + "").toString() + sp.getBoolean((meal_num + "").toString(), false), Toast.LENGTH_LONG).show();
                //0=breakfast;1=lunch;2=dinner;
            }
        }
        )
    }

    fun wcd(caidan: String?): ArrayList<HashMap<String, Object>> {
        val cd = ArrayList<HashMap<String, Object>>()
        var map: HashMap<String, Object>
        val key = arrayOf("bh", "lb", "cm", "", "", "dj", "zd", "fs", "")
        map = HashMap<String, Object>()
        val str = arrayOf("编号", "类别", "菜名", "", "", "单价", "最大份数", "份数", "")
        for (l in 0..8)
            if (l == 0 || l == 1 || l == 2 || l == 5 || l == 6 || l == 7)
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
                if (t == 1 || t == 2 || t == 5 || t == 6 || t == 7)
                    map.put(key[t], item)
            }
            cd.add(map)
            temp = temp.substring(k + 1)
        }
        return cd
    }

    //Rewrite BaseAdapter
    //To be replaced with TabLayout
    internal inner class Adpa @Override
    constructor(var context: Context, var ol: ArrayList<HashMap<String, Object>>) : BaseAdapter() {

        val count: Int
            @Override
            get() = ol.size()

        @Override
        fun getItem(position: Int): Object? {
            return null
        }

        @Override
        fun getItemId(posotion: Int): Long {
            return 0
        }

        @Override
        fun getView(position: Int, convert: View?, parent: ViewGroup): View {
            var convert = convert
            val viewholder: ViewHolder
            if (convert == null) {
                viewholder = ViewHolder()
                convert = LayoutInflater.from(context).inflate(R.layout.listitem_allowed, null)
                viewholder.bh = convert!!.findViewById(R.id.bh) as TextView
                viewholder.lb = convert!!.findViewById(R.id.lb) as TextView
                viewholder.cm = convert!!.findViewById(R.id.cm) as TextView
                viewholder.dj = convert!!.findViewById(R.id.dj) as TextView
                viewholder.fs = convert!!.findViewById(R.id.fs) as TextView
                viewholder.rg = convert!!.findViewById(R.id.rg) as RadioGroup
                viewholder.rb0 = convert!!.findViewById(R.id.rb0) as RadioButton
                viewholder.rb1 = convert!!.findViewById(R.id.rb1) as RadioButton
                viewholder.rb2 = convert!!.findViewById(R.id.rb2) as RadioButton
                viewholder.rb3 = convert!!.findViewById(R.id.rb3) as RadioButton
                convert!!.setTag(viewholder)
            } else {
                viewholder = convert!!.getTag()
            }
            viewholder.rg!!.setOnCheckedChangeListener(object : OnCheckedChangeListener() {
                @Override
                fun onCheckedChanged(group: RadioGroup, numid: Int) {
                    when (numid) {
                        R.id.rb0 -> order_num = 0
                        R.id.rb1 -> order_num = 1
                        R.id.rb2 -> order_num = 2
                        R.id.rb3 -> order_num = 3
                    }
                    //Keep state
                    var map = HashMap<String, Object>()
                    map = ol.get(position)
                    map.put("fs", (order_num.toString() + "").toString())
                    ol.set(position, map)
                    editor!!.putString("Repeater1_GvReport_" + meal_num + "_TxtNum_" + (position - 1) + "@", order_num.toString() + "|")
                    editor!!.commit()
                }
            })
            viewholder.bh!!.setText(ol.get(position).get("bh").toString())
            viewholder.lb!!.setText(ol.get(position).get("lb").toString())
            viewholder.cm!!.setText(ol.get(position).get("cm").toString())
            viewholder.dj!!.setText(ol.get(position).get("dj").toString())
            viewholder.fs!!.setText(ol.get(position).get("fs").toString())
            if (position == 0) {
                viewholder.fs!!.setVisibility(View.VISIBLE)
                viewholder.rg!!.setVisibility(View.INVISIBLE)
            } else {
                viewholder.fs!!.setVisibility(View.GONE)
                viewholder.rg!!.setVisibility(View.VISIBLE)
                val num = ol.get(position).get("fs") as String
                val top = ol.get(position).get("zd") as String
                when (num) {
                    "0" -> viewholder.rb0!!.setChecked(true)
                    "1" -> viewholder.rb1!!.setChecked(true)
                    "2" -> viewholder.rb2!!.setChecked(true)
                    "3" -> viewholder.rb3!!.setChecked(true)
                }
                when (top) {
                    "0" -> {
                        viewholder.rb0!!.setVisibility(View.VISIBLE)
                        viewholder.rb1!!.setVisibility(View.INVISIBLE)
                        viewholder.rb2!!.setVisibility(View.INVISIBLE)
                        viewholder.rb3!!.setVisibility(View.INVISIBLE)
                    }
                    "1" -> {
                        viewholder.rb0!!.setVisibility(View.VISIBLE)
                        viewholder.rb1!!.setVisibility(View.VISIBLE)
                        viewholder.rb2!!.setVisibility(View.INVISIBLE)
                        viewholder.rb3!!.setVisibility(View.INVISIBLE)
                    }
                    "2" -> {
                        viewholder.rb0!!.setVisibility(View.VISIBLE)
                        viewholder.rb1!!.setVisibility(View.VISIBLE)
                        viewholder.rb2!!.setVisibility(View.VISIBLE)
                        viewholder.rb3!!.setVisibility(View.INVISIBLE)
                    }
                    "3" -> {
                        viewholder.rb0!!.setVisibility(View.VISIBLE)
                        viewholder.rb1!!.setVisibility(View.VISIBLE)
                        viewholder.rb2!!.setVisibility(View.VISIBLE)
                        viewholder.rb3!!.setVisibility(View.VISIBLE)
                    }
                }
            }
            return convert
        }

        internal inner class ViewHolder {
            var bh: TextView? = null
            var lb: TextView? = null
            var cm: TextView? = null
            var dj: TextView? = null
            var fs: TextView? = null
            var rg: RadioGroup? = null
            var rb0: RadioButton? = null
            var rb1: RadioButton? = null
            var rb2: RadioButton? = null
            var rb3: RadioButton? = null
        }
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exittime > 2000) {
                //To be replaced with Snackbar
                Toast.makeText(this, "订单未提交，返回？", Toast.LENGTH_SHORT).show()
                exittime = System.currentTimeMillis()
                return true
            } else {
                editor!!.clear()
                editor!!.commit()
                this.finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
