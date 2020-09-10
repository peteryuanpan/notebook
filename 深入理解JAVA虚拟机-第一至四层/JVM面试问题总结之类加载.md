
- [JVM面试问题总结之类加载](#JVM面试问题总结之类加载)
  - [讲述SPI机制](#什么是SPI机制)
  - [讲述线程上下文类加载器](#什么是线程上下文类加载器)
  
# JVM面试问题总结之类加载

## 讲述SPI机制

### 口述版

TODO

### 文字版

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

## 讲述线程上下文类加载器

### 口述版

TODO

### 文字版

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
