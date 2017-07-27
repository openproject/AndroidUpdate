package com.jayfeng.update.ui;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jayfeng.update.R;
import com.jayfeng.update.Utils;

public class CornerCenterDialog extends Dialog {

    private View mBgView;

    private TextView titleView;
    private Button confirmButton;
    private Button cancelButton;
    private TextView contentView;

    public CornerCenterDialog(@NonNull Context context) {
        super(context);
        init();
    }

    public CornerCenterDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init();
    }

    protected CornerCenterDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.au_dialog_corner_center);

        cancleOnBg();

        titleView = findViewById(R.id.title);
        confirmButton = findViewById(R.id.confirm);
        cancelButton = findViewById(R.id.cancel);
        contentView = findViewById(R.id.content);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    public void setTitle(String title) {
        if (titleView != null) {
            titleView.setText(title);
        }
    }

    public void setContent(String content) {
        contentView.setText(content);
    }

    public void setConfirmText(String text) {
        if (confirmButton != null) {
            confirmButton.setText(text);
        }
    }

    public void setCancelText(String text) {
        if (cancelButton != null) {
            cancelButton.setText(text);
        }
    }

    public void setConfirmOnClickListener(View.OnClickListener confirmOnClickListener) {
        if (confirmButton != null && confirmOnClickListener != null) {
            confirmButton.setOnClickListener(confirmOnClickListener);
        }
    }

    public void setCancelOnClickListener(View.OnClickListener cancelOnClickListener) {
        if (cancelButton != null && cancelOnClickListener != null) {
            cancelButton.setOnClickListener(cancelOnClickListener);
        }
    }

    @Override
    public void show() {
        super.show();

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        getWindow().getDecorView().setPadding(Utils.dp2px(4), Utils.dp2px(4), Utils.dp2px(4), Utils.dp2px(4));
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);
    }

    protected void cancleOnBg() {
        mBgView = findViewById(R.id.bg);
        if (mBgView != null) {
            mBgView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }
}
