package com.jayfeng.lesscode.update.app;

import android.app.Application;

import com.jayfeng.lesscode.core.$;
import com.jayfeng.update.AU;
import com.jayfeng.update.AUConfig;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        $.getInstance()
                .context(getApplicationContext())
                .log(BuildConfig.DEBUG, "update")
                .build();

        // UpdateManager init - MUST
        AUConfig auConfig = new AUConfig();
        auConfig.setContext(getApplicationContext());
        auConfig.setUpdateIcon(R.mipmap.ic_launcher);
        AU.init(auConfig);
    }
}
