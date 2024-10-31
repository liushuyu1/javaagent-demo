package com.javaagent.demo;

import com.taobao.arthas.compiler.DynamicCompiler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @Author liushuyu
 * @Date 2021/4/13 12:11
 * @Version
 * @Desc
 */
public class DynamicCompilerTest {



//    public static void main(String[] args) throws IOException {
//
//        // 使用 Arthas 动态编译
//        DynamicCompiler dynamicCompiler = new DynamicCompiler(Thread.currentThread().getContextClassLoader());
//
//        String sourceCode = FileUtils.readFileToString(new File("E:\\juzix\\IdeaProjects\\practice\\javaagent-demo\\src\\main\\resources\\NetworkParameters.java"));
//
//        dynamicCompiler.addSource("NetworkParameters", sourceCode);
//        Map<String, byte[]> byteCodes = dynamicCompiler.buildByteCodes();
//
//        File outputDir = new File("E:\\juzix\\IdeaProjects\\practice\\javaagent-demo\\src\\main\\resources");
//
//        for (Map.Entry<String, byte[]> entry : byteCodes.entrySet()) {
//            File byteCodeFile = new File(outputDir, StringUtils.replace(entry.getKey(), ".", File.separator) + ".class");
//            FileUtils.writeByteArrayToFile(byteCodeFile, entry.getValue());
//        }
//    }
}
