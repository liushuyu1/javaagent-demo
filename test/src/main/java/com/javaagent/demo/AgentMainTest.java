package com.javaagent.demo;

import com.sun.tools.attach.*;

import java.util.List;

/**
 * @Author liushuyu
 * @Date 2021/4/11 17:23
 * @Version
 * @Desc
 */
public class AgentMainTest {

    public static void main(String[] args) throws Exception{
        //获取当前系统中所有 运行中的 虚拟机
        System.out.println("running JVM start ");
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : list) {
            //如果虚拟机的名称为 xxx 则 该虚拟机为目标虚拟机，获取该虚拟机的 pid
            //然后加载 agent.jar 发送给该虚拟机
            System.out.println(vmd.displayName());
            if (vmd.displayName().endsWith("com.jdk8.demo.DemoApplication")) {
                VirtualMachine virtualMachine = VirtualMachine.attach(vmd.id());
                virtualMachine.loadAgent("javaagent-demo/src/main/resources/javaagent-demo-agentmain.jar");
                virtualMachine.detach();
            }
        }
    }
}
