# 深入理解JAVA并发与集合

### 前言

我希望写多篇文章，组合成一份文章集，包含关于JAVA并发与集合的入门原理、常见问题、代码例子证明等

读者阅读它，能像读小说一样，从第一篇文章，读到最后一篇文章，尽量把讳莫如深难懂的内容写得简单易懂，当然这也需要读者具有一定的JAVA功底

并发问题是比较抽象的，一个并发程序的执行结果可能会出乎程序员的预料，并发的不确定性让编程工作具有挑战性

### 声明

转载请注明出处：https://github.com/peteryuanpan/notebook/blob/master/深入理解JAVA并发与集合

作者：潘缘，来自电子科技大学，2017届

本文部分内容来自鲁班学院的课程，感谢老师们以及同学们对我的帮助，这里尤其感谢一下fox老师，您的课是讲得真好！

参考的书籍：《深入理解JAVA虚拟机》、《JAVA并发编程之美》、《JAVA并发编程实战》

参考的优秀博文：[Snailclimb/JavaGuide](https://github.com/Snailclimb/JavaGuide)

### 并发的定义

在计算机科学中，并发是一种能力，指在一个系统中，拥有多个计算，这些计算有同时执行、潜在交互的特性，但不影响计算结果

> In computer science, concurrency is the ability of different parts or units of a program, algorithm, or problem to be executed out-of-order or in partial order, without affecting the final outcome. From https://en.wikipedia.org/wiki/Concurrency_(computer_science)

### 集合的定义

在计算机科学中，集合是一个抽象的概念，它的具象容器可装载零至多个，有相同类型且可放在一起操作的数据

> In computer science, a collection or container is a grouping of some variable number of data items (possibly zero) that have some shared significance to the problem being solved and need to be operated upon together in some controlled fashion. A collection is a concept applicable to abstract data types, and does not prescribe a specific implementation as a concrete data structure. From https://en.wikipedia.org/wiki/Collection_(abstract_data_type)

### JAVA语言的水深

JAVA这潭大湖，从上往下，一共有7层（人为定义的）

- JAVA语法
- JAVA字节码
- JVM原理
- JVM源码
- 操作系统层
- 汇编码层
- CPU层

在[深入理解JAVA虚拟机-第一至三层](../深入理解JAVA虚拟机-第一至三层)文章集中，我强调了文章内容只包含1-3层，不考虑4层以下，且只在单线程的情况下总结JVM原理，而本篇文章集需要讨论的是多线程、高并发问题，这就需要涉及到一些操作系统与CPU原理

### 如何学习

学习方向：基础概念 + 代码例子 => 源码阅读 + 图解分析 => 应用实战

学习方法
- 看视频和文章，辩证地理解清楚多个基础概念，要有代码例子，记录笔记（点）
- 自行阅读并发和集合类中的源码，同时画图分析，写成文章（线），形成文章集（面）
- 做高并发相关的工具或项目，用到线程池、原子类、集合等技术，从应用实战中理解

### 文章目录

#### 第0章：应用场景与源码分析
- 并发部分
  - [Thread](应用场景与源码分析/Thread.md)
  - [ThreadLocal](应用场景与源码分析/ThreadLocal.md)
  - [FutureTask](应用场景与源码分析/FutureTask.md)
  - [ReentrantLock]
  - [ReentrantReadWriteLock]
  - [Semaphore]
  - [CountDownLath]
  - [CyclicBarrier]
  - [ThreadPoolExecutor]
  - [ForkJoinPool]
- 集合部分
  - [ArrayList]
  - [LinkedList]
  - [Vector]
  - [CopyOnWriteArrayList]
  - [HashMap](应用场景与源码分析/HashMap.md)
  - [ConcurrentHashMap](应用场景与源码分析/ConcurrentHashMap.md)

#### 第1章：JAVA线程基础
- [JAVA线程基础](JAVA线程基础.md)
  - [进程与线程](JAVA线程基础.md#进程与线程)
  - [并发与并行](JAVA线程基础.md#并发与并行)
  - [启动线程](JAVA线程基础.md#启动线程)
    - [JAVA启动线程](JAVA线程基础.md#JAVA启动线程)
      - [Thread](JAVA线程基础.md#Thread)
      - [Runnable](JAVA线程基础.md#Runnable)
      - [Callable](JAVA线程基础.md#Callable)
      - [ExecutorService](JAVA线程基础.md#ExecutorService)
    - [JVM启动线程](JAVA线程基础.md#JVM启动线程)
  - [线程的生命周期](JAVA线程基础.md#线程的生命周期)
  - [等待唤醒机制](JAVA线程基础.md#等待唤醒机制)
    - [虚假唤醒](JAVA线程基础.md#虚假唤醒)
    - [Object等待唤醒](JAVA线程基础.md#Object等待唤醒)
    - [Thread等待唤醒](JAVA线程基础.md#Thread等待唤醒)
    - [LockSupport等待唤醒](JAVA线程基础.md#LockSupport等待唤醒)
  - [线程中断机制](JAVA线程基础.md#线程中断机制)
  - [并发基础概念](JAVA线程基础.md#并发基础概念)
    - [守护线程与用户线程](JAVA线程基础.md#守护线程与用户线程)
    - [线程上下文切换](JAVA线程基础.md#线程上下文切换)
    - [多线程模型](JAVA线程基础.md#多线程模型)
    - [用户态与内核态](JAVA线程基础.md#用户态与内核态)
    - [轻量级线程之协程](JAVA线程基础.md#轻量级线程之协程)

#### 第2章：JAVA内存模型
- [JAVA内存模型](JAVA内存模型.md)
  - TODO

#### 第3章：线程安全原理
- [线程安全原理](线程安全原理.md)
  - TODO

#### 第4章：线程池原理
- 线程池原理
  - TODO

#### 第5章：并发面试题
- 并发基础面试题
  - TODO
- 并发进阶面试题
  - TODO

#### 第6章：集合面试题
- [List面试题](List面试题.md)
  - TODO
- [HashMap面试题](HashMap面试题.md)
  - TODO
