# 深入理解JAVA虚拟机-第一至四层

### 前言

我希望写多篇文章，组合成一份文章集，包含关于JAVA虚拟机的入门原理、常见问题、代码例子证明等

读者阅读它，能像读小说一样，从第一篇文章，读到最后一篇文章，中间不带卡主，尽量把讳莫如深难懂的内容写得简单易懂。当然这也需要读者具有一定的JAVA功底

### 声明

转载请注明出处：https://github.com/peteryuanpan/notebook/blob/master/深入理解JAVA虚拟机-第一至四层/README.md

作者：潘缘，来自电子科技大学，2017届

本文部分内容来自鲁班学院的课程，感谢子牙老师和同学们对我的帮助

还有部分内容来自《深入理解JAVA虚拟机》第二版以及第三版，第二版是基于JDK7的，第三版是基于JDK8及以后的，本文内容是基于JDK8的（Hotspot实现），我会去对比第二版与第三版的差异点，并记录在文中

### JAVA虚拟机的定义

无论讨论什么，我们先需要将定义讨论清楚，否则就会有无穷的争执

JAVA虚拟机是一种能够运行JAVA字节码程序的虚拟机器

> A Java virtual machine (JVM) is a virtual machine that enables a computer to run Java programs as well as programs written in other languages that are also compiled to Java bytecode. From https://en.wikipedia.org/wiki/Java_virtual_machine.

### JAVA语言的水深

JAVA这潭大湖，从上往下，一共有7层（人为定义的）

- JAVA语言
- JAVA字节码
- JVM原理
- JVM源码
- 操作系统层
- 汇编层
- 硬编码层

笔者的能力是有限的，望大家见谅，这篇文章能接触到的水深，只有第 1-3 层 + 第4层表面，第4层JVM源码往下，这篇文章不会深入包含

如果读者要问我为何第4层开始往下不深入包含了，我只能说，我也很无奈呀，实在能力有限，短时间内还做不到呐

### 关于第4层

第4层是JVM源码

在准备这份笔记同时，实际上是我在整理自己对JVM原理等的理解

但是我发现，纯理论的理解，是有瓶颈的，你可以通过图表去加深理解，可以通过网上更多资料细节去巩固理解，但是你永远只是停留在理论上，没有实践

就好比，高中时上化学课，背元素周期表、化学反应方程式、基础化学分子的现象特征等，这些固然需要，但你希望深入理解化学，深入记忆，只停留在理论不足以的，一定需要实践，即做实验，这也是我国学生的一个普遍问题点------动手能力差

鲁班学院的子牙老师，有提供Ubuntu虚拟机环境的镜像，上面可以调试jvm，查看源码并理解

