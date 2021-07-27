package com.xiongmaozhijin.logcatlibrary.logcat;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalStorageHelper {

    @NonNull
    private final BlockingQueue<String> mInputQueues = new LinkedBlockingQueue<>();
    private Process mProcess;
    private boolean running = true;
    private LocalStorageIsAction mLocalStorageIsAction;

    public LocalStorageHelper() {
    }

    public void enable(String logDir) {
        mLocalStorageIsAction = new LocalStorageIsAction(logDir);
        execLogcatCommand();
        final Runnable runnable = () -> {
            while (running) {
                try {
                    String readLine = mInputQueues.take();
                    mLocalStorageIsAction.readLine(readLine);
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
            final String command = "logcat -v threadtime";
            mProcess = Runtime.getRuntime().exec(command);
            ReadInputStreamThread readInputStreamThread = new ReadInputStreamThread(mProcess.getInputStream(), mInputQueues);
            ReadErrorStreamThread readErrorStreamThread = new ReadErrorStreamThread(mProcess.getErrorStream(), mInputQueues);
            readInputStreamThread.start();
            readErrorStreamThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        running = false;
        if (mProcess != null) {
            mProcess.destroy();
        }
        if (mLocalStorageIsAction != null) {
            mLocalStorageIsAction.close();
        }
    }
}
