- [JVM面试问题总结](#JVM面试问题总结)
  - [JVM面试之类加载](#JVM面试之类加载)
    - [讲述类加载机制](#讲述类加载机制)：定义、时机、目的
    - [讲述SPI机制](#讲述SPI机制)
    - [讲述线程上下文类加载器](#讲述线程上下文类加载器)
  - [JVM面试之运行时数据区域](#JVM面试之运行时数据区域)
    - [讲述运行时数据区域](#讲述运行时数据区域)：定义、五大部分
    - [讲述程序计数器](#讲述程序计数器)
    - [讲述方法区](#讲述方法区)
    - [讲述堆区](#讲述堆区)

# JVM面试问题总结

## JVM面试之类加载

### 讲述类加载机制

- 类加载的定义
- 类加载的时机
- 类加载的目的

类加载是Java虚拟机将class数据二进制字节流读入类加载器，解析出类的元信息（InstanceKlass）并写入方法区，解析出类的Class对象（InstanceMirrorKlass）并写入堆区等过程

[补充]class数据二进制字节流无关乎来源，可以是操作系统文件（单个文件 或 jar包中），可以是网络流数据，可以是数据库数据等

[补充]javac编译器会将.java文件编译成.class文件，用于java虚拟机类加载

类加载会在4种情况下执行，一是java虚拟机启动时，加载mainClass（main方法所在的类）；二是字节码引擎执行遇到new、putstatic、getstatic、invokestatic时，若对应类未被加载，会进行类加载；三是加载一个类时，若父类未被加载，会优先加载父类，再加载子类；四是利用反射机制（Class.forname(packageName.ClassName)）加载一个类时，会进行类加载

类加载的目的是将class数据解析成类的元信息、类的Class对象等数据存入运行时数据区域，为后续的对象实例化、静态变量的取值及赋值、静态方法的调用等过程提供数据准备

### 讲述SPI机制

TODO

文字补充

SPI机制是指，比如，java.sql.Driver类是启动类加载器加载的，而它的具体实现类 com.mysql.cj.jdbc.Driver 是应用程序类加载器加载的，不符合双亲委派模型，需要一个SPI服务机制，通过ServiceLoader.load(packageName.ClassName.class)方式来实现类加载

ServiceLoader的内部实现，会读取classpath中的META-INF/services/packageName的内容，加载内容所指向的类，这就是SPI机制的实现方式

可以参考ServiceLoader中的 private class LazyIterator implements Iterator< S > 的 hasNextService 和 nextService方法

```java
private boolean hasNextService() {
    String fullName = PREFIX + service.getName(); // META-INF/services/ 加上接口的全限定类名，就是文件服务类的文件
    nextName = pending.next(); // 拿到第一个实现类的类名
}
private S nextService() {
    String cn = nextName; // 全限定类名
    c = Class.forName(cn, false, loader); // 创建类的Class对象
    S p = service.cast(c.newInstance()); // 通过newInstance实例化
}
```

通过上面方法实现了类加载

### 讲述线程上下文类加载器

TODO

文字补充

Thread类内部有一个私有成员变量ClassLoader contextClassLoader，这就是线程上下文类加载器

```java
public
class Thread implements Runnable {
..
    /* The context ClassLoader for this thread */
    private ClassLoader contextClassLoader;
```

很多框架中，比如JDBC，都会实现静态代码块，在类加载时，调用ServiceLoader.load(packageName.ClassName.class)。可以参考java.sql.DriverManager的loadInitialDrivers方法

```java
ServiceLoader.load(packageName.ClassName.class) 会调用 Thread.currentThread().getContextClassLoader() 来获取 contextClassLoader 这个类加载器，用于后续类加载

    public static <S> ServiceLoader<S> load(Class<S> service) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return ServiceLoader.load(service, cl);
    }
```

如果先前写了Thread.currentThread().setContextClassLoader(myLoader)，设置了contextClassLoader，那么getContextClassLoader获取到的就是myLoader，myLoader是自定义类加载器
如果没设置，默认是系统类加载器（应用程序类加载器）

ServiceLoader.load(packageName.ClassName.class)还会实现具体的类加载，上面已讲

## JVM面试之运行时数据区域

### 讲述运行时数据区域

- 运行时数据区域的定义
- 五大部分

Java虚拟机在操作系统中申请了一块内存区域，用于运行Java程序，这块内存区域中有5个部分，分别是程序计数器、虚拟机栈、本地方法栈、方法区、堆区

### 讲述程序计数器

- 程序计数器的定义
- 与字节码解释器的关系
- 线程私有

程序计数器是线程所执行的字节码的行号指示器

字节码解释器工作时就是改变程序计数器的计数值来选取下一行需要执行的字节码指令

线程私有的，每个线程都有一个程序计数器

### 讲述虚拟机栈及本地方法栈

### 讲述方法区

### 讲述堆区
