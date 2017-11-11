package org.szesmaker.ordermeal;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.RadioGroup.*;
import java.util.*;
import android.view.View.OnClickListener;
public class AllowedList extends Activity
{
    @Override
    private CheckBox ordered;
    private ListView list;
    private int meal_num = -1, order_num = -1;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_holder);
        ordered = (CheckBox) this.findViewById(R.id.ordered);
        list = (ListView) this.findViewById(R.id.list);
        sp = getSharedPreferences("orderlist", MODE_PRIVATE);
        editor = sp.edit();
        Intent remote = getIntent();
        int flag = remote.getIntExtra("flag", -1);
        meal_num = flag - 1;
        int order = remote.getIntExtra("ordered", 0);
        String caidan = null;
        switch (flag)
        {
            case 1:
                caidan = remote.getStringExtra("zao");
                break;
            case 2:
                caidan = remote.getStringExtra("wuu");
                break;
            case 3:
                caidan = remote.getStringExtra("wan");
                break;
        }
        //Fake hash. To be improved.
        switch (order)
        {
            case 0:
                ordered.setChecked(false);
                break;
            case 2:
                if (flag == 1)
                    ordered.setChecked(true);
                else
                    ordered.setChecked(false);
                break;
            case 4:
                if (flag == 2)
                    ordered.setChecked(true);
                else
                    ordered.setChecked(false);
                break;
            case 6:
                if (flag == 1 || flag == 2)
                    ordered.setChecked(true);
                else
                    ordered.setChecked(false);
                break;
            case 8:
                if (flag == 3)
                    ordered.setChecked(true);
                else
                    ordered.setChecked(false);
                break;
            case 10:
                if (flag == 1 || flag == 3)
                    ordered.setChecked(true);
                else
                    ordered.setChecked(false);
                break;
            case 12:
                if (flag == 2 || flag == 3)
                    ordered.setChecked(true);
                else
                    ordered.setChecked(false);
                break;
            case 14:
                ordered.setChecked(true);
                break;
        }
        ArrayList<HashMap<String,Object>> ol = wcd(caidan);
        Adpa adapter = new Adpa(this, ol);
        list.setAdapter(adapter);
        ordered.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    CheckBox cb = (CheckBox) v;
                    boolean check = cb.isChecked();
                    editor.putBoolean((meal_num + "").toString(), check);
                    editor.commit();
                    //Toast.makeText(ab_list.this, (meal_num + "").toString() + sp.getBoolean((meal_num + "").toString(), false), Toast.LENGTH_LONG).show();
                    //0=breakfast;1=lunch;2=dinner;
                }
            }
        );
    }
    public ArrayList<HashMap<String,Object>> wcd(String caidan)
    {
        ArrayList<HashMap<String,Object>> cd = new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> map;
        String key[]={"bh","lb","cm","","","dj","zd","fs",""};
        map = new HashMap<String,Object>();
        String str[]={"编号","类别","菜名","","","单价","最大份数","份数",""};
        for (int l = 0;l < 9;l++)
            if (l == 0 || l == 1 || l == 2 || l == 5 || l == 6 || l == 7)
                map.put(key[l], str[l]);
        cd.add(map);
        String temp = caidan.substring(caidan.indexOf("0"));
        for (int i = 0;i <= 9;i++)
        {
            if (!temp.substring(0, 1).equals(i + ""))
                continue;
            map = new HashMap<String,Object>();
            map.put(key[0], i);
            int j = 0,k = 0;
            for (int t=1;t <= 8;t++)
            {
                j = temp.indexOf(" ", k);
                k = temp.indexOf(" ", j + 2);
                String item = temp.substring(j + 1, k);
                if (t == 1 || t == 2 || t == 5 || t == 6 || t == 7)
                    map.put(key[t], item);
            }
            cd.add(map);
            temp = temp.substring(k + 1);
        }
        return cd;
    }
    //Rewrite BaseAdapter
    //To be replaced with TabLayout
    class Adpa extends BaseAdapter
    {
        Context context;
        ArrayList<HashMap<String,Object>> ol;

        @Override
        public Adpa(Context context, ArrayList<HashMap<String,Object>> ol)
        {
            this.context = context;
            this.ol = ol;   
        }

        @Override
        public int getCount()
        {
            return ol.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int posotion)
        {
            return 0;
        }

        @Override
        public View getView(int position, View convert, ViewGroup parent)
        {
            final int dish_num = position;
            ViewHolder viewholder;
            if (convert == null)
            {
                viewholder = new ViewHolder();
                convert = LayoutInflater.from(context).inflate(R.layout.listitem_allowed, null);
                viewholder.bh = (TextView) convert.findViewById(R.id.bh);
                viewholder.lb = (TextView) convert.findViewById(R.id.lb);
                viewholder.cm = (TextView) convert.findViewById(R.id.cm);
                viewholder.dj = (TextView) convert.findViewById(R.id.dj);
                viewholder.fs = (TextView) convert.findViewById(R.id.fs);
                viewholder.rg = (RadioGroup) convert.findViewById(R.id.rg);
                viewholder.rb0 = (RadioButton) convert.findViewById(R.id.rb0);
                viewholder.rb1 = (RadioButton) convert.findViewById(R.id.rb1);
                viewholder.rb2 = (RadioButton) convert.findViewById(R.id.rb2);
                viewholder.rb3 = (RadioButton) convert.findViewById(R.id.rb3);
                convert.setTag(viewholder);
            }
            else
            {
                viewholder = (ViewHolder) convert.getTag();
            }
            viewholder.rg.setOnCheckedChangeListener(new OnCheckedChangeListener(){
                    @Override
                    public void onCheckedChanged(RadioGroup group, int numid)
                    {
                        switch (numid)
                        {
                            case R.id.rb0:
                                order_num = 0;
                                break;
                            case R.id.rb1:
                                order_num = 1;
                                break;
                            case R.id.rb2:
                                order_num = 2;
                                break;
                            case R.id.rb3:
                                order_num = 3;
                                break;
                        }
                        //Keep state
                        HashMap<String,Object> map = new HashMap<String,Object>();
                        map = ol.get(dish_num);
                        map.put("fs", (order_num + "").toString());
                        ol.set(dish_num, map);
                        editor.putString("Repeater1_GvReport_" + meal_num + "_TxtNum_" + (dish_num - 1) + "@", order_num + "|");
                        editor.commit();
                    }
                });
            viewholder.bh.setText(ol.get(position).get("bh").toString());
            viewholder.lb.setText(ol.get(position).get("lb").toString());
            viewholder.cm.setText(ol.get(position).get("cm").toString());
            viewholder.dj.setText(ol.get(position).get("dj").toString());
            viewholder.fs.setText(ol.get(position).get("fs").toString());
            if (position == 0)
            {
                viewholder.fs.setVisibility(View.VISIBLE);
                viewholder.rg.setVisibility(View.INVISIBLE);
            }
            else
            {
                viewholder.fs.setVisibility(View.GONE);
                viewholder.rg.setVisibility(View.VISIBLE);
                String num = (String) ol.get(position).get("fs");
                String top = (String) ol.get(position).get("zd");
                switch (num)
                {
                    case "0":
                        viewholder.rb0.setChecked(true);
                        break;
                    case "1":
                        viewholder.rb1.setChecked(true);
                        break;
                    case "2":
                        viewholder.rb2.setChecked(true);
                        break;
                    case "3":
                        viewholder.rb3.setChecked(true);
                        break;
                }
                switch (top)
                {
                    case "0":
                        viewholder.rb0.setVisibility(View.VISIBLE);
                        viewholder.rb1.setVisibility(View.INVISIBLE);
                        viewholder.rb2.setVisibility(View.INVISIBLE);
                        viewholder.rb3.setVisibility(View.INVISIBLE);
                        break;
                    case "1":
                        viewholder.rb0.setVisibility(View.VISIBLE);
                        viewholder.rb1.setVisibility(View.VISIBLE);
                        viewholder.rb2.setVisibility(View.INVISIBLE);
                        viewholder.rb3.setVisibility(View.INVISIBLE);
                        break;
                    case "2":
                        viewholder.rb0.setVisibility(View.VISIBLE);
                        viewholder.rb1.setVisibility(View.VISIBLE);
                        viewholder.rb2.setVisibility(View.VISIBLE);
                        viewholder.rb3.setVisibility(View.INVISIBLE);
                        break;
                    case "3":
                        viewholder.rb0.setVisibility(View.VISIBLE);
                        viewholder.rb1.setVisibility(View.VISIBLE);
                        viewholder.rb2.setVisibility(View.VISIBLE);
                        viewholder.rb3.setVisibility(View.VISIBLE);
                        break;
                }
            }
            return convert;
        }
        class ViewHolder
        {
            TextView bh,lb,cm,dj,fs;
            RadioGroup rg;
            RadioButton rb0,rb1,rb2,rb3;
        }
    }
    @Override
    private long exittime = -2001;
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            if (System.currentTimeMillis() - exittime > 2000)
            {
                //To be replaced with Snackbar
                Toast.makeText(this, "订单未提交，返回？", Toast.LENGTH_SHORT).show();
                exittime = System.currentTimeMillis();
                return true;
            }
            else
            {
                editor.clear();
                editor.commit();
                this.finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
