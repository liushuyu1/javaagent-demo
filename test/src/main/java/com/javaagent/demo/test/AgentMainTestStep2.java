package com.javaagent.demo.test;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.List;

/**
 * 测试agentmain第二步启动该类
 */
public class AgentMainTestStep2 {

    public static void main(String[] args) throws Exception{
        //获取当前系统中所有 运行中的 虚拟机
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        for (VirtualMachineDescriptor vmd : list) {
            //如果虚拟机的名称为 xxx 则 该虚拟机为目标虚拟机，获取该虚拟机的 pid
            if (vmd.displayName().endsWith("AgentMainTestStep1")) {
                //attach功能介绍：https://www.jianshu.com/p/39d189961773
                //目标jvm禁用attach功能的方法：启动时加上jvm参数-XX:+DisableAttachMechanism
                VirtualMachine vm = VirtualMachine.attach(vmd.id());
                //然后加载 agent.jar 发送给该虚拟机
                vm.loadAgent("javaagent/target/javaagent-1.0.0.jar");
                vm.detach();
            }
        }
    }
}
