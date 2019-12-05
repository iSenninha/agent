package com.senninha.agent;

import com.senninha.agent.plugin.AgentCommandEnum;

import java.lang.instrument.Instrumentation;

/**
 * agent main
 * Coded by senninha on 2019/12/3
 */
public class Agent {
    public static void premain(String args, Instrumentation inst) {
        if (args == null) {
            System.err.println("input args is null");
            return;
        }
        String[] param = args.split("_");
        AgentCommandEnum agentCommandEnum = AgentCommandEnum.valueOf(param);
        if (agentCommandEnum == null) {
            System.err.println(String.format("can not found command:%s", args));
            help();
            return;
        }
        agentCommandEnum.action(param, inst);
    }

    private static void help() {
        System.out.println("================help====================");
        for (AgentCommandEnum value : AgentCommandEnum.values()) {
            System.out.println(value.getDesc());
        }
        System.out.println("================help end====================");
    }

    public static void agentmain(String args, Instrumentation inst) {
        premain(args, inst);
    }
}
