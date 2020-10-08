# 深入理解JAVA多线程高并发

### 前言

可以说，这份文章集是 [深入理解JAVA虚拟机-第一至三层](https://github.com/peteryuanpan/notebook/blob/master/深入理解JAVA虚拟机-第一至三层) 的第8章，由于内容实在过多且重要，我将它单独作为一个文章集来写。同样的，将包括JAVA多线程高并发的入门原理、常见问题、代码例子证明等

### 声明

转载请注明出处：https://github.com/peteryuanpan/notebook/blob/master/深入理解JAVA多线程高并发

作者：潘缘，来自电子科技大学，2017届

本文部分内容来自鲁班学院的课程，感谢老师们以及同学们对我的帮助，这里尤其感谢一下fox老师，您的课是讲得真好！

还有两本书作为参考，一是《深入理解JAVA虚拟机》（第二版及第三版皆有），二是《JAVA并发编程实战》（原文版及译文版皆有）

### 多线程高并发的定义

无论讨论什么，我们先需要将定义讨论清楚，否则就会有无穷的争执

标题“多线程高并发”可以分为四个概念：进程、线程、并发、并行

进程（Process）是计算机程序关于某数据集合上的一次运行活动，它是CPU资源分配的最小单位

> In computing, a process is the instance of a computer program that is being executed by one or many threads. It contains the program code and its activity.

线程（Thread）是进程的真子集，它是CPU运算调度的最小单位

> In computer science, a thread of execution is the smallest sequence of programmed instructions that can be managed independently by a scheduler. In most cases a thread is a component of a process.

并发（Concurrency）是指，进程B的开始时间是在进程A的开始时间与结束时间之间，则A和B是并发的

> In computer science, concurrency is the ability of different parts or units of a program, algorithm, or problem to be executed out-of-order or in partial order, without affecting the final outcome.

并行（Parallel Execution）是并发的真子集，指同一时间两个进程运行在不同的机器上或者同一个机器不同的核心上

> Parallel computing is a type of computation where many calculations or the execution of processes are carried out simultaneously.

上面的概念基本都来自维基百科和百度百科，中英文不强对应，我只是将它们都列出来，便于多语言理解。关于并发与并行的概念借鉴了 https://www.zhihu.com/question/33515481 中starrynight给的答案，来自《深入理解计算机系统》。可以看出，即使已经尽量通俗了，进程与线程的概念还是比较抽象，我承认，进程与线程的概念不是一两句话能讲明白的，需要融入代码运行经验以及计算机发展史。在后面，我会专门章节，详细阐述进程与线程的区别、并发与并行的区别等

### 如何学习JAVA并发

两个方向：概念、应用

JAVA并发中概念非常多，比如并发三大特性（原子性、可见性、有序性），JMM内存模型，CPU缓存架构，缓存一致性协议，volatile，synchronzied，CAS，AQS，ReentrantLock，Semaphore&CyclicBarrier，ForkJoin，Atomic原子类，java.util.concurrent包下的类，各种锁机制（自适应自旋、偏向锁、轻量级锁、重量级锁、独占锁、共享锁、读写锁、公平锁、非公平锁），锁膨胀，线程池，线程复用

学习方法
- 通过定义和详细的例子，辩证地理解清楚每个概念
- 将多个概念写成文章，画成图形，融汇成“一个概念”，点=>线=>面的过程
- 做项目（找场景、创建场景），应用到多线程高并发，解决具体问题

并发问题是比较抽象的，一个并发程序它的执行结果可能会出乎程序编写者的预料，它的不完全可控性、不确定性有时让人抓狂

要把并发的理论放到实际应用项目中，去理解它，而不要只是停留在理论和概念

### 文章导读

**第1章是并发编程基础概念**。并发中有非常多的概念，因此我需要一篇文章来简单介绍它们，便于后续章节深入问题时可以做到一笔带过（请参考第1章的XX概念），这一章的中心思想是点到为止，不要过深的去探讨一个问题，把定义和基础概念解释清楚即可了，如果遇到一个概念一定需要深入的话，考虑把它放到后面章节中详细讲解

### 文章目录

#### 第1章：并发编程基础概念
- TODO
