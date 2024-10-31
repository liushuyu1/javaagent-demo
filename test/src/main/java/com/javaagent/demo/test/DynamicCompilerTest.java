package com.javaagent.demo.test;

import com.taobao.arthas.compiler.DynamicCompiler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * 编写arthas动态编译java文件的demo
 */
public class DynamicCompilerTest {

    public static void main(String[] args) throws IOException {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        //使用arthas动态编译resources文件夹下的D.java成D.class，并将D.class输出到resources目录下
        DynamicCompiler dynamicCompiler = new DynamicCompiler(contextClassLoader);
        String sourceCode = FileUtils.readFileToString(new File("test/src/main/resources/D.java"));
        dynamicCompiler.addSource("D", sourceCode);
        Map<String, byte[]> byteCodes = dynamicCompiler.buildByteCodes();
        File outputDir = new File("test/src/main/resources");

        for (Map.Entry<String, byte[]> entry : byteCodes.entrySet()) {
            File byteCodeFile = new File(outputDir, StringUtils.replace(entry.getKey(), ".", File.separator) + ".class");
            FileUtils.writeByteArrayToFile(byteCodeFile, entry.getValue());
        }
    }
}
