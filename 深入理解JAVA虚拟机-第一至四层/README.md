# 深入理解JAVA虚拟机-第一至四层

### 前言

我希望写多篇文章，组合成一份文章集，包含关于JAVA虚拟机的入门原理、常见问题、代码例子证明等

读者阅读它，能像读小说一样，从第一篇文章，读到最后一篇文章，中间不带卡主，尽量把讳莫如深难懂的内容写得简单易懂。当然这也需要读者具有一定的JAVA功底

### JAVA虚拟机的定义

无论讨论什么，我们先需要将定义讨论清楚，否则就会有无穷的争执

JAVA虚拟机是一种能够运行JAVA字节码程序的虚拟机器

> A Java virtual machine (JVM) is a virtual machine that enables a computer to run Java programs as well as programs written in other languages that are also compiled to Java bytecode. From https://en.wikipedia.org/wiki/Java_virtual_machine.

### JAVA语言的水深

JAVA这潭大湖，从上往下，一共有7层（人为定义的）。参考 [JAVA学习蓝图，从基础、集合、并发、JVM原理，到应用框架](https://github.com/peteryuanpan/notebook/issues/51)

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
- [类加载机制](#类加载机制)
  - [类加载的定义](#类加载的定义)
  - [类加载的输入和输出结果](#类加载的输入和输出结果)
  - [InstanceKlass和InstanceMirrorKlass是什么](#instanceklass和instancemirrorklass是什么)
  - [类加载什么时候会进行](#类加载什么时候会进行)
    - [加载mainClass](#加载mainClass)
    - [new或getstatic或putstatic或invokestatic](#new或getstatic或putstatic或invokestatic)
    - [优先加载父类](#优先加载父类)
    - [反射](#反射)
  - [类加载笔试题统一解法][#类加载笔试题统一解法]
  - [运行期动态类加载](#运行期动态类加载)
  - [广义的类文件是二进制字节流](#广义的类文件是二进制字节流)
  - [类加载的五个过程](#类加载的五个过程)
    - [加载](#加载)
    - [验证](#验证)
    - [准备](#准备)
    - [解析](#解析)
    - [初始化](#初始化)
- [类加载器]
  - 三种系统类加载器
    - 启动类加载器
    - 拓展类加载器
    - 应用类加载器
  - 双亲委派模型
  - 破坏双亲委派模型
    - 自定义类加载器
    - 线程上下文类加载器与SPI机制
  
#### 第2章：Class类文件结构与字节码手册

#### 第3章：JVM内存模型之运行时数据区域

#### 第4章：对象的创建使用与内存布局

#### 第5章：垃圾收集算法与垃圾收集器

#### 第6章：JVM调优工具与实战

----------------------------------------------------------------------------------------------------------------------

# 类加载机制

### 类加载的定义

> The Java ClassLoader is a part of the Java Runtime Environment that dynamically loads Java classes into the Java Virtual Machine. The Java run time system does not need to know about files and file systems because of classloaders. Java classes aren't loaded into memory all at once, but when required by an application. From https://www.geeksforgeeks.org/classloader-in-java/

**类加载（包括类加载器）是Java虚拟机运行时环境的一部分，它能够动态地将Java类文件加载进入JVM中。**

由于类加载等过程实现了多平台的支持，因此Java是跨平台的

类加载是懒加载模式，并非一次性将所有.class文件都加载，而是需要时加载

### 类加载的输入和输出结果

类加载是JVM启动后靠前的几步之一

输入是.class文件

输出是许多数据写入内存区域，比如InstanceKlass到方法区，InstanceMirrorKlass到堆区

#### TODO
- 除了InstanceKlass和InstanceMirrorKlass，还有InstanceRefKlass、ArrayKlass、TypeArrayKlass、ObjArrayKlass，它们分别是写到内存区域中哪一块？
- 写入方法区和堆区有代码例子证明吗？

![image](http://tswork.peterpy.cn/java_runtime.png)

### InstanceKlass和InstanceMirrorKlass是什么

Klass是Java类在JVM中的存在形式（补充：OOP是JAVA对象在JVM中的存在形式）

InstanceKlass是类的元信息数据

InstanceMirrorKlass是类的Class对象

参考：[鲁班学院 第三期第一节 带你探索JVM底层之类加载](https://github.com/peteryuanpan/notebook/issues/43)

![image](https://user-images.githubusercontent.com/10209135/89729486-ced8f380-da68-11ea-81d4-e4b19825a4a0.png)

来看一下openjdk8源码

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/oops/instanceKlass.hpp#L138

```cpp
// An InstanceKlass is the VM level representation of a Java class.
// It contains all information needed for at class at execution runtime.

//  InstanceKlass layout:
//    [C++ vtbl pointer           ] Klass
//    [subtype cache              ] Klass
//    [instance size              ] Klass
//    [java mirror                ] Klass
//    [super                      ] Klass
//    [access_flags               ] Klass
//    [name                       ] Klass
//    [first subklass             ] Klass
//    [next sibling               ] Klass
//    [array klasses              ]
//    [methods                    ]
//    [local interfaces           ]
//    [transitive interfaces      ]
//    [fields                     ]
//    [constants                  ]
//    [class loader               ]
//    [source file name           ]
//    [inner classes              ]
//    [static field size          ]
//    [nonstatic field size       ]
//    [static oop fields size     ]
//    [nonstatic oop maps size    ]
//    [has finalize method        ]
//    [deoptimization mark bit    ]
//    [initialization state       ]
//    [initializing thread        ]
//    [Java vtable length         ]
//    [oop map cache (stack maps) ]
//    [EMBEDDED Java vtable             ] size in words = vtable_len
//    [EMBEDDED nonstatic oop-map blocks] size in words = nonstatic_oop_map_size
//      The embedded nonstatic oop-map blocks are short pairs (offset, length)
//      indicating where oops are located in instances of this klass.
//    [EMBEDDED implementor of the interface] only exist for interface
//    [EMBEDDED host klass        ] only exist for an anonymous class (JSR 292 enabled)

class InstanceKlass: public Klass {
  friend class VMStructs;
  friend class ClassFileParser;
  friend class CompileReplay;

 protected:
  // Constructor
  InstanceKlass(int vtable_len,
                int itable_len,
                int static_field_size,
                int nonstatic_oop_map_size,
                ReferenceType rt,
                AccessFlags access_flags,
                bool is_anonymous);
```

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/oops/instanceMirrorKlass.hpp#L41

```cpp
// An InstanceMirrorKlass is a specialized InstanceKlass for
// java.lang.Class instances.  These instances are special because
// they contain the static fields of the class in addition to the
// normal fields of Class.  This means they are variable sized
// instances and need special logic for computing their size and for
// iteration of their oops.

class InstanceMirrorKlass: public InstanceKlass {
  friend class VMStructs;
  friend class InstanceKlass;

 private:
  static int _offset_of_static_fields;

  // Constructor
  InstanceMirrorKlass(int vtable_len, int itable_len, int static_field_size, int nonstatic_oop_map_size, ReferenceType rt, AccessFlags access_flags,  bool is_anonymous)
    : InstanceKlass(vtable_len, itable_len, static_field_size, nonstatic_oop_map_size, rt, access_flags, is_anonymous) {}

 public:
  InstanceMirrorKlass() { assert(DumpSharedSpaces || UseSharedSpaces, "only for CDS"); }
  // Type testing
  bool oop_is_instanceMirror() const             { return true; }
```

可以看出来
- InstanceKlass中定义了Java运行时环境类所需的所有数据信息，比如 constants 常量池、static field size 静态成员变量大小、methods 方法 等（An InstanceKlass is the VM level representation of a Java class. It contains all information needed for at class at execution runtime.）
- InstanceMirrorKlass是InstanceKlass的一个子类
- InstanceMirrorKlass是java.lang.Class类专用的InstanceKlass（An InstanceMirrorKlass is a specialized InstanceKlass for java.lang.Class instances.）

简单总结一下，InstanceKlass包含了Java运行时环境中类所需的所有数据信息，在类加载这一步，类加载器会将.class文件读入类加载器的class content，然后以InstanceKlass的形式写入JVM内存区域的方法区中，而InstanceMirrorKlass是类所对应的Class对象（java.lang.Class）的InstanceKlass

> 辗转反侧了很久，终于理解了InstanceKlass和InstanceMirrorKlass。（2020-09-03）

> 对于InstanceKlass，在Java中，对于所有类，类的内部属性轮廓是不是可以认为是一样的？类都有父类、实现接口、变量、方法、代码块等，而变量和方法也有属性，即可访问性（public、private、protected、default）、静态与非静态，还有数组形式的。无论如何，类的内部属性以及属性的属性，是有限的、可列举的，那么在JVM中以C++代码就可以用一个类来表示，它就是IntanceKlass，这一层是比较好理解的。

> 对于InstanceMirrorKlass的理解，首先需要理解什么是java.lang.Class，我们来看一下这个类的注释吧：Instances of the class Class represent classes and interfaces in a running Java application. 翻译过来说，就是类或接口在Java运行时环境中的一个表达方式，再看看java.lang.Class的方法就知道，getConstructors()、getMethods()、getFields()、getDeclaredFields()，这些都是类的内部属性，且对于任何类来说，都可以用形如A.class的方法来获取java.lang.Class，即每个Java类都有一个java.lang.Class，那么在JVM中以C++代码表示，每个类的java.lang.Class就是InstanceMirrorKlass。java.lang.Class也是一个类，InstanceMirrorKlass是InstanceKlass的子类。

小补充下，关于InstanceRefKlass、ArrayKlass、TypeArrayKlass、ObjArrayKlass，可以参考：[关于JVM中InstanceKlass及ArrayKlass的理解](https://github.com/peteryuanpan/notebook/issues/49)

### 类加载什么时候会进行

参考
- [Java类加载及其关键过程原理：加载](https://github.com/peteryuanpan/notebook/issues/54#issuecomment-674741086)
- 《深入理解Java虚拟机》第二版 7.2节

五种情况
- 当虚拟机启动时，用户需要指定一个要执行的主类（包含main()方法的那个类），虚拟机会先初始化这个主类
- 遇到 new、getstatic、putstatic 或 invokestatic 这4条字节码指令时，如果类没有进行过初始化，则需要先触发其初始化
- 当初始化一个类的时候，如果发现其父类还没有进行过初始化，则需要先触发其父类的初始化
- 使用 java.lang.reflect 包的方法对类进行反射调用的时候，如果类没有进行过初始化，则需要先触发其初始化
- 档使用 JDK1.7 的动态语言支持时，如果一个 java.lang.invoke.MethodHandle 实例最后的解析结果 REF_getstatic、REF_putStatic、REF_invokeStatic 的方法句柄，并且这个方法句柄所对应的类没有进行过初始化，则需要先触发其初始化

第五种情况可以忽略不讨论

前四种情况分别举一下例子

#### 加载mainClass

当虚拟机启动时，用户需要指定一个要执行的主类（包含main()方法的那个类），虚拟机会先初始化这个主类

```java
package com.peter.jvm.example;

public class MainClassTest1 {

    static {
        System.out.println("11");
    }

    public static void main(String[] args) {

    }
}
```

会输出11，虚拟机会对主类进行类加载

来看下openjdk8源码：https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/jdk/src/share/bin/java.c#L444

```cpp
int JNICALL
JavaMain(void * _args)
{
...
/*
     * Get the application's main class.
     ...
*/
    mainClass = LoadMainClass(env, mode, what);
..
    mainID = (*env)->GetStaticMethodID(env, mainClass, "main",
                                       "([Ljava/lang/String;)V");
...
    (*env)->CallStaticVoidMethod(env, mainClass, mainID, mainArgs);
```

#### new或getstatic或putstatic或invokestatic

遇到 new、getstatic、putstatic 或 invokestatic 这4条字节码指令时，如果类没有进行过初始化，则需要先触发其初始化

##### new

```java
package com.peter.jvm.example;

public class ByteCodeNewTest1 {

    public static void main(String[] args) {
        ByteCodeNewTest1 t = new ByteCodeNewTest1();
    }
}

class ByteCodeNewTest12 {

    static {
        System.out.println("11");
    }

    ByteCodeNewTest12() {
        System.out.println(a);
        a = "22";
        System.out.println(a);
    }

    static String a = "33";

    static {
        System.out.println(a);
        a = "44";
        System.out.println(a);
    }
}
```

输出结果
```
11
33
44
44
22
```

字节码
```
0 new #2 <com/peter/jvm/example/ByteCodeNewTest12>
3 dup
4 invokespecial #3 <com/peter/jvm/example/ByteCodeNewTest12.<init>>
7 astore_1
8 return
```

解释

ByteCodeNewTest12 t = new ByteCodeNewTest12(); 对应 new ByteCodeNewTest12，ByteCodeNewTest12，输出11、a、a，第一个a是33，第二个a是44，然后调用构造函数，invokespecial ByteCodeNewTest12.init，输出a、a，第一个a是44，第二个a是22

注意：newarray，anewarray不会触发类加载

##### getstatic

```java
package com.peter.jvm.example;

public class ByteCodeGetStaticTest1 {
    
    public static void main(String[] args) {
        String a = ByteCodeGetStaticTest11.a;
        String b = ByteCodeGetStaticTest11.a;
    }
}

class ByteCodeGetStaticTest11 {

    static {
        System.out.println("11");
    }

    public static String a = "22";

    static {
        System.out.println("33");
    }
}
```

输出结果
```
11
33
```

字节码
```
0 getstatic #2 <com/peter/jvm/example/ByteCodeGetStaticTest11.a>
3 astore_1
4 getstatic #2 <com/peter/jvm/example/ByteCodeGetStaticTest11.a>
7 astore_2
8 return
```

解释

String a = ByteCodeGetStaticTest11.a; 对应 getstatic ByteCodeGetStaticTest11.a，会触发ByteCodeGetStaticTest11的类加载，输出11、33

String b = ByteCodeGetStaticTest11.a; 对应 getstatic ByteCodeGetStaticTest11.a，会尝试ByteCodeGetStaticTest11的类加载，发现加载过了，就不加载了

##### putsatic

```java
package com.peter.jvm.example;

public class ByteCodePutStaticTest1 {

    public static void main(String[] args) {
        ByteCodePutStaticTest11.a = "44";
        String a = ByteCodePutStaticTest11.a;
        System.out.println(a);
    }
}

class ByteCodePutStaticTest11 {

    static {
        System.out.println("11");
        //System.out.print(a); // 编译不过
    }

    public static String a = "22";

    static {
        System.out.println("33");
        System.out.println(a);
    }
}
```

输出结果
```
11
33
22
44
```

字节码
```
 0 ldc #2 <44>
 2 putstatic #3 <com/peter/jvm/example/ByteCodePutStaticTest11.a>
 5 getstatic #3 <com/peter/jvm/example/ByteCodePutStaticTest11.a>
 8 astore_1
 9 getstatic #4 <java/lang/System.out>
12 aload_1
13 invokevirtual #5 <java/io/PrintStream.println>
16 return
```

解释

ByteCodePutStaticTest1.a = "44"; 对应 putstatic ByteCodePutStaticTest1.a，会触发对ByteCodePutStaticTest1类加载，输出11、33、a，此时a还是22

String a = ByteCodePutStaticTest1.a; 对应 getstatic ByteCodePutStaticTest1.a，会尝试对ByteCodePutStaticTest1类加载，由于已经加载过了，不加载，a变为44

System.out.println(a); 输出44

##### invokestatic

```java
package com.peter.jvm.example;

public class ByteCodeInvokeStaticTest1 {

    public static void main(String[] args) {
        ByteCodeInvokeStaticTest11.aa();
    }
}

class ByteCodeInvokeStaticTest11 {

    {
        System.out.println("33");
    }

    static {
        System.out.println("44");
    }

    static void aa() {
        System.out.println(a);
    }

    static {
        System.out.println("66");
    }

    public static String a = new String("11");

    public String b = new String("22");

    void bb() {
        System.out.println("55");
    }
}
```

输出结果
```
44
66
11
```

字节码
```
0 invokestatic #2 <com/peter/jvm/example/ByteCodeInvokeStaticTest11.aa>
3 return
```

解释

ByteCodeInvokeStaticTest11.aa(); 对应着 invokestatic ByteCodeInvokeStaticTest11.aa，会先触发对ByteCodeInvokeStaticTest11类加载，输出44、66，再invoke aa，输出11

#### 优先加载父类

当初始化一个类的时候，如果发现其父类还没有进行过初始化，则需要先触发其父类的初始化

下面以invokestatic举例子（new、getstatic、putstatic同理）

##### 加载一个类时，会先去加载它的父类

```java
public class ByteCodeInvokeStaticSuperClassTest1 {

    public static void main(String[] args) {
        ByteCodeInvokeStaticSuperClassTest11.aa();
    }
}

class ByteCodeInvokeStaticSuperClassTest11 extends ByteCodeInvokeStaticSuperClassTest12 {

    static {
        System.out.println("11");
    }

    ByteCodeInvokeStaticSuperClassTest11() {
        System.out.println("22");
    }

    static void aa() {
        System.out.println("33");
    }

    static {
        System.out.println("44");
    }
}

class ByteCodeInvokeStaticSuperClassTest12 {

    static {
        System.out.println("55");
    }

    ByteCodeInvokeStaticSuperClassTest12() {
        System.out.println("66");
    }

    static void aa() {
        System.out.println("77");
    }

    static {
        System.out.println("88");
    }

}
```

输出结果
```
55
88
11
44
33
```

字节码
```
0 invokestatic #2 <com/peter/jvm/example/ByteCodeInvokeStaticSuperClassTest11.aa>
3 return
```

解释

ByteCodeInvokeStaticSuperClassTest11.aa(); 对应着 invokestatic ByteCodeInvokeStaticSuperClassTest11.aa，会先对父类ByteCodeInvokeStaticSuperClassTest11加载，输出55、88，然后对子类ByteCodeInvokeStaticSuperClassTest11加载，输出11、44，最后invokestatic ByteCodeInvokeStaticSuperClassTest11.aa，输出33

##### 加载一个类时，不会去加载它的子类

```java
package com.peter.jvm.example;

public class ByteCodeInvokeStaticSubClassTest1 {

    public static void main(String[] args) {
        ByteCodeInvokeStaticSubClassTest12.aa();
    }
}

class ByteCodeInvokeStaticSubClassTest11 extends ByteCodeInvokeStaticSubClassTest12 {

    static {
        System.out.println("11");
    }

    ByteCodeInvokeStaticSubClassTest11() {
        System.out.println("22");
    }

    static void aa() {
        System.out.println("33");
    }

    static {
        System.out.println("44");
    }
}

class ByteCodeInvokeStaticSubClassTest12 {

    static {
        System.out.println("55");
    }

    ByteCodeInvokeStaticSubClassTest12() {
        System.out.println("66");
    }

    static void aa() {
        System.out.println("77");
    }

    static {
        System.out.println("88");
    }

}
```

输出结果
```
55
88
77
```

字节码
```
0 invokestatic #2 <com/peter/jvm/example/ByteCodeInvokeStaticSubClassTest12.aa>
3 return
```

解释

ByteCodeInvokeStaticSubClassTest12.aa(); 对应着 invokestatic ByteCodeInvokeStaticSubClassTest12.aa，会触发对类ByteCodeInvokeStaticSubClassTest12加载，输出55、88，然后invokestatic ByteCodeInvokeStaticSubClassTest12.aa，输出77。由于ByteCodeInvokeStaticSubClassTest11是子类，对父类加载不会触发对子类加载

#### 反射

使用 java.lang.reflect 包的方法对类进行反射调用的时候，如果类没有进行过初始化，则需要先触发其初始化

```java
package com.peter.jvm.example;

public class ReflectClassTest1 {

    public static void main(String[] args) {
        try {
            Class c = ReflectClassTest11.class;
            c.getMethod("b").invoke(new Object());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ReflectClassTest11 {

    static {
        System.out.println("11");
    }

    public static String a = "22";

    static {
        System.out.println("33");
    }

    public static void b() {
        System.out.println("44");
    }
}
```

输出结果
```
11
33
44
```

解释

通过反射，调用函数b，会先进行类加载

如果try代码块中改成 Class c = Class.forName("com.peter.jvm.example.ReflectClassTest11");，也会进行类加载

#### 关于抽象类

与普通类规则一样

#### 关于接口（Interface）

- 接口中可以定义域（field），即变量，但隐式的都是static和final的
- 对接口进行类加载时，会对其成员变量（默认都是static的）进行初始化
- 对接口进行类加载时，不会加载其父接口
- 对实现一个接口的类进行类加载时，不会对接口进行类加载

```java
public class InterfaceClassLoaderTest1 implements InterfaceClassLoaderTest11 {

    public static void main(String[] args) {
        InterfaceClassLoaderTest1 t = new InterfaceClassLoaderTest1();
        System.out.println("11");
        InterfaceClassLoaderTest11.a.toString();
    }
}

interface InterfaceClassLoaderTest11 extends InterfaceClassLoaderTest12 {
    InterfaceClassLoaderTest13 a = new InterfaceClassLoaderTest13();
    InterfaceClassLoaderTest15 b = new InterfaceClassLoaderTest15();
}

interface InterfaceClassLoaderTest12 {
    InterfaceClassLoaderTest14 a = new InterfaceClassLoaderTest14();
}

class InterfaceClassLoaderTest13 {
    static {
        System.out.println("33");
    }
}

class InterfaceClassLoaderTest14 {
    static {
        System.out.println("44");
    }
}

class InterfaceClassLoaderTest15 {
    static {
        System.out.println("55");
    }
}
```

输出结果
```
11
33
55
```

字节码
```
 0 new #2 <com/peter/jvm/example/InterfaceClassLoaderTest1>
 3 dup
 4 invokespecial #3 <com/peter/jvm/example/InterfaceClassLoaderTest1.<init>>
 7 astore_1
 8 getstatic #4 <java/lang/System.out>
11 ldc #5 <11>
13 invokevirtual #6 <java/io/PrintStream.println>
16 getstatic #7 <com/peter/jvm/example/InterfaceClassLoaderTest11.a>
19 invokevirtual #8 <java/lang/Object.toString>
22 pop
23 return
```

解释

InterfaceClassLoaderTest1 t = new InterfaceClassLoaderTest1 (); 对应 new InterfaceClassLoaderTest1 ，会对类InterfaceClassLoaderTest1 进行类加载，但不会对接口InterfaceClassLoaderTest11 进行类加载，因此无输出

System.out.println("11");，输出11

InterfaceClassLoaderTest11.a.toString(); 对应 getstatic InterfaceClassLoaderTest11.a，会对InterfaceClassLoaderTest11进行类加载，InterfaceClassLoaderTest13 a和InterfaceClassLoaderTest15 b都是static的，它们都执行了new，都会加载这两个类，因此输出33和55

对接口进行类加载时，不会加载其父接口

### 类加载笔试题统一解法

通过上面例子，我们见到了一些关于类加载触发时机、父子类类加载关系的题目，基本上题型都一样，给一段简单的代码，请问代码的输出结果是什么，有的是问答题，有的是选择题，这类题目在Java基础笔试中占一定比重

经过总结，统一解法满足以下节点
1、main函数所在类先进行加载，结束后调用main方法
2、默认情况下，已经加载过的类不会加载（非默认情况是指自定义类加载器）
3、类加载会在“准备”阶段，执行static修饰的代码块（static{}），对static修饰的域（field，即成员变量）进行赋值
4、类加载执行顺序与代码书写顺序一致
5、当遇到new、getstatic、putstatic 或 invokestatic 这4条字节码指令时，会进行类加载且插入结算

下面我们以几个典型例子来证明上面统一解法的合理性

#### 类加载笔试题统一解法例子1

TODO

### 运行期动态类加载

《深入理解Java虚拟机》第二版 7.1节

Java里天生可以动态拓展的语言特性就是依赖运行期动态加载和动态链接这个特点实现的。

例如，如果编写一个面向接口的应用程序，可以等到运行时再指定其实际的实现类；用户可以通过Java预定义的和自定义加载器，让一个本地的应用程序可以在运行时从网络其他地方加载一个二进制流作为程序代码的一部分，这种组装应用程序的方式目前已广泛应用于Java程序中。

### 广义的类文件是二进制字节流

狭义的class文件，指某个存在于具体磁盘中的文件；广义的class文件，指二进制字节流（符合class文件格式），比如jar包中的class文件，磁盘中单个class文件，网络流加载而来的字节流，从数据库中读取的，从加密文件中读取的，都可以

### 类加载的五个过程

#### 加载

TODO

#### 验证

TODO

#### 准备

TODO

#### 解析

TODO

#### 初始化

TODO
