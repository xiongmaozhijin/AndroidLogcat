package com.xiongmaozhijin.androidlogcat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.xiongmaozhijin.logcatlibrary.logcat.AndroidLogcatManager;
import com.xiongmaozhijin.logcatlibrary.logcat.LocalStorageIsAction;

import java.util.Random;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        AndroidLogcatManager.getsInstance().init(getApplicationContext());
        AndroidLogcatManager.getsInstance().addInputStreamAction(new LocalStorageIsAction());
        AndroidLogcatManager.getsInstance().showMonitor(this);
        log("Main");

        findViewById(R.id.btnSecondActivity).setOnClickListener(v -> {
            Intent intent = new Intent(this, SecondActivity.class);
            startActivity(intent);
        });
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