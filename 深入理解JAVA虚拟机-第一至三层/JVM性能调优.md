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

#### jps-虚拟机进程状况工具

#### jstat-虚拟机统计信息监视工具

#### jinfo-Java配置信息工具

#### jmap-Java内存映像工具

#### jhat-虚拟机堆转储快照分析工具

#### jstack-Java堆栈跟踪工具

### 可视化故障处理工具

#### JHSDB-基于服务性代理的调试工具

#### JConsole-Java监视与管理控制台

### 生产环境中的故障处理工具

#### VisualVM-多合一故障处理工具

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

