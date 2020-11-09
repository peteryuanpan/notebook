# 深入理解设计模式

### 前言

关于设计模式，我一直认为它非常像数据结构与算法，在做LeetCode这样的算法题过程中，有一个比较深的感触是————看100遍算法原理不如自己实现1遍，但是，只实现过一遍例题，还不足以理解某个算法，若能做到不同变型的题目都handle过，那理解程度会非常深入，且很难忘却，即使长久时间不用了，只要简单的唤醒一下记忆就能很快想起来，这就是coding中的“肌肉记忆”！

因此我希望以一种稍微特别的方式来写这份文章集，我不希望像其他博主那样板书式地将设计模式概念一一写在博文中，这样既不能帮助加深理解，也不能帮助复习（难道你要去记忆概念文字吗?）。我的方式是：基本定义 + 代码例子 + 关联记忆

下面简要说一下这三部分，每个设计模式都有一个官方定义，这是任何介绍设计模式都必不可少的内容，这一部分用 定义 + 简单的解释 我认为就够了。相比之下，最关键的部分是代码例子，比如单例模式的3种实现方式、策略模式的基本例子等，通过例子引出一个“记忆切入口”，每当提到某个模式的时候，先想到这个“记忆切入口”，然后唤醒对这个模式的深入记忆，比如单例模式————volatile+synchonized、策略模式————封装对象的行为并与对象组合使用等。最后一部分，是通过关联的方式加深记忆，比如JDK中有哪些模块使用了哪个设计模式，Spring中使用了哪些设计模式，设计模式与设计模式之间存在什么联系性等

**声明**

转载请注明出处：https://github.com/peteryuanpan/notebook/blob/master/深入理解设计模式

本文主要内容来自《Head First设计模式》，这本书通过非常简单易懂的方式介绍了15种主要的设计模式，以及简要的介绍了剩下的8种设计模式

还有部分内容会参考网上的博文

### 设计模式的定义

在软件领域中，设计模式是一种以“复用”来解决常见问题的方案或模板

> In software engineering, a software design pattern is a general, reusable solution to a commonly occurring problem within a given context in software design. It is not a finished design that can be transformed directly into source or machine code. Rather, it is a description or template for how to solve a problem that can be used in many different situations. From https://en.wikipedia.org/wiki/Software_design_pattern

### 如何学习设计模式

学习方法
- 以看书或视频为主，理清楚主要几种模式的基本定义、经典例子
- 自己写文章做总结，并在工作中结合代码加深记忆，在生活中的空余时间刺激联想记忆

### 设计模式之间联系

已学习模式有：策略模式、观察者模式、装饰者模式、工厂模式（抽象工厂模式）、单例模式、模板方法

中英文结合 ...

TODO：画一幅图?

### 文章目录

#### 第0章：设计模式基本原则
- [设计模式基本原则](#设计模式基本原则.md)
  - TODO

#### 第1章：创建型模式
- 工厂模式
  - Alias抽象工厂模式
- 单例模式
- 原型模式
- 建造者模式

#### 第2章：结构型模式
- 装饰器模式
- 适配器模式
- 外观模式
- 桥接模式
- 组合模式
- 代理模式
- 享元模式
- 过滤器模式?

#### 第3章：行为型模式

#### 第4章：设计模式面试题
- [设计模式面试题](#设计模式面试题.md)
  - TODO
