package com.jayfeng.update;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.jayfeng.update.ui.CornerCenterDialog;
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
public final class UpdateManager {

    public static final String KEY_DOWNLOAD_URL = "download_url";
    public static final int REQUEST_CODE = 3423;

    public static final int STYLE_MATERIAL_DESIGN = 1;
    public static final int STYLE_CORNER_CENTER = 2;

    public static Context sContext;
    public static String sDownloadSDPath;
    public static int sUpdateIcon;

    /**
     * config the download path and notification icon
     *
     * @param context context
     * @param downloadSDPath downloadSDPath
     * @param updateIcon updateIcon
     */
    public static void init(Context context, String downloadSDPath, int updateIcon) {
        sContext = context;
        sDownloadSDPath = downloadSDPath;
        sUpdateIcon = updateIcon;
    }

    /**
     * if has update by version code
     *
     * @param vercode vercode
     * @return return
     */
    public static boolean hasUpdate(int vercode) {
        if (vercode <= Utils.vercode(sContext)) {
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
    public static void download(Context context, String download) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(KEY_DOWNLOAD_URL, download);
        context.startService(intent);
    }

    public static void show(final Context context,
                            final int vercode,
                            final String vername,
                            final String download,
                            final String log,
                            final int style) {

        // no update
        if (!hasUpdate(vercode)) {
            return;
        }

        if (style == STYLE_MATERIAL_DESIGN) {
            show(context, vercode, vername, download, log);
        } else if (style == STYLE_CORNER_CENTER) {
            showCornerCenter(context, vercode, vername, download, log);
        }
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
                            final String log) {
        // no update
        if (!hasUpdate(vercode)) {
            return;
        }

        // if has update, show to dialog with update log
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.au_download_dialog_title) + vername)
                .setMessage(log)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AndPermission.with(context)
                                .requestCode(REQUEST_CODE)
                                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .callback(new PermissionListener() {
                                    @Override
                                    public void onSucceed(int requestCode, List<String> grantPermissions) {
                                        download(context, download);
                                    }

                                    @Override
                                    public void onFailed(int requestCode, List<String> deniedPermissions) {
                                        Activity activity = Utils.getActivityFromContext(context);
                                        AndPermission.defaultSettingDialog(activity, REQUEST_CODE)
                                                .setTitle(context.getString(R.string.au_permission_deny_title))
                                                .setMessage(context.getString(R.string.au_permission_deny_message))
                                                .setPositiveButton(context.getString(R.string.au_permission_deny_ok))
                                                .show();
                                    }
                                }).start();
                    }
                }).show();
    }

    public static void showCornerCenter(final Context context,
                                         final int vercode,
                                         final String vername,
                                         final String download,
                                         final String log) {
        final Activity activity = Utils.getActivityFromContext(context);

        final CornerCenterDialog updateDialog = new CornerCenterDialog(activity);
        updateDialog.setTitle(context.getString(R.string.au_download_dialog_title) + vername);
        updateDialog.setContent(log);
        updateDialog.setConfirmOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(download)) {
                    download(activity, download);
                    updateDialog.dismiss();
                }
            }
        });

        if (!activity.isFinishing()) {
            updateDialog.show();
        }
    }
}
