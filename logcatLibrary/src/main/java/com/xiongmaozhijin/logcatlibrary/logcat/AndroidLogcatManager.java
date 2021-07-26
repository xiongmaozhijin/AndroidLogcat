package com.xiongmaozhijin.logcatlibrary.logcat;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.xiongmaozhijin.logcatlibrary.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AndroidLogcatManager {

    private final static AndroidLogcatManager sInstance = new AndroidLogcatManager();

    private Context context;
    private final boolean isLogcatEnable = true;

    @NonNull
    private final BlockingQueue<String> mInputQueues = new LinkedBlockingQueue<>();
    @NonNull
    private final Set<IInputStreamAction> mReadInputStreamListener = new HashSet<>();

    private Process mProcess;
    private ReadInputStreamThread mReadInputStreamThread;
    private ReadErrorStreamThread mReadErrorStreamThread;

    private boolean hadInit = false;

    private String mLogcatTag = "*";
    private String mLogcatLogLevel = "V";
    private String mLogcatSearchKeyword = ".*";


    @NonNull
    private final Thread mReadThread = new Thread() {

        @SuppressWarnings("LoopConditionNotUpdatedInsideLoop")
        @Override
        public void run() {
            super.run();
            while (isLogcatEnable) {
                try {
                    String readLine = mInputQueues.take();
                    for (IInputStreamAction action : mReadInputStreamListener) {
                        action.readLine(readLine);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private AndroidLogcatManager() {
    }

    public static AndroidLogcatManager getsInstance() {
        return sInstance;
    }

    public synchronized void init(Context context) {
        if (hadInit) return;
        hadInit = true;
        this.context = context;
        mReadThread.start();
        execLogcatCommand();
    }

    public String getCommand() {
        return createCommand();
    }

    public void setmLogcatTag(String mLogcatTag) {
        this.mLogcatTag = TextUtils.isEmpty(mLogcatTag) ? "*" : mLogcatTag;
    }

    public void setmLogcatLogLevel(String mLogcatLogLevel) {
        this.mLogcatLogLevel = mLogcatLogLevel;
    }

    public void setmLogcatSearchKeyword(String mLogcatSearchKeyword) {
        this.mLogcatSearchKeyword = TextUtils.isEmpty(mLogcatSearchKeyword) ? ".*" : mLogcatSearchKeyword;
    }

    private String createCommand() {
        String filterTag = mLogcatTag + ":" + mLogcatLogLevel;
        if (!"*".equals(mLogcatTag)) {
            filterTag = filterTag + " *:S";
        }
        final String regexSearch = "-e " + mLogcatSearchKeyword;
        final String command = "logcat -v threadtime -T 1000" + " " + filterTag + " " + " " + regexSearch;
        return command;
    }

    public void updateCommand() {
        execLogcatCommand();
    }

    private void execLogcatCommand() {
        try {
            if (mProcess != null) {
                mProcess.destroy();
            }
            mProcess = Runtime.getRuntime().exec(createCommand());
            mReadInputStreamThread = new ReadInputStreamThread(mProcess.getInputStream(), mInputQueues);
            mReadErrorStreamThread = new ReadErrorStreamThread(mProcess.getErrorStream(), mInputQueues);
            mReadInputStreamThread.start();
            mReadErrorStreamThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    private View layoutView;
    WindowManager.LayoutParams layoutParams;
    WindowManager windowManager;

    public void showMonitor(Activity activity) {
        this.activity = activity;

        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        layoutParams.format = PixelFormat.RGBA_8888;//悬浮框背景颜色
        layoutParams.gravity = Gravity.START | Gravity.TOP;

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;//悬浮框宽度
        layoutParams.width = activity.getResources().getDisplayMetrics().heightPixels / 4 * 2;//悬浮框长度
        layoutParams.height = activity.getResources().getDisplayMetrics().heightPixels / 4 * 2;//悬浮框长度
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;//悬浮框长度

//        layoutParams.x = windowManager.getDefaultDisplay().getWidth();//初始显示位置
        layoutParams.y = 100;//初始显示位置

        layoutView = LayoutInflater.from(activity.getApplicationContext()).inflate(R.layout.layout_logcat_wrapper, null);
        layoutView.setBackgroundColor(activity.getResources().getColor(R.color.white));
//        layoutView.setAlpha((float) 0.9);

        windowManager.addView(layoutView, layoutParams);
    }

    public void hideMonitor() {
        windowManager.removeView(layoutView);
    }

    public void showMonitor() {
        windowManager.addView(layoutView, layoutParams);
    }

    public void addInputStreamAction(IInputStreamAction inputStreamAction) {
        mReadInputStreamListener.add(inputStreamAction);
    }


    private final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private final SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private SimpleDateFormat rightSdf = null;

    public LogcatUiItem parseLine(@NonNull final String line) {
        final String SPLIT = " ";
        String parseLine = line;
        String date, time, pid, tid, logLevel, tag, content;

        date = "N/A";
        parseLine = trimFirst4Split(parseLine);
        int indexOf = line.indexOf(SPLIT);
        if (indexOf != -1) {
            date = parseLine.substring(0, indexOf);
        }

        time = "N/A";
        if (indexOf != -1) {
            parseLine = line.substring(indexOf);
            parseLine = trimFirst4Split(parseLine);
            indexOf = parseLine.indexOf(SPLIT);
            if (indexOf != -1) {
                time = parseLine.substring(0, indexOf);
            }
        }

        // check right line
        boolean check = false;
        if (rightSdf != null) {
            try {
                rightSdf.parse(date + " " + time);
                check = true;
            } catch (Exception e) {
                // ...
            }
        }

        if (rightSdf == null) {
            try {
                sdf1.parse(date + " " + time);
                check = true;
                rightSdf = sdf1;
            } catch (Exception e) {
                // ...
            }
        }

        if (rightSdf == null) {
            try {
                sdf2.parse(date + " " + time);
                check = true;
                rightSdf = sdf2;
            } catch (Exception e) {
                // ...
            }
        }

        if (!check) {
            final String NA = "N/A";
            return new LogcatUiItem(false, line, date, time, NA, NA, NA, "V", NA, line);
        }

        pid = "N/A";
        if (indexOf != -1) {
            parseLine = parseLine.substring(indexOf);
            parseLine = trimFirst4Split(parseLine);
            indexOf = parseLine.indexOf(SPLIT);
            if (indexOf != -1) {
                pid = parseLine.substring(0, indexOf);
            }
        }

        tid = "N/A";
        if (indexOf != -1) {
            parseLine = parseLine.substring(indexOf);
            parseLine = trimFirst4Split(parseLine);
            indexOf = parseLine.indexOf(SPLIT);
            if (indexOf != -1) {
                tid = parseLine.substring(0, indexOf);
            }
        }

        logLevel = "V";
        if (indexOf != -1) {
            parseLine = parseLine.substring(indexOf);
            parseLine = trimFirst4Split(parseLine);
            indexOf = parseLine.indexOf(SPLIT);
            if (indexOf != -1) {
                logLevel = parseLine.substring(0, indexOf).trim();
            }
        }

        tag = "N/A";
        if (indexOf != -1) {
            parseLine = parseLine.substring(indexOf);
            parseLine = trimFirst4Split(parseLine);
            indexOf = parseLine.indexOf(": ");
            if (indexOf != -1) {
                tag = parseLine.substring(0, indexOf);
            }
        }

        content = "N/A";
        if (indexOf != -1) {
            parseLine = parseLine.substring(indexOf);
            parseLine = parseLine.substring(": ".length());
            content = parseLine;
        }


        return new LogcatUiItem(true, line, date, time, pid, tid, "", logLevel, tag, content);
    }

    private final String trimFirst4Split(String parseLine) {
        final String split = " ";
        for (int i = 0; i < 4; i++) {
            if (parseLine.startsWith(split)) {
                parseLine = parseLine.substring(1);
            } else {
                break;
            }
        }
        return parseLine;
    }
}
