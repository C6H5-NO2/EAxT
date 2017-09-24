package cn.szsy.ordermeal;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import android.view.inputmethod.*;
import android.content.SharedPreferences.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
public class MainActivity extends Activity
{
	@Override
	Button submit;
	EditText name, pass;
	SharedPreferences sp;
	SharedPreferences.Editor editor;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		submit = (Button) this.findViewById(R.id.sign_in);
		name = (EditText) this.findViewById(R.id.xuehao);
		pass = (EditText) this.findViewById(R.id.mima);
		sp = getSharedPreferences("code",MODE_WORLD_READABLE);
		editor = sp.edit();
		name.setText(sp.getString("name","").toString());
		pass.setText(sp.getString("pass","").toString());
		submit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);			
				editor.putString("name",name.getText().toString());
				editor.commit();
				new login().execute();
			}
		});
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(1, 1, 1, "关于").setIcon(android.R.drawable.ic_menu_info_details);
		//menu.add(1, 2, 2, "设置").setIcon(android.R.drawable.ic_menu_agenda);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		switch(id)
		{
			case 1:
				((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				AlertDialog.Builder build = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
				build.setTitle("Developer: C6H5-NO2");
				build.create().show();
				break;
		}
		return false;
	}
	class login extends AsyncTask<Void, Void, Integer>
	{
		String respond = "";
		ProgressDialog window = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
		@Override
		protected void onPreExecute()
		{
			window.setCancelable(false);
			window.setProgress(ProgressDialog.STYLE_SPINNER);
			window.setMessage("       正在登录");
			window.show();
		}
		@Override
		protected Integer doInBackground(Void[] p1)
		{
			CookieManager cookie = new CookieManager();
			CookieHandler.setDefault(cookie);
			Document doc;
			try
			{ doc = Jsoup.connect("http://passport-yun.szsy.cn/login?service=http://gzb.szsy.cn/card/Default.aspx").timeout(3000).get();}
			catch (IOException e)
			{ return 0;}
			/*String html = getHTML("http://passport-yun.szsy.cn/login?service=http://gzb.szsy.cn/card/Default.aspx");
			 if (html == "")
			 return 0;
			 Document doc = Jsoup.parse(html);*/
			respond = post("http://passport-yun.szsy.cn/login?service=http://gzb.szsy.cn/card/Default.aspx",
					       	doc.select("input[name=execution]").first().attr("value"),
					       	doc.select("input[name=lt]").first().attr("value"),
					       	pass.getText().toString(),
				    	   	name.getText().toString());
			CookieStore cs = cookie.getCookieStore();
			if (respond.indexOf("<!DOCTYPE>") != -1)
				return 0;
			return 1;
		}
		@Override
		protected void onPostExecute(Integer flag)
		{
			window.hide();
			if (flag==1)
			{
				editor.putString("pass",pass.getText().toString());
				editor.commit();
				Intent loginjump = new Intent();
				loginjump.putExtra("out", respond);
				loginjump.putExtra("id", name.getText().toString());
				loginjump.setClass(MainActivity.this, dc.class);
				startActivity(loginjump);
				overridePendingTransition(R.anim.push_bottom_out,0);
			}
			else
			{
				editor.putString("pass","");
				editor.commit();
				Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
			}
		}
	}
	/*Bug inside!
	public String getHTML(String url)
	{
		String html = "";
		StringBuffer buffer = new StringBuffer();
		try
		{
			HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
			connection.setDoInput(true);
			connection.setConnectTimeout(3000);
			connection.connect();
			InputStream stream = connection.getInputStream();
			byte[] b = new byte[4096]; //bug here
			while (stream.read(b) != -1)
				buffer.append(new String(b));
			html = buffer.toString();
			connection.disconnect();
		}
		catch (Exception e)
		{
			html = "";
		}
		return html;
	}*/
	public String post(String url, String execution, String lt, String password, String username)
	{
		Map<String, String> params = new HashMap<String, String>(5);
		params.put("_eventId", "submit");
		params.put("execution", execution);
		params.put("lt", lt);
		params.put("password", password);
		params.put("username", username);
		String data = sendHttpRequest(url, params, "utf-8");
		//String data = encapsulate(params, "utf-8").toString();
		return data;
	}
	public String sendHttpRequest(String url, Map<String, String> params, String encode)
	{
		String html = "";
		StringBuffer buffer = new StringBuffer();
		byte[] data = encapsulate(params, encode).toString().getBytes();
		try
		{
			HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setConnectTimeout(3000);
			connection.connect();
			connection.getOutputStream().write(data);
			InputStream stream = connection.getInputStream();
			byte[] b = new byte[4096];
			while (stream.read(b) != -1)
				buffer.append(new String(b));
			html = buffer.toString();
			connection.disconnect();
		}
		catch (Exception e) 
		{
			html = "";
		}
		return html;
	}
	public StringBuffer encapsulate(Map<String, String> params, String encode)
	{
		StringBuffer buffer = new StringBuffer();
		try
		{
			for (Map.Entry<String, String> entry : params.entrySet())
			{
				buffer
					.append(entry.getKey())
					.append("=")
					.append(URLEncoder.encode(entry.getValue(), encode))
					.append("&");
			}
			buffer.deleteCharAt(buffer.length() - 1);
		}
		catch (Exception e)
		{
			return null;
		}
		return buffer;
	}
}

