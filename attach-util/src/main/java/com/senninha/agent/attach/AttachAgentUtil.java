package com.senninha.agent.attach;

import com.sun.tools.attach.VirtualMachine;

/**
 * Coded by senninha on 2020/1/22
 */
public class AttachAgentUtil {
    public static void main(String[] args) throws Exception {
        // args[1] = pid; args[0] = agentPath; args[2] = param
        VirtualMachine vm = VirtualMachine.attach(args[1]);
        vm.loadAgent(args[0], args[2]);
    }
}
