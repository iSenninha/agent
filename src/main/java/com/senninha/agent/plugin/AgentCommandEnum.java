package com.senninha.agent.plugin;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;

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
    };
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
        System.err.println(String.format("can not find%s", param[0]));
        return null;
    }
}
