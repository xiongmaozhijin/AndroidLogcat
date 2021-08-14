package com.xiongmaozhijin.ilogcat.ui;

import android.content.Context;

import androidx.annotation.LayoutRes;

public interface IWindowDialog {
    void create(Context context, @LayoutRes int layoutId);

    void show();

    void hide();
}
