package com.xiongmaozhijin.ilogcat.core;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.Nullable;

public class LogcatParam {

    /**
     * 过滤tag数组
     */
    @Nullable
    public String[] filterTags = {};
    /**
     * 日志等级
     */
    public String logLevel = "V";
    /**
     * 过滤字符，正则
     */
    public String filterKeyword = ".*";

    public String logStorageDir;

    public LogcatParam(Context context) {
        logStorageDir = context.getCacheDir().getAbsolutePath();
    }

    public String createCommand() {
        String concatTag;
        if (filterTags == null || filterTags.length == 0) {
            concatTag = "*:" + logLevel;
        } else {
            final StringBuilder tagBuilder = new StringBuilder();
            for (int i = 0; i < filterTags.length; i++) {
                tagBuilder.append(filterTags[i]);
                tagBuilder.append(":");
                tagBuilder.append(logLevel);
                if (i != filterTags.length - 1) {
                    tagBuilder.append(" ");
                }
            }
            concatTag = tagBuilder.toString();
        }

        if (filterTags != null && filterTags.length > 0) {
            concatTag = concatTag + " *:S";
        }

        final String regexSearch = "-e " + filterKeyword;
        return "logcat -v threadtime -T 1000" + " " + concatTag + " " + " " + regexSearch;
    }


    public String createCommand2() {
        String cmdTag;
        final String logLevel = "V";
        if (filterTags == null || filterTags.length == 0) {
            cmdTag = "*:" + logLevel;
        } else {
            final StringBuilder tagBuilder = new StringBuilder();
            for (int i = 0; i < filterTags.length; i++) {
                tagBuilder.append(filterTags[i]);
                tagBuilder.append(":");
                tagBuilder.append(logLevel);
                if (i != filterTags.length - 1) {
                    tagBuilder.append(" ");
                }
            }
            cmdTag = tagBuilder.toString();
        }
        if (filterTags != null && filterTags.length > 0) {
            cmdTag = cmdTag + " *:S";
        }
        return "logcat -v threadtime" + " " + cmdTag;
    }
}