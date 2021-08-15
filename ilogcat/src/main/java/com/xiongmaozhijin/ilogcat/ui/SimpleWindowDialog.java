package com.xiongmaozhijin.ilogcat.ui;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.xiongmaozhijin.ilogcat.R;

public class SimpleWindowDialog implements IWindowDialog {

    private View layoutView;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;
    private boolean hadCreate = false;
    private final Context context;

    public SimpleWindowDialog(Context context) {
        this.context = context;
    }

    private void create(Context context, int layoutId) {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.format = PixelFormat.RGBA_8888; //悬浮框背景颜色
        layoutParams.gravity = Gravity.START | Gravity.TOP;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT; //悬浮框宽度
        // layoutParams.height = activity.getResources().getDisplayMetrics().heightPixels / 4 * 3;//悬浮框长度
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT; //悬浮框长度
        // layoutParams.x = windowManager.getDefaultDisplay().getWidth(); //初始显示位置
        layoutParams.y = 100; //初始显示位置
        layoutView = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.layout_logcat_wrapper, null);
        layoutView.setBackgroundColor(context.getResources().getColor(R.color.white));
        // layoutView.setAlpha((float) 0.9);
        hadCreate = true;
    }

    @Override
    public void show() {
        if (!hadCreate) {
            create(context, R.layout.layout_logcat_wrapper);
        }
        if (layoutView.getParent() == null) windowManager.addView(layoutView, layoutParams);
    }

    @Override
    public void hide() {
        if (layoutView.getParent() != null) windowManager.removeView(layoutView);
    }

}
