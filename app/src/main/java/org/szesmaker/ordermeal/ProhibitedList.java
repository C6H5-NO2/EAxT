package org.szesmaker.ordermeal;
import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;
import java.util.*;
public class ProhibitedList extends Activity
{
    private CheckBox ordered;
    private ListView list;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_holder);
        ordered = (CheckBox) this.findViewById(R.id.ordered);
        list = (ListView) this.findViewById(R.id.list);
        Intent remote = getIntent();
        int flag = remote.getIntExtra("flag", -1);
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
        ordered.setClickable(false);
        ArrayList<HashMap<String,Object>> ol = wcd(caidan);
        SimpleAdapter sap = new SimpleAdapter(this, ol, R.layout.listitem_prohibited, new String[] { "bh","lb","cm","dj","dg"}, new int[] {R.id.bh, R.id.lb,R.id.cm,R.id.dj,R.id.fs});
        list.setAdapter(sap);
    }
    public ArrayList<HashMap<String,Object>> wcd(String caidan)
    {
        ArrayList<HashMap<String,Object>> cd = new ArrayList<HashMap<String,Object>>();
        HashMap<String,Object> map;
        String key[]={"bh","lb","cm","","","dj","zd","dg",""};
        map = new HashMap<String,Object>();
        String str[]={"编号","类别","菜名","","","单价","最大份数","份数",""};
        for (int l = 0;l < 9;l++)
            if (l == 0 || l == 1 || l == 2 || l == 5 || l == 7)
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
                if (t == 1 || t == 2 || t == 5 || t == 7)
                    map.put(key[t], item);
            }
            cd.add(map);
            temp = temp.substring(k + 1);
        }
        return cd;
    }
}
