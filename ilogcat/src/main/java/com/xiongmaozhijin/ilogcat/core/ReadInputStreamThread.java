package com.xiongmaozhijin.ilogcat.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

class ReadInputStreamThread extends Thread {
    private final InputStream inputStream;
    private final BlockingQueue<String> inputQueue;

    public ReadInputStreamThread(InputStream inputStream, BlockingQueue<String> inputQueue) {
        this.inputStream = inputStream;
        this.inputQueue = inputQueue;
    }

    @Override
    public void run() {
        super.run();
        final BufferedReader bufReader = new BufferedReader(new InputStreamReader(inputStream));
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
