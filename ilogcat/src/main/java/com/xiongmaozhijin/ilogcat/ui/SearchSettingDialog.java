package com.xiongmaozhijin.ilogcat.ui;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xiongmaozhijin.ilogcat.R;

public class SearchSettingDialog extends Dialog {

    public interface Listener {
        void onConfirm(String searchTag, String searchContent);
    }

    private EditText edtSearchTag;
    private EditText edtSearchContent;
    private Button btnConfirm;
    @Nullable
    private Listener mListener;

    public SearchSettingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.layout_search_dialog);
        edtSearchTag = findViewById(R.id.edtSearchTag);
        edtSearchContent = findViewById(R.id.edtSearchContent);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(v -> onConfirm());
    }

    private void onConfirm() {
        if (mListener != null) {
            mListener.onConfirm(edtSearchTag.getText().toString(), edtSearchContent.getText().toString());
        }
    }

    public void setmListener(@Nullable Listener mListener) {
        this.mListener = mListener;
    }
}
