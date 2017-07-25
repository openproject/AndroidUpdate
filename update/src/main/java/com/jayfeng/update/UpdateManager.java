package com.jayfeng.update;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.widget.Toast;

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

    public static Context sContext;
    public static String sDownloadSDPath;
    public static int sUpdateIcon;

    /**
     * config the download path and notification icon
     *
     * @param downloadSDPath
     * @param updateIcon
     */
    public static void init(Context context, String downloadSDPath, int updateIcon) {
        sContext = context;
        sDownloadSDPath = downloadSDPath;
        sUpdateIcon = updateIcon;
    }

    /**
     * if has update by version code
     *
     * @param vercode
     * @return
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
     * @param context
     * @param download
     */
    public static void download(Context context, String download) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(KEY_DOWNLOAD_URL, download);
        context.startService(intent);
    }

    /**
     * check to update by version code
     *
     * @param vercode
     * @param vername
     * @param download
     * @param log
     * @return
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
                .setTitle(context.getString(R.string.less_app_download_dialog_title) + vername)
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
                                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                        download(context, download);
                                    }

                                    @Override
                                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                                        Activity activity = Utils.getActivityFromContext(context);
                                        AndPermission.defaultSettingDialog(activity, REQUEST_CODE)
                                                .setTitle("没有权限")
                                                .setMessage("您拒绝了访问存储卡权限导致无法下载APP，请在设置中授权后再试！")
                                                .setPositiveButton("好，去设置")
                                                .show();
                                    }
                                }).start();
                    }
                }).show();
    }
}
