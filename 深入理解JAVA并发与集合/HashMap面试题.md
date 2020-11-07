- [HashMap面试题](#HashMap面试题)
  - [JDK7与JDK8中HashMap实现原理上的相同及不同点](#JDK7与JDK8中HashMap实现原理上的相同及不同点)
  - [JDK8中HashMap数组长度为何是2的幂次方](#JDK8中HashMap数组长度为何是2的幂次方)
  - [JDK8中HashMap数组什么时候扩容](#JDK8中HashMap数组什么时候扩容)
  - [JDK8中HashMap为什么要使用红黑树](#JDK8中HashMap为什么要使用红黑树)
  - [JDK8中HashMap什么时候将链表转化为红黑树](#JDK8中HashMap什么时候将链表转化为红黑树)
  - [JDK8中HashMap红黑树实现原理](#JDK8中HashMap红黑树实现原理)
  - [JDK8中HashMap4种遍历方式](#JDK8中HashMap4种遍历方式)
  - [JDK7中HashMap2个线程resize时循环链表问题](#JDK7中HashMap2个线程resize时循环链表问题)
  - [JDK8中HashMap2个线程同时get会发生什么](#JDK8中HashMap2个线程同时get会发生什么)
  - [JDK8中HashMap2个线程同时put会发生什么](#JDK8中HashMap2个线程同时put会发生什么)
  - [JDK8中HashMap1个线程put1个线程get会发生什么](#JDK8中HashMap1个线程put1个线程get会发生什么)
  - [JDK8中Hashtable与HashMap实现原理上的相同及不同点](#JDK8中Hashtable与HashMap实现原理上的相同及不同点)
  - [JDK8中LinkedHashMap与HashMap实现原理上的相同及不同点](#JDK8中LinkedHashMap与HashMap实现原理上的相同及不同点)
  - [JDK8中ConcurrentHashMap与HashMap实现原理上的相同及不同点](#JDK8中ConcurrentHashMap与HashMap实现原理上的相同及不同点)
  - [JDK7与JDK8中ConcurrentHashMap保证线程安全实现原理上的相同及不同点](#JDK7与JDK8中ConcurrentHashMap保证线程安全实现原理上的相同及不同点)

# HashMap面试题

参考
- [HashMap源码分析](应用场景与源码分析/HashMap.md)
- [ConcurrentHashMap源码分析](应用场景与源码分析/ConcurrentHashMap.md)
- [面试问题记录汇总](https://github.com/peteryuanpan/notebook/issues/85)
- [Java集合框架常见面试题](https://github.com/Snailclimb/JavaGuide/blob/master/docs/java/collection/Java%E9%9B%86%E5%90%88%E6%A1%86%E6%9E%B6%E5%B8%B8%E8%A7%81%E9%9D%A2%E8%AF%95%E9%A2%98.md)
- [HashMap与ConcurrentHashMap面试要点](https://www.yuque.com/books/share/9f4576fb-9aa9-4965-abf3-b3a36433faa6/doh8wb)
- [HashMap相关面试题](https://www.yuque.com/books/share/9f4576fb-9aa9-4965-abf3-b3a36433faa6/hv4o3e)
- [ConcurrentHashMap相关面试题](https://www.yuque.com/books/share/9f4576fb-9aa9-4965-abf3-b3a36433faa6/biiid7)
- [HashMap的7种遍历方式与性能分析](https://mp.weixin.qq.com/s/Zz6mofCtmYpABDL1ap04ow)
- [简书：为什么HashMap线程不安全](https://www.jianshu.com/p/e2f75c8cce01)

### JDK7与JDK8中HashMap实现原理上的相同及不同点

回答这个问题，需要对JDK7及JDK8中HashMap底层实现原理比较清楚，可以先自行回忆一下，若不清楚可以看 [HashMap源码分析](应用场景与源码分析/HashMap.md)

相同点
- TODO

不同点，从几个层面来看
- 底层数据结构：JDK7是链表数组，JDK8是链表数组 + 红黑树
- Hash复杂度：JDK7Hash算法更复杂，Hash更散列，JDK8有红黑树保障，Hash算法更简单，计算消耗更小
- 链表插入法：JDK7是头插法（多线程扩容的情况下使用头插法会出现循环链表的问题），JDK8是尾插法
- 扩容：JDK7是将旧链表元素一个个地通过重新计算index转移到新链表上，JDK8是将每个旧链表（或红黑树）分为两个链表（或红黑树），有的元素插入新数组的低位链表（或红黑树），有的元素插入新数组的高位链表（或红黑树）
- 重新哈希：JDK7扩容时可能重新对所有key进行rehash（与哈希种子有关），JDK8无这样的逻辑
- put覆盖策略：JDK8多了一个方法 putIfAbsent(key,value)，允许put时不覆盖已存在节点

### JDK8中HashMap数组长度为何是2的幂次方

JDK7中也一样，数组长度n设计为2的幂次方，这样index=(n-1)&hash，就相当于index=hash%n，主要是降低了计算消耗，其次让数组扩容过程变得简单

每次扩容数组长度增加1倍，在JDK8的实现中利用了这个特点，扩容时将一个链表（或红黑树）分为两个链表（或红黑树）进行插入

### JDK8中HashMap数组什么时候扩容

JDK7中也一样，一般是当 size > threshold 时进行扩容，size是节点个数，threshold是扩容阈值

一般情况下，threshold = capacity * loadFactor，capacity是数组长度，loadFactor是扩容引子

### JDK8中HashMap为什么要使用红黑树

JDK7中数组+链表的实现方式，可能造成一个链表过长，而查询效率低下，JDK8中链表会在特定条件下转为红黑树，提高了查询效率

### JDK8中HashMap什么时候将链表转化为红黑树

这个题很容易答错，大部分答案就是：当链表中的元素个数大于等于8时就会把链表转化为红黑树

但是其实还有另外一个限制：当发现链表中的元素个数大于等于8之后，还会判断一下当前数组的长度，如果数组长度小于64时，此时并不会转化为红黑树，而是进行扩容，扩容的原因是，如果数组长度还比较小，就先利用扩容来缩小链表的长度

因此，正确的答案是：**当链表中的元素个数大于等于8，并且数组的长度大于等于64时才会将链表转为红黑树**

### JDK8中HashMap红黑树实现原理

如何理解红黑树内部维护了一个双向链表?

### JDK8中HashMap4种遍历方式

### JDK7中HashMap2个线程resize时循环链表问题

关键在于JDK7中HashMap使用的是头插法，会出现循环链表，而JDK8中HashMap使用的是尾插法，则不会出现

processon画一下图

看一下周瑜老师的视频

### JDK8中HashMap2个线程同时get会发生什么

不会有线程安全问题，都能正常获取到数据

get方法不会触发resize，不会使链表或红黑树节点发生变动

### JDK8中HashMap2个线程同时put会发生什么

代码例子测试

会出现keySet集合元素个数变少的情况

processon画一下图

### JDK8中HashMap1个线程put1个线程get会发生什么

代码例子测试

### JDK8中Hashtable与HashMap实现原理上的相同及不同点

### JDK8中LinkedHashMap与HashMap实现原理上的相同及不同点

### JDK8中ConcurrentHashMap与HashMap实现原理上的相同及不同点

### JDK7与JDK8中ConcurrentHashMap保证线程安全实现原理上的相同及不同点

回答这个问题，需要对JDK7及JDK8中ConcurrentHashMap底层实现原理比较清楚，可以先自行回忆一下，若不清楚可以看 [ConcurrentHashMap源码分析](应用场景与源码分析/ConcurrentHashMap.md)

要涉及到扩容

JDK8中ConcurrentHashMap的CounterCell?
