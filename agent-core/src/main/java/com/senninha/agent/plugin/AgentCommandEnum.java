package com.senninha.agent.plugin;

import com.google.gson.GsonBuilder;
import com.senninha.util.CommonUtil;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.jsr223.GroovyScriptEngineImpl;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

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
    GROOVY_SCRIPT("gs", "gs scriptPath \n output will be print at tmp directory") {
        @Override
        public void action(String[] param, Instrumentation instrumentation) {
            FileOutputStream fos;
            Date current = new Date();
            try {
                fos = new FileOutputStream(CommonUtil.getTmpFileDirectory() + File.separator + "agent.log", true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }
            try {
                GroovyShell groovyShell = new GroovyShell(Thread.currentThread().getContextClassLoader());
                String script;
                FileInputStream sis = null;
                try {
                    sis = new FileInputStream(param[1]);
                    byte[] b = new byte[1024];
                    StringBuilder sb = new StringBuilder(1024);
                    int read = sis.read(b);
                    while (read != -1) {
                        sb.append(new String(b, 0, read, "utf-8"));
                        read = sis.read(b);
                    }
                    script = sb.toString();
                } catch (Exception e) {
                    fos.write(e.getMessage().getBytes());
                    fos.write('\n');
                    return;
                } finally {
                    if (sis != null) {
                        sis.close();
                    }
                }
                Object result = groovyShell.evaluate(script);
                String json = new GsonBuilder().setPrettyPrinting().create().toJson(result);

                fos.write(current.toString().getBytes());
                fos.write('\n');
                fos.write(json.getBytes());
                fos.write('\n');
                fos.write(current.toString().getBytes());
                fos.write('\n');
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    fos.write(e.getMessage().getBytes());
                    fos.write('\n');
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                    }
                }
            }
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
        System.err.println(String.format("can not find %s", param[0]));
        return null;
    }
}
