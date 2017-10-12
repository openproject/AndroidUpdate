package com.jayfeng.lesscode.update.app;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.jayfeng.lesscode.core.FileLess;
import com.jayfeng.lesscode.core.ToastLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.jayfeng.update.AU;
import com.jayfeng.update.AUConfig;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Version mVersion;

    private Button mCheckDefaultButton;
    private Button mCheckDefaultWithForceButton;
    private Button mCheckCustomWithForceButton;

    private Button mClearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init test data
        mVersion = new Version();
        mVersion.setVercode(112);
        mVersion.setVername("V1.1");
        mVersion.setDownload("http://www.apk.anzhi.com/data3/apk/201506/09/3a978f27369b4a8bf6de1270da9871ec_86281300.apk");
        mVersion.setLog("upgrade content");

        // AU init - MUST
        initAUConfig();

        initViewAndListener();
    }

    /**
     * AU init - MUST
     * Notice: downloadWhenCacel means slient download when cancel
     * if the network is WIFI and the app has the WRITE_EXTERNAL_STORAGE permission
     */
    private void initAUConfig() {
        AUConfig auConfig = new AUConfig();
        // must
        auConfig.setContext(getApplicationContext()); // Context
        auConfig.setUpdateIcon(R.mipmap.ic_launcher); // Notification icon
        // optional
        auConfig.setDownloadWhenCacel(true);
        AU.init(auConfig);
    }

    private void initViewAndListener() {
        mCheckDefaultButton = ViewLess.$(this, R.id.check_default);
        mCheckDefaultWithForceButton = ViewLess.$(this, R.id.check_default_force);
        mCheckCustomWithForceButton = ViewLess.$(this, R.id.check_custom_force);
        mClearButton = ViewLess.$(this, R.id.clear);

        mCheckDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVersion.setForce(0);
                AU.show(MainActivity.this,
                        mVersion.getVercode(), mVersion.getVername(), mVersion.getDownload(), mVersion.getLog(), mVersion.getForce() == 1);
            }
        });

        mCheckDefaultWithForceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVersion.setForce(1);
                AU.show(MainActivity.this,
                        mVersion.getVercode(), mVersion.getVername(), mVersion.getDownload(), mVersion.getLog(), mVersion.getForce() == 1);
            }
        });

        mCheckCustomWithForceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVersion.setForce(1);
                CustomDialog.showCustomUpdateDialog(MainActivity.this, mVersion, false);
            }
        });

        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();

                ToastLess.$("清除成功");
            }
        });
    }

    private void clear() {
        FileLess.$del(getCacheDir());
        FileLess.$del(getExternalCacheDir());
        FileLess.$del(new File(Environment.getExternalStorageDirectory(), getPackageName()), true);
    }

}
