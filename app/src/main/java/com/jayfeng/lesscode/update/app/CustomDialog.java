package com.jayfeng.lesscode.update.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.jayfeng.lesscode.core.DisplayLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.jayfeng.update.AU;
import com.jayfeng.update.AUUtils;

public class CustomDialog extends Dialog {

    protected TextView titleView;
    protected TextView contentView;
    protected Button okButton;
    protected Button cancelButton;

    public CustomDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CustomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected CustomDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom);

        titleView = ViewLess.$(this, R.id.title);
        contentView = ViewLess.$(this, R.id.content);
        okButton = ViewLess.$(this, R.id.ok);
        cancelButton = ViewLess.$(this, R.id.cancel);

    }

    public void setTitle(String title) {
        titleView.setText(title);
    }

    public void setContent(String content) {
        contentView.setText(content);
    }

    public void hideCancel() {
        cancelButton.setVisibility(View.GONE);
    }

    public void setOkClickListener(View.OnClickListener listener) {
        okButton.setOnClickListener(listener);
    }

    public void setCancelClickListener(View.OnClickListener listener) {
        cancelButton.setOnClickListener(listener);
    }

    @Override
    public void show() {
        super.show();
        int padding = DisplayLess.$dp2px(16);
        getWindow().getDecorView().setPadding(padding, padding, padding, padding);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(lp);
    }

    public static void showCustomUpdateDialog(final Context context, final Version version, boolean noForce) {

        final boolean isForce = !noForce && version.getForce() == 1;
        final CustomDialog updateDialog = new CustomDialog(context);

        updateDialog.setTitle("发现新版本：" + version.getVername());
        updateDialog.setContent(version.getLog());
        updateDialog.setOkClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // click confirm
                AU.auConfirm(context, updateDialog, version.getDownload(), isForce);
            }
        });
        updateDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // click cancel
                AU.auCancel(context, updateDialog, version.getDownload());
            }
        });
        if (isForce) {
            updateDialog.hideCancel();
            AUUtils.forceUpdateDialog(context, updateDialog);
        }
        updateDialog.show();
    }
}
