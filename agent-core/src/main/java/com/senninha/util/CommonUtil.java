package com.senninha.util;

/**
 * Coded by senninha on 2020/1/21
 */
public class CommonUtil {
    public static String getTmpFileDirectory() {
        return System.getProperty("java.io.tmpdir", "/tmp/");
    }
}
