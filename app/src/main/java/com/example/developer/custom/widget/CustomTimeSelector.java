package com.example.developer.custom.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.example.developer.custom.R;
import com.example.developer.custom.util.TimeUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Developer on 2016/8/26.
 */
public class CustomTimeSelector extends LinearLayout {
    public final static int INVALID = -1;
    private final Calendar calendar = Calendar.getInstance();
    private NumberPicker hour_numberPicker;
    private NumberPicker minute_numberPicker;
    private boolean isSetStep = false;
    private String[] times;

    public interface ValueChangeListener {
        void valueChange(final int totalMinute);
    }

    public ValueChangeListener valueChangeListener;
    private int minHour, maxHour;

    private NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            getTotalMinute();
        }
    };

    public CustomTimeSelector(Context context) {
        super(context);
        init(context);
    }

    public CustomTimeSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTimeSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.custom_time_selector, this);
        hour_numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker1);
        minute_numberPicker = (NumberPicker) view.findViewById(R.id.numberPicker2);

        hour_numberPicker.setMaxValue(23);
        hour_numberPicker.setMinValue(0);

        minute_numberPicker.setMaxValue(59);
        minute_numberPicker.setMinValue(0);

        setNumberPickerProperty(hour_numberPicker);
        setNumberPickerProperty(minute_numberPicker);

        hour_numberPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        minute_numberPicker.setValue(calendar.get(Calendar.MINUTE));
    }

    private void setNumberPickerProperty(NumberPicker numberPicker) {
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                if (value < 10) {
                    return "0" + value;
                }
                return value + "";
            }
        });
        numberPicker.setOnValueChangedListener(onValueChangeListener);
    }


    /**
     * 设置最小预约时间和最大预约时间
     *
     * @param minHour
     * @param maxHour
     */
    public void setParameter(final int minHour, final int maxHour) {
        this.minHour = minHour;
        this.maxHour = maxHour;
        calendar.add(Calendar.HOUR_OF_DAY, minHour);
        hour_numberPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        calendar.setTime(new Date());
    }

    /**
     * 设置步长
     *
     * @param step
     */
    public void setStep(final int step) {
        isSetStep = true;
        final int currentMinute = calendar.get(Calendar.MINUTE);
        List<String> timesList = new ArrayList<>();
        int time = currentMinute;
        do {
            if (time < 60) {
                timesList.add(time + "");
            }
            if (time == 60) {
                timesList.add("00");
            }
            time = time + step;
        } while (time <= 60);
        timesList.add((time - 60) + "");
        times = timesList.toArray(new String[timesList.size()]);
        minute_numberPicker.setMaxValue(times.length - 1);
        minute_numberPicker.setDisplayedValues(times);
    }

    public void getTotalMinute() {
        if (null == valueChangeListener) {
            return;
        }
        final int totalMinute;
        if (isSetStep) {
            totalMinute = ((hour_numberPicker.getValue() * 60) + Integer.parseInt(times[minute_numberPicker.getValue()]));
        } else {
            totalMinute = ((hour_numberPicker.getValue() * 60) + (minute_numberPicker.getValue()));
        }
        final int time = TimeUtils.getDifferenceTime(totalMinute);
        if (!TimeUtils.isValid(time, minHour, maxHour)) {
            valueChangeListener.valueChange(INVALID);
            return;
        }
        valueChangeListener.valueChange(time);
    }

    public void setValueChangeListener(ValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
        getTotalMinute();
    }
}
