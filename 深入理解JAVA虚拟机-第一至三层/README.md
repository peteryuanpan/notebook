# 深入理解JAVA虚拟机

### 前言

我希望写多篇文章，组合成一份文章集，包含关于JAVA虚拟机的入门原理、常见问题、代码例子证明等。

读者阅读它，能像读小说一样，从第一篇文章，读到最后一篇文章，中间不带卡主，尽量把讳莫如深难懂的内容写得简单易懂。当然这也需要读者具有一定的JAVA功底。

### JAVA虚拟机的定义

无论讨论什么，我们先需要将定义讨论清楚，否则就会有无穷的争执。

JAVA虚拟机是一种能够运行JAVA字节码程序的虚拟机器。

> A Java virtual machine (JVM) is a virtual machine that enables a computer to run Java programs as well as programs written in other languages that are also compiled to Java bytecode. From https://en.wikipedia.org/wiki/Java_virtual_machine.

### JAVA语言的水深

JAVA这潭大湖，从上往下，一共有7层。参考 [#51](https://github.com/peteryuanpan/notebook/issues/51)

- JAVA语言
- JAVA字节码
- JVM原理
- JVM源码
- 汇编层
- 操作系统OS层
- CPU层

笔者的能力是有限的，望大家见谅，这篇文章能接触到的水深，只有 1 - 3 层，第4层JVM源码往下，这篇文章不会包含。

如果读者要问我为何第4层开始往下不涉及了，我只能说，我也很无奈呀，实在能力有限，短时间内还做不到呐。


