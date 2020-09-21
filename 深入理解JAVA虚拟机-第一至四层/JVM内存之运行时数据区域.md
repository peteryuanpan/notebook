- [JVM内存之运行时数据区域](#JVM内存之运行时数据区域)
  - [运行时数据区域的定义](#运行时数据区域的定义)
  - [程序计数器](#程序计数器)
  - [虚拟机栈与本地方法栈](#虚拟机栈与本地方法栈)
    - [虚拟机栈的定义](#虚拟机栈的定义)
    - [本地方法栈与虚拟机栈的区别](#本地方法栈与虚拟机栈的区别)
    - [栈帧的概念](#栈帧的概念)
    - [虚拟机栈与本地方法栈溢出](#虚拟机栈与本地方法栈溢出)
      - [例子1-单线程调用方法过深导致栈深度溢出](#例子1-单线程调用方法过深导致栈深度溢出)
      - [例子2-过多线程调用方法导致OutOfMemory](#例子2-过多线程调用方法导致OutOfMemory)
  - [方法区](#方法区)
    - [方法区的定义](#方法区的定义)
    - [永久代及元空间](#永久代及元空间)
    - [元空间取代永久代的理由](#元空间取代永久代的理由)
    - [运行时常量池](#运行时常量池)
    - [方法区溢出](#方法区溢出)
      - [例子1-基于JDK6的字符串常量池溢出](#例子1-基于JDK6的字符串常量池溢出)
      - [例子2-使用CGLib让方法区内存溢出](#例子2-使用CGLib让方法区内存溢出)

# JVM内存之运行时数据区域

> 先写一下我的理解与困惑吧，运行时数据区其实是很多了解过JVM的程序员既熟悉又陌生的知识点。之所以这么说，是因为运行时数据区域中的5大区域，即程序计数器、虚拟机栈、本地方法栈、堆区、方法区，程序员们一定听过，就算不了解JAVA，在其他语言（比如C++）中也有堆栈的概念，多少能猜到虚拟机栈、堆区的大致作用，这就是“熟悉”。认真看过《深入理解JAVA虚拟机》这本书，就能明白每个区域的具体作用是什么，特点是什么，存储什么类型数据。然而，过几个月时间不复习，这几块知识会逐渐遗忘，方法区除了存储类的元信息外，还存储什么，能讲全吗，运行时常量池和字符串常量池的区别是什么，等等，这说明一般的程序员们对运行时数据区域没有深入的理解，这就是“陌生”

> 死记硬背不是长久之计，个人认为要深入理解，需要具体例子来充实，有几个方法<br>
> 一，从基本单位的定义及功能来理解。比如程序计数器、虚拟机栈与本地方法栈的定义，栈帧中局部变量表、操作数栈、动态链接、方法返回地址的具体定义，堆区与方法区详细地存储了哪些数据<br>
> 二，从几个区域之间的指向关系来理解。比如虚拟机栈指向方法区表示什么，虚拟机栈指向堆区表示什么，方法区指向堆区表示什么，堆区指向方法区表示什么，指向的含义是否牵强，是否准确，是否多样<br>
> 三，从OOM（Out Of Memory）例子来理解。比如堆区溢出例子如何实现的，虚拟机栈和本地方法栈溢出例子如何实现的，方法区和运行时常量池溢出有几种例子，直接内存会存在溢出的情况吗

> 上面是我关于本文开篇之前的思考，也给本文内容指明了方向，要从深入理解这个出发点来写，而不是简单的复制粘贴已有的概念，上面所指的“既熟悉又陌生的程序员”，其实我也是其中之一，希望在我写完后，比现在清晰，就是进步了

> 顺便说一下，本文涉及的内容与第4章：字节码执行引擎与即时编译器，第5章：对象的创建使用与字符串常量池，第6章：垃圾收集算法与垃圾收集器，都有很大关系，到了具体章节会有更深入的讲解，本章是一个开头

### 运行时数据区域的定义

Java虚拟机在执行Java程序的过程中会把它所管理的内存划分为若干个不同的数据区域

[口述版]Java虚拟机在操作系统中申请了一块内存区域，用于运行Java程序，这块内存区域中有5个部分，分别是程序计数器、虚拟机栈、本地方法栈、方法区、堆区

> 网上有人喜欢将这一块叫JVM内存模型，只是叫法的不同，我不喜欢那么叫，因为容易与JAVA内存模型搞混淆，就叫JVM内存之运行时数据区域，它强调的是虚拟机在操作系统中分配的具体内存区域部分，而JAVA内存模型强调的是主内存与工作内存之间的通信关系

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

#### 本地方法栈与虚拟机栈的区别

二者内部结构是一样的，本地方法栈为native方法服务，虚拟机栈为非native方法服务

#### 栈帧的概念

栈帧是虚拟机栈的基本元素，用于支持虚拟机栈进行方法调用和方法执行的数据结构

每个方法从调用到执行完成的过程，都对应着一个栈帧在虚拟机栈中从入栈到出栈的过程，即一个栈帧对应着一次方法调用执行

栈帧中含有局部变量表、操作数栈、动态链接、方法返回地址（保存现场、恢复现场）、附加信息，这一部分我们在【第5章：字节码执行引擎与即时编译器】中深入讲解

简单来说，前4个部分如下

局部变量表：用于存放方法参数和方法内局部变量的数据结构

操作数栈：用于实际存放方法调用执行中入栈和出栈数据的数据结构

动态链接：在运行期间转化为直接引用的符号引用（还有一部分符号引用，在类加载的“解析”阶段转化为直接引用）

返回地址：方法正常返回时，用于恢复上层方法执行状态的程序计数器的值（方法异常返回时，返回地址通过异常处理器表来确定，栈帧中不保存）

#### 虚拟机栈与本地方法栈溢出

在JAVA虚拟机规范中描述了两种异常
- 如果线程请求的栈深度大于虚拟机所允许的最大深度，将抛出StackOverflowError异常
- 如果虚拟机在扩展栈时无法申请到足够的内存空间，则抛出OutOfMemoryError异常

当栈空间无法继续分配时，到底是内存溢出，还是栈深度溢出，其本质上只是对同一件事情的两种描述而已

> 栈溢出时，内存溢出==栈深度溢出，可以想象一个栈，大小为10节火车轨道，每次放入一节，当放到第11节时，不够放了，这时候既是内存溢出，也是栈深度溢出

##### 例子1-单线程调用方法过深导致栈深度溢出

```java
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

输出结果（-Xss160K）
```
stack length: 773
java.lang.StackOverflowError
	at com.peter.jvm.example2.overflow.JavaVMStackOverflowTest.stackLeak(JavaVMStackOverflowTest.java:9)
	at com.peter.jvm.example2.overflow.JavaVMStackOverflowTest.stackLeak(JavaVMStackOverflowTest.java:9)
  ...
```

输出结果（-Xss100K）
```
The stack size specified is too small, Specify at least 160k
Error: Could not create the Java Virtual Machine.
Error: A fatal exception has occurred. Program will exit.
```

解释

本例子用一个线程循环调用方法，直到栈深度溢出为止，设置了虚拟机参数-Xss1M，Xss设置Java线程堆栈大小为160KB，最终一共调用了773次，大概可以算出一个栈帧大小为 58.37B（1 * 160 * 1024 / 773）

注意Java线程堆栈大小最小为160KB，设置100KB会报错 

> 留一个思考，当设置-Xss分别为500K、1000K、1500K、2000K、2500K、3000K时，栈深度分别是7383、17975、28405、80136、47551、61670，算出来的栈帧大小分别是69.34B、56.96B、54.07B、25.55B、53.83KB、49.81KB。会发现当-Xss为2000时，明显栈深度有一个突增，多次调试发现，栈深度会变化，从27000 ~ 100000不等。这是为什么呢？

##### 例子2-过多线程调用方法导致OutOfMemory

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

本例子没有对一个方法进行循环调用，而是采用了创建多个线程，每个线程调用一次方法，方法中阻塞、长时间不执行完成，单个线程的栈深度是不会超的（否则会报StackOverFlow错误），而是内存超了，java.lang.OutOfMemoryError: unable to create new native thread

> 无法调用 java.lang.Thread.start0(Native Method)，这是本地方法栈超限了吗？有办法调大这个值不？根据《深入理解JAVA虚拟机》第二版2.4.2节，对于HotSpot来说，-Xoss是设置本地方法栈大小的参数，但实际上无效。总之，我尝试过-Xoss、-Xss（等价于 -XX:ThreadStackSize）、-Xmx等，都执行结果都是创建了2028个线程后报错，没有找到办法调限

### 方法区

#### 方法区的定义

方法区用于存放类的元信息，如类名、父类名、运行时常量池、字段描述、方法描述、访问修饰符等

方法区是线程共享的内存区域

#### 永久代及元空间

方法区是规范，永久代（PermGen）和元空间（Metaspace）是具体实现

JDK6、JDK7、JDK8，Hotspot的 方法区的实现 发生了翻天覆地的变化

在JDK6及以前，方法区的实现就只有永久代，永久代存储了类的元信息、静态变量、运行时常量池、字符串常量池、即时编译器编译后的代码等数据

在JDK7，永久代中存储的部分数据已经开始转移到堆区（Java Heap）或本地内存（Native Memory）中了。比如，符号引用（Symbols）转移到了本地内存，字符串常量池（Interned Strings）转移到了堆区，类的静态变量（Class Statics）转移到了堆区

在JDK8中，Hotspot取消了永久代，改用与JRockit、J9一样在本地内存中实现的元空间，把永久代剩余的内容（主要是类的元信息）全部移到了元空间，永久代真的成了永久的记忆，永久代的参数 -XX:PermSize 和 -XX:MaxPermSize 也随之失效

永久代实现的方法区，与堆区内存是连续的

永久代的垃圾收集是与堆区的老年代捆绑在一起的，无论谁满了，都会触发永久代和老年代的垃圾收集

![image](https://user-images.githubusercontent.com/10209135/93434392-aa3f2980-f8fa-11ea-8f41-4e82ee6f793e.png)

元空间实现的方法区，与堆区内存不连续，而且是存在于本地内存

本地内存（Native memory），也称为C-Heap，是供JVM自身进程使用的。当堆区（Java Heap）空间不足时会触发GC，但本地内存空间不够却不会触发GC

![image](https://user-images.githubusercontent.com/10209135/93434461-c642cb00-f8fa-11ea-8a11-34cf5fee0246.png)

#### 元空间取代永久代的理由

这是一个开放性的问题，从几个方面来回答：OOM问题、垃圾回收、计算机内存的发展、HotSpot与JRockit代码合并

表面上看是为了避免OOM异常，因为通常使用PermSize和MaxPermSize设置永久代的大小就决定了永久代的上限，但是不是总能知道应该设置为多大合适，如果使用默认值很容易遇到OOM错误

当使用元空间时，可以加载多少类的元数据就不再由MaxPermSize控制，而由系统的实际可用空间来控制

更深层的原因还是要合并HotSpot和JRockit的代码，JRockit从来没有所谓的永久代，也不需要开发运维人员设置永久代的大小，但是运行良好。同时也不用担心运行性能问题了，在覆盖到的测试中，程序启动和运行速度降低不超过1%，但是这点性能损失换来了更大的安全保障

永久代的垃圾收集是与堆区的老年代捆绑在一起的，无论谁满了，都会触发永久代和老年代的垃圾收集，这给垃圾回收造成了一定的麻烦，试想，垃圾回收时还要去区分对象和类的元信息等，二者的垃圾回收没有紧密的联系，但却会相互触发，元空间减少了这样的麻烦，堆区的垃圾回收自己做，而元空间在本地内存，不会触发垃圾回收

还有一个深层的原因，计算机内存的发展，起初是计算机内存是很小的，慢慢的开始有32位，但依然需要限制，因此永久代与堆区绑在一起，要限制一起限制，而后来有64位，内存允许越来越宽松，敢把方法区用本地内存来实现了

> 部分参考：[面试官 | JVM 为什么使用元空间替换了永久代？](https://zhuanlan.zhihu.com/p/111809384)

#### 运行时常量池

运行时常量池是方法区的一部分。Class文件中的常量池，用于存放编译器生成的各种字面量和符号引用，这部分内容在类加载后，进入方法区的运行时常量池中存放

Class文件的常量池 => 方法区的运行时常量池 是类加载过程之一

运行时常量池可以与Class文件中的常量池对应，关于后者在第2章中有深入的讲解，这里不赘述了

然而，容易混淆的是运行时常量池与字符串常量池，前者是在方法区，后者是在堆区，前者是存储各种字面量、符号引用（包括字符串常量的），后者存储的是指向堆区中字符串对象的引用（只有java.lang.String实例的引用）

关于字符串常量，我们来具体讲解一下

下面这个例子是基于JDK8的

```java
package com.peter.jvm.example2.String;

public class ConstantStringInfoTest {
    String name = "aaa";
    String name1 = "bbb";

    public static void main(String[] args) {
        ConstantStringInfoTest test = new ConstantStringInfoTest();
    }
}
```

javap反编译部分结果（javap -c -v ConstantStringInfoTest.class）
```
Constant pool:
   #1 = Methodref          #7.#24         // java/lang/Object."<init>":()V
   #2 = String             #25            // aaa
   #3 = Fieldref           #6.#26         // com/peter/jvm/example2/String/ConstantStringInfoTest.name:Ljava/lang/String;
   #4 = String             #27            // bbb
   #5 = Fieldref           #6.#28         // com/peter/jvm/example2/String/ConstantStringInfoTest.name1:Ljava/lang/String;
   #6 = Class              #29            // com/peter/jvm/example2/String/ConstantStringInfoTest
   #7 = Class              #30            // java/lang/Object
   #8 = Utf8               name
   #9 = Utf8               Ljava/lang/String;
  #10 = Utf8               name1
  #11 = Utf8               <init>
  #12 = Utf8               ()V
  #13 = Utf8               Code
  #14 = Utf8               LineNumberTable
  #15 = Utf8               LocalVariableTable
  #16 = Utf8               this
  #17 = Utf8               Lcom/peter/jvm/example2/String/ConstantStringInfoTest;
  #18 = Utf8               main
  #19 = Utf8               ([Ljava/lang/String;)V
  #20 = Utf8               args
  #21 = Utf8               [Ljava/lang/String;
  #22 = Utf8               SourceFile
  #23 = Utf8               ConstantStringInfoTest.java
  #24 = NameAndType        #11:#12        // "<init>":()V
  #25 = Utf8               aaa
  #26 = NameAndType        #8:#9          // name:Ljava/lang/String;
  #27 = Utf8               bbb
  #28 = NameAndType        #10:#9         // name1:Ljava/lang/String;
  #29 = Utf8               com/peter/jvm/example2/String/ConstantStringInfoTest
  #30 = Utf8               java/lang/Object

public class com.peter.jvm.example2.String.ConstantStringInfoTest {
  java.lang.String name;

  java.lang.String name1;

  public com.peter.jvm.example2.String.ConstantStringInfoTest();
    Code:
       0: aload_0
       1: invokespecial #1                  // Method java/lang/Object."<init>":()V
       4: aload_0
       5: ldc           #2                  // String aaa
       7: putfield      #3                  // Field name:Ljava/lang/String;
      10: aload_0
      11: ldc           #4                  // String bbb
      13: putfield      #5                  // Field name1:Ljava/lang/String;
      16: return

  public static void main(java.lang.String[]);
    Code:
       0: new           #6                  // class com/peter/jvm/example2/String/ConstantStringInfoTest
       3: dup
       4: invokespecial #7                  // Method "<init>":()V
       7: astore_1
       8: return
```

再看下jclasslib

![image](https://user-images.githubusercontent.com/10209135/93455555-13c93300-f90f-11ea-940b-6b616a58a841.png)

![image](https://user-images.githubusercontent.com/10209135/93455572-188de700-f90f-11ea-9af9-0b0f5bd8aa10.png)

可以看到，Constant pool中2、4都是CONSTANT_String_info，它们分别指向了一个CONSTANT_Utf8_info，其中包含了字符串内容和长度

注意，上面的Constant pool是类文件里的常量池，不完全等于运行时常量池

类加载时，类加载器会将类文件常量池中的数据封装成相应的CONSTANT_* 结构存入运行时常量池，这时运行时常量池中index=2及index=4的位置存放的是JVM_CONSTANT_UnresolvedString（而不是JVM_CONSTANT_String_info），它表示符号引用，还未解析成直接引用，还没有解析字符串字面量，而解析过程是在执行ldc命令时做的

> 参考 [类加载机制#类加载之解析](类加载机制.md#类加载之解析)<br>
> 解析阶段是虚拟机将常量池内的符号引用替换为直接引用的过程<br>
> 解析动作主要针对类或接口、字段、类方法、接口方法、方法类型、方法句柄和调用点限定符7类符号引用进行，分别对应于CONSTANT_Class_info、CONSTANT_Field_info、CONSTANT_Methodref_info、CONSTANT_InterfaceMethodref_info、CONSTANT_MethodType_info、CONSTANT_MethodHandle_info和CONSTANT_InvokeDynamic_info<br>
> 可以发现，没有CONSTANT_String_info，即字符串常量的符号引用在类加载之解析阶段不会被解析成直接引用<br>

执行引擎执行init方法中的字节码ldc时，会做以下步骤
- 根据ldc后面的操作数，去运行时常量池中找对应的值
- 若值已解析，则直接返回字符串在堆中的引用
- 若值未解析，则在字符串常量池中找CONSTANT_String_info结构，该结构存放了字符串的具体内容及字符串的长度等
- 同时，还要到 字符串常量池 中找该字符串的引用
- 若找到，则返回引用
- 若找不到，则在堆中创建一个String对象，并将引用存储在字符串常量池中，返回引用

上面的例子，解释了字符串常量在 类文件常量池、运行时常量池、字符串常量池 中的生成逻辑关系，会在第5章再进行深入讲解

> 部分参考：[启明南：正确理解Java的常量池](https://mp.weixin.qq.com/s/QtisE3z-MXYpdKnknJvTkA)

#### 方法区溢出

##### 例子1-基于JDK6的字符串常量池溢出

```java
package com.peter.jvm.example2.constantPool;

import java.util.List;
import java.util.ArrayList;

public class StringConstantPoolOOM {

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        int i = 0;
        while (true) {
            list.add(String.valueOf(i++).intern());
        }
    }
}
```

输出结果（JDK6，-XX:PermSize=10M -XX:MaxPermSize=10M）
```
Exception in thread "main" java.lang.OutOfMemoryError: PermGen space
```

解释

PermGen space是Hotspot中永久代的名词，上面代码在JDK7及以后，不会报错，会很长时间一直循环进行下去

##### 例子2-使用CGLib让方法区内存溢出

```java
package com.peter.jvm.example2.constantPool;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class CGLibJavaMethodAreaOOM {

    public static void main(String[] args) {
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMObject.class);
            enhancer.setUseCache(false);
            enhancer.setCallback(new MethodInterceptor() {
                @Override
                public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                    return methodProxy.invokeSuper(obj, args);
                }
            });
            enhancer.create();
        }
    }

    static class OOMObject {
    }
}
```

输出结果（JDK7，-XX:PermSize=10M -XX:MaxPermSize=10M）
```
Caused by: java.lang.OutOfMemoryError: PermGen space
```

输出结果（JDK8，-XX:MetaspaceSize=10M -XX:MaxMetaspaceSize=10M）
```
Exception in thread "main" java.lang.OutOfMemoryError: Metaspace
	at java.lang.Class.forName0(Native Method)
	at java.lang.Class.forName(Class.java:348)
	at net.sf.cglib.core.ReflectUtils.defineClass(ReflectUtils.java:386)
	at net.sf.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:219)
	at net.sf.cglib.proxy.Enhancer.createHelper(Enhancer.java:377)
	at net.sf.cglib.proxy.Enhancer.create(Enhancer.java:285)
	at com.peter.jvm.example2.constantPool.CGLibJavaMethodAreaOOM.main(CGLibJavaMethodAreaOOM.java:22)
```

解释

使用CGLib动态代理技术，可以不断的创建新的类，包括类的元信息存储于方法区，类的Class对象存储于堆区

类的元信息，在JDK7中是存储在永久代中的，因此使用PermSize参数来限制；在JDK8中是存储在元空间中的，因此使用MetaspaceSize来限制

无论是JDK7还是JDK8，合理设置参数，都可以复现出java.lang.OutOfMemoryError错误

> CGLib技术在很多应用型技术栈里都有体现，比如 ASM，Spring AOP等，这部分必须了解

### 堆区

#### 堆区的定义

堆区是存放对象实例的内存区域，是垃圾收集器管理的主要区域

现在的收集器基本都采用分代收集算法，堆区还可以分为新生代和老年代，新生代还可以分为Eden区，From Survivor区，To Survivor区

从内存分配角度来看，线程共享的JAVA堆中可能划分出多个线程私有的分配缓冲区（Thread Local Allocation Buffer, TLAB），不过无论如何划分，都与存放内容无关，无论哪个区域，存储的都仍然是对象实例

#### 新生代与老年代与永久代

说明定义、默认比例关系、调整比例关系方法

在JDK7及之前，堆内存被通常被分为下面三部分

1.新生代（Young Generation）
2.老年代（Old Generation）
3.永久代（Permanent Generation）

![image](https://user-images.githubusercontent.com/10209135/93738526-95bfa180-fc18-11ea-90fc-795c992e0283.png)

JDK8之后HotSpot的永久代被彻底移除了（JDK1.7 就已经开始了），取而代之是元空间，元空间使用的是直接内存

在JDK8及以后，堆内存只有新生代和老年代

![image](https://user-images.githubusercontent.com/10209135/93738570-b4be3380-fc18-11ea-9fb1-26627792e7e2.png)

以下是查看和设置 堆区及新生代内存大小的命令

环境版本是
```
java version "1.8.0_231"
Java(TM) SE Runtime Environment (build 1.8.0_231-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.231-b11, mixed mode)
```

查看堆区内存大小
```
java -XX:+PrintFlagsFinal -version | findStr "HeapSize"
uintx InitialHeapSize := 268435456
```

查看新生代内存大小
```
java -XX:+PrintFlagsFinal -version | findStr "NewSize"
uintx NewSize := 89128960
```

可以看出，HotSpot实现，JDK8版本中，新生代与老年代内存大小默认比例是1:2，新生代占堆区的1/3，老年代占堆区的2/3

设置VM参数例子

-Xms20M 设置堆区内存初始大小为20M

-Xmx20M 设置堆区内存最大大小为20M

-Xmn10M 设置新生代内存大小为10M

#### Eden区与两个Survivor区

Eden区、From Survivor区、To Survivor区是新生代的子区

对象优先在Eden区分配，当Eden区没有空间时，虚拟机将发起一次MinorGC

> 新生代GC（MinorGC）：指发生在新生代的垃圾收集动作，因为JAVA对象大多都具备朝生夕灭的特性，所以MinorGC非常频繁，一般回收速度也比较快

> 老年代GC（MajorGC / FullGC）：指发生在老年代的GC，出现了MajorGC，经常会伴随还少一次的MinorGC（但非绝对，在 Paraller Scavenge 收集器的收集策略里就有直接进行 MajorGC 的策略选择过程）。MajorGC的速度一般会比MinorGC慢10倍以上。尽量避免Major和FullGC是调优策略之一

如果对象在Eden区出生并经过第一次MinorGC后仍然能存活，并且能被Survivor容纳的话，将被移动到Survivor区中，并且对象年龄设为1

对象在Survivor区每“熬过”一次MinorGC，年龄就增加1，直到15岁后（默认），就会晋升到老年代中

From Survivor区与To Survivor区是两个内存大小相等的区域（一定相等，不可修改），共同负责存储Eden区中还存活的对象

垃圾回收时（复制算法），将Eden区和其中一个Survivor区还存活的对象一次性地复制到另一块Survivor区中

HotSpot实现，JDK8版本中，Eden区与两个Survivor区内存大小默认比例是8:1:1，也就是新生代可用内存大小为整个新生代内存大小的90%（Eden区 + 1个Survivor区）

可以通过VM参数 -XX:SurvivorRatio=A 来设置Eden区与两个Survivor区内存大小比例为A

#### 浅谈新生代到老年代

讲下从新生代到老年代的四种情况，不举具体例子

#### 字符串常量池

#### 堆区溢出

### 直接内存

### 区域间的指向关系
