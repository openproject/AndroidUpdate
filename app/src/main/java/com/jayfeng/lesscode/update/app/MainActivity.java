package com.jayfeng.lesscode.update.app;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Version mVersion;

    private Button mCheckDefaultButton;
    private Button mCheckCornerCenterButton;
    private Button mCheckCornerBottomButton;

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
     */
    private void initAUConfig() {
        AUConfig auConfig = new AUConfig();
        auConfig.setContext(getApplicationContext());
        auConfig.setUpdateIcon(R.mipmap.ic_launcher);
        auConfig.setDownloadWhenCacel(true);
        AU.init(auConfig);
    }

    private void initViewAndListener() {
        mCheckDefaultButton = ViewLess.$(this, R.id.check_default);
        mCheckCornerCenterButton = ViewLess.$(this, R.id.check_corner_center);
        mCheckCornerBottomButton = ViewLess.$(this, R.id.check_corner_bottom);
        mClearButton = ViewLess.$(this, R.id.clear);

        mCheckDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AU.show(MainActivity.this,
                        mVersion.getVercode(), mVersion.getVername(), mVersion.getDownload(), mVersion.getLog());
            }
        });

        mCheckCornerCenterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AU.show(MainActivity.this,
                        mVersion.getVercode(), mVersion.getVername(), mVersion.getDownload(), mVersion.getLog(),
                        AU.STYLE_CORNER_CENTER);
            }
        });

        mCheckCornerBottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AU.show(MainActivity.this,
                        mVersion.getVercode(), mVersion.getVername(), mVersion.getDownload(), mVersion.getLog(),
                        AU.STYLE_CORNER_BOTTOM);
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
