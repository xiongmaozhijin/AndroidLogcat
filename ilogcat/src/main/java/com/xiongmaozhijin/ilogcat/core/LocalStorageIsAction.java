package com.xiongmaozhijin.ilogcat.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class LocalStorageIsAction implements IInputStreamAction {

    private File logFile;
    private BufferedWriter bufferedWriter;

    public LocalStorageIsAction(String logDir) {
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            final String filename = sdf.format(new Date()) + "_logcat.txt";
            final File dir = new File(logDir);
            if (!dir.exists()) {
                boolean mkdirs = dir.mkdirs();
            }

            logFile = new File(logDir, filename);
            if (!logFile.exists()) {
                boolean newFile = logFile.createNewFile();
            }
            bufferedWriter = new BufferedWriter(new FileWriter(logFile));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void readLine(String line) {
        try {
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void close() {
        try {
            if (bufferedWriter != null) {
                bufferedWriter.flush();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
