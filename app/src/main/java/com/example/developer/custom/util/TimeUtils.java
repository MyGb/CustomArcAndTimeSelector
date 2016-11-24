package com.example.developer.custom.util;

import java.util.Calendar;

/**
 * Created by Developer on 2016/8/30.
 */
public class TimeUtils {
    /**
     * 返回用户选择的时间和当前时间的差值
     *
     * @param time
     * @return
     */
    public static int getDifferenceTime(final int time) {
        Calendar calendar = Calendar.getInstance();
        final int totalTime = (calendar.get(Calendar.HOUR_OF_DAY)) * 60 + calendar.get(Calendar.MINUTE);
        int resultTime = time - totalTime;
        if (resultTime <= 0) {
            resultTime = resultTime + 24 * 60;
        }
        return resultTime;
    }

    /**
     * 判断该时间是否有效
     *
     * @param currentTime
     * @param minHour
     * @param maxHour
     * @return
     */
    public static boolean isValid(final int currentTime, final int minHour, final int maxHour) {
        if (minHour == 0 && maxHour == 0) {
            return true;
        }
        if (currentTime < minHour * 60 || currentTime > maxHour * 60) {
            return false;
        }
        return true;
    }
}
