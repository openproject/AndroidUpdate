package com.jayfeng.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.net.URLEncoder;

/**
 * update service for downloading apk
 */
public class AUService extends Service {

    public static final String TAG = "AUService";

    public static final String KEY_SHOW_UI = "show";

    private static final int DOWNLOAD_STATE_FAILURE = -1;
    private static final int DOWNLOAD_STATE_SUCCESS = 0;
    private static final int DOWNLOAD_STATE_START = 1;
    private static final int DOWNLOAD_STATE_INSTALL = 2;
    private static final int DOWNLOAD_STATE_ERROR_SDCARD = 3;
    private static final int DOWNLOAD_STATE_ERROR_URL = 4;
    private static final int DOWNLOAD_STATE_ERROR_FILE = 5;

    private static final int NOTIFICATION_ID = 3956;
    private NotificationManager mNotificationManager = null;
    private NotificationCompat.Builder mNotificationBuilder = null;
    private PendingIntent mPendingIntent = null;

    private String mDownloadSDPath;
    private String mDownloadUrl;
    private File mDestDir;
    private File mDestFile;

    private boolean mIsDownloading = false;
    private boolean mIsShowUI = false;

    private String mAppName = "";

    private Handler.Callback mHandlerCallBack = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_STATE_SUCCESS:
                    if (mIsShowUI) {
                        Toast.makeText(getApplicationContext(), R.string.au_download_success, Toast.LENGTH_LONG).show();
                        install(mDestFile);
                    }
                    break;
                case DOWNLOAD_STATE_FAILURE:
                    if (mIsShowUI) {
                        Toast.makeText(getApplicationContext(), R.string.au_download_failure, Toast.LENGTH_LONG).show();
                    }
                    mNotificationManager.cancel(NOTIFICATION_ID);
                    break;
                case DOWNLOAD_STATE_START:
                    if (mIsShowUI) {
                        Toast.makeText(getApplicationContext(), R.string.au_download_start, Toast.LENGTH_LONG).show();
                    }
                    break;
                case DOWNLOAD_STATE_INSTALL:
                    if (mIsShowUI) {
                        Toast.makeText(getApplicationContext(), R.string.au_download_install, Toast.LENGTH_LONG).show();
                    }
                    break;
                case DOWNLOAD_STATE_ERROR_SDCARD:
                    if (mIsShowUI) {
                        Toast.makeText(getApplicationContext(), R.string.au_download_error_sdcard, Toast.LENGTH_LONG).show();
                    }
                    break;
                case DOWNLOAD_STATE_ERROR_URL:
                    if (mIsShowUI) {
                        Toast.makeText(getApplicationContext(), R.string.au_download_error_url, Toast.LENGTH_LONG).show();
                    }
                    break;
                case DOWNLOAD_STATE_ERROR_FILE:
                    if (mIsShowUI) {
                        Toast.makeText(getApplicationContext(), R.string.au_download_error_file, Toast.LENGTH_LONG).show();
                    }
                    mNotificationManager.cancel(NOTIFICATION_ID);
                    break;
                default:
                    break;
            }
            return true;
        }
    };
    private Handler mHandler = new Handler(mHandlerCallBack);

    private AUHttp.DownloadCallBack mDownloadCallBack = new AUHttp.DownloadCallBack() {

        private int NOTIFICATION_INTERVAL_MIN = 160;
        private long mCurrentTime = 0;
        private int mCurrentProgress = 0;

        @Override
        public void onDownloading(int progress) {
            if ((progress != mCurrentProgress && System.currentTimeMillis() - mCurrentTime > NOTIFICATION_INTERVAL_MIN)
                    || progress == 100) {
                mCurrentTime = System.currentTimeMillis();
                mCurrentProgress = progress;
                mNotificationBuilder.setProgress(100, progress, false);
                mNotificationBuilder.setContentText(getString(R.string.au_download_ongoing) + progress + "%");
                Log.d(TAG, "apk downloading progress:" + progress + "");

                if (mIsShowUI) {
                    mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
                }

            }
        }

        @Override
        public void onDownloaded() {
            mNotificationBuilder.setContentText(getString(R.string.au_download_notification_success));
            mNotificationBuilder.setProgress(0, 0, false);
            mNotificationBuilder.setDefaults(Notification.DEFAULT_ALL);
            if (mIsShowUI) {
                mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
            }
            if (mDestFile.exists() && mDestFile.isFile() && checkApkFile(mDestFile.getPath())) {
                Message msg = mHandler.obtainMessage();
                msg.what = DOWNLOAD_STATE_SUCCESS;
                mHandler.sendMessage(msg);
            }
            mNotificationManager.cancel(NOTIFICATION_ID);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mIsShowUI = intent.getBooleanExtra(KEY_SHOW_UI, false);

        // check downloading state
        if (mIsDownloading) {
            if (mIsShowUI) {
                Toast.makeText(this, R.string.au_download_downloading, Toast.LENGTH_SHORT).show();
            }
            return super.onStartCommand(intent, flags, startId);
        }

        mDownloadUrl = intent.getStringExtra(AU.KEY_DOWNLOAD_URL);
        if (TextUtils.isEmpty(AU.sAUConfig.getDownloadSDPath())) {
            mDownloadSDPath = getPackageName() + "/download";
        } else {
            mDownloadSDPath = AU.sAUConfig.getDownloadSDPath();
        }

        if (TextUtils.isEmpty(mDownloadUrl)) {
            sendMessage(DOWNLOAD_STATE_ERROR_URL);
            return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
        }

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mDestDir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + mDownloadSDPath);
            if (mDestDir.exists()) {
                File destFile = new File(mDestDir.getPath() + "/" + URLEncoder.encode(mDownloadUrl));
                if (destFile.exists() && destFile.isFile() && checkApkFile(destFile.getPath())) {

                    if (mIsShowUI) {
                        sendMessage(DOWNLOAD_STATE_INSTALL);
                        install(destFile);
                    }
                    stopSelf();
                    return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
                }
            }
        } else {
            sendMessage(DOWNLOAD_STATE_ERROR_SDCARD);
            return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
        }

        mAppName = AUUtils.appname(this);

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);

        mNotificationBuilder.setSmallIcon(AU.sAUConfig.getUpdateIcon() != 0 ? AU.sAUConfig.getUpdateIcon() : R.drawable.au_android_update_icon);
        mNotificationBuilder.setContentTitle(mAppName);
        mNotificationBuilder.setContentText(getString(R.string.au_download_start));
        mNotificationBuilder.setProgress(100, 0, false);
        mNotificationBuilder.setAutoCancel(true);

        Intent completingIntent = new Intent();
        completingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        completingIntent.setClass(getApplicationContext(), AUService.class);
        mPendingIntent = PendingIntent.getActivity(AUService.this, R.string.au_name, completingIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotificationBuilder.setContentIntent(mPendingIntent);

        mNotificationManager.cancel(NOTIFICATION_ID);
        if (mIsShowUI) {
            mNotificationManager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
        }

        // start the download thread
        new UpdateThread().start();

        return super.onStartCommand(intent, Service.START_FLAG_REDELIVERY, startId);
    }

    /**
     * validate the apk file
     *
     * @param apkFilePath apkFilePath
     * @return return
     */
    public boolean checkApkFile(String apkFilePath) {
        boolean result;
        try {
            PackageManager pManager = getPackageManager();
            PackageInfo pInfo = pManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
            if (pInfo == null) {
                result = false;
            } else {
                result = true;
            }
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /**
     * start system intent to install apk
     *
     * @param apkFile
     */
    private void install(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, getString(R.string.au_provider_file_authorities), apkFile);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void sendMessage(int what) {
        Message msg = mHandler.obtainMessage();
        msg.what = what;
        mHandler.sendMessage(msg);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    /**
     * download thread
     */
    class UpdateThread extends Thread {

        @Override
        public void run() {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if (mDestDir == null) {
                    mDestDir = new File(Environment.getExternalStorageDirectory().getPath() + "/" + mDownloadSDPath);
                }

                if (mDestDir.exists() && !mDestDir.isDirectory()) {
                    mDestDir.delete();
                }

                if (mDestDir.exists() || mDestDir.mkdirs()) {
                    Log.d(TAG, "start download apk to sdcard download apk.");
                    download();
                } else {
                    sendMessage(DOWNLOAD_STATE_ERROR_FILE);
                }
            } else {
                sendMessage(DOWNLOAD_STATE_ERROR_SDCARD);
            }
            mIsDownloading = false;
            stopSelf();
        }

        private void download() {
            mDestFile = new File(mDestDir.getPath() + "/" + URLEncoder.encode(mDownloadUrl));

            if (mDestFile.exists()
                    && mDestFile.isFile()
                    && checkApkFile(mDestFile.getPath())) {
                if (mIsShowUI) {
                    sendMessage(DOWNLOAD_STATE_INSTALL);
                    install(mDestFile);
                }
            } else {
                try {
                    sendMessage(DOWNLOAD_STATE_START);
                    mIsDownloading = true;
                    AUHttp.download(mDownloadUrl, mDestFile, false, mDownloadCallBack);
                } catch (Exception e) {
                    sendMessage(DOWNLOAD_STATE_FAILURE);
                    e.printStackTrace();
                }
            }
        }
    }
}
