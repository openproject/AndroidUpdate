package com.jayfeng.update;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

/**
 * check update util
 * {
 * "vercode":1,
 * "vername":"v1.1",
 * "download":"http://www.jayfeng.com/lesscode-app.apk",
 * "log":"upgrade content"
 * }
 */
public final class AU {

    public static final String KEY_DOWNLOAD_URL = "download_url";

    public static final int REQUEST_CODE = 3423;

    public static AUConfig sAUConfig;

    /**
     * config
     *
     * @param auConfig AUConfig
     */
    public static void init(AUConfig auConfig) {
        sAUConfig = auConfig;
    }

    /**
     * if has update by version code
     *
     * @param vercode vercode
     * @return return
     */
    public static boolean hasUpdate(int vercode) {
        if (vercode <= AUUtils.vercode(sAUConfig.getContext())) {
            return false;
        }
        return true;
    }

    /**
     * start update service to download apk
     *
     * @param context context
     * @param download download
     */
    public static void download(Context context, String download, boolean showUI) {
        Intent intent = new Intent(context, AUService.class);
        intent.putExtra(KEY_DOWNLOAD_URL, download);
        intent.putExtra(AUService.KEY_SHOW_UI, showUI);
        context.startService(intent);
    }

    /**
     * DEFAULT MATERIAL DESIGN
     * check to update by version code
     *
     * @param context context
     * @param vercode vercode
     * @param vername vername
     * @param download download
     * @param log log
     */
    public static void show(final Context context,
                            final int vercode,
                            final String vername,
                            final String download,
                            final String log,
                            final boolean force) {
        // no update
        if (!hasUpdate(vercode)) {
            return;
        }

        // if has update, show to dialog with update log
        final AlertDialog updateDialog;
        AlertDialog.Builder updateBuilder = new AlertDialog.Builder(context);
        updateBuilder.setTitle(context.getString(R.string.au_download_dialog_title) + vername);
        updateBuilder.setMessage(log);
        updateBuilder.setPositiveButton(android.R.string.ok, null);

        if (!force) {
            updateBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int i) {
                    auCancel(context, dialog, download);
                }
            });
        }

        updateDialog = updateBuilder.create();

        if (force) {
            AUUtils.forceUpdateDialog(context, updateDialog);
        }

        updateDialog.show();
        Button okButton = updateDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auConfirm(context, updateDialog, download, force);
            }
        });
    }

    public static void auCancel(Context context, DialogInterface dialog, String download) {
        if (AUUtils.isSilentDownload(context)
                || AndPermission.hasPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (!TextUtils.isEmpty(download)) {
                download(context, download, false);
                dialog.dismiss();
            }
        } else {
            dialog.dismiss();
        }
    }

    public static void auConfirm(final Context context, final DialogInterface dialog, final String download, final boolean force) {
        AndPermission.with(context)
                .requestCode(REQUEST_CODE)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, List<String> grantPermissions) {
                        download(context, download, true);
                        if (!force) {
                            dialog.dismiss();
                        }
                    }

                    @Override
                    public void onFailed(int requestCode, List<String> deniedPermissions) {
                        Activity activity = AUUtils.getActivityFromContext(context);
                        AndPermission.defaultSettingDialog(activity, REQUEST_CODE)
                                .setTitle(context.getString(R.string.au_permission_deny_title))
                                .setMessage(context.getString(R.string.au_permission_deny_message))
                                .setPositiveButton(context.getString(R.string.au_permission_deny_ok))
                                .show();
                    }
                }).start();
    }
}
