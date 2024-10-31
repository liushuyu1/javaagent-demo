package com.javaagent.demo;


import com.javaagent.demo.common.B;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;

/**
 * 编写了一个agentmain的Demo
 * agentmain这种方式对类的修改是由限制的，对比原来的老类，由如下要求：
 * 1.父类是同一个；
 * 2. 实习那的接口数也要相同；
 * 3. 类访问符必须一致；
 * 4. 字段数和字段名必须一致；
 * 5. 新增的方法必须是 private static/final 的；
 * 6. 可以删除修改方法；
 */
@Slf4j
public class AgentMainDemo {

    public static void agentmain(String agentArgs, Instrumentation inst) throws UnmodifiableClassException {
        log.info("进入agentmain方法啦！！！！！");
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer){
                if("com/javaagent/demo/common/B".equals(className)){
                    try {
                        log.info("准备搞事：使用agentmain重新加载com.javaagent.demo.common.B类");
                        // 从ClassPool获得CtClass对象
                        final ClassPool classPool = ClassPool.getDefault();
                        classPool.insertClassPath(new LoaderClassPath(loader));
                        //对指定的类进行重构
                        final CtClass clazz = classPool.get("com.javaagent.demo.common.B");
                        CtMethod convertToAbbr = clazz.getDeclaredMethod("hello");
                        String methodBody = "System.out.println(\"我被agentmain在运行时重构了！\");";
                        convertToAbbr.setBody(methodBody);
                        // 返回字节码，并且detachCtClass对象
                        byte[] byteCode = clazz.toBytecode();
                        //detach的意思是将内存中曾经被javassist加载过的Date对象移除，如果下次有需要在内存中找不到会重新走javassist加载
                        clazz.detach();
                        return byteCode;
                    } catch (Exception ex) {
                        log.error(ex.getMessage(),ex);
                    }
                }
                return classfileBuffer;
            }
        },true);
        //通知jvm要重新加载哪个类
        inst.retransformClasses(B.class);
    }
}
