package com.javaagent.demo.test;

import com.javaagent.demo.common.B;

import java.util.concurrent.TimeUnit;


/**
 * 测试agentmain第一步先启动该类
 */
public class AgentMainTestStep1 {

    public static void main(String[] args) throws InterruptedException {
        /**
         * 执行预期：
         * 1.AgentMainTestStep1启动之后，b.hello()方法输出"B.hello()被调用啦！！"
         * 2.AgentMainTestStep1启动之后，b.hello()方法输出"我被agentmain在运行时重构了！"
         * 此时说明agentmain在运行时成功修改了B.class的字节码
         */
        while(true){
            B b = new B();
            b.hello();
            TimeUnit.SECONDS.sleep(3);
        }
    }
}
