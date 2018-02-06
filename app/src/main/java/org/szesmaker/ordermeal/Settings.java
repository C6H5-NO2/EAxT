package org.szesmaker.ordermeal;
import android.app.*;
import android.content.*;
import android.os.*;
import android.provider.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.graphics.*;
public class Settings extends Activity
{
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        Switch savePass = (Switch) this.findViewById(R.id.savePass);
        TextView checkupdate = (TextView) this.findViewById(R.id.checkupdate);
        TextView about = (TextView) this.findViewById(R.id.about);
        SharedPreferences sp = getSharedPreferences("code", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();
        this.setTitle("设置");
        savePass.setChecked(sp.getBoolean("savePassword", true));
        savePass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                editor.putBoolean("savePassword", isChecked);
                editor.putString("password", "");
                editor.commit();
            }
        });
        checkupdate.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                //new checkUpdate().execute();
                AlertDialog.Builder build = new AlertDialog.Builder(Settings.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                build.setTitle("检查更新");
                build.setMessage("我不想写了，你们自己去下载罢\nhttps://szesmaker.org/");
                build.setPositiveButton("复制", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface p1, int p2) {
                        ((ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("text","https://szesmaker.org/"));
                    }
                });
                build.create().show();
            }
        });
        about.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                AlertDialog.Builder build = new AlertDialog.Builder(Settings.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
                build.setTitle("Developer: C6H5-NO2");
                build.setMessage("Version: 0.2.3\nLicense: MIT\nFollow us on Github: szes-maker");
                build.create().show();
            }
        });
    }
}
