package com.javaagent.demo.test;

import com.javaagent.demo.common.A;
import com.javaagent.demo.common.C;


/**
 * 测试javaagent的premain方法
 */
public class PreMainTest {

    public static void main(String[] args) {

        /**
         * 修改之前的A
         * public class A {
         *     public void hello(){
         *         System.out.println("A.hello()被调用啦！！");
         *     }
         * }
         * 使用premain成功修改之后的A
         * public class A {
         *     public void hello(){
         *         System.out.println("A.hello()被premain在加载时重构了！");
         *     }
         * }
         */
        A a = new A();
        a.hello();//如果输出"A.hello()被premain在加载时重构了！"，则表示premain成功了

        /**
         * 修改之前的C
         * public class C {
         *     public void hello(){
         *         System.out.println("C.hello()被调用啦！！");
         *     }
         * }
         * 使用premain成功修改之后的A
         * public class C {
         *     public void hello(){
         *         System.out.println("C.hello()被Arthas重新编译加载啦！！");
         *     }
         * }
         */
        C c = new C();
        c.hello();//如果输出"C.hello()被Arthas重新编译加载啦！！"，则表示premain成功了
    }
}
