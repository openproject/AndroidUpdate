package com.jayfeng.lesscode.core;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import com.jayfeng.lesscode.update.R;

/**
 * check update util
 * {
 * "vercode":1,
 * "vername":"v1.1",
 * "download":"http://www.jayfeng.com/lesscode-app.apk",
 * "log":"upgrade content"
 * }
 */
public final class UpdateLess {

    public static String sDownloadSDPath;
    public static int sUpdateIcon;

    /**
     * config the download path and notification icon
     * @param downloadSDPath
     * @param updateIcon
     */
    public static void $config(String downloadSDPath, int updateIcon) {
        sDownloadSDPath = downloadSDPath;
        sUpdateIcon = updateIcon;
    }

    /**
     * check to update by version code
     * which parse form the updateJson
     *
     * @return 有更新则返回true, 否则返回false
     */
    public static boolean $check(final Context context, String updateJson) {
        int vercode = 0;
        String vername = "";
        String log = "";
        String download;

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(updateJson);
            vercode = jsonObject.optInt("vercode");
            vername = jsonObject.optString("vername");
            download = jsonObject.optString("download");
            log = jsonObject.optString("log");
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return $check(context, vercode, vername, download, log);
    }

    /**
     * check to update by version code
     *
     * @param context
     * @param vercode
     * @param vername
     * @param download
     * @param log
     * @return
     */
    public static boolean $check(final Context context,
                                 int vercode,
                                 String vername,
                                 final String download,
                                 String log) {
        // no update
        if (!$hasUpdate(vercode)) {
            return false;
        }

        // if has update, show to dialog with update log
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.less_app_download_dialog_title) + vername)
                .setMessage(log)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, UpdateService.class);
                        intent.putExtra($.KEY_DOWNLOAD_URL, download);
                        context.startService(intent);
                    }
                }).show();

        return true;
    }

    /**
     * if has update by version code
     *
     * @param vercode
     * @return
     */
    public static boolean $hasUpdate(int vercode) {
        if (vercode <= AppLess.$vercode()) {
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
    public static void $download(Context context, String download) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra($.KEY_DOWNLOAD_URL, download);
        context.startService(intent);
    }
}
