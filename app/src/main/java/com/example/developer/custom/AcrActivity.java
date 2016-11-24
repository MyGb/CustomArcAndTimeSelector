package com.example.developer.custom;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.developer.custom.widget.Arc;

import java.util.ArrayList;

/**
 * Created by Developer on 2016/9/9.
 */
public class AcrActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acr);
        final Arc arc = (Arc) findViewById(R.id.arc);
        ArrayList<String> list = new ArrayList<>();
        list.add("one");
        list.add("two");
        list.add("three");
        arc.setPointNum(list.size(), list);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arc.progressChange(1);
            }
        },2000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arc.progressChange(2);
            }
        },4000);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                arc.progressChange(3);
            }
        },6000);
    }
}
