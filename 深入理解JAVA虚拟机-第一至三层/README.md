# 深入理解JAVA虚拟机-第一至三层

### 前言

我希望写多篇文章，组合成一份文章集，包含关于JAVA虚拟机的入门原理、常见问题、代码例子证明等。

读者阅读它，能像读小说一样，从第一篇文章，读到最后一篇文章，中间不带卡主，尽量把讳莫如深难懂的内容写得简单易懂。当然这也需要读者具有一定的JAVA功底。

### JAVA虚拟机的定义

无论讨论什么，我们先需要将定义讨论清楚，否则就会有无穷的争执。

JAVA虚拟机是一种能够运行JAVA字节码程序的虚拟机器。

> A Java virtual machine (JVM) is a virtual machine that enables a computer to run Java programs as well as programs written in other languages that are also compiled to Java bytecode. From https://en.wikipedia.org/wiki/Java_virtual_machine.

### JAVA语言的水深

JAVA这潭大湖，从上往下，一共有7层（人为定义的）。参考 [JAVA学习蓝图，从基础、集合、并发、JVM原理，到应用框架](https://github.com/peteryuanpan/notebook/issues/51)

- JAVA语言
- JAVA字节码
- JVM原理
- JVM源码
- 汇编层
- 操作系统OS层
- CPU层

笔者的能力是有限的，望大家见谅，这篇文章能接触到的水深，只有 1 - 3 层，第4层JVM源码往下，这篇文章不会深入包含。

如果读者要问我为何第4层开始往下不涉及了，我只能说，我也很无奈呀，实在能力有限，短时间内还做不到呐。

### 关于第4层

第4层是JVM源码

在准备这份笔记同时，实际上是我在整理自己对JVM原理等的理解

但是我发现，纯理论的理解，是有瓶颈的，你可以通过图标去加深理解，可以通过网上更多资料细节去巩固理解，但是你永远只是停留在理论上，没有实践

就好比，高中时上化学课，背元素周期表、化学反应方程式、基础化学分子的现象特征等，这些固然需要，但你希望深入理解化学，深入记忆，只停留在理论不足以的，一定需要实践，即做实验，这也是我国学生的一个普遍问题点------动手能力差

鲁班学院的子牙老师，有提供Ubuntu虚拟机环境的镜像，上面可以调试jvm，查看源码并理解

我做了个笔记，记录于：[Windows上用虚拟机运行Ubuntu环境并调试JVM](https://github.com/peteryuanpan/notebook/issues/89) ，这份笔记非常有用，但也只是个开始

因此，虽然这篇文章接触到的水深，只有 1 - 3 层，但是我也会尽量的通过第 4 层，拿出源码例子，来帮助第 3 层加深理解

不深入包含第 4 层，含义是只是用源码例子来解释，但不会去深入分析，甚至去编写JVM源码的一部分code来理解。。做不到
