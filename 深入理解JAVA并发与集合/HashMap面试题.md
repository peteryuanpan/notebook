- [HashMap面试题](#HashMap面试题)
  - [JDK7与JDK8中HashMap的不同点](#JDK7与JDK8中HashMap的不同点)
  - [JDK8中数组长度为何是2的幂次方](#JDK8中数组长度为何是2的幂次方)
  - [JDK8中HashMap为什么要使用红黑树](#JDK8中HashMap为什么要使用红黑树)
  - [JDK8中HashMap什么时候将链表转化为红黑树](#JDK8中HashMap什么时候将链表转化为红黑树)
  - [JDK8中HashMap红黑树实现原理](#JDK8中HashMap红黑树实现原理)
  - [JDK8中HashMap多种遍历方式](#JDK8中HashMap多种遍历方式)
  - [JDK7中HashMap多线程循环链表问题](#JDK7中HashMap多线程循环链表问题)
  - [JDK8中HashMap2个线程同时put会发生什么](#JDK8中HashMap2个线程同时put会发生什么)
  - [JDK8中HashMap1个线程put1个线程get会发生什么](#JDK8中HashMap1个线程put1个线程get会发生什么)
  - [JDK7与JDK8中ConcurrentHashMap的不同点](#JDK7与JDK8中ConcurrentHashMap的不同点)
  - [JDK7中ConcurrentHashMap如何保证线程安全](#JDK7中ConcurrentHashMap如何保证线程安全)
  - [JDK8中ConcurrentHashMap如何保证线程安全](#JDK8中ConcurrentHashMap如何保证线程安全)

# HashMap面试题

参考
- [HashMap源码分析](应用场景与源码分析/HashMap.md)
- [ConcurrentHashMap源码分析](应用场景与源码分析/ConcurrentHashMap.md)
- [Java集合框架常见面试题](https://github.com/Snailclimb/JavaGuide/blob/master/docs/java/collection/Java%E9%9B%86%E5%90%88%E6%A1%86%E6%9E%B6%E5%B8%B8%E8%A7%81%E9%9D%A2%E8%AF%95%E9%A2%98.md)
- [HashMap与ConcurrentHashMap面试要点](https://www.yuque.com/books/share/9f4576fb-9aa9-4965-abf3-b3a36433faa6/doh8wb)
- [HashMap的7种遍历方式与性能分析](https://mp.weixin.qq.com/s/Zz6mofCtmYpABDL1ap04ow)
- [面试问题记录汇总](https://github.com/peteryuanpan/notebook/issues/85)

### JDK7与JDK8中HashMap的不同点

回答这个问题，需要对JDK7及JDK8中HashMap底层实现原理，hash、put、get、remove、resize等方法实现原理比较清楚，可以先自行回忆一下，若不清楚可以看 [HashMap源码分析](应用场景与源码分析/HashMap.md)

从几个层面来看
- 底层数据结构：JDK7是链表数组，JDK8是链表数组 + 红黑树
- Hash复杂度：JDK7Hash算法更复杂，Hash更散列，JDK8有红黑树保障，Hash算法更简单，计算消耗更小
- 链表插入法：JDK7是头插法（多线程扩容的情况下使用头插法会出现循环链表的问题），JDK8是尾插法
- 扩容：JDK7是每次转移一个元素，JDK8是先算出来当前位置上哪些元素在新数组的低位上，哪些在新数组的高位上，然后在一次性转移
- 重新哈希：扩容时JDK7可能重新对key进行hash（与哈希种子有关），JDK8无这样的逻辑
- put覆盖策略：JDK8多了一个方法 putIfAbsent(key,value)，允许put时不覆盖已存在节点

### JDK8中数组长度为何是2的幂次方

JDK7中也一样，数组长度n设计为2的幂次方，这样index=(n-1)&hash，就相当于index=hash%n，一是降低了计算消耗，二是让数组扩容过程变得简单

### JDK8中HashMap为什么要使用红黑树

JDK7中数组+链表的实现方式，可能造成一个链表过长，而查询效率低下，JDK8中链表会在特定条件下转为红黑树，提高了查询效率

### JDK8中HashMap什么时候将链表转化为红黑树

这个题很容易答错，大部分答案就是：当链表中的元素个数大于等于8时就会把链表转化为红黑树

但是其实还有另外一个限制：当发现链表中的元素个数大于等于8之后，还会判断一下当前数组的长度，如果数组长度小于64时，此时并不会转化为红黑树，而是进行扩容，扩容的原因是，如果数组长度还比较小，就先利用扩容来缩小链表的长度

因此，正确的答案是：**当链表中的元素个数大于等于8，并且数组的长度大于等于64时才会将链表转为红黑树**

### JDK8中HashMap红黑树实现原理

### JDK8中HashMap多种遍历方式

### JDK7中HashMap多线程循环链表问题

### JDK8中HashMap2个线程同时put会发生什么

### JDK8中HashMap1个线程put1个线程get会发生什么

### JDK7与JDK8中ConcurrentHashMap的不同点

### JDK7中ConcurrentHashMap如何保证线程安全

### JDK8中ConcurrentHashMap如何保证线程安全
