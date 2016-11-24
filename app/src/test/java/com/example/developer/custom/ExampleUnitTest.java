package com.example.developer.custom;

import org.junit.Test;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        //assertEquals(4, 2 + 2);
        byte[] data = new byte[]{(byte) 0xfb, (byte) 0x00, (byte) 0x00, (byte) 0x07};
        System.out.print(bytesToHexString(data, data.length)+"\n");
        System.out.print(bytesToHexString2(data, data.length));
    }


    public static String bytesToHexString(byte[] src, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    public static String bytesToHexString2(byte[] src, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < len; i++) {
            long v = src[i] & 0xFF;
            String hv = Long.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}