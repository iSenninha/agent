package com.senninha.agent.attach;

import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.IOException;


/**
 * Coded by senninha on 2020/1/22
 */
public class AttachAgentUtil {
    public static void main(String[] args) throws Exception {
        // args[1] = pid; args[0] = agentPath; args[2] = param
        String pid = args[1];
        String command = args[2];
        String agentPath = args[0];
        if (handleStartJmx(command, agentPath, pid)) {
            return;
        }
        VirtualMachine vm = VirtualMachine.attach(pid);
        vm.loadAgent(agentPath, command);
    }

    private static boolean handleStartJmx(String command, String agentPath, String pid) {
        if (!"jmxStart".equals(command)) {
            return false;
        }
        try {
            String jmxServiceUrl = getJmxServiceUrl(pid);
            System.out.println("jmx service:" + jmxServiceUrl);
        } catch (Exception e) {
            System.err.println("start jmx fail!");
            e.printStackTrace();
        }
        return true;
    }

    private static String getJmxServiceUrl(String pid) throws Exception {
        String jmxServiceUrl;
        VirtualMachine vm;
        String jmxAgentAddressProp = "com.sun.management.jmxremote.localConnectorAddress";
        // 1. attach vm
        vm = VirtualMachine.attach(pid);
        try {
            // 2. has start jmx?
            jmxServiceUrl = (String) vm.getAgentProperties().get(jmxAgentAddressProp);
            if (jmxServiceUrl != null) {
                return jmxServiceUrl;
            }

            // 3. find jmx-agent jar
            String home = vm.getSystemProperties().getProperty("java.home");
            boolean isJar = true;
            // Normally in ${java.home}/jre/lib/management-agent.jar but might
            // be in ${java.home}/lib in build environments.
            String agentPath = home + File.separator + "jre" + File.separator + "lib" + File.separator
                    + "management-agent.jar";
            File f = new File(agentPath);
            if (!f.exists()) {
                agentPath = home + File.separator + "lib" + File.separator + "management-agent.jar";
                f = new File(agentPath);
                if (!f.exists()) {
                    // open jdk only has *.so
                    isJar = false;
                }
            }
            agentPath = f.getCanonicalPath();
            String agentOption = "com.sun.management.jmxremote";
            if (isJar) {
                vm.loadAgent(agentPath, agentOption);
            } else {
                vm.loadAgentLibrary("management_agent", agentOption);
            }
            // 4. find jmx address again
            jmxServiceUrl = (String) vm.getAgentProperties().get(jmxAgentAddressProp);
            if (jmxServiceUrl == null) {
                throw new RuntimeException("start jmx fail!!!!");
            }
        } finally {
            try {
                vm.detach();
            } catch (IOException e) {
                // ignore
            }
        }
        return jmxServiceUrl;
    }

}
