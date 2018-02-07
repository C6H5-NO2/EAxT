package org.szesmaker.ordermeal;
import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.RadioGroup.*;
import java.util.*;
import android.view.View.OnClickListener;
import org.w3c.dom.Text;

public class AllowedList extends Activity
{
    //@Override
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
                convert = LayoutInflater.from(context).inflate(R.layout.listitem, null);
                viewholder.bh = (TextView) convert.findViewById(R.id.bh);
                viewholder.lb = (TextView) convert.findViewById(R.id.lb);
                viewholder.cm = (TextView) convert.findViewById(R.id.cm);
                viewholder.dj = (TextView) convert.findViewById(R.id.dj);
                viewholder.fs = (TextView) convert.findViewById(R.id.fs);
                convert.setTag(viewholder);
            }
            else
            {
                viewholder = (ViewHolder) convert.getTag();
            }

            list.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    if(position==0) return;
                    else
                    {

                        int numCap=ol.get(position).get("zd").toString().charAt(0)-'0';
                        TextView numTv=(TextView) view.findViewById(R.id.fs);
                        int num=numTv.getText().charAt(0)-'0';
                        num=(num+1)%(numCap+1);
                        numTv.setText(""+num);

                        //Keep state in ol updated
                        HashMap<String,Object> map = new HashMap<String,Object>();
                        map = ol.get(position);
                        map.put("fs", Integer.toString(num));
                        ol.set(position, map);
                        editor.putString("Repeater1_GvReport_" + meal_num + "_TxtNum_" + (position - 1) + "@", num + "|");
                        editor.commit();
                        return;
                    }
                }
            });

            viewholder.bh.setText(ol.get(position).get("bh").toString());
            viewholder.lb.setText(ol.get(position).get("lb").toString());
            viewholder.cm.setText(ol.get(position).get("cm").toString());
            viewholder.dj.setText(ol.get(position).get("dj").toString());
            viewholder.fs.setText(ol.get(position).get("fs").toString());

            viewholder.fs.setVisibility(View.VISIBLE);
            String num = ol.get(position).get("fs").toString();
            String top = ol.get(position).get("zd").toString();
            viewholder.fs.setText(num);
            viewholder.fs.setTextColor(0xFF4E6CEF);

            return convert;
        }
        class ViewHolder
        {
            TextView bh,lb,cm,dj,fs;
        }
    }
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
