package com.javaagent.demo;


import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

/**
 * 编写了一个Agent的Demo
 */
@Slf4j
public class AgentMainDemo {

    public static void agentmain(String agentArgs, Instrumentation inst) {
        log.info("agentmain start");
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer){
                if("com/javaagent/demo/B".equals(className)){
                    try {
                        log.info("#####################准备搞事"+className);
                        // 从ClassPool获得CtClass对象
                        final ClassPool classPool = ClassPool.getDefault();
                        log.info("########1");
                        final CtClass clazz = classPool.get("com.javaagent.demo.B");
                        log.info("########2");
                        CtMethod convertToAbbr = clazz.getDeclaredMethod("hello");
                        log.info("########3");
                        log.info("原方法convertToAbbr.toString()："+convertToAbbr.toString());
                        log.info("原方法convertToAbbr.getLongName()："+convertToAbbr.getLongName());
                        log.info("原方法convertToAbbr.getName()："+convertToAbbr.getName());
                        log.info("原方法convertToAbbr.getGenericSignature()："+convertToAbbr.getGenericSignature());
                        log.info("原方法convertToAbbr.getSignature()："+convertToAbbr.getSignature());
                        log.info("原方法convertToAbbr.getMethodInfo()："+convertToAbbr.getMethodInfo());
                        log.info("原方法convertToAbbr.getMethodInfo2()："+convertToAbbr.getMethodInfo2());
                        log.info("原方法convertToAbbr.getMethodInfo().getDescriptor()："+convertToAbbr.getMethodInfo().getDescriptor());
                        log.info("原方法convertToAbbr.getMethodInfo2().getDescriptor()："+convertToAbbr.getMethodInfo2().getDescriptor());
                        String methodBody = "System.out.println(\"我被agentmain在运行时重构了！\");";
                        convertToAbbr.setBody(methodBody);
                        log.info("########4");
                        // 返回字节码，并且detachCtClass对象
                        byte[] byteCode = clazz.toBytecode();
                        //detach的意思是将内存中曾经被javassist加载过的Date对象移除，如果下次有需要在内存中找不到会重新走javassist加载
                        clazz.detach();
                        log.info("########5");
                        return byteCode;
                    } catch (Exception ex) {
                        log.info("########6");
                        log.error(ex.getMessage(),ex);
                    }
                }
                return classfileBuffer;
            }
        });
    }
}
