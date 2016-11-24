package com.example.developer.custom;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button mBtnTimeSelector, mBtnArc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        configView();
    }

    private void initView() {
        mBtnTimeSelector = (Button) findViewById(R.id.btn_timeSelector);
        mBtnArc = (Button) findViewById(R.id.btn_acr);
    }

    private void configView() {
        mBtnTimeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), TimeSelectorActivity.class));
            }
        });
        mBtnArc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AcrActivity.class));
            }
        });
    }


    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                mExitTime = System.currentTimeMillis();
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            } else {
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
