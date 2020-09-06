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
- [类加载机制](#类加载机制)
  - [类加载的定义](#类加载的定义)
  - [JAVA运行时环境逻辑图](#JAVA运行时环境逻辑图)
  - [类加载的输入和输出结果](#类加载的输入和输出结果)
  - [InstanceKlass和InstanceMirrorKlass是什么](#instanceklass和instancemirrorklass是什么)
  - [类加载的五个过程](#类加载的五个过程)
  - [类加载之加载](#类加载之加载)
    - [加载的规范定义](#加载的规范定义)
    - [广义的类文件是二进制字节流](#广义的类文件是二进制字节流)
    - [运行期动态类加载是特性](#运行期动态类加载是特性)
    - [数组类的加载过程](#数组类的加载过程)
  - [类加载之验证](#类加载之验证)
    - [验证的规范定义](#验证的规范定义)
    - [四种验证方法](#四种验证方法)
    - [关闭大部分验证措施](#关闭大部分验证措施)
  - [类加载之准备](#类加载之准备)
    - [准备的规范定义](#准备的规范定义)
    - [数据类型的零值](#数据类型的零值)
    - [static+final修饰的变量](#static+final修饰的变量)
  - [类加载之解析](#类加载之解析)
    - [解析的规范定义](#解析的规范定义)
    - [符号引用替换为直接引用](#符号引用替换为直接引用)
  - [类加载之初始化](#类加载之初始化)
    - [初始化的规范定义](#初始化的规范定义)
    - [clinit方法的理解](#clinit方法的理解)
  - [类加载什么时候会进行](#类加载什么时候会进行)
    - [加载mainClass](#加载mainClass)
    - [new或getstatic或putstatic或invokestatic](#new或getstatic或putstatic或invokestatic)
    - [优先加载父类](#优先加载父类)
    - [反射](#反射)
  - [类加载笔试题统一解法](#类加载笔试题统一解法)
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

#### 第3章：JVM运行时数据区域与对象的解密

#### 第4章：垃圾收集算法与垃圾收集器

#### 第5章：JVM运维与调优工具实战

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

> 在第二版（7.3.1节）中，InstanceMirrorKlass（即java.lang.Class对象）是写在方法区中的，这是JDK7的逻辑；而在第三版（7.3.1节）中，InstanceMirrorKlass是写在堆内存中的，这是JDK8的逻辑

> 除了InstanceKlass和InstanceMirrorKlass，还有InstanceRefKlass、ArrayKlass、TypeArrayKlass、ObjArrayKlass，它们分别是写到内存区域中哪一块？

> 写入方法区和堆区有代码例子证明吗？

### JAVA运行时环境逻辑图

![image](http://tswork.peterpy.cn/java_runtime.png)

### InstanceKlass和InstanceMirrorKlass是什么

Klass是Java类在JVM中的存在形式（补充：OOP是JAVA对象在JVM中的存在形式）

InstanceKlass是类的元信息数据

InstanceMirrorKlass是类的Class对象

下面是这几个Klass的继承关系图

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

> 对于InstanceKlass，在Java中，对于所有类，类的内部属性轮廓是不是可以认为是一样的？类都有父类、实现接口、变量、方法、代码块等，而变量和方法也有属性，即可访问性（public、private、protected、default）、静态与非静态，还有数组形式的。无论如何，类的内部属性以及属性的属性，是有限的、可列举的，那么在JVM中以C++代码就可以用一个类来表示，它就是InstanceKlass，这一层是比较好理解的。

> 对于InstanceMirrorKlass的理解，首先需要理解什么是java.lang.Class，我们来看一下这个类的注释吧：Instances of the class Class represent classes and interfaces in a running Java application. 翻译过来说，就是类或接口在Java运行时环境中的一个表达方式，再看看java.lang.Class的方法就知道，getConstructors()、getMethods()、getFields()、getDeclaredFields()，这些都是类的内部属性，且对于任何类来说，都可以用形如A.class的方法来获取java.lang.Class，即每个Java类都有一个java.lang.Class，那么在JVM中以C++代码表示，每个类的java.lang.Class就是InstanceMirrorKlass。java.lang.Class也是一个类，InstanceMirrorKlass是InstanceKlass的子类。

#### ArrayKlass、TypeArrayKlass、ObjArrayKlass的理解

在理解了InstanceKlass后，这三个类就很好理解了

- ArrayKlass表示的是数组类型（ArrayKlass is the abstract baseclass for all array classes）
- TypeArrayKlass表示基本数组类型（A TypeArrayKlass is the klass of a typeArray, It contains the type and size of the elements），type是八大基本类型
- ObjArrayKlass表示引用数组类型（ObjArrayKlass is the klass for objArrays），type是Reference

下面的源码部分

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/oops/arrayKlass.hpp
```cpp
// ArrayKlass is the abstract baseclass for all array classes

class ArrayKlass: public Klass {
  friend class VMStructs;
 private:
  int      _dimension;         // This is n'th-dimensional array.
  Klass* volatile _higher_dimension;  // Refers the (n+1)'th-dimensional array (if present).
  Klass* volatile _lower_dimension;   // Refers the (n-1)'th-dimensional array (if present).
  int      _vtable_len;        // size of vtable for this klass
  oop      _component_mirror;  // component type, as a java/lang/Cl
```

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/oops/typeArrayKlass.hpp
```cpp
// A TypeArrayKlass is the klass of a typeArray
// It contains the type and size of the elements

class TypeArrayKlass : public ArrayKlass {
  friend class VMStructs;
 private:
  jint _max_length;            // maximum number of elements allowed in an array

  // Constructor
  TypeArrayKlass(BasicType type, Symbol* name);
  static TypeArrayKlass* allocate(ClassLoaderData* loader_data, BasicType type, Symbol* name, TRAPS);
 public:
  TypeArrayKlass() {} // For dummy objects.
```

https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/hotspot/src/share/vm/oops/objArrayKlass.hpp
```cpp
// ObjArrayKlass is the klass for objArrays

class ObjArrayKlass : public ArrayKlass {
  friend class VMStructs;
 private:
  Klass* _element_klass;            // The klass of the elements of this array type
  Klass* _bottom_klass;             // The one-dimensional type (InstanceKlass or TypeArrayKlass)

  // Constructor
  ObjArrayKlass(int n, KlassHandle element_klass, Symbol* name);
  static ObjArrayKlass* allocate(ClassLoaderData* loader_data, int n, KlassHandle klass_handle, Symbol* name, TRAPS);
 public:
  // For dummy objects
  ObjArrayKlass() {}
```

#### InstanceRefKlass 的理解

描述java.lang.ref.Reference的子类，这部分的概念与强软弱虚引用、垃圾回收有关系，见第5章

### 类加载的五个过程

生命周期是由7个阶段组成，类的加载说的是前5个阶段，即加载=>验证=>准备=>解析=>初始化

> 提一点，不要认为类加载是严格地前一阶段结束后一阶段开始，加载阶段尚未完成时，连接阶段（验证、准备、解析）可能已经开始了，但它们的开始时间是保持着固定的先后顺序的

![image](https://user-images.githubusercontent.com/10209135/92302466-f30fed80-ef9e-11ea-9b2b-a1615c445177.png)

### 类加载之加载

#### 加载的规范定义

- 通过一个的全限定名（类名+包名）来获取定义此类的二进制字节流
- 将这个字节流所代表的静态存储结构转化为方法区的运行时数据结构
- 在内存中生成一个代表这个类的java.lang.Class对象，作为方法区这个类的各种数据访问入口

> 在【类加载的输入和输出结果】中我们提过，输入是.class文件，输出是许多数据写入内存区域，比如InstanceKlass到方法区，InstanceMirrorKlass到堆区，与上面这段话是对应着的

> 上面的类加载过程能在JVM源码中找到位置吗？

#### 广义的类文件是二进制字节流

狭义的class文件，指某个存在于具体磁盘中的文件

广义的class文件，指二进制字节流（符合class文件格式），比如ZIP、JAR、EAR、WAR包中的class文件，从网络中获取的（典型场景：Applet），运行时计算生成的（反射技术），由其他文件生成的（JSP文件生成对应的Class类），从数据库中获取的（应用较少，比如SAP Netweaver），都可以。虚拟机规范没有指明加载方法、位置等，因此具体实现的舞台很大

#### 运行期动态类加载是特性

Java里天生可以动态拓展的语言特性就是依赖运行期动态加载和动态链接这个特点实现的

例如，如果编写一个面向接口的应用程序，可以等到运行时再指定其实际的实现类；用户可以通过Java预定义的和自定义加载器，让一个本地的应用程序可以在运行时从网络其他地方加载一个二进制流作为程序代码的一部分，这种组装应用程序的方式目前已广泛应用于Java程序中

#### 数组类的加载过程

对于数组类而言，数组类本身不通过类加载器创建，它是由Java虚拟机直接创建的。但数组类与类加载器仍然有很密切的关系，因为数组类的元素类型（ElementType，指的是数组去掉所有维度的类型）最终是要靠类加载器去创建

创建过程遵循以下规则

- 如果数组的组件类型（Component Type，指的是数组去掉一个维度的类型）是引用类型，那就递归采用 自定义类加载器 => 应用程序类加载器 => 拓展类加载器 => 启动类加载器 的加载过程
- 如果数组的组件类型是基本类型（比如 int[]数组），就标记为与引导类加载器关联

> 引导类加载器我理解为启动类加载器

> 上面的数组类类加载过程能在JVM源码中找到位置吗？

### 类加载之验证

#### 验证的规范定义

确保Class文件的字节流中包含的信息符合当前虚拟机的要求，并且不会危害虚拟机自身的安全

> 第一，验证这个动作对于编写JVM的程序员来说，一定是一件非常繁琐的事情，因为需要考虑的因素非常之多，但是，对于JAVA应用层业务层的程序员来说，有一点点前人修路后人走路的感觉，而且这个前人修的路还不会坏，后人可以随意的走，因为虚拟机经过了好几代的迭代，到了JDK8及以后，验证这一步已经非常成熟了，连虚拟机规范都从最初的10页不到内容，增加到了130页（2011年发布的《Java虚拟机规范（JavaSE7版）》）

> 第二，如果是经过javac编译器编译出的class文件，理论上应该不需要进行那么复杂的检查流程，比如公司内部老员工给另一个老员工传了份文件，还检查什么呢。但是编写JVM的程序员很辛苦，他们当然不能只考虑到一种情况，还要考虑到种种意外的可能情况

> 总的来说，个人觉得验证这一步不是那么的重要，不需要去特别记忆，但是验证的过程实际上是对Class文件结构的更深入理解，从这个角度来看就存在重要性了

#### 四种验证方法

- 文件格式验证：字节流内容是否符合Class文件格式规范
- 元数据验证：字节码描述的语义信息是否符合Java语言规范
- 字节码验证：字节码语义（主要是方法体）是否可执行无危害；但由于停机问题，无法100%验证
- 符号引用验证：符号引用转化为直接引用是否符合规范，对类自身以外（常量池中的各种符号引用）的信息进行匹配性校验

下面简单的列举一下虚拟机中的四种验证以及验证点

##### 文件格式验证

第一阶段需要验证字节流是否符合Class文件格式的规范

比如
- 是否以魔数0xCAFEABE开头
- 常量池中的常量中是否有不被支持的常量类型（检查常量tag标志）
- Class文件中各个部分及文件本身是否有被删除的或附加的其他信息

这阶段的验证是基于二进制字节流进行的，只有通过了这个阶段的验证后，字节流才会进入内存的方法区中进行存储，所以后面的3个验证阶段全部是基于方法区的存储结构进行的，不会再直接操作字节流

##### 元数据验证

第二阶段是对字节码描述的信息进行语义分析，以保证其描述的信息符合Java语言规范的要求

比如
- 这个类是否有父类（除了java.lang.Object以外，所有的类应当都有父类）
- 这个类的父类是否继承了不允许被继承的类（被final修饰的类）
- 如果这个类不是抽象类，是否实现了其父类或接口之中要求实现的所有方法
- 类中的字段、方法是否与父类产生矛盾（例如覆盖了父类的final字段，或者出现不符合规则的方法重载等）

##### 字节码验证

第三阶段是整个验证过程中最复杂的一个阶段，主要目的是通过数据流和控制流分析，确定程序语义是合法的、符合逻辑的

第二阶段会对元数据信息的数据类型进行校验，这一阶段是将对类的方法体进行校验分析，保证被校验类的方法在运行时不会做出危害虚拟机安全的事件

比如
- 保证任意时刻操作数栈的数据类型与指令代码序列都能配合工作，比如不会出现这样的情况：在操作数栈放置了一个int类型的数据，使用时却按long类型来加载入本地变量表中
- 保证跳转指令不会跳转到方法体以外的字节码指令上
- 保证方法体中的类型转换是有效的，比如父类对象赋值给子类数据类型，甚至把对象赋值给与它毫无继承关系、完全不相干的一个数据类型，则是危险和不合法的

如果一个类的方法体的字节码没有通过字节码验证，那肯定是有问题的，但是，如果一个方法体通过了字节码验证，也不能说明一定是安全的，即时字节码验证之中进行了大量的校验检查，也不能保证这一点，这里涉及了离线数学中一个很著名的问题“Halting Problem”

> 停机问题（英语：halting problem）是逻辑数学中可计算性理论的一个问题。通俗地说，停机问题就是判断任意一个程序是否能在有限的时间之内结束运行的问题。该问题等价于如下的判定问题：是否存在一个程序P，对于任意输入的程序w，能够判断w会在有限时间内结束或者死循环。艾伦·图灵在1936年用对角论证法证明了，不存在解决停机问题的通用算法。参考：https://wiwiki.kfd.me/wiki/停机问题

##### 符号引用验证

最后一个阶段的校验发生在虚拟机将符号引用转化为直接引用的时候，这个转化动作将在连接的第三阶段————解析阶段中发生

符号引用验证可以看做是对类自身以外（常量池中的各种符号引用）的信息进行匹配性校验

比如
- 符号引用中通过字符串描述的全限定名是否能找到对应的类
- 在指定类中是否存在符合方法的字段描述符以及简单名称所描述的方法和字段
- 符号引用中的类、字段、方法的访问性（private、protected、public、default）是否可被当前类访问

> 上面四种验证及验证点，能在JVM源码中找到位置吗？

#### 关闭大部分验证措施

对于虚拟机的类加载机制来说，验证阶段时一个非常重要的阶段，但不是一定必要的。如果所运行代码都已经被反复使用和验证过，那么在实施阶段可以考虑使用 -Xverify:none 参数来关闭大部分的类验证措施，以缩短虚拟机类加载的时间

### 类加载之准备

#### 准备的规范定义

准备阶段正式为静态变量（static修饰的）分配内存于堆区，并设置静态变量的零值（final修饰的除外）。这里不包括普通实例变量

> 在JDK7及之前，Hotspot使用的永久代来实现方法区时，静态变量（也成为类变量）是在方法区中分配的，而JDK8及以后，静态变量会随着Class对象一起移到Java堆中

#### 数据类型的零值

这里再加一点干货：基本数据类型的长度、范围、自动转换关系

|数据类型|零值|长度|范围|
|--|--|--|--|
|boolean|false|1个字节|true或false|
|byte|(byte)0|1个字节|-128~127|
|char|'\u0000'|2个字节|从字符型对应的整型数来划分，其表示范围是0~65535|
|short|(short)0|2个字节|-32768~32767|
|int|0|4个字节|-2147483648~2147483647|
|long|0L|8个字节|-9223372036854775808 ~ 9223372036854775807|
|float|0.0f|4个字节|-3.4E38~3.4E38|
|double|0.0d|8个字节|-1.7E308~1.7E308|
|reference|null|-|-|

##### 自动转换关系

- (byte，short，char) -> int -> long -> float -> double
- 不符合上面关系链的，由高到低的，需要强制转换，比如 int a = 0; char c = (char) a;
- boolean不支持与其他基本类型转换，包括强制转换
- long占8个字节，float占4个字节，但long可以自动转换为float，是因为long是有符号整形，float、double是浮点表示法表示的，二者底层表示方式不一样

#### static+final修饰的变量

static、final同时修饰的变量，不会赋零值，而会赋初值

在javac编译时会为变量生成ConstantValue属性，在准备阶段虚拟机会初始化为ConstantValue属性所指定的值

比如
```java
public static int v1 = 123;
public static final int v2 = 123;
```
在准备阶段结束后，v1是0，v2是123

> 这里要说明的更清楚一些，准备阶段后面还有一个初始化阶段，在初始化阶段，会给静态变量赋初值

> 因此在做题时，不要因为准备阶段是赋零值，而去思考输出结果可能是零值，实际上你无法做到实现一个Java代码，准备阶段后（初始化阶段没进行）输出变量结果，必须要初始化阶段结束，才可能System.out.println的

### 类加载之解析

#### 解析的规范定义

解析阶段是虚拟机将常量池内的符号引用替换为直接引用的过程

解析动作主要针对类或接口、字段、类方法、接口方法、方法类型、方法句柄和调用点限定符7类符号引用进行，分别对应于CONSTANT_Class_info、CONSTANT_Field_info、CONSTANT_Methodref_info、CONSTANT_InterfaceMethodref_info、CONSTANT_MethodType_info、CONSTANT_MethodHandle_info和CONSTANT_InvokeDynamic_info

解析动作具体来看，有
- 类或接口的解析。假设有一个类D，要把一个从未解析过的符号引用N解析为一个类或接口C的直接引用，分C是或不是数组类型来处理
  - 如果C不是一个数组类型，那么虚拟机会把代表N的全限定名传递给D的类加载器，去加载这个类C（第1点）
  - 如果C是一个数组类型，并且数组的元素类型为对象，也就是N的描述符会是类似“【Ljava/lang/Integer”的形式，那么将会按照（第1点）的规则加载数组元素类型，接着虚拟机会生成一个代表此数组维度和元素的数组对象
- 字段解析。需要对字段表内的class_index项中索引的CONSTANT_Class_info符号引用（字段所属的类或接口的符号引用）进行解析
- 类方法解析。需要对方法表的class_index项中索引的CONSTANT_Methodref_info符号引用（方法所属的类或接口的符号引用）进行解析
- 接口方法解析。需要对接口方法表的class_index项中索引的CONSTANT_InterfaceMethodref_info符号引用（方法所属的类或接口的符号引用）进行解析

> 解析阶段的部分关键词是：常量池、符号引用、直接引用、指针、字段表、方法表等，理解这个阶段，需要对运行时常量池、Class文件中的常量池有一定的理解

> 不要认为常量池只是与字符串常量、基本类型数据常量有关系而已，它还与类方法、接口方法等有关系

#### 符号引用替换为直接引用

符号引用（Symbolic References）：符号引用以一组符号来描述所引用的目标，符号可以是任何形式的字面量，只要使用时能无歧义地定位到目标即可

直接引用（Direct References）：直接引用可以是直接指向目标的指针、相对偏移量或是一个能间接定位到目标的句柄

> 其实上面的说法，个人认为是比较讳莫如深的，下面我们举一个代码例子来理解

```java
package com.peter.jvm.example;

public class SymbolicReferencesToDirectReferencesTest {

    public static void main(String[] args) {
        SymbolicReferencesToDirectReferencesTest test = new SymbolicReferencesToDirectReferencesTest();
        //while(true);
    }
}
```

这段代码，查看字节码中的常量池（Constant Pool）部分

Constant_Class_info内容为Class name: cp_info_#20 <com/peter/jvm/example/SymbolicReferencesToDirectReferencesTest>

此时是符号引用

![image](https://user-images.githubusercontent.com/10209135/92321733-f074cd00-f05e-11ea-8d0f-5bd7c74c90b2.png)

再用HSDB分析一下

Constant Type = JVM_CONSTANT_Class 时，Constant Value = public class com.peter.jvm.example.SymbolicReferencesToDirectReferencesTest @0x00000007c0060828

此时已经是直接引用了（转换为了内存地址）

![image](https://user-images.githubusercontent.com/10209135/92321723-d9ce7600-f05e-11ea-8ba7-6ab97c1b1991.png)

### 类加载之初始化

#### 初始化的规范定义

#### clinit方法的理解

### 类加载什么时候会进行

参考《深入理解Java虚拟机》第二版 7.2节

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

遇到 new、getstatic、putstatic或invokestatic这4条字节码指令时，如果类没有进行过初始化，则需要先触发其初始化

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

通过上面例子，我们见到了一些关于类加载触发时机、父子类类加载关系的题目，基本上题型都一样，给一段简单的代码，请问代码的输出结果是什么，有的是问答题，有的是选择题，这类题目在Java基础笔试题中占一定比重

经过总结，统一解法满足以下七条规则
- 规则一：main函数所在类最先进行加载，结束后调用main方法
- 规则二：加载一个类之前，会先加载该类的父类，但不会加载该类实现的接口
- 规则三：加载一个接口，不会加载其父接口
- 规则四：默认情况下，同一个类加载器，已经加载过的类不会加载（非默认情况是指自定义类加载器）
- 规则五：类加载会在“初始化”阶段，调用clinit方法，执行静态代码块，对静态变量赋初值
- 规则六：clinit方法执行顺序与代码书写顺序保持一致
- 规则七：当遇到new、getstatic、putstatic或invokestatic这4条字节码指令时，会插入结算新的类加载，再执行指令，再继续旧的类加载

下面举一个例子来充实上面的规则

```java
package com.peter.jvm.example;

public class ClassLoaderAllTest {

    static {
        System.out.println("ClassLoaderAllTest clinit 1");
    }

    static ClassLoaderAllTest1 test1 = new ClassLoaderAllTest1();

    static {
        System.out.println("ClassLoaderAllTest clinit 2");
    }

    public static void main(String[] args) {
        System.out.println("ClassLoaderAllTest main 1");
        ClassLoaderAllTest6 test6 = ClassLoaderAllTest4.test6;
        System.out.println("ClassLoaderAllTest main 2");
        ClassLoaderAllTest5.test5();
    }
}

class ClassLoaderAllTest1 extends ClassLoaderAllTest2 implements ClassLoaderAllTest4, ClassLoaderAllTest5 {
    static {
        System.out.println("ClassLoaderAllTest1 clinit");
        ClassLoaderAllTest3.test3_1 = "ClassLoaderAllTest3 test3_1 Test1";
    }
    ClassLoaderAllTest1() {
        System.out.println("ClassLoaderAllTest1 init");
    }
}

class ClassLoaderAllTest2 {
    static {
        System.out.println("ClassLoaderAllTest2 clinit");
        System.out.println(ClassLoaderAllTest3.test3_2);
    }
}

class ClassLoaderAllTest3 {
    static String test3_1 = "ClassLoaderAllTest3 test3_1";
    static final String test3_2 = "ClassLoaderAllTest3 test3_2";
    static {
        System.out.println(test3_1);
    }
}

interface ClassLoaderAllTest4 extends ClassLoaderAllTest5 {
    ClassLoaderAllTest7 test7 = new ClassLoaderAllTest7();
    ClassLoaderAllTest6 test6 = new ClassLoaderAllTest6();
}

interface ClassLoaderAllTest5 {
    ClassLoaderAllTest7 test7 = new ClassLoaderAllTest7();
    ClassLoaderAllTest8 test8 = new ClassLoaderAllTest8();
    static void test5() {
        System.out.println("ClassLoaderAllTest5 test5");
    }
}

class ClassLoaderAllTest6 {
    static {
        System.out.println("ClassLoaderAllTest6 clinit");
    }
}

class ClassLoaderAllTest7 {
    static {
        System.out.println("ClassLoaderAllTest7 clinit");
    }
}

class ClassLoaderAllTest8 {
    static {
        System.out.println("ClassLoaderAllTest8 clinit");
    }
}
```

输出结果
```
ClassLoaderAllTest clinit 1
ClassLoaderAllTest2 clinit
ClassLoaderAllTest3 test3_2
ClassLoaderAllTest1 clinit
ClassLoaderAllTest3 test3_1
ClassLoaderAllTest1 init
ClassLoaderAllTest clinit 2
ClassLoaderAllTest main 1
ClassLoaderAllTest7 clinit
ClassLoaderAllTest6 clinit
ClassLoaderAllTest main 2
ClassLoaderAllTest8 clinit
ClassLoaderAllTest5 test5
```

#### 解释
##### 程序开始
##### 加载ClassLoaderAllTest类开始
- 由于规则一，因此最先加载ClassLoaderAllTest类
- 由于规则五、六，因此执行System.out.println("ClassLoaderAllTest clinit 1"); 再执行static ClassLoaderAllTest1 test1 = new ClassLoaderAllTest1(); 再执行System.out.println("ClassLoaderAllTest static 2");
- 由于执行了System.out.println("ClassLoaderAllTest clinit 1"); 因此输出ClassLoaderAllTest clinit 1
- 再执行static ClassLoaderAllTest1 test1 = new ClassLoaderAllTest1()
- static ClassLoaderAllTest1 test1 = new ClassLoaderAllTest1();对应的字节码是new #9 <com/peter/jvm/example/ClassLoaderAllTest1>
- 由于规则七，因此先加载ClassLoaderAllTest1类，再new
##### 加载ClassLoaderAllTest1类开始（尚未开始）
- class ClassLoaderAllTest1 extends ClassLoaderAllTest2，即ClassLoaderAllTest2是ClassLoaderAllTest1的父类
- 由于规则二，因此先加载ClassLoaderAllTest2类
- class ClassLoaderAllTest1 implements ClassLoaderAllTest4, ClassLoaderAllTest5，即ClassLoaderAllTest1实现了ClassLoaderAllTest4、ClassLoaderAllTest5接口
- 由于规则三，因此不会加载ClassLoaderAllTest4、ClassLoaderAllTest5接口
##### 加载ClassLoaderAllTest2类开始
- 由于规则五、六，因此执行System.out.println("ClassLoaderAllTest2 clinit"); 再执行System.out.println(ClassLoaderAllTest3.test3_2);
- 由于执行了System.out.println("ClassLoaderAllTest2 clinit"); 因此输出ClassLoaderAllTest2 clinit
- 再执行System.out.println(ClassLoaderAllTest3.test3_2); 因此输出ClassLoaderAllTest3 test3_2
- System.out.println(ClassLoaderAllTest3.test3_2);对应字节码是getstatic #2 <java/lang/System.out>、ldc #6 <ClassLoaderAllTest3 test3_2>、invokevirtual #4 <java/io/PrintStream.println>，这里没有getstatic ClassLoaderAllTest3.test3_2，因此不会去加载ClassLoaderAllTest3
- 没有getstatic ClassLoaderAllTest3.test3_2的原因是test3_2是final修饰的，编译器会对它做处理，在准备阶段就会初始化为一个ConstantValue属性所指定的值
##### 加载ClassLoaderAllTest2类完毕
- 加载完ClassLoaderAllTest2类，回来，加载ClassLoaderAllTest1类
##### 加载ClassLoaderAllTest1类开始（正式开始）
- 由于规则五、六，执行System.out.println("ClassLoaderAllTest1 clinit"); 再执行ClassLoaderAllTest3.test3_1 = "ClassLoaderAllTest3 test3_1 Test1";
- 由于执行了System.out.println("ClassLoaderAllTest1 clinit");，因此输出ClassLoaderAllTest1 clinit
- 再执行ClassLoaderAllTest3.test3_1 = "ClassLoaderAllTest3 test3_1 Test1";
- ClassLoaderAllTest3.test3_1 = "ClassLoaderAllTest3 test3_1 Test1";对应字节码是putstatic #6 <com/peter/jvm/example/ClassLoaderAllTest3.test3_1>
- 由于规则七，因此先加载ClassLoaderAllTest3类，再putstatic
##### 加载ClassLoaderAllTest3类开始
- 由于规则五、六，因此执行static String test3_1 = "ClassLoaderAllTest3 test3_1"; 再执行System.out.println(test3_1); 再执行ClassLoaderAllTest3.test3_1 = "ClassLoaderAllTest3 test3_1 Test1";
- 由于执行了System.out.println(test3_1); 因此输出ClassLoaderAllTest3 test3_1，而不是输出ClassLoaderAllTest3 test3_1 Test1
##### 加载ClassLoaderAllTest3类完毕
##### 加载ClassLoaderAllTest1类完毕
- 加载完ClassLoaderAllTest1类，回来，执行new #9 <com/peter/jvm/example/ClassLoaderAllTest1>，实例化ClassLoaderAllTest1类
- 由于执行了System.out.println("ClassLoaderAllTest1 init"); 因此输出ClassLoaderAllTest1 init
- 继续，由于执行了System.out.println("ClassLoaderAllTest clinit 2"); 因此输出ClassLoaderAllTest clinit 2
##### 加载ClassLoaderAllTest类完毕
##### 执行ClassLoaderAllTest的main方法开始
- 由于执行了System.out.println("ClassLoaderAllTest main 1"); 因此输出ClassLoaderAllTest main 1
- 再执行ClassLoaderAllTest6 test6 = ClassLoaderAllTest4.test6;
- ClassLoaderAllTest6 test6 = ClassLoaderAllTest4.test6;对应的字节码是getstatic #5 <com/peter/jvm/example/ClassLoaderAllTest4.test6>
- 由于规则七，因此先加载ClassLoaderAllTest4接口，再getstatic
- interface ClassLoaderAllTest4 extends ClassLoaderAllTest5，即ClassLoaderAllTest5是ClassLoaderAllTest4的父接口
- 由于规则三，因此不会加载ClassLoaderAllTest5接口
##### 加载ClassLoaderAllTest4接口开始
- 接口中的变量都是默认static、final修饰的
- 由于规则五、六，执行ClassLoaderAllTest7 test7 = new ClassLoaderAllTest7(); 再执行ClassLoaderAllTest6 test6 = new ClassLoaderAllTest6();
- ClassLoaderAllTest7 test7 = new ClassLoaderAllTest7();对应的字节码是new #1 <com/peter/jvm/example/ClassLoaderAllTest7>
- 由于规则七，因此先加载ClassLoaderAllTest7类，再new
##### 加载ClassLoaderAllTest7类开始
- 由于规则五、六，执行System.out.println("ClassLoaderAllTest7 clinit");
- 由于执行了System.out.println("ClassLoaderAllTest7 clinit"); 因此输出ClassLoaderAllTest7 clinit
##### 加载ClassLoaderAllTest7类结束
- ClassLoaderAllTest6 test6 = new ClassLoaderAllTest6();对应字节码是new #4 <com/peter/jvm/example/ClassLoaderAllTest6>
- 由于规则七，因此先加载ClassLoaderAllTest6类，再new
##### 加载ClassLoaderAllTest6类开始
- 由于规则五、六，执行System.out.println("ClassLoaderAllTest6 clinit");
- 由于执行了System.out.println("ClassLoaderAllTest6 clinit"); 因此输出ClassLoaderAllTest6 clinit
##### 加载ClassLoaderAllTest6类结束
##### 加载ClassLoaderAllTest4接口结束
- 回到main方法
- 由于执行了System.out.println("ClassLoaderAllTest main 2"); 因此输出ClassLoaderAllTest main 2
- 继续，执行ClassLoaderAllTest5.test5();
- ClassLoaderAllTest5.test5();对应着invokestatic #7 <com/peter/jvm/example/ClassLoaderAllTest5.test5>
- 由于规则七，因此先加载ClassLoaderAllTest5类，再invokestatic
##### 加载ClassLoaderAllTest5类开始
- 由于规则五、六，执行ClassLoaderAllTest7 test7 = new ClassLoaderAllTest7(); 再ClassLoaderAllTest8 test8 = new ClassLoaderAllTest8();
- ClassLoaderAllTest7 test7 = new ClassLoaderAllTest7();对应字节码是new #4 <com/peter/jvm/example/ClassLoaderAllTest7>
- 由于规则七，因此先加载ClassLoaderAllTest7类，再new
- 由于规则四，已经加载过的类不会加载
- ClassLoaderAllTest8 test8 = new ClassLoaderAllTest8();对应字节码是new #7 <com/peter/jvm/example/ClassLoaderAllTest8>
- 由于规则七，因此先加载ClassLoaderAllTest8类，再new
##### 加载ClassLoaderAllTest8类开始
- 由于规则五、六，执行System.out.println("ClassLoaderAllTest8 clinit");
- 由于执行了System.out.println("ClassLoaderAllTest8 clinit"); 因此输出ClassLoaderAllTest8 clinit
##### 加载ClassLoaderAllTest8类结束
- 执行invokestatic #7 <com/peter/jvm/example/ClassLoaderAllTest5.test5>
- 由于执行了System.out.println("ClassLoaderAllTest5 clinit"); 因此输出ClassLoaderAllTest5 test5
##### 执行ClassLoaderAllTest的main方法结束
##### 程序结束
