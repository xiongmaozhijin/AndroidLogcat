package com.xiongmaozhijin.ilogcat.core;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class LogcatParseUtils {

    private static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.US);
    private static SimpleDateFormat rightSdf = null;

    public static LogcatUiItem parseLine(@NonNull final String line) {
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

    private static String trimFirst4Split(String parseLine) {
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
