package com.xiongmaozhijin.ilogcat.core;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LocalStorageCacheStrategy implements ICacheStrategy {

    private final String logDir;

    public LocalStorageCacheStrategy(String logDir) {
        this.logDir = logDir;
    }

    @Override
    public void checkCache() {
        try {
            _deleteInvalidFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _deleteInvalidFile() throws ParseException {
        // delete 2 days before
        final File dir = new File(logDir);
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        if (dir.exists()) {
            File[] listFiles = dir.listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    if (file != null && file.isFile()) {
                        if (file.getName() != null && file.getName().contains("_")) {
                            int i = file.getName().indexOf("_");
                            if (i != -1) {
                                final String date = file.getName().substring(0, i);
                                long time = sdf.parse(date).getTime();
                                if (time < Calendar.getInstance().getTimeInMillis() - 2 * 24 * 60 * 60 * 1000L) {
                                    boolean delete = file.delete();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
