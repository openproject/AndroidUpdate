package com.jayfeng.update;

import android.content.Context;

public class AUConfig {

    public Context context;
    public String downloadSDPath;
    public int updateIcon;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getDownloadSDPath() {
        return downloadSDPath;
    }

    public void setDownloadSDPath(String downloadSDPath) {
        this.downloadSDPath = downloadSDPath;
    }

    public int getUpdateIcon() {
        return updateIcon;
    }

    public void setUpdateIcon(int updateIcon) {
        this.updateIcon = updateIcon;
    }
}
