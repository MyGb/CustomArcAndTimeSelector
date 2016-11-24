package com.example.developer.custom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.developer.custom.widget.CustomTimeSelector;

/**
 * Created by Developer on 2016/9/9.
 */
public class TimeSelectorActivity extends AppCompatActivity {
    private TextView mTimeHint1, mTimeHint2, mTimeHint3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeselector);
        mTimeHint1 = (TextView) findViewById(R.id.timeHint1);
        mTimeHint2 = (TextView) findViewById(R.id.timeHint2);
        mTimeHint3 = (TextView) findViewById(R.id.timeHint3);
        CustomTimeSelector customTimeSelector1 = (CustomTimeSelector) findViewById(R.id.customTimeSelector1);
        CustomTimeSelector customTimeSelector2 = (CustomTimeSelector) findViewById(R.id.customTimeSelector2);
        CustomTimeSelector customTimeSelector3 = (CustomTimeSelector) findViewById(R.id.customTimeSelector3);

        customTimeSelector1.setValueChangeListener(new CustomTimeSelector.ValueChangeListener() {
            @Override
            public void valueChange(int totalMinute) {
                setHint(totalMinute, mTimeHint1);
            }
        });

        customTimeSelector2.setParameter(1, 4);
        customTimeSelector2.setValueChangeListener(new CustomTimeSelector.ValueChangeListener() {
            @Override
            public void valueChange(int totalMinute) {
                setHint(totalMinute, mTimeHint2);
            }
        });

        mTimeHint3 = (TextView) findViewById(R.id.timeHint3);
        customTimeSelector3.setStep(30);
        customTimeSelector3.setValueChangeListener(new CustomTimeSelector.ValueChangeListener() {
            @Override
            public void valueChange(int totalMinute) {
                setHint(totalMinute, mTimeHint3);
            }
        });
    }

    private void setHint(final int totalMinute, TextView mTimeHint) {
        if (totalMinute == CustomTimeSelector.INVALID) {
            mTimeHint.setText("选择无效.");
            return;
        }
        final int hour = totalMinute / 60;
        final int minute = totalMinute % 60;
        mTimeHint.setText(hour + "小时" + minute + "分钟后完成.");
    }
}
