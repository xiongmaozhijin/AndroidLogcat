package com.xiongmaozhijin.ilogcat.core;

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
    private String mLogDir;
    private String[] mFilterArray;

    public LocalStorageHelper() {
    }

    public void setLogDir(String logDir) {
        this.mLogDir = logDir;
    }

    public void setFilterArray(String[] tags) {
        this.mFilterArray = tags == null ? new String[]{} : tags;
    }

    public void enable() {
        if (mLogDir == null) {
            throw new IllegalArgumentException("Must set logDir first");
        }
        mICacheStrategy = new LocalStorageCacheStrategy(mLogDir);
        mLocalStorageStreamAction = new LocalStorageStreamAction(mLogDir);

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
            final String command = createCommand();

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

    private String createCommand() {
        String filterTag;

        final String logLevel = "V";
        if (mFilterArray == null || mFilterArray.length == 0) {
            filterTag = "*:" + logLevel;

        } else {
            final StringBuilder tagBuilder = new StringBuilder();
            for (int i = 0; i < mFilterArray.length; i++) {
                tagBuilder.append(mFilterArray[i]);
                tagBuilder.append(":");
                tagBuilder.append(logLevel);
                if (i != mFilterArray.length - 1) {
                    tagBuilder.append(" ");
                }
            }

            filterTag = tagBuilder.toString();
        }

        if (mFilterArray != null && mFilterArray.length > 0) {
            filterTag = filterTag + " *:S";
        }

        return "logcat -v threadtime" + " " + filterTag;
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
