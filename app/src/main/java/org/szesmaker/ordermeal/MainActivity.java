package org.szesmaker.ordermeal;
import android.app.*;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.view.inputmethod.*;
import android.widget.*;
import java.io.*;
import java.math.*;
import java.net.*;
import java.security.*;
import java.util.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
public class MainActivity extends Activity {
    private Button submit;
    private EditText un,pw;
    private ImageView szsy;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        submit = (Button) this.findViewById(R.id.sign_in);
        un = (EditText) this.findViewById(R.id.username);
        pw = (EditText) this.findViewById(R.id.password);
        szsy = (ImageView) this.findViewById(R.id.szsy);
        sp = getSharedPreferences("code", MODE_PRIVATE);
        editor = sp.edit();
        un.setText(sp.getString("username", "").toString());
        if (sp.getBoolean("savePassword", true))
            pw.setText(sp.getString("password", "").toString());
        szsy.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Settings.class);
                startActivity(intent);
            }
        });
        submit.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
                editor.putString("username", un.getText().toString());
                editor.commit();
                new Login().execute();
            }
        });
    }
    private class Login extends AsyncTask<Void, Void, Integer> {
        String respond = "";
        ProgressDialog window = new ProgressDialog(MainActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        @Override protected void onPreExecute() {
            window.setCancelable(false);
            window.setProgress(ProgressDialog.STYLE_SPINNER);
            window.setMessage("       正在登录");
            window.show();
        }
        @Override protected Integer doInBackground(Void[] p1) {
            String password = encodeMD5(pw.getText().toString());
            if (password.equals(""))
                return 0;
            CookieManager cm = new CookieManager();
            CookieHandler.setDefault(cm);
            Document doc;
            try {
                doc = Jsoup.connect("http://passport-yun.szsy.cn/login?service=http://gzb.szsy.cn/card/Default.aspx").followRedirects(true).timeout(5000).get();
            }
            catch (IOException e) {
                return 0;
            }
            respond = post("http://passport-yun.szsy.cn/login?service=http://gzb.szsy.cn/card/Default.aspx",
                           doc.select("input[name=execution]").first().attr("value"),
                           doc.select("input[name=lt]").first().attr("value"),
                           password,
                           un.getText().toString());
            CookieStore cs = cm.getCookieStore();
            if (respond.indexOf("深圳实验学校一卡通管理系统") != -1)
                return 1;
            return 0;
        }
        @Override protected void onPostExecute(Integer flag) {
            window.hide();
            if (flag == 1) {
                editor.putString("password", pw.getText().toString());
                editor.commit();
                Intent intent = new Intent();
                intent.putExtra("httpRespond", respond);
                intent.putExtra("cardID", un.getText().toString());
                intent.setClass(MainActivity.this, Order.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_out_bottom, 0);
            }
            else {
                editor.putString("password", "");
                editor.commit();
                Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private String post(String url, String execution, String lt, String password, String username) {
        Map<String, String> params = new HashMap<String, String>(8);
        params.put("_eventId", "submit");
        params.put("captcha", "null");
        params.put("code", "");
        params.put("execution", execution);
        params.put("lt", lt);
        params.put("password", password);
        params.put("phone", "");
        params.put("username", username);
        String data = sendHttpRequest(url, params, "utf-8");
        return data;
    }
    private String sendHttpRequest(String url, Map<String, String> params, String encode) {
        String html = "";
        StringBuffer buffer = new StringBuffer();
        byte[] data = encapsulate(params, encode).toString().getBytes();
        try {
            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.connect();
            connection.getOutputStream().write(data);
            InputStream stream = connection.getInputStream();
            byte[] b = new byte[4096];
            while (stream.read(b) != -1)
                buffer.append(new String(b));
            html = buffer.toString();
            connection.disconnect();
        }
        catch (Exception e) {
            html = "";
        }
        return html;
    }
    private StringBuffer encapsulate(Map<String, String> params, String encode) {
        StringBuffer buffer = new StringBuffer();
        try {
            for (Map.Entry<String, String> entry : params.entrySet())
                buffer
                    .append(entry.getKey())
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), encode))
                    .append("&");
            buffer.deleteCharAt(buffer.length() - 1);
        }
        catch (Exception e) {
            return null;
        }
        return buffer;
    }
    private String encodeMD5(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] b = md.digest(s.getBytes("UTF-8"));
            s = new BigInteger(1, b).toString(16);
            return s;
        }
        catch (Exception e) {
            return "";
        }
    }
}
