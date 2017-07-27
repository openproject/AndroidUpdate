package com.jayfeng.update.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.design.widget.BottomSheetDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jayfeng.update.AUUtils;
import com.jayfeng.update.R;

public class AUCornerBottomDialog extends BottomSheetDialog {

    protected TextView titleView;
    protected Button confirmButton;
    protected Button cancelButton;
    protected TextView contentView;

    public AUCornerBottomDialog(Context context) {
        super(context);
        init();
    }

    public AUCornerBottomDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    public AUCornerBottomDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setContentView(R.layout.au_dialog_corner_bottom);
        getWindow().findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);

        titleView = (TextView) findViewById(R.id.title);
        confirmButton = (Button) findViewById(R.id.confirm);
        cancelButton = (Button) findViewById(R.id.cancel);
        contentView = (TextView) findViewById(R.id.content);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
//        ViewLess.$(this, R.id.root_container).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//
//        ViewLess.$(this, R.id.content_container).setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
    }

    @Override
    public void show() {
        super.show();
        setCanceledOnTouchOutside(true);
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

    public void excludeStatusBar() {
        int screenHeight = getScreenHeight(AUUtils.getActivityFromContext(getContext()));
        int statusBarHeight = getStatusBarHeight(getContext());
        int dialogHeight = screenHeight - statusBarHeight;
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
    }

    private static int getScreenHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
}
