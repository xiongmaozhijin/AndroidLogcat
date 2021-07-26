package com.xiongmaozhijin.androidlogcat;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xiongmaozhijin.logcatlibrary.logcat.AndroidLogcatManager;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(getApplicationContext(), "申请权限", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }
        }


        log("Main");

        findViewById(R.id.btnSecondActivity).setOnClickListener(v -> {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.btnSecondActivity).postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    AndroidLogcatManager.getsInstance().init(getApplicationContext());
                    AndroidLogcatManager.getsInstance().showMonitor(this);
                }
            }
        }, 2_000L);
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("---------->");
    }

    private void log(String str) {
        Log.v("HelloTag", str + "lalalala：" + new Random().nextInt());
        Log.d("HelloTag", str + "lalalala：" + new Random().nextInt());
        Log.i("HelloTag", str + "lalalala：" + new Random().nextInt());
        Log.w("HelloTag", str + "lalalala：" + new Random().nextInt());
        Log.e("HelloTag", str + "lalalala：" + new Random().nextInt());

    }
}