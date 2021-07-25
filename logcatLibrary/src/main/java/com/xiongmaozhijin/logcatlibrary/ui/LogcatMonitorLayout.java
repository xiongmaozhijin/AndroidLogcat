package com.xiongmaozhijin.logcatlibrary.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiongmaozhijin.logcatlibrary.R;
import com.xiongmaozhijin.logcatlibrary.logcat.AndroidLogcatManager;
import com.xiongmaozhijin.logcatlibrary.logcat.IInputStreamAction;
import com.xiongmaozhijin.logcatlibrary.logcat.LogcatUiItem;


public class LogcatMonitorLayout extends FrameLayout {

    private int mLogLevel = 0;
    private boolean mIsFindRegex = false;
    private TextView txvSearchDetail;
    private String mSearchKeyword = "";
    private Button btnLogcat;
    private View layoutLogcat;
    private TextView txvHeaderDetail;

    private LogcatItemAdapter mLogcatItemAdapter;

    private InnerHandler mInnerHandler;
    static final long RECY_UPDATE_DELAY = 320L;

    private final String[] mLogLevelArrays = getResources().getStringArray(R.array.LogLevel);


    public LogcatMonitorLayout(@NonNull Context context) {
        this(context, null);
    }

    public LogcatMonitorLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LogcatMonitorLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr);
    }

    @SuppressLint("SetTextI18n")
    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_logcat_monitor, this, true);

        layoutLogcat = findViewById(R.id.layoutLogcat);
        txvHeaderDetail = findViewById(R.id.txvHeaderDetail);

        CheckBox chbScrollBottom = findViewById(R.id.chbScrollBottom);
        chbScrollBottom.setOnCheckedChangeListener((buttonView, isChecked) -> mLogcatItemAdapter.setmIsScrollBottom(isChecked));

        btnLogcat = findViewById(R.id.btnLogcat);
        btnLogcat.setOnClickListener(v -> {
            if (layoutLogcat.getVisibility() == View.VISIBLE) {
                layoutLogcat.setVisibility(View.GONE);
            } else {
                layoutLogcat.setVisibility(View.VISIBLE);
            }
        });

        final Spinner logLevelSpinner = findViewById(R.id.logLevelSpinner);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, mLogLevelArrays);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        logLevelSpinner.setAdapter(adapter);
        logLevelSpinner.setSelection(0);
        logLevelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mLogLevel = position;
                updateHeaderDetail();
                final String[] logLevel = new String[]{"V", "D", "I", "W", "E", "A"};
                AndroidLogcatManager.getsInstance().setmLogcatLogLevel(logLevel[position]);
                updateCommand();
                updateHeaderDetail();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.btnFilter).setOnClickListener(v -> {
            onSearchFilter();
        });

        final CheckBox chbRegex = findViewById(R.id.chbRegex);
        chbRegex.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mIsFindRegex = isChecked;
        });

        final TextView txvPhoneMode = findViewById(R.id.txvPhoneMode);
        final String phoneMode = Build.MODEL + " " + Build.BRAND + " API:" + Build.VERSION.SDK_INT;
        txvPhoneMode.setText(phoneMode);
        txvPhoneMode.setOnClickListener(v -> {
            toast(phoneMode);
        });

        final TextView txvPackageName = findViewById(R.id.txvPackageName);
        txvPackageName.setText(getContext().getPackageName());
        txvPackageName.setOnClickListener(v -> {
            if (v instanceof TextView) toast(((TextView) v).getText().toString());
        });

        updateHeaderDetail();

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mLogcatItemAdapter = new LogcatItemAdapter(recyclerView);
        recyclerView.setAdapter(mLogcatItemAdapter);

        mInnerHandler = new InnerHandler(mLogcatItemAdapter);
        mInnerHandler.sendEmptyMessageDelayed(InnerHandler.MSG_UPDATE_RECY, RECY_UPDATE_DELAY);

        AndroidLogcatManager.getsInstance().addInputStreamAction(new IInputStreamAction() {
            @Override
            public void readLine(String line) {
                final LogcatUiItem item = AndroidLogcatManager.getsInstance().parseLine(line);
                mLogcatItemAdapter.addItemWithoutNotify(item);
            }
        });
    }

    private void updateCommand() {
        mLogcatItemAdapter.clearData();
        AndroidLogcatManager.getsInstance().updateCommand();
    }

    @SuppressLint("SetTextI18n")
    private void updateHeaderDetail() {
        final String RL = "\n";
        final String s1 = "日志：" + mLogLevelArrays[mLogLevel];
        final String s2 = RL + "型号：" + Build.MODEL + " " + Build.BRAND + " API:" + Build.VERSION.SDK_INT;
        final String s3 = RL + "包名：" + getContext().getPackageName();
        final String s4 = RL + "搜索：" + AndroidLogcatManager.getsInstance().getCommand();
        txvHeaderDetail.setText(s1 + s2 + s3 + s4);
    }

    private void onSearchFilter() {
        AndroidLogcatManager.getsInstance().hideMonitor();
        final SearchSettingDialog dialog = new SearchSettingDialog(getContext());
        dialog.setmListener((searchTag, searchContent) -> {
            AndroidLogcatManager.getsInstance().setmLogcatTag(searchTag);
            AndroidLogcatManager.getsInstance().setmLogcatSearchKeyword(searchContent);
            updateCommand();
            updateHeaderDetail();
            dialog.dismiss();
        });
        dialog.setOnDismissListener(dialog1 -> AndroidLogcatManager.getsInstance().showMonitor());
        dialog.show();
    }


    private void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    static class InnerHandler extends Handler {
        private final LogcatItemAdapter adapter;

        public static final int MSG_UPDATE_RECY = 1;

        public InnerHandler(LogcatItemAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (MSG_UPDATE_RECY == msg.what) {
                this.adapter.notifyPro();
                removeMessages(InnerHandler.MSG_UPDATE_RECY);
                sendEmptyMessageDelayed(InnerHandler.MSG_UPDATE_RECY, RECY_UPDATE_DELAY);

            }
        }
    }
}