我做了个笔记，记录于：[Windows上用虚拟机运行Ubuntu环境并调试JVM](https://github.com/peteryuanpan/notebook/issues/89) ，这份笔记非常有用，但也只是个开始

因此，虽然这篇文章接触到的水深，只有第 1-3 层 + 第4层表面，但是我也会尽量的通过第 4 层，拿出源码例子，来帮助第 3 层加深理解

不深入包含第 4 层，含义是只是用源码例子来解释，但不会去深入分析，甚至去编写JVM源码的一部分code来理解。。做不到

push了一份jvm源码到 https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror ，接下来分析、Debug都用它

### 如何学习JVM

两个方向：内存模型、字节码

一个好方法：学习了一块知识点后，自己开视频录制，复述出来，一遍一遍地过，直到讲明白为止

### 文章目录

#### 文章大纲

标题 => 定义、解释 => 问题1、回答1、...、问题n、回答n（回答要准确、简扼，能证明则证明）

#### 第1章：类加载机制与类加载器
- [类加载机制](类加载机制.md#类加载机制)
  - [类加载的定义](类加载机制.md#类加载的定义)
  - [JAVA运行时环境逻辑图](类加载机制.md#JAVA运行时环境逻辑图)
  - [类加载的输入和输出结果](类加载机制.md#类加载的输入和输出结果)
  - [InstanceKlass和InstanceMirrorKlass是什么](类加载机制.md#InstanceKlass和InstanceMirrorKlass是什么)
  - [ArrayKlass和TypeArrayKlass和ObjArrayKlass是什么](类加载机制.md#ArrayKlass和TypeArrayKlass和ObjArrayKlass是什么)
  - [类加载的五个过程](类加载机制.md#类加载的五个过程)
  - [类加载之加载](类加载机制.md#类加载之加载)
    - [加载的规范定义](类加载机制.md#加载的规范定义)
    - [数组类的加载过程](类加载机制.md#数组类的加载过程)
  - [类加载之验证](类加载机制.md#类加载之验证)
    - [验证的规范定义](类加载机制.md#验证的规范定义)
    - [四种验证方法](类加载机制.md#四种验证方法)
  - [类加载之准备](类加载机制.md#类加载之准备)
    - [准备的规范定义](类加载机制.md#准备的规范定义)
    - [数据类型的零值](类加载机制.md#数据类型的零值)
  - [类加载之解析](类加载机制.md#类加载之解析)
    - [解析的规范定义](类加载机制.md#解析的规范定义)
    - [符号引用替换为直接引用](类加载机制.md#符号引用替换为直接引用)
  - [类加载之初始化](类加载机制.md#类加载之初始化)
    - [初始化的规范定义](类加载机制.md#初始化的规范定义)
    - [clinit方法的深入理解](类加载机制.md#clinit方法的深入理解)
  - [类加载什么时候会进行](类加载机制.md#类加载什么时候会进行)
    - [加载mainClass](类加载机制.md#加载mainClass)
    - [new或getstatic或putstatic或invokestatic](类加载机制.md#new或getstatic或putstatic或invokestatic)
    - [优先加载父类](类加载机制.md#优先加载父类)
    - [反射](类加载机制.md#反射)
  - [类加载笔试题统一解法](类加载机制.md#类加载笔试题统一解法)
- [类加载器](#类加载器)
  - [类加载器的定义](#类加载器的定义)
  - [类加载的唯一性](#类加载的唯一性)
  - [双亲委派模型的定义](#双亲委派模型的定义)
  - [启动类加载器](#启动类加载器)
    - [启动类加载器的定义](#启动类加载器的定义)
    - [启动类加载器的管辖范围](#启动类加载器的管辖范围)
  - [拓展类加载器](#拓展类加载器)
    - [拓展类加载器的定义](#拓展类加载器的定义)
    - [拓展类加载器的管辖范围](#拓展类加载器的管辖范围)
  - [应用类加载器](#应用类加载器)
    - [应用类加载器的定义](#应用类加载器的定义)
    - [应用类加载器的管辖范围](#应用类加载器的管辖范围)
  - [双亲委派模型的实现源码](#双亲委派模型的实现源码)
    - [ClassLoader实现源码](#ClassLoader实现源码)
    - [双亲委派模型之JVM源码](#双亲委派模型之JVM源码)
    - [沙箱安全机制](#沙箱安全机制)
  - [破坏双亲委派模型](#破坏双亲委派模型)
    - [破坏双亲委派模型的意义](#破坏双亲委派模型的意义)
    - [线程上下文类加载器与SPI机制](#线程上下文类加载器与SPI机制)
    - [自定义类加载器](#自定义类加载器)

#### 第2章：JVM运行时数据区域与字节码执行引擎

#### 第3章：Class类文件结构与字节码手册

#### 第4章：垃圾收集算法与垃圾收集器

#### 第5章：JVM运维与调优工具实战

# 类加载器

### 类加载器的定义

类加载阶段中，“通过一个类的全限定名来获取描述此类的二进制字节流”这个动作称为加载，实现这个动作的代码模块称为“类加载器”

类加载器一共可分为几种：启动类加载器（Bootstrap ClassLoader）、拓展类加载器（Extension ClassLoader）、应用程序类加载器（Application ClassLoader）、线程上下文类加载器（Thread Context ClassLoader）、自定义类加载器

### 类加载的唯一性

两个类相等，当且仅当，这两个类来源于同一个Class文件 且 被同一个类加载器加载

下面的代码示例演示了关于instanceof判定不同类加载器加载同一个类的字节流的结果

```java
package com.peter.jvm.example;

import java.io.InputStream;

public class ClassLoaderDefineClassTest1 {

    public static void main(String[] arg) throws Exception {
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    System.out.println(name + (is == null ? " is null" : " is NOT null"));
                    if (is == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (Exception e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };
        Object obj = myLoader.loadClass("com.peter.jvm.example.ClassLoaderDefineClassTest1").newInstance();
        System.out.println(obj.getClass());
        System.out.println(obj instanceof com.peter.jvm.example.ClassLoaderDefineClassTest1);
    }
}
```

输出结果
```
com.peter.jvm.example.ClassLoaderDefineClassTest1 is NOT null
java.lang.Object is null
java.lang.ClassLoader is null
com.peter.jvm.example.ClassLoaderDefineClassTest1$1 is NOT null
class com.peter.jvm.example.ClassLoaderDefineClassTest1
false
```

解释

myLoader.loadClass("com.peter.jvm.example.ClassLoaderDefineClassTest1").newInstance();

调用自定义类加载器 读取类（com.peter.jvm.example.ClassLoaderDefineClassTest1）的字节流，并实例化

defineClass用于读取字节流，最终是一个native方法

Object obj是通过myLoader实例化的对象，它的class确实是com.peter.jvm.example.ClassLoaderDefineClassTest1

但是，由于类加载器不一致，因此myLoader与启动类加载器加载的类不相等，instance结果也为false

### 双亲委派模型的定义

双亲委派模型是类加载器之间的逻辑父子层级关系

双亲委派模型的工作过程是
- 如果一个类加载收到了来加载请求，先检查是否已经被加载过，加载过则返回
- 若没有加载则调用父类加载器的locadClass()方法
- 若父类加载为空则默认使用启动类加载器作为父加载器
- 若父类加载器加载失败，抛出ClassNotFoundException异常后，再调用自己的findClass()方法进行加载

使用ClassLoader中的loadClass方法可以更直观的看明白这个过程
```java
    protected Class<?> loadClass(String name, boolean resolve)
        throws ClassNotFoundException
    {
        synchronized (getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class<?> c = findLoadedClass(name);
            if (c == null) {
                long t0 = System.nanoTime();
                try {
                    if (parent != null) {
                        c = parent.loadClass(name, false);
                    } else {
                        c = findBootstrapClassOrNull(name);
                    }
                } catch (ClassNotFoundException e) {
                    // ClassNotFoundException thrown if class not found
                    // from the non-null parent class loader
                }

                if (c == null) {
                    // If still not found, then invoke findClass in order
                    // to find the class.
                    long t1 = System.nanoTime();
                    c = findClass(name);

                    // this is the defining class loader; record the stats
                    sun.misc.PerfCounter.getParentDelegationTime().addTime(t1 - t0);
                    sun.misc.PerfCounter.getFindClassTime().addElapsedTimeFrom(t1);
                    sun.misc.PerfCounter.getFindClasses().increment();
                }
            }
            if (resolve) {
                resolveClass(c);
            }
            return c;
        }
    }
```

#### 双亲委派模型的时序图

![image](https://camo.githubusercontent.com/66b56028009831b2d3c4b87bf7530fadf8277606/68747470733a2f2f63646e2e6e6c61726b2e636f6d2f79757175652f302f323032302f706e672f323137393831352f313539363138303137353132392d31303161373435312d363462382d346436612d386165392d3439663931356636646633342e706e67)

### 启动类加载器

#### 启动类加载器的定义

#### 启动类加载器的管辖范围

### 拓展类加载器

#### 拓展类加载器的定义

#### 拓展类加载器的管辖范围

### 应用类加载器

#### 应用类加载器的定义

#### 应用类加载器的管辖范围
