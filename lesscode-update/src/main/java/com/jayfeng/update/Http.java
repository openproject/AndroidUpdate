package com.jayfeng.update;

import android.content.ContentValues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public final class Http {

    public static final int HTTP_CONNECT_TIMEOUT = 5000;
    public static final int HTTP_READ_TIMEOUT = 5000;

    /**
     * 异步下载文件
     * @param downloadUrl
     * @param dest
     * @param append
     * @param callBack
     * @return
     * @throws Exception
     */
    public static long download(String downloadUrl, File dest, boolean append, DownloadCallBack callBack) throws Exception {
        return download(downloadUrl, dest, append, new ContentValues(), callBack);
    }

    /**
     * 异步下载文件,支持自定义Header
     * @param downloadUrl
     * @param dest
     * @param append
     * @param header
     * @param callBack
     * @return
     * @throws Exception
     */
    public static long download(String downloadUrl, File dest, boolean append, ContentValues header, DownloadCallBack callBack) throws Exception {
        int progress = 0;
        long remoteSize = 0;
        int currentSize = 0;
        long totalSize = -1;

        if (!append && dest.exists() && dest.isFile()) {
            dest.delete();
        }

        if (append && dest.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(dest);
                currentSize = fis.available();
            } catch (IOException e) {
                throw e;
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }

        InputStream is = null;
        FileOutputStream os = null;
        try {
            URL u = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) u.openConnection();
            conn.setUseCaches(false);
            conn.setConnectTimeout(HTTP_CONNECT_TIMEOUT);
            conn.setReadTimeout(HTTP_READ_TIMEOUT);
            conn.setRequestMethod("GET");
            // 设置断点续传的起始位置
            if (currentSize > 0) {
                conn.setRequestProperty("RANGE", "bytes=" + currentSize + "-");
            }
            // 自定义header
            for (Map.Entry<String, Object> entry : header.valueSet()) {
                String key = entry.getKey();
                String value = entry.getValue().toString();
                conn.setRequestProperty(key, value);
            }
            if (conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
                    || conn.getResponseCode()== HttpURLConnection.HTTP_MOVED_TEMP) {
                // 重定向地址下载
                String redirectDownloadUrl = conn.getHeaderField("location");
                return download(redirectDownloadUrl, dest, append, header, callBack);
            } else if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = conn.getInputStream();
                remoteSize = conn.getContentLength();
                String contentEndcoding = conn.getHeaderField("Content-Encoding");
                if (contentEndcoding != null && contentEndcoding.equalsIgnoreCase("gzip")) {
                    is = new GZIPInputStream(is);
                }
                os = new FileOutputStream(dest, append);
                byte buffer[] = new byte[8192];
                int readSize = 0;
                while ((readSize = is.read(buffer)) > 0) {
                    os.write(buffer, 0, readSize);
                    os.flush();
                    totalSize += readSize;
                    // 通知回调下载进度
                    if (callBack != null) {
                        progress = (int) (totalSize * 100 / remoteSize);
                        callBack.onDownloading(progress);
                    }
                }

                if (progress != 100) {
                    callBack.onDownloading(100);
                }

                if (totalSize < 0) {
                    totalSize = 0;
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (os != null) {
                os.close();
            }
            if (is != null) {
                is.close();
            }
        }

        if (totalSize < 0) {
            throw new Exception("Download file fail: " + downloadUrl);
        }

        // 下载完成并通知回调
        if (callBack != null) {
            callBack.onDownloaded();
        }

        return totalSize;
    }

    /**
     * 下载回调:下载进度和下载完成
     * onDownloading,带一个进度值:0~100
     * onDownloaded
     */
    public interface DownloadCallBack {
        void onDownloading(int progress);
        void onDownloaded();
    }
}
