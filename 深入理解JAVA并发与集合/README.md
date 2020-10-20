# 深入理解JAVA并发与集合

### 前言

我希望写多篇文章，组合成一份文章集，包含关于JAVA并发与集合的入门原理、常见问题、代码例子证明等

读者阅读它，能像读小说一样，从第一篇文章，读到最后一篇文章，尽量把讳莫如深难懂的内容写得简单易懂，当然这也需要读者具有一定的JAVA功底

并发问题是比较抽象的，一个并发程序的执行结果可能会出乎程序员的预料，并发的不确定性让编程工作具有挑战性

### 声明

转载请注明出处：https://github.com/peteryuanpan/notebook/blob/master/深入理解JAVA并发与集合

作者：潘缘，来自电子科技大学，2017届

本文部分内容来自鲁班学院的课程，感谢老师们以及同学们对我的帮助，这里尤其感谢一下fox老师，您的课是讲得真好！

还有两本书作为参考，一是《深入理解JAVA虚拟机》（第二版及第三版皆有），二是《JAVA并发编程实战》（原文版及译文版皆有）

除此外，也参考了网上的许多优秀博文，会在文章内容中引用说明

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

在[深入理解JAVA虚拟机-第一至三层](../深入理解JAVA虚拟机-第一至三层)文章集中，我强调了文章内容只包含1-3层，不考虑4层以下，且只在单线程的情况下总结JVM的原理，而本篇文章集需要讨论的是多线程、高并发问题，这就必须要深入到第4层以下，涉及操作系统原理、CPU原理（只原理，不源码）

### 如何学习

两个方向：概念、应用

学习方法
- 通过定义和详细的例子，辩证地理解清楚每个概念，记录笔记，形成点=>线=>面
- 将并发应用到集合中去，写例子测试，阅读源码理解

### 文章目录

#### 第0章：并发基础概念
- [并发基础概念](并发基础概念.md)

#### 第A章：java.util.HashMap
- [HashMap源码分析](HashMap源码分析.md)
  - TODO
- [HashMap面试题](HashMap面试题.md)
  - TODO
  
#### 第B章：java.util.concurrent.ConcurrentHashMap
- [ConcurrentHashMap源码分析](ConcurrentHashMap源码分析.md)
  - TODO
- [ConcurrentHashMap面试题](ConcurrentHashMap面试题.md)
  - TODO
  
#### 第C章：java.util.List
- [ArrayList源码分析]
  - TODO
- [LinkedList源码分析]
  - TODO
- [Vector源码分析]
  - TODO
- [CopyOnWriteArrayList源码分析]
  - TODO
- [List面试题]
  - TODO
