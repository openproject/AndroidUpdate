package com.jayfeng.lesscode.update.app;

import android.app.Application;

import com.jayfeng.lesscode.core.$;
import com.jayfeng.update.UpdateManager;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        $.getInstance()
                .context(getApplicationContext())
                .log(BuildConfig.DEBUG, "update")
                .build();

        // UpdateManager init - MUST
        UpdateManager.init(getApplicationContext(),
                null, R.mipmap.ic_launcher);
    }
}
