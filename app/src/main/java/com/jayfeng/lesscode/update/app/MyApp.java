package com.jayfeng.lesscode.update.app;

import android.app.Application;

import com.jayfeng.lesscode.core.$;
import com.jayfeng.lesscode.core.UpdateLess;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // LessCode初始化 - 必须
        $.getInstance()
                .context(getApplicationContext())
                .log(BuildConfig.DEBUG, "LESSCODE-UPDATE")
                .build();

        // UpdateLess初始化 - 必须
        UpdateLess.$config(null, R.mipmap.ic_launcher);
    }
}
