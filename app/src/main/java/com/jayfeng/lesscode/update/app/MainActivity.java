package com.jayfeng.lesscode.update.app;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.jayfeng.lesscode.core.FileLess;
import com.jayfeng.lesscode.core.ToastLess;
import com.jayfeng.lesscode.core.ViewLess;
import com.jayfeng.update.UpdateManager;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button mCheckButton;
    private Button mClearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCheckButton = ViewLess.$(this, R.id.check);
        mClearButton = ViewLess.$(this, R.id.clear);

        mCheckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
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

    private void check() {
        String updateJson = "{\n" +
                "      \"vercode\":112,\n" +
                "      \"vername\":\"V1.1\",\n" +
                "      \"download\":\"http://www.apk.anzhi.com/data3/apk/201506/09/3a978f27369b4a8bf6de1270da9871ec_86281300.apk\",\n" +
                "      \"log\":\"upgrade content\"\n" +
                "      }";
        UpdateManager.check(this, updateJson);
    }

    private void clear() {
        FileLess.$del(getCacheDir());
        FileLess.$del(getExternalCacheDir());
        FileLess.$del(new File(Environment.getExternalStorageDirectory(), getPackageName()), true);
    }

}
