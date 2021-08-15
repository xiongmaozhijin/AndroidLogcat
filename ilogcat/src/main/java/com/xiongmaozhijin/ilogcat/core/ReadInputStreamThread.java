package com.xiongmaozhijin.ilogcat.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.concurrent.BlockingQueue;

public class ReadInputStreamThread extends Thread {
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
        } catch (InterruptedException e) {
            // e.printStackTrace();
        } catch (InterruptedIOException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
