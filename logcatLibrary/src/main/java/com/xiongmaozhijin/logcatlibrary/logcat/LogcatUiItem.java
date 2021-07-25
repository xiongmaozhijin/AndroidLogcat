package com.xiongmaozhijin.logcatlibrary.logcat;

public class LogcatUiItem {
    public final boolean parseLineRight;
    public final String line;
    public final String date;
    public final String time;
    public final String pid;
    public final String tid;
    public final String pckName;

    public final String logLevel;
    public final String tag;
    public final String content;

    public LogcatUiItem(boolean parseLineRight, String line, String date, String time, String pid, String tid, String pckName, String logLevel, String tag, String content) {
        this.parseLineRight = parseLineRight;
        this.line = line;
        this.date = date;
        this.time = time;
        this.pid = pid;
        this.tid = tid;
        this.pckName = pckName;
        this.logLevel = logLevel;
        this.tag = tag;
        this.content = content;
    }
}
