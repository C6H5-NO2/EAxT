package cn.szsy.ordermeal;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.view.*;
import android.content.*;
import java.util.*;
import android.content.SharedPreferences.*;
import android.util.*;
import java.net.*;
import java.io.*;
import org.jsoup.nodes.*;
import org.jsoup.*;
import java.lang.reflect.*;

public class able extends TabActivity
{
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	private String view,gen,event,date;

    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		String zao = "",wuu = "",wan = "";
		int ordered = 0;
		sp = getSharedPreferences("orderlist", MODE_WORLD_READABLE);
		editor = sp.edit();
		Intent dca = getIntent();
		zao = dca.getStringExtra("zao");
		wuu = dca.getStringExtra("wuu");
		wan = dca.getStringExtra("wan");
		date = dca.getStringExtra("date");
		ordered = dca.getIntExtra("ordered", 0);
		view = dca.getStringExtra("view");
		gen = dca.getStringExtra("gen");
		event = dca.getStringExtra("event");
		this.setTitle(date.substring(0, 4) + "年" + date.substring(5, 7) + "月" + date.substring(8) + "日菜单");
		TabHost tab = getTabHost();
		TabHost.TabSpec tab1 = tab.newTabSpec("tab1");
		TabHost.TabSpec tab2 = tab.newTabSpec("tab2");
		TabHost.TabSpec tab3 = tab.newTabSpec("tab3");
		Intent remote;
		if (!zao.equals(""))
		{
			remote = new Intent();
			remote.setClass(this, ab_list.class);
			remote.putExtra("flag", 1);
			remote.putExtra("zao", zao);
			remote.putExtra("ordered", ordered);
			tab1.setContent(remote);
			tab1.setIndicator("早餐菜单");
			tab.addTab(tab1);
			prepare(zao, 0);
		}
		if (!wuu.equals(""))
		{
			remote = new Intent();
			remote.setClass(this, ab_list.class);
			remote.putExtra("flag", 2);
			remote.putExtra("wuu", wuu);
			remote.putExtra("ordered", ordered);
			tab2.setContent(remote);
			tab2.setIndicator("午餐菜单");
			tab.addTab(tab2);
			prepare(wuu, 1);
		}
		if (!wan.equals(""))
		{
			remote = new Intent();
			remote.setClass(this, ab_list.class);
			remote.putExtra("flag", 3);
			remote.putExtra("wan", wan);
			remote.putExtra("ordered", ordered);
			tab3.setContent(remote);
			tab3.setIndicator("晚餐菜单");
			tab.addTab(tab3);
			prepare(wan, 2);
		}
		getOverflowMenu();
	}

	public void prepare(String caidan, int meal_num)
	{
		ArrayList<HashMap<String,Object>> ol = wcd(caidan);
		for (int position = 1;position < ol.size();position++)
			editor.putString("Repeater1_GvReport_" + meal_num + "_TxtNum_" + (position - 1) + "@", ol.get(position).get("fs").toString() + "|");
		editor.commit();
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(1, 1, 1, "提交订单");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (item.getItemId() == 1)
		{
			ordermeal om = new ordermeal();
			om.execute();
		}
		return false;
	}
	
	private void getOverflowMenu()
	{
		try
		{
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null)
			{
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config,false);
			}
		}
		catch(Exception e)
		{
			Toast.makeText(able.this, "不支持该版本", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	class ordermeal extends AsyncTask<Void, Void, Void>
	{
		boolean dd = true;
		String resp = "",url = "";
		ProgressDialog winlod = new ProgressDialog(able.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
		@Override
		protected void onPreExecute()
		{
			winlod.setCancelable(false);
			winlod.setProgress(ProgressDialog.STYLE_SPINNER);
			winlod.setMessage("       正在提交");
			winlod.show();
		}
		@Override
		protected Void doInBackground(Void[] p1)
		{
			try
			{
				url = "http://gzb.szsy.cn/card/Restaurant/RestaurantUserMenu/RestaurantUserMenu.aspx?Date=" + date;
				for (int i=0;i < 3;i++)
				{
					boolean bol=fix(url, i);
					if(!bol)
					{
						dd=false; 
						break;
					}
				}
				if(dd)
					resp = sendHttpRequest(url, makemap());
			}
			catch (Exception e)
			{
				dd = false;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void result)
		{
			winlod.hide();
			if (resp.equals(""))
				dd = false;
			if (dd)
			{
				Toast.makeText(able.this, "提交成功", Toast.LENGTH_SHORT).show();
				editor.clear();
				editor.commit();
				able.this.finish();
			}
			else
				Toast.makeText(able.this, "提交失败", Toast.LENGTH_SHORT).show();
		}
	}

	
	public boolean fix(String url, int meal_num)
	{
		String temp;
		Boolean yun,xin;
		yun = sp.getBoolean(("y" + meal_num).toString(), false);
		xin = sp.getBoolean((meal_num + "").toString(), false);
		int i;
		if (yun == false && xin == true)
		{
			String encode = "utf-8";
			StringBuffer stringBuffer = new StringBuffer();
			Map<String, String> params = new HashMap<String, String>();
			for (i = 0;i < meal_num;i++)
				if (sp.getBoolean((i + "").toString(), false))
					params.put("Repeater1$ctl0" + i + "$CbkMealtimes", "on");
			params.put("__EVENTARGUMENT", "");
			params.put("__EVENTTARGET", "Repeater1$ctl0" + meal_num + "$CbkMealtimes");
			params.put("__EVENTVALIDATION", event);
			params.put("__LASTFOCUS", "");
			params.put("__VIEWSTATEENCRYPTED", "");
			params.put("__VIEWSTATEGENERATOR", gen);
			params.put("DrplstRestaurantBasis1$DrplstControl", "4d05282b-b96f-4a3f-ba54-fc218266a524");
			params.put("Repeater1$ctl0" + meal_num + "$CbkMealtimes", "on");
			try
			{
				for (Map.Entry<String, String> entry : params.entrySet())
				{
					stringBuffer
						.append(URLEncoder.encode(entry.getKey(), encode))
						.append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
				}
				stringBuffer.append(URLEncoder.encode("__VIEWSTATE", encode)).append("=").append(URLEncoder.encode(view, encode));
			}
			catch (Exception e)
			{
				return false;
			}
			temp = sendHttpRequest(url, stringBuffer);
			try
			{
				Document doc = Jsoup.connect(url).get();
				view = doc.select("input#__VIEWSTATE").first().attr("value");
				gen = doc.select("input#__VIEWSTATEGENERATOR").first().attr("value");
				event = doc.select("input#__EVENTVALIDATION").first().attr("value");
			}
			catch(Exception e)
			{
				return false;
			}
		}
		else if (yun == true && xin == false)
		{
			String encode = "utf-8";
			StringBuffer stringBuffer = new StringBuffer();
			Map<String, String> params = new HashMap<String, String>();
			for (i = 0;i < meal_num;i++)
				if (sp.getBoolean((i + "").toString(), false))
					params.put("Repeater1$ctl0" + i + "$CbkMealtimes", "on");
			params.put("__EVENTARGUMENT", "");
			params.put("__EVENTTARGET", "Repeater1$ctl0" + meal_num + "$CbkMealtimes");
			params.put("__EVENTVALIDATION", event);
			params.put("__LASTFOCUS", "");
			params.put("__VIEWSTATEENCRYPTED", "");
			params.put("__VIEWSTATEGENERATOR", gen);
			params.put("DrplstRestaurantBasis1$DrplstControl", "4d05282b-b96f-4a3f-ba54-fc218266a524");
			try
			{
				for (Map.Entry<String, String> entry : params.entrySet())
				{
					stringBuffer
						.append(URLEncoder.encode(entry.getKey(), encode))
						.append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
				}
				stringBuffer.append(URLEncoder.encode("__VIEWSTATE", encode)).append("=").append(URLEncoder.encode(view, encode));
			}
			catch (Exception e)
			{
				return false;
			}
			temp = sendHttpRequest(url, stringBuffer);
			try
			{
				Document doc = Jsoup.connect(url).get();
				view = doc.select("input#__VIEWSTATE").first().attr("value");
				gen = doc.select("input#__VIEWSTATEGENERATOR").first().attr("value");
				event = doc.select("input#__EVENTVALIDATION").first().attr("value");
			}
			catch(Exception e)
			{
				return false;
			}
		}
		return true;
	}

	public StringBuffer makemap()
	{
		String encode = "utf-8";
		StringBuffer stringBuffer = new StringBuffer();
		Map<String, String> params = new HashMap<String, String>();
		params.put("__CALLBACKID", "__Page");
		String temp = "",bd = "";
		int i = 0,j = 0;
		for (i = 0;i < 3;i++)
		{
			if (sp.getBoolean((i + "").toString(), false))	//不订餐
			{
				params.put("Repeater1$ctl0" + i + "$CbkMealtimes", "on");
				for (j = 0;j < 9;j++)
				{
					temp = sp.getString("Repeater1_GvReport_" + i + "_TxtNum_" + j + "@", "");
					if (!temp.equals(""))
						bd = bd + "Repeater1_GvReport_" + i + "_TxtNum_" + j + "@0|";
				}
			}
			else
				for (j = 0;j < 9;j++)
				{
					temp = sp.getString("Repeater1_GvReport_" + i + "_TxtNum_" + j + "@", "");
					if (!temp.equals(""))
						bd = bd + "Repeater1_GvReport_" + i + "_TxtNum_" + j + "@" + temp;
				}
		}
		params.put("__CALLBACKPARAM", bd);
		params.put("__EVENTARGUMENT", "");
		params.put("__EVENTTARGET", "");
		params.put("__EVENTVALIDATION", event);
		params.put("__LASTFOCUS", "");
		params.put("__VIEWSTATEENCRYPTED", "");
		params.put("__VIEWSTATEGENERATOR", gen);
		params.put("DrplstRestaurantBasis1$DrplstControl", "4d05282b-b96f-4a3f-ba54-fc218266a524");
		try
		{
			for (Map.Entry<String, String> entry : params.entrySet())
			{
				stringBuffer
					.append(URLEncoder.encode(entry.getKey(), encode))
					.append("=")
					.append(URLEncoder.encode(entry.getValue(), encode))
					.append("&");
			}
			stringBuffer.append(URLEncoder.encode("__VIEWSTATE", encode)).append("=").append(URLEncoder.encode(view, encode));
		}
		catch (Exception e)
		{
			return null;
		}
		return stringBuffer;
	}

	public String sendHttpRequest(String url, StringBuffer upload)
	{
		String ret = "";
		StringBuffer buffer = new StringBuffer();
		byte[] data = upload.toString().getBytes();
		try
		{
			HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setConnectTimeout(3000);
			con.connect();
			con.getOutputStream().write(data);
			InputStream is = con.getInputStream();
			byte[] b = new byte[1024];
			while (is.read(b) != -1)
				buffer.append(new String(b));
			ret = buffer.toString();
			con.disconnect();
		}
		catch (Exception e) 
		{
			ret = "";
		}
		return ret;
	}

}

