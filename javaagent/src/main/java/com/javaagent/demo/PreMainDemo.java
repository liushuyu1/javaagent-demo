package com.javaagent.demo;


import com.taobao.arthas.compiler.DynamicCompiler;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Map;

/**
 * 编写了一个javaagent的premain的Demo
 * premain是可以在别的java项目运行前进行拦截类加载流程。在别的项目启动时加上运行参数
 * java -javaagent:javaagent.jar -jar application.jar
 */
@Slf4j
public class PreMainDemo {


    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("PreMain start");
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer){
                log.info("#####################准备搞事"+className);
                /**
                 * 少量代码修改
                 * 使用javassist技术进行字节码修改
                 */
                if("com/javaagent/demo/A".equals(className)){
                    try {
                        log.info("###########################遇到了A");
                        // 从ClassPool获得CtClass对象
                        ClassPool classPool = ClassPool.getDefault();
                        classPool.insertClassPath(new LoaderClassPath(loader));
                        log.info("########1");
                        final CtClass clazz = classPool.get("com.javaagent.demo.A");
                        log.info("########2");
                        CtMethod convertToAbbr = clazz.getDeclaredMethod("hello");
                        log.info("########3");
                        String methodBody = "System.out.println(\"A.hello()被premain在加载时重构了！\");";
                        convertToAbbr.setBody(methodBody);
                        log.info("########4");
                        // 返回字节码，并且detachCtClass对象
                        byte[] byteCode = clazz.toBytecode();
                        // 将CtClass从ClassPool中移除，预防OOM
                        clazz.detach();
                        log.info("########5");
                        classfileBuffer = byteCode;
                    } catch (Exception ex) {
                        log.error(ex.getMessage(),ex);
                    }
                }

                /**
                 * 少量代码修改可以使用上面方法，大量代码修改建议使用下面方法
                 * 1.使用Arthas动态编译的功能将之前已经修改好的java文件转换成类字节码
                 * 2.然后加载到内存中替换掉原来的类
                 * 3.比如下面的就是将修改后的C.java文件替换掉原来的C.class类。
                 */
                if("com/javaagent/demo/C".equals(className)){
                    log.info("###########################遇到了C");
                    // 使用 Arthas 动态编译
                    DynamicCompiler dynamicCompiler = new DynamicCompiler(Thread.currentThread().getContextClassLoader());

                    String sourceCode = null;
                    try {
                        sourceCode = FileUtils.readFileToString(new File("src/main/resources/C.java"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    dynamicCompiler.addSource("C", sourceCode);
                    Map<String, byte[]> byteCodes = dynamicCompiler.buildByteCodes();

                    for (Map.Entry<String, byte[]> entry : byteCodes.entrySet()) {
                        log.info("entry.getKey:" + entry.getKey());
                        if(entry.getKey().endsWith("C")){
                            classfileBuffer = entry.getValue();
                            break;
                        }
                    }
                }

                return classfileBuffer;
            }
        });
    }

}
