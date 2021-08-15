package com.xiongmaozhijin.androidlogcat;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.xiongmaozhijin.ilogcat.ILogcatManager;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ILogcatManager.getsInstance().init(getApplicationContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(getApplicationContext(), "申请权限", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
            }
        }


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        log("Main");

        findViewById(R.id.btnSecondActivity).setOnClickListener(v -> {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        });


        findViewById(R.id.btnSecondActivity).postDelayed(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                }
            }
        }, 2_000L);
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("---------->");
    }

    public void btnShowLogWindow(View view) {
        Log.d(TAG, "btnShowLogWindow()");
        ILogcatManager.getsInstance().getLogcatWindow().show();
    }

    public void btnLocalStorage(View view) {
        Log.d(TAG, "btnLocalStorage()");
        ILogcatManager.getsInstance().enableLocalStorage();
    }


    public void btnCrash(View view) {
        Log.d(TAG, "btnCrash()");
        throw new RuntimeException("IO error: " + System.currentTimeMillis());
    }

    public void btnAddLog(View view) {
        Log.d(TAG, "btnAddLog()");
        Log.d("SpeedMonitor", "this is log to debug-" + System.currentTimeMillis());
    }

    private void log(String str) {
        Log.v("Okhttp", str + "lalalala：" + new Random().nextInt());
        Log.d("Okhttp", str + "lalalala：" + new Random().nextInt());
        Log.i("EventBus", str + "lalalalaBus：" + new Random().nextInt());
        Log.w("EventBus", str + "lalalalaEBus：" + new Random().nextInt());
        Log.e("MainActivityDebug", str + "lalalala：" + new Random().nextInt());

    }
}