# javaagent介绍
## 什么是javaagent
javaagent字面意思java代理，其实就是类的代理，有点类似于jdk里面的对象的代理。
它允许在java项目启动时或者运行时在外部对java项目中的类加载进行拦截和干预，
比如对原先类的字节码进行删除或者修改之类的操作。

## 有什么用
使用场景比如说
+ 修改第三方类库jar包中的类字节码（实现方法：通过premain）
> 有个第三方的jar包类库，里面有个类的方法或者功能代码之类的不符合你的需求，
你需要修改他。但是总所周知，第三方jar包类库你是没有源码，修改不了的。
这时候你就可以通过javaagent来拦截这个类加载时候的字节码，然后通过修改字节码，
返回新的类字节码给jvm，这样就能满足你的需求了。

+ 热部署其他环境的代码（实现方法：通过agentmain）
> 平时我们代码更新了，然后需要将功能部署到测试环境，又不想重启测试环境。
此时我们就可以通过agentmain方式，在不重启测试环境的情况下将新代码部署到测试环境中

## 现在有哪些地方用到了这个技术
1. 破解IDEA的注册码，需要用上javaagent，绕过IDEA的注册验证逻辑
2. IDE的debug功能，通过点击IDE的编辑框可以干预运行中的代码（应该是在启动时在类字节码中埋点）
3. sonar的代码扫描，计算单元测试覆盖率
4. alibaba的ttl线程池实现。（通过javaagent将线程池和线程的字节码修改掉）
5. alibaba的热部署工具

## 我在什么地方用过
之前项目中需要引用platon sdk的jar包，
但是里面有个NetworkParameters类中的功能并不能满足我的需求。
由于这个jar包很多项目都在使用，所以我并不能修改jar包的代码。
此时我就用了这个技术，在导入这个jar包，加载这个类的时候，
我使用了javaagent对类加载进行拦截和修改类字节码实现了功能的替换。

## 怎么用javaagent
网上太多参考了，推荐下面两个链接
1. premain使用：https://www.cnblogs.com/stateis0/p/9062199.html
2. agentmain使用： https://www.cnblogs.com/stateis0/p/9062201.html

## 写了个简单的demo
https://github.com/liushuyu1/javaagent-demo
+ [common]模块只是一些简单的类
+ [javaagent]模块是javaagent的demo
+ [test]模块是测试javaagent的demo
1. 使用IDE加载项目之后，先执行mvn clean install
2. 测试premain请运行PreMainTest，然后观察控制台
3. 测试agentmain先运行AgentMainTestStep1，然后运行AgentMainTestStep2，然后观察控制台
4. 测试Arthas的动态编译字节码，请运行DynamicCompilerTest

## 需要注意的点
### 1.agentmain方法的编写
这种方式对类的修改是由限制的，对比原来的老类，由如下要求：
1. 父类是同一个；
2. 实习那的接口数也要相同；
3. 类访问符必须一致；
4. 字段数和字段名必须一致；
5. 新增的方法必须是 private static/final 的；
6. 可以删除修改方法；
### 2.运行premain和agentmain
需要注意的是：
1. 很多时候，执行premain和agentmain的时候报错了并不会打印日志。
此时可以看看test的项目是否将premain和agentmain代码需要的包都依赖了
2. 项目执行失败，可以看看各个包的版本是否有问题，以及jdk的版本