package com.jayfeng.lesscode.update.app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.jayfeng.lesscode.core.UpdateLess;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        String updateJson = "{\n" +
                "      \"vercode\":112,\n" +
                "      \"vername\":\"V1.1\",\n" +
                "      \"download\":\"http://www.apk.anzhi.com/data3/apk/201506/09/3a978f27369b4a8bf6de1270da9871ec_86281300.apk\",\n" +
                "      \"log\":\"upgrade content\"\n" +
                "      }";
        UpdateLess.$check(this, updateJson);
    }

}
