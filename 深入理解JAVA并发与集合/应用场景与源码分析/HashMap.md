- [HashMap](#HashMap)
  - [应用场景](#应用场景)
  - [核心思想](#核心思想)
  - [类结构分析](#类结构分析)
    - [类图](#类图)
    - [数据结构](#数据结构)
    - [核心方法](#核心方法)

# HashMap

参考
- [HashMap(JDK1.8)源码+底层数据结构分析](https://github.com/Snailclimb/JavaGuide/blob/master/docs/java/collection/HashMap(JDK1.8)%E6%BA%90%E7%A0%81+%E5%BA%95%E5%B1%82%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E5%88%86%E6%9E%90.md)
- [一文看懂HashMap](https://segmentfault.com/a/1190000022184751)

### 应用场景

### 核心思想

### 类结构分析

#### 类图

#### 数据结构

JDK8

```java
public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable {
    // 序列号
    private static final long serialVersionUID = 362498820763181265L;    
    // 默认的初始容量是16
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;   
    // 最大容量
    static final int MAXIMUM_CAPACITY = 1 << 30; 
    // 默认的扩容引子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 链表转为红黑树的第一个条件：链表长度大于8
    static final int TREEIFY_THRESHOLD = 8; 
    // 当红黑树上的节点数小于这个值时会转成链表
    static final int UNTREEIFY_THRESHOLD = 6;
    // 链表转为红黑树的第二个条件：数组长度大于等于64
    static final int MIN_TREEIFY_CAPACITY = 64;
    // 存放元素的数组，长度总是2的幂次倍
    transient Node<k,v>[] table; 
    // 存放具体元素的集
    transient Set<map.entry<k,v>> entrySet;
    // 存放元素的个数，注意这个不等于数组的长度
    transient int size;
    // 每次扩容和更改map结构的计数器
    transient int modCount;   
    // 当数组长度*扩容引子超过临界值时，会对数组进行扩容
    int threshold;
    // 扩容引子
    final float loadFactor;
}
```

JDK7

#### 核心方法

##### put方法

##### get方法

