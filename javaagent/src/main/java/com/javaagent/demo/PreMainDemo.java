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
        log.info("进入premain方法啦！！！！！");
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer){
                /**
                 * 少量代码修改
                 * 使用javassist技术进行字节码修改
                 */
                if("com/javaagent/demo/common/A".equals(className)){
                    try {
                        log.info("准备搞事：使用premain重新加载com.javaagent.demo.common.A类");
                        // 从ClassPool获得CtClass对象
                        ClassPool classPool = ClassPool.getDefault();
                        classPool.insertClassPath(new LoaderClassPath(loader));
                        final CtClass clazz = classPool.get("com.javaagent.demo.common.A");
                        CtMethod convertToAbbr = clazz.getDeclaredMethod("hello");
                        String methodBody = "System.out.println(\"A.hello()被premain在加载时重构了！\");";
                        convertToAbbr.setBody(methodBody);
                        // 返回字节码，并且detachCtClass对象
                        byte[] byteCode = clazz.toBytecode();
                        // 将CtClass从ClassPool中移除，预防OOM
                        clazz.detach();
                        classfileBuffer = byteCode;
                    } catch (Exception ex) {
                        /**
                         * 需要注意的是：
                         * 很多时候，执行premain的时候报错了并不会打印日志。
                         * 此时可以看看运行javaagent的项目是否将以上代码需要的包都依赖了
                         * 这里需要依赖javassist
                         */
                        log.error(ex.getMessage(),ex);
                    }
                }

                /**
                 * 少量代码修改可以使用上面方法，大量代码修改建议使用下面方法
                 * 1.使用Arthas动态编译的功能将之前已经修改好的java文件转换成类字节码
                 * 2.然后加载到内存中替换掉原来的类
                 * 3.比如下面的就是将修改后的C.java文件替换掉原来的C.class类。
                 */
                if("com/javaagent/demo/common/C".equals(className)){
                    try {
                        log.info("准备搞事：使用premain重新加载com.javaagent.demo.common.C类");
                        // 使用 Arthas 动态编译
                        DynamicCompiler dynamicCompiler = new DynamicCompiler(Thread.currentThread().getContextClassLoader());
                        File cJava = new File("test/src/main/resources/C.java");
                        log.info("test/src/main/resources/C.java是否存在：" + cJava.exists());
                        String sourceCode = FileUtils.readFileToString(cJava);
                        dynamicCompiler.addSource("C", sourceCode);

                        /** 这里的byteCodes里面，一个类就有一份字节码，如果有内部类之类的就算多一份**/
                        Map<String, byte[]> byteCodes = dynamicCompiler.buildByteCodes();
                        for (Map.Entry<String, byte[]> entry : byteCodes.entrySet()) {
                            log.info("类的全限定名:" + entry.getKey());
                            if(entry.getKey().endsWith("C")){
                                classfileBuffer = entry.getValue();
                                break;
                            }
                        }
                    } catch (IOException e) {
                        /**
                         * 需要注意的是：
                         * 很多时候，执行premain的时候报错了并不会打印日志。
                         * 此时可以看看运行javaagent的项目是否将以上代码需要的包都依赖了
                         * 这里需要依赖
                         * arthas-memorycompiler
                         * commons-lang3
                         * org.apache.commons.io
                         */
                        log.error(e.getMessage(),e);
                    }
                }

                return classfileBuffer;
            }
        });
    }

}
