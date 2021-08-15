package com.xiongmaozhijin.ilogcat.core;

import android.content.Context;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LogcatParam {

    private final List<String> mDefaultTags = new ArrayList<>();
    /**
     * 过滤tag数组
     */
    @Nullable
    public List<String> filterTags = new ArrayList<>();
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
        logStorageDir = Objects.requireNonNull(context.getExternalCacheDir()).getAbsolutePath();
        mDefaultTags.add("AndroidRuntime");
    }

    public String createCommand() {
        String concatTag;
        if (filterTags == null || filterTags.size() == 0) {
            concatTag = "*:" + logLevel;
        } else {
            final StringBuilder tagBuilder = new StringBuilder();
            for (int i = 0; i < filterTags.size(); i++) {
                tagBuilder.append(filterTags.get(i));
                tagBuilder.append(":");
                tagBuilder.append(logLevel);
                if (i != filterTags.size() - 1) {
                    tagBuilder.append(" ");
                }
            }
            concatTag = tagBuilder.toString();
        }

        if (filterTags != null && filterTags.size() > 0) {
            concatTag = concatTag + " *:S";
        }

        final String regexSearch = "-e " + filterKeyword;
        return "logcat -v threadtime -T 1000" + " " + concatTag + " " + " " + regexSearch;
    }


    public String createCommand2() {
        String cmdTag;
        final String logLevel = "V";
        if (filterTags == null || filterTags.size() == 0) {
            cmdTag = "*:" + logLevel;
        } else {
            final StringBuilder tagBuilder = new StringBuilder();
            filterTags.addAll(mDefaultTags);
            for (int i = 0; i < filterTags.size(); i++) {
                tagBuilder.append(filterTags.get(i));
                tagBuilder.append(":");
                tagBuilder.append(logLevel);
                if (i != filterTags.size() - 1) {
                    tagBuilder.append(" ");
                }
            }
            cmdTag = tagBuilder.toString();
        }
        if (filterTags != null && filterTags.size() > 0) {
            cmdTag = cmdTag + " *:S";
        }
        return "logcat -v -T 500 threadtime" + " " + cmdTag;
    }
}
