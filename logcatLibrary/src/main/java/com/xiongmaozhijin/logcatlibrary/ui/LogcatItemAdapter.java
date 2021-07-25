package com.xiongmaozhijin.logcatlibrary.ui;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xiongmaozhijin.logcatlibrary.R;
import com.xiongmaozhijin.logcatlibrary.logcat.LogcatUiItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class LogcatItemAdapter extends RecyclerView.Adapter<LogcatItemAdapter.LogcatViewHolder> {

    private final List<LogcatUiItem> mDataList = Collections.synchronizedList(new ArrayList<>());
    private boolean mIsScrollBottom = false;
    private final RecyclerView recyclerView;

    public LogcatItemAdapter(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public LogcatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_logcat_item, parent, false);
        return new LogcatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogcatViewHolder holder, int position) {
        final LogcatUiItem logcatUiItem = mDataList.get(position);
        final String s1 = logcatUiItem.time;
        final String s2 = logcatUiItem.logLevel + "/" + logcatUiItem.tag;
        final String s3 = logcatUiItem.content;

        if (logcatUiItem.parseLineRight) {
            holder.txvDateTime.setText(s1);
            holder.txvLogLevelTag.setText(s2);
            holder.txvContent.setText(s3);
        } else {
            holder.txvDateTime.setText("");
            holder.txvLogLevelTag.setText("");
            holder.txvContent.setText(s3);
        }


        for (TextView textView : holder.textViewList) {
            if (Objects.equals("E", logcatUiItem.logLevel)) {
                textView.setTextColor(Color.parseColor("#ae1f2b"));
//                textView.setTextColor(Color.RED);
            } else if (Objects.equals("W", logcatUiItem.logLevel)) {
                textView.setTextColor(Color.parseColor("#497ffc"));
//                textView.setTextColor(Color.BLUE);
            } else {
                textView.setTextColor(Color.parseColor("#474747"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void addItemWithoutNotify(LogcatUiItem item) {
        mDataList.add(item);
    }

    public void clearData() {
        mDataList.clear();
    }

    public void notifyPro() {
        notifyDataSetChanged();
        if (mIsScrollBottom) {
            if (getItemCount() > 0) {
                recyclerView.scrollToPosition(getItemCount() - 1);
            }
        }
    }

    public void setmIsScrollBottom(boolean mIsScrollBottom) {
        this.mIsScrollBottom = mIsScrollBottom;
    }

    static class LogcatViewHolder extends RecyclerView.ViewHolder {
        public TextView txvDateTime;
        public TextView txvLogLevelTag;
        public TextView txvContent;
        public List<TextView> textViewList = new ArrayList<>();


        public LogcatViewHolder(@NonNull View itemView) {
            super(itemView);
            txvDateTime = itemView.findViewById(R.id.txvDateTime);
            txvLogLevelTag = itemView.findViewById(R.id.txvLogLevelTag);
            txvContent = itemView.findViewById(R.id.txvContent);

            textViewList.add(txvDateTime);
            textViewList.add(txvLogLevelTag);
            textViewList.add(txvContent);
        }
    }

}
