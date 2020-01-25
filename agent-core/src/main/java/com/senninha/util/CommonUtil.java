package com.senninha.util;

import java.io.*;

/**
 * Coded by senninha on 2020/1/21
 */
public class CommonUtil {
    public static String getTmpFileDirectory() {
        return System.getProperty("java.io.tmpdir", "/tmp/");
    }

    public static void appendWithLF(String content, String fileName) {
        append(content, fileName, true, null);
    }

    public static void appendWithLF(String content, String fileName, Exception e) {
        append(content, fileName, true, e);
    }

    private static class FakePrintWriter extends PrintWriter {
        public FakePrintWriter(OutputStream out, boolean autoFlush) {
            super(out, autoFlush);
        }
    }

    public static void append(String content, String fileName, boolean needLF, Exception e) {
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
        try (PrintWriter fakePrintWriter = new FakePrintWriter(new FileOutputStream(fileName, true), true)) {
            fakePrintWriter.write(content);
            if (needLF) {
                fakePrintWriter.write('\n');
            }
            if (e != null) {
                e.printStackTrace(fakePrintWriter);
                if (needLF) {
                    fakePrintWriter.write('\n');
                }
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
