package com.senninha.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Coded by senninha on 2020/1/21
 */
public class CommonUtil {
    public static String getTmpFileDirectory() {
        return System.getProperty("java.io.tmpdir", "/tmp/");
    }

    public static void appendWithLF(String content, String fileName) {
        append(content, fileName, true);
    }

    public static void append(String content, String fileName, boolean needLF) {
        File file = new File(fileName);
        if (file.isDirectory()) {
            throw new RuntimeException(String.format("%s is a directory"));
        }
        if (!file.exists()) {
            int endIndex = fileName.lastIndexOf(File.separator);
            String dir = fileName.substring(0, endIndex == -1 ? fileName.length() : endIndex);
            File f = new File(dir);
            f.mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(fileName, true)) {
            fos.write(content.getBytes());
            if (needLF) {
                fos.write('\n');
            }
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
