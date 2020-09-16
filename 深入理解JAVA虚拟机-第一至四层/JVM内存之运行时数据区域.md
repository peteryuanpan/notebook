- [JVM内存之运行时数据区域](#JVM内存之运行时数据区域)
  - [运行时数据区域的定义](#运行时数据区域的定义)
  - [程序计数器](#程序计数器)
  - [虚拟机栈与本地方法栈](#虚拟机栈与本地方法栈)
    - [虚拟机栈的定义](#虚拟机栈的定义)
    - [栈帧的概念](#栈帧的概念)
    - [本地方法栈与虚拟机栈的区别](#本地方法栈与虚拟机栈的区别)
    - [虚拟机栈与本地方法栈溢出](#虚拟机栈与本地方法栈溢出)

# JVM内存之运行时数据区域

> 先写一下我的理解与困惑吧，运行时数据区其实是很多了解过JVM的程序员既熟悉又陌生的知识点。之所以这么说，是因为运行时数据区域中的5大区域，即程序计数器、虚拟机栈、本地方法栈、堆区、方法区，程序员们一定听过，就算不了解JAVA，在其他语言（比如C++）中也有堆栈的概念，多少能猜到虚拟机栈、堆区的大致作用，这就是“熟悉”。认真看过《深入理解JAVA虚拟机》这本书，就能明白每个区域的具体作用是什么，特点是什么，存储什么类型数据。然而，过几个月时间不复习，这几块知识会逐渐遗忘，方法区除了存储类的元信息外，还存储什么，能讲全吗，运行时常量池和字符串常量池分别存储在哪里，等等，这说明一般的程序员们对运行时数据区域没有深入的理解，这就是“陌生”

> 死记硬背不是长久之计，个人认为要深入理解，需要具体例子来充实，有几个方法<br>
> 一，从基本单位的定义及功能来理解。比如程序计数器、虚拟机栈与本地方法栈的定义，栈帧中局部变量表、操作数栈、动态链接、方法返回地址的具体定义，堆区与方法区详细地存储了哪些数据
> 二，从几个区域之间的指向关系来理解。比如虚拟机栈指向方法区表示什么，虚拟机栈指向堆区表示什么，方法区指向堆区表示什么，堆区指向方法区表示什么，指向的含义是否牵强，是否准确，是否多样<br>
> 三，从OOM（Out Of Memory）例子来理解。比如堆区溢出例子如何实现的，虚拟机栈和本地方法栈溢出例子如何实现的，方法区和运行时常量池溢出有几种例子，直接内存会存在溢出的情况吗

> 上面是我关于本文开篇之前的思考，也给本文内容指明了方向，要从深入理解这个出发点来写，而不是简单的复制粘贴已有的概念，上面所指的，“既熟悉又陌生的程序员”其实我也是其中之一，希望在我写完后，比现在清晰，就是进步了

> 顺便说一下，本文涉及的内容与第4章：对象的创建使用与字符串常量池，第5章：字节码执行引擎与即时编译器，有很大关系，到了具体章节会有更深入的讲解，本章是一个开头

### 运行时数据区域的定义

Java虚拟机在执行Java程序的过程中会把它所管理的内存划分为若干个不同的数据区域

[口述版]Java虚拟机在操作系统中申请了一块内存区域，用于运行Java程序，这块内存区域中有5个部分，分别是程序计数器、虚拟机栈、本地方法栈、方法区、堆区

### 程序计数器

程序计数器是一块较小的内存空间，是线程所执行的字节码的行号指示器

比如下面这段字节码，0 2 3 5等就是程序计数器的计数值

```
    0 bipush 10
    2 istore_1
    3 bipush 20
    5 istore_2
    6 iload_1
    7 iload_2
    8 iadd
    9 ireturn
```

字节码解释器工作时就是改变程序计数器的计数值来选取下一行需要执行的字节码指令

分支、循环、跳转、异常处理、线程恢复等基础功能都需要依赖程序计数器来完成

程序计数器是线程私有的。由于Java虚拟机是通过线程轮流切换并分配处理器执行时间的方式来完成的，在任何一个确定的时刻，一个处理器只会执行一条线程中的指令，每个线程中都有一个独立的程序计数器

程序计数器是唯一一个在Java虚拟机规范中没有任何规定OutOfMemeory情况的区域

### 虚拟机栈与本地方法栈

#### 虚拟机栈的定义

虚拟机栈描述的是Java方法调用与执行的内存模型

虚拟机栈是线程私有的，每个线程都有一个虚拟机栈

#### 栈帧的概念

栈帧是虚拟机栈的基本元素，用于支持虚拟机栈进行方法调用和方法执行的数据结构

每个方法从调用到执行完成的过程，都对应着一个栈帧在虚拟机栈中从入栈到出栈的过程，即一个栈帧对应着一次方法调用执行

栈帧中含有局部变量表、操作数栈、动态链接、方法返回地址（保存现场、恢复现场）、附加信息，这一部分我们在【第5章：字节码执行引擎与即时编译器】中深入讲解

简单来说，前4个部分如下

局部变量表：用于存放方法参数和方法内局部变量的数据结构

操作数栈：用于实际存放方法调用执行中入栈和出栈数据的数据结构

动态链接：在运行期间转化为直接引用的符号引用（还有一部分符号引用，在类加载的“解析”阶段转化为直接引用）

返回地址：方法正常返回时，用于恢复上层方法执行状态的程序计数器的值（方法异常返回时，返回地址通过异常处理器表来确定，栈帧中不保存）

#### 本地方法栈与虚拟机栈的区别

二者内部结构是一样的，本地方法栈为native方法服务，虚拟机栈为非native方法服务

#### 虚拟机栈与本地方法栈溢出

在JAVA虚拟机规范中描述了两种异常
- 如果线程请求的栈深度大于虚拟机所允许的最大深度，将抛出StackOverflowError异常
- 如果虚拟机在扩展栈时无法申请到足够的内存空间，则抛出OutOfMemoryError异常

当栈空间无法继续分配时，到底是内存溢出，还是栈深度溢出，其本质上只是对同一件事情的两种描述而已

> 栈溢出时，内存溢出==栈深度溢出，可以想象一个栈，大小为10节火车轨道，每次放入一节，当放到第11节时，不够放了，这时候既是内存溢出，也是栈深度溢出

##### 例子1：单线程调用方法过多导致栈深度溢出

```java
//VM Args: -Xss1M
package com.peter.jvm.example2.overflow;

public class JavaVMStackOverflowTest {

    private int stackLength = 1;

    public void stackLeak() {
        stackLength ++;
        stackLeak();
    }

    public static void main(String[] args) {
        JavaVMStackOverflowTest test = new JavaVMStackOverflowTest();
        try {
            test.stackLeak();
        } catch (Throwable e) {
            System.out.println("stack length: " + test.stackLength);
            e.printStackTrace();
        }
    }
}
```

输出结果
```
stack length: 17964
java.lang.StackOverflowError
	at com.peter.jvm.example2.overflow.JavaVMStackOverflowTest.stackLeak(JavaVMStackOverflowTest.java:9)
	at com.peter.jvm.example2.overflow.JavaVMStackOverflowTest.stackLeak(JavaVMStackOverflowTest.java:9)
  ...
```

解释

本例子用一个线程循环调用方法，直到栈深度溢出为止，设置了虚拟机参数-Xss1M，Xss设置Java线程堆栈大小为1MB，最终一共调用了17964次，大概可以算出一个栈帧大小为 58.37B

##### 例子2：多线程调用方法过多导致OutOfMemory



```java
package com.peter.jvm.example2.overflow;

public class JavaVMStackOOMTest {

    private void test() {
        System.out.println(Thread.currentThread().getName());
        String a[] = new String[1000];
        for (int i = 0; i < 1000; i ++) a[i] = new String("11");
        try {
            Thread.sleep(1000000000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " finished.");
    }

    public void stackLeakByThread() {
        while (true) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    test();
                }
            });
            thread.start();
        }
    }

    public static void main(String[] args) {
        try {
            JavaVMStackOOMTest test = new JavaVMStackOOMTest();
            test.stackLeakByThread();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
package com.peter.jvm.example2.overflow;

public class JavaVMStackOOMTest {

    private void test() {
        System.out.println(Thread.currentThread().getName());
        String a[] = new String[1000];
        for (int i = 0; i < 1000; i ++) a[i] = new String("11");
        try {
            Thread.sleep(1000000000000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " finished.");
    }

    public void stackLeakByThread() {
        while (true) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    test();
                }
            });
            thread.start();
        }
    }

    public static void main(String[] args) {
        try {
            JavaVMStackOOMTest test = new JavaVMStackOOMTest();
            test.stackLeakByThread();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
```

输出结果
```
...
Thread-2026
Thread-2027
Thread-2028
java.lang.OutOfMemoryError: unable to create new native thread
	at java.lang.Thread.start0(Native Method)
	at java.lang.Thread.start(Thread.java:717)
	at com.peter.jvm.example2.overflow.JavaVMStackOOMTest.stackLeakByThread(JavaVMStackOOMTest.java:30)
	at com.peter.jvm.example2.overflow.JavaVMStackOOMTest.main(JavaVMStackOOMTest.java:37)
Error occurred during initialization of VM
java.lang.OutOfMemoryError: unable to create new native thread
```

解释

