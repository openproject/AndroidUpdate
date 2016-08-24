package com.jayfeng.lesscode.update.app;

import android.app.Application;

import com.jayfeng.lesscode.core.$;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        $.getInstance()
                .context(getApplicationContext())
                .log(BuildConfig.DEBUG, "LESSCODE-UPDATE")
                .update(null, 4)
                .http(5000, 5000)
                .build();
    }
}
