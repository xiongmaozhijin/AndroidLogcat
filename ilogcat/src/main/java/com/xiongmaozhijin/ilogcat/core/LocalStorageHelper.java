package com.xiongmaozhijin.ilogcat.core;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalStorageHelper {

    @NonNull
    private final BlockingQueue<String> mInputQueues = new LinkedBlockingQueue<>();
    private Process mProcess;
    private boolean running = true;
    private LocalStorageStreamAction mLocalStorageStreamAction;
    private ICacheStrategy mICacheStrategy;
    private final LogcatParam mLogcatParam;
    private boolean mHadEnable = false;

    public LocalStorageHelper(Context context) {
        mLogcatParam = new LogcatParam(context);
    }

    public LogcatParam getLogcatParam() {
        return mLogcatParam;
    }

    public synchronized void enable() {
        if (mLogcatParam.logStorageDir == null) {
            throw new IllegalArgumentException("Must set logDir first");
        }
        if (mHadEnable) return;
        mHadEnable = true;
        mICacheStrategy = new LocalStorageCacheStrategy(mLogcatParam.logStorageDir);
        mLocalStorageStreamAction = new LocalStorageStreamAction(mLogcatParam.logStorageDir);

        final Runnable workRunnable = () -> {
            mICacheStrategy.checkCache();
        };
        final Thread workThread = new Thread(workRunnable);
        workThread.start();

        final Runnable runnable = () -> {
            try {
                workThread.join();
            } catch (InterruptedException e) {
                // e.printStackTrace();
            }
            execLogcatCommand();
            while (running) {
                try {
                    String readLine = mInputQueues.take();
                    mLocalStorageStreamAction.readLine(readLine);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();


    }

    private void execLogcatCommand() {
        try {
            if (mProcess != null) {
                mProcess.destroy();
            }
            final String command = mLogcatParam.createCommand2();
            mInputQueues.offer(command);
            mProcess = Runtime.getRuntime().exec(command);
            ReadInputStreamThread readInputStreamThread = new ReadInputStreamThread(mProcess.getInputStream(), mInputQueues);
            ReadErrorStreamThread readErrorStreamThread = new ReadErrorStreamThread(mProcess.getErrorStream(), mInputQueues);
            readInputStreamThread.start();
            readErrorStreamThread.start();
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    public void close() {
        running = false;
        try {
            if (mProcess != null) {
                mProcess.destroy();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        if (mLocalStorageStreamAction != null) {
            mLocalStorageStreamAction.close();
        }
    }
}
