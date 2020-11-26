- [JVM性能调优](#JVM性能调优)
  - [基础故障处理工具](#基础故障处理工具)
    - [jps-虚拟机进程状况工具](#jps-虚拟机进程状况工具)
    - [jstat-虚拟机统计信息监视工具](#jstat-虚拟机统计信息监视工具)
    - [jinfo-Java配置信息工具](#jinfo-Java配置信息工具)
    - [jmap-Java内存映像工具](#jmap-Java内存映像工具)
    - [jhat-虚拟机堆转储快照分析工具](#jhat-虚拟机堆转储快照分析工具)
    - [jstack-Java堆栈跟踪工具](#jstack-Java堆栈跟踪工具)
  - [可视化故障处理工具](#可视化故障处理工具)
    - [JHSDB-基于服务性代理的调试工具](#JHSDB-基于服务性代理的调试工具)
    - [JConsole-Java监视与管理控制台](#JConsole-Java监视与管理控制台)
  - [生产环境中的故障处理工具](#生产环境中的故障处理工具)
    - [VisualVM-多合一故障处理工具](#VisualVM-多合一故障处理工具)
    - [Arthas-Alibaba开源的Java诊断工具](#Arthas-Alibaba开源的Java诊断工具)
  - [性能调优记录](#性能调优记录)
    - [常用调优命令](#常用调优命令)
    - [程序死循环问题排查](#程序死循环问题排查)
    - [程序死锁问题排查](#程序死锁问题排查)
    - [CPU占用过高问题排查](#CPU占用过高问题排查)
    - [JAVA反射是否真的很慢](#JAVA反射是否真的很慢)

# JVM性能调优

> 文章标题中包含了“性能调优”，但是，我个人觉得，调优案例这一部分，目前手上有的书本资料远远不够，《深入理解JAVA虚拟机》只是描述了一下案例简介，完全没有深入分析（当然也理解，篇幅有限，不可能做深入分析）。这一部分，需要结合实际项目来分析，成本比较高，我手上一时半会没有好的项目例子。因此呢，我将文章核心放小一些：至少对JVM各种运维工具进行一次介绍，介绍它们的基本功能，能实战演练的就尝试一次，会涉及到的工具有：jps、jstat、jinfo、jmap、jhat、jstack、JHSDB、JConsole、VisualVM、Arthas。其中重点介绍 VisualVM 和 Arthas 两款工具

> 在不断学习的过程中，我接触到了许多实战例子，我将它们逐一记录了下来，同时，我想到是否可以多维度的模拟性能调优案例来自行测试，比如搭建一套Spring Web应用，不断模拟用户端请求，创建大量的Bean，通过VisualVM或者Arthas工具查看内存调用情况。至此我的思路就展开了

### 基础故障处理工具

安装了JAVA后，在 %JAVA_HOME%/bin/ 下存在许多工具，它们是JDK内部自带的工具，用于问题排查和分析

这些工具的源码都在 https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/tree/master/jdk/src/share/classes/sun/tools ，都是开源的，且是JAVA代码

![image](https://user-images.githubusercontent.com/10209135/100352674-505c8d80-3028-11eb-809e-90fce04409af.png)

#### jps-虚拟机进程状况工具

jps，JVM Process Status Tool，显示指定系统内所有的 Hotspot 虚拟机进程。查看源码：[Jps.java](https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/jdk/src/share/classes/sun/tools/jps/Jps.java)

命令指南
```
jps -help
usage: jps [-help]
       jps [-q] [-mlvV] [<hostid>]

Definitions:
    <hostid>:      <hostname>[:<port>]
```

通过如下方法可以打印本地电脑上 jps 命令输出的本地 hsperfdata 文件所在文件夹

```java
package jtools;

import sun.misc.VMSupport;

public class JPSTempFile {

    public static void main(String[] args) {
        System.out.println(VMSupport.getVMTemporaryDirectory());
    }
}
```

输出结果
```
C:\Users\Admin\AppData\Local\Temp\
```

最终路径 = 输出结果 + "hsperfdata_<Username>"（我这里是Admin）

![image](https://user-images.githubusercontent.com/10209135/100350483-ef7f8600-3024-11eb-96ee-d0e93d7191cc.png)

再查询jps命令，可以看出来是一一对应的

```
jps -l
15976 sun.tools.jps.Jps
10876
5692 org.jetbrains.jps.cmdline.Launcher
```

#### jstat-虚拟机统计信息监视工具

jstat，JVM Statistics Monitoring Tool，是用于监视虚拟机各种运行状态信息的命令行工具。它可以显示本地或者远程虚拟机进程中 类加载、内存、垃圾收集、JIT编译 等运行数据。jstat 可以理解为 jconsole、jvisualvm 图形界面工具的命令行版本

命令指南

```
jstat
invalid argument count
Usage: jstat -help|-options
       jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]

Definitions:
  <option>      An option reported by the -options option
  <vmid>        Virtual Machine Identifier. A vmid takes the following form:
                     <lvmid>[@<hostname>[:<port>]]
                Where <lvmid> is the local vm identifier for the target
                Java virtual machine, typically a process id; <hostname> is
                the name of the host running the target Java virtual machine;
                and <port> is the port number for the rmiregistry on the
                target host. See the jvmstat documentation for a more complete
                description of the Virtual Machine Identifier.
  <lines>       Number of samples between header lines.
  <interval>    Sampling interval. The following forms are allowed:
                    <n>["ms"|"s"]
                Where <n> is an integer and the suffix specifies the units as
                milliseconds("ms") or seconds("s"). The default units are "ms".
  <count>       Number of samples to take before terminating.
  -J<flag>      Pass <flag> directly to the runtime system.
```

|命令|解释|
|--|--|
|jstat -class pid|显示加载class的数量，及所占空间等信息|
|jstat -compiler pid|显示VM实时编译的数量等信息|
|jstat -gc pid|可以显示gc的信息，查看gc的次数，及时间|其中最后五项，分别是young gc的次数，young gc的时间，full gc的次数，full gc的时间，gc的总时间|
|jstat -gccapacity|可以显示，VM内存中三代（young,old,perm）对象的使用和占用大小，如：PGCMN显示的是最小perm的内存使用量，PGCMX显示的是perm的内存最大使用量，PGC是当前新生成的perm内存占用量，PC是但前perm内存占用量|其他的可以根据这个类推， OC是old内纯的占用量|
|jstat -gcnew pid|new对象的信息|
|jstat -gcnewcapacity pid|new对象的信息及其占用量|
|jstat -gcold pid|old对象的信息|
|jstat -gcoldcapacity pid|old对象的信息及其占用量|
|jstat -gcpermcapacity pid| perm对象的信息及其占用量|
|jstat -util pid|统计gc信息统计|
|jstat -printcompilation pid|当前VM执行的信息|

以 -gc 为例子，先通过 jps 获取 pid，然后执行 jstat -gc <pid>

```
jstat -gc 16952
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
2048.0 2048.0  0.0    0.0   12800.0   5329.2   34304.0      0.0     4480.0 776.7  384.0   76.6       0    0.000   0      0.000    0.000
```

#### jinfo-Java配置信息工具

#### jmap-Java内存映像工具

#### jhat-虚拟机堆转储快照分析工具

#### jstack-Java堆栈跟踪工具

### 可视化故障处理工具

#### JHSDB-基于服务性代理的调试工具

#### JConsole-Java监视与管理控制台

### 生产环境中的故障处理工具

#### VisualVM-多合一故障处理工具

TODO

安装插件VisualGC，完成后就可以查看VisualGC界面了，该界面对于分析非常有用

![image](https://user-images.githubusercontent.com/10209135/100307198-37c88500-2fe0-11eb-9741-5d8f569b2039.png)

#### Arthas-Alibaba开源的Java诊断工具

### 性能调优案例

#### 常用调优命令

java -XX:+PrintCommandLineFlags -version，查看JVM常用参数

```
-XX:InitialHeapSize=267373952 -XX:MaxHeapSize=4277983232 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC
java version "1.8.0_231"
Java(TM) SE Runtime Environment (build 1.8.0_231-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.231-b11, mixed mode)
```

java -XX:+PrintFlagsFinal -version，查看JVM主要参数

```
    uintx MarkStackSize                             = 4194304                             {product}
    uintx MarkStackSizeMax                          = 536870912                           {product}
    uintx MarkSweepAlwaysCompactCount               = 4                                   {product}
    uintx MarkSweepDeadRatio                        = 1                                   {product}
     intx MaxBCEAEstimateLevel                      = 5                                   {product}
     intx MaxBCEAEstimateSize                       = 150                                 {product}
    uintx MaxDirectMemorySize                       = 0                                   {product}
     bool MaxFDLimit                                = true                                {product}
    uintx MaxGCMinorPauseMillis                     = 4294967295                          {product}
    uintx MaxGCPauseMillis                          = 4294967295                          {product}
    uintx MaxHeapFreeRatio                          = 100                                 {manageable}
    uintx MaxHeapSize                              := 4278190080                          {product}
     intx MaxInlineLevel                            = 9                                   {product}
     intx MaxInlineSize                             = 35                                  {product}
     intx MaxJNILocalCapacity                       = 65536                               {product}
     intx MaxJavaStackTraceDepth                    = 1024                                {product}
     intx MaxJumpTableSize                          = 65000                               {C2 product}
     intx MaxJumpTableSparseness                    = 5                                   {C2 product}
     intx MaxLabelRootDepth                         = 1100                                {C2 product}
     intx MaxLoopPad                                = 11                                  {C2 product}
    uintx MaxMetaspaceExpansion                     = 5451776                             {product}
    uintx MaxMetaspaceFreeRatio                     = 70                                  {product}
    uintx MaxMetaspaceSize                          = 4294901760                          {product}
    uintx MaxNewSize                               := 1426063360                          {product}
     intx MaxNodeLimit                              = 75000                               {C2 product}

...

java version "1.8.0_231"
Java(TM) SE Runtime Environment (build 1.8.0_231-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.231-b11, mixed mode)
```

#### 程序死循环问题排查

#### 程序死锁问题排查

#### CPU占用过高问题排查

#### JAVA反射是否真的很慢

