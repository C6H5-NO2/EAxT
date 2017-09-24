package cn.szsy.ordermeal;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import java.util.*;

public class unable extends TabActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		String date,zao,wuu,wan;
		int ordered = 0;
		Intent dca = getIntent();
		zao = dca.getStringExtra("zao");
		wuu = dca.getStringExtra("wuu");
		wan = dca.getStringExtra("wan");
		date = dca.getStringExtra("date");
		ordered = dca.getIntExtra("ordered",0);
		this.setTitle(date.substring(0,4)+"年"+date.substring(5,7)+"月"+date.substring(8)+"日菜单");
		TabHost tab = getTabHost();
		TabHost.TabSpec tab1 = tab.newTabSpec("tab1");
		TabHost.TabSpec tab2 = tab.newTabSpec("tab2");
		TabHost.TabSpec tab3 = tab.newTabSpec("tab3");
		Intent remote;
		if (!zao.equals(""))
		{
			remote = new Intent();
			remote.setClass(this,un_list.class);
			remote.putExtra("flag",1);
			remote.putExtra("zao",zao);
			remote.putExtra("ordered",ordered);
			tab1.setContent(remote);
			tab1.setIndicator("早餐菜单");
			tab.addTab(tab1);
		}
		if (!wuu.equals(""))
		{
			remote = new Intent();
			remote.setClass(this,un_list.class);
			remote.putExtra("flag",2);
			remote.putExtra("wuu",wuu);
			remote.putExtra("ordered",ordered);
			tab2.setContent(remote);
			tab2.setIndicator("午餐菜单");
			tab.addTab(tab2);
		}
		if (!wan.equals(""))
		{
			remote = new Intent();
			remote.setClass(this,un_list.class);
			remote.putExtra("flag",3);
			remote.putExtra("wan",wan);
			remote.putExtra("ordered",ordered);
			tab3.setContent(remote);
			tab3.setIndicator("晚餐菜单");
			tab.addTab(tab3);
		}
	}
	
}

