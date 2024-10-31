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