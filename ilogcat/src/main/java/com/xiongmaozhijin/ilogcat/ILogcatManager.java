package com.xiongmaozhijin.ilogcat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.xiongmaozhijin.ilogcat.core.IInputStreamAction;
import com.xiongmaozhijin.ilogcat.core.LocalStorageHelper;
import com.xiongmaozhijin.ilogcat.core.LogcatParam;
import com.xiongmaozhijin.ilogcat.core.ReadErrorStreamThread;
import com.xiongmaozhijin.ilogcat.core.ReadInputStreamThread;
import com.xiongmaozhijin.ilogcat.ui.IWindowDialog;
import com.xiongmaozhijin.ilogcat.ui.SimpleActivityLifecycleCallbacks;
import com.xiongmaozhijin.ilogcat.ui.SimpleWindowDialog;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ILogcatManager {

    private final static ILogcatManager sInstance = new ILogcatManager();
    private Context context;
    private final boolean isLogcatEnable = true;

    @NonNull
    private final BlockingQueue<String> mInputQueues = new LinkedBlockingQueue<>();
    @NonNull
    private final Set<IInputStreamAction> mReadInputStreamListener = new HashSet<>();

    private Process mProcess;

    private final LocalStorageHelper mLocalStorageHelper = new LocalStorageHelper();

    private boolean hadInit = false;

    private final LogcatParam mLogcatParam = new LogcatParam();

    private IWindowDialog mIWindowDialog = new SimpleWindowDialog();

    private Activity mTopActivity;

    @NonNull
    private final Runnable mReadRunnable = () -> {
        //noinspection LoopConditionNotUpdatedInsideLoop
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
    };

    private ILogcatManager() {
    }

    public static ILogcatManager getsInstance() {
        return sInstance;
    }

    public synchronized void init(Context context) {
        if (hadInit) return;
        hadInit = true;
        this.context = context;
        new Thread(mReadRunnable).start();
        initActivityCallback(context);
        execLogcatCommand();
    }

    private void initActivityCallback(Context context) {
        Application application = (Application) context.getApplicationContext();
        application.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                super.onActivityResumed(activity);
                mTopActivity = activity;
            }
        });
    }

    public Activity getTopActivity() {
        return mTopActivity;
    }

    public void setWindowDialog(IWindowDialog windowDialog) {
        this.mIWindowDialog = windowDialog;
    }

    public IWindowDialog getLogcatWindow() {
        return mIWindowDialog;
    }

    public String getCommand() {
        return mLogcatParam.createCommand();
    }

    public void setLogcatTag(String... tag) {
        mLogcatParam.filterTags = tag;
    }

    public void setLogLevel(String logLevel) {
        mLogcatParam.logLevel = logLevel;
    }

    public void setKeyword(String keyword) {
        mLogcatParam.filterKeyword = TextUtils.isEmpty(keyword) ? ".*" : keyword;
    }


    public void enableLocalStorage(String logDir, String[] filterArray) {
        mLocalStorageHelper.setLogDir(logDir);
        mLocalStorageHelper.setFilterArray(filterArray);
        mLocalStorageHelper.enable();
    }

    public void updateCommand() {
        execLogcatCommand();
    }

    private void execLogcatCommand() {
        try {
            if (mProcess != null) {
                mProcess.destroy();
            }
            mProcess = Runtime.getRuntime().exec(mLogcatParam.createCommand());
            ReadInputStreamThread mReadInputStreamThread = new ReadInputStreamThread(mProcess.getInputStream(), mInputQueues);
            ReadErrorStreamThread mReadErrorStreamThread = new ReadErrorStreamThread(mProcess.getErrorStream(), mInputQueues);
            mReadInputStreamThread.start();
            mReadErrorStreamThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addInputStreamAction(IInputStreamAction inputStreamAction) {
        mReadInputStreamListener.add(inputStreamAction);
    }


    public void closeResource() {
        mLocalStorageHelper.close();
    }
}
