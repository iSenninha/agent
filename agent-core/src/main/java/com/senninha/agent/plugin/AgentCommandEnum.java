package com.senninha.agent.plugin;

import com.google.gson.GsonBuilder;
import com.senninha.util.CommonUtil;
import groovy.lang.GroovyShell;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * agent command
 * Coded by senninha on 2019/12/3
 */
public enum AgentCommandEnum {
    SAVE_INS_2_MAIN_PROCESS("saveIns2MainProcess",
            "add vmOptions like:-javaagent:/xx/agent-1.0-SNAPSHOT.jar=saveIns2MainProcess_targetClazzName_targetFieldName") {
        public void action(String[] param, Instrumentation instrumentation) {
            String targetClazzName = "com.senninha.util.objsize.ObjectSizeUtil";
            String fieldName = "instrumentation";
            if (param.length >= 2) {
                targetClazzName = param[1];
            }
            if (param.length >= 3) {
                fieldName = param[2];
            }
            try {
                System.err.println(String.format("load agent %s", targetClazzName));
                Class<?> targetClazz = Class.forName(targetClazzName);
                Field instrumentationField = targetClazz.getDeclaredField(fieldName);
                instrumentationField.setAccessible(true);
                instrumentationField.set(null, instrumentation);
                System.out.println("save ins 2 main process success");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    },
    GROOVY_SCRIPT("gs", "gs_scriptPath " +
            "\t execute script, output will be append on tmp directory") {
        @Override
        public void action(String[] param, Instrumentation instrumentation) {
            String logName = CommonUtil.getTmpFileDirectory() + File.separator + "agent.log";
            try {
                GroovyShell groovyShell = new GroovyShell(Thread.currentThread().getContextClassLoader());
                String script;
                try (FileInputStream sis = new FileInputStream(param[1])) {
                    byte[] b = new byte[1024];
                    StringBuilder sb = new StringBuilder(1024);
                    int read = sis.read(b);
                    while (read != -1) {
                        sb.append(new String(b, 0, read, StandardCharsets.UTF_8));
                        read = sis.read(b);
                    }
                    script = sb.toString();
                } catch (Exception e) {
                    CommonUtil.appendWithLF(e.getMessage(), logName);
                    return;
                }
                Object result = groovyShell.evaluate(script);
                String json = new GsonBuilder().setPrettyPrinting().create().toJson(result);
                String current = new Date().toString();
                CommonUtil.appendWithLF(current, logName);
                CommonUtil.appendWithLF(json, logName);
                CommonUtil.appendWithLF(current, logName);
            } catch (Exception e) {
                CommonUtil.appendWithLF(e.getMessage(), logName);
            }
        }
    },
    HOT_SWAP("hotSwap", "hotSwap_hotSwapClassDirectory " +
            "\t hot swap class output will be append on tmp directory") {
        @Override
        public void action(String[] param, Instrumentation instrumentation) {
            String logName = CommonUtil.getTmpFileDirectory() + File.separator + "hotSwap.log";
            CommonUtil.appendWithLF("start redefine", logName);
            String fileDirectory = param[1];
            File hotSwapDirectory = new File(fileDirectory);
            if (!hotSwapDirectory.isDirectory()) {
                CommonUtil.appendWithLF(String.format("%s doesn't exist or is a file", fileDirectory), logName);
                return;
            }
            Map<String, ClassDefinition> redefineClass = new HashMap<>(16);
            for (String redefineClassName : hotSwapDirectory.list()) {
                if (!redefineClassName.endsWith(".class")) {
                    continue;
                }
                File f = new File(redefineClassName);
                if (f.isDirectory()) {
                    continue;
                }
                int lastSeparator = redefineClassName.lastIndexOf(File.separator);
                String clazzName = redefineClassName.substring(lastSeparator == -1 ? 0 : lastSeparator + 1);
                clazzName = clazzName.replace(".class", "");
                Class<?> clazz;
                try {
                    clazz = Class.forName(clazzName);
                } catch (ClassNotFoundException e) {
                    CommonUtil.appendWithLF(String.format("%s can not find this class", redefineClassName), logName);
                    continue;
                }
                byte[] bytes = new byte[(int) f.length()];
                try (FileInputStream fileInputStream = new FileInputStream(f)) {
                    fileInputStream.read(bytes);
                } catch (FileNotFoundException e) {
                    CommonUtil.appendWithLF(e.getMessage(), logName);
                    continue;
                } catch (IOException e) {
                    CommonUtil.appendWithLF(e.getMessage(), logName);
                    continue;
                }
                redefineClass.put(clazz.getName(), new ClassDefinition(clazz, bytes));
            }
            for (Class loadedClass : instrumentation.getAllLoadedClasses()) {
                ClassDefinition classDefinition = redefineClass.get(loadedClass.getName());
                if (classDefinition == null) {
                    continue;
                }
                try {
                    instrumentation.redefineClasses(classDefinition);
                } catch (ClassNotFoundException e) {
                    CommonUtil.appendWithLF(e.getMessage(), logName);
                } catch (UnmodifiableClassException e) {
                    CommonUtil.appendWithLF(e.getMessage(), logName);
                }
                CommonUtil.appendWithLF(String.format("%s redefine success", loadedClass.getName()), logName);
            }
            CommonUtil.appendWithLF("finish redefine", logName);
        }
    },
    ;
    private String command;
    private String desc;

    AgentCommandEnum(String command, String desc) {
        this.command = command;
        this.desc = desc;
    }

    public String getCommand() {
        return command;
    }

    public String getDesc() {
        return desc;
    }

    public abstract void action(String[] param, Instrumentation instrumentation);

    public static AgentCommandEnum valueOf(String[] param) {
        if (param == null || param.length < 1) {
            System.err.println("command is null");
            return null;
        }
        for (AgentCommandEnum value : values()) {
            if (value.command.equals(param[0])) {
                return value;
            }
        }
        return null;
    }
}
