package com.xiongmaozhijin.logcatlibrary.logcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

class ReadErrorStreamThread extends Thread {

    private final InputStream errorStream;
    private final BlockingQueue<String> inputQueue;

    public ReadErrorStreamThread(InputStream errorStream, BlockingQueue<String> inputQueue) {
        this.errorStream = errorStream;
        this.inputQueue = inputQueue;
    }

    @Override
    public void run() {
        super.run();
        final BufferedReader bufReader = new BufferedReader(new InputStreamReader(errorStream));
        try {
            String readLine;
            while ((readLine = bufReader.readLine()) != null) {
                inputQueue.put(readLine);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
