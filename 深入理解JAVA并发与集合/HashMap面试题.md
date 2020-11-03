- [HashMap面试题](#HashMap面试题)
  - [JDK7及JDK8中HashMap实现原理](#JDK7及JDK8中HashMap实现原理)
  - [JDK7及JDK8中数组长度为何是二的幂次方](#JDK7及JDK8中数组长度为何是二的幂次方)
  - [JDK8中HashMap为什么要使用红黑树](#JDK8中HashMap为什么要使用红黑树)
  - [JDK8中HashMap什么时候将链表转化为红黑树](#JDK8中HashMap什么时候将链表转化为红黑树)
  - [JDK8中HashMap红黑树实现原理]
  - [JDK7与JDK8中HashMap的不同点](#JDK7与JDK8中HashMap的不同点)
  - [JDK7中HashMap多线程循环链表问题](#JDK7中HashMap多线程循环链表问题)
  - [JDK8中HashMap线程不安全问题](#JDK8中HashMap线程不安全问题)
  - [JDK7及JDK8中ConcurrentHashMap实现原理](#JDK7及JDK8中ConcurrentHashMap实现原理)
  - [JDK7中ConcurrentHashMap如何保证线程安全](#JDK7中ConcurrentHashMap如何保证线程安全)
  - [JDK8中ConcurrentHashMap如何保证线程安全](#JDK8中ConcurrentHashMap如何保证线程安全)
  - [JDK7与JDK8中ConcurrentHashMap的不同点](#JDK7与JDK8中ConcurrentHashMap的不同点)

# HashMap面试题

参考
- [HashMap源码分析](应用场景与源码分析/HashMap.md)
- [ConcurrentHashMap源码分析](应用场景与源码分析/ConcurrentHashMap.md)
- [Java集合框架常见面试题](https://github.com/Snailclimb/JavaGuide/blob/master/docs/java/collection/Java%E9%9B%86%E5%90%88%E6%A1%86%E6%9E%B6%E5%B8%B8%E8%A7%81%E9%9D%A2%E8%AF%95%E9%A2%98.md)
- [HashMap与ConcurrentHashMap面试要点](https://www.yuque.com/books/share/9f4576fb-9aa9-4965-abf3-b3a36433faa6/doh8wb)
- [HashMap的7种遍历方式与性能分析](https://mp.weixin.qq.com/s/Zz6mofCtmYpABDL1ap04ow)
- [面试问题记录汇总](https://github.com/peteryuanpan/notebook/issues/85)

### JDK7及JDK8中HashMap实现原理

JDK7：链表数组

JDK8: 链表数组、红黑树

JDK8中既使用了单向链表，也使用了双向链表，双向链表主要是为了链表操作方便，应该在插入，扩容，链表转红黑树，红黑树转链表的过程中都要操作链表
TODO：没理解

### JDK7及JDK8中数组长度为何是二的幂次方

### JDK8中HashMap为什么要使用红黑树

JDK7中数组+链表的实现方式，可能造成一个链表过长，而查询效率低下，JDK8中链表会在特定条件下转为红黑树，提高了查询效率

### JDK8中HashMap什么时候将链表转化为红黑树

这个题很容易答错，大部分答案就是：当链表中的元素个数大于8时就会把链表转化为红黑树

但是其实还有另外一个限制：当发现链表中的元素个数大于8之后，还会判断一下当前数组的长度，如果数组长度小于64时，此时并不会转化为红黑树，而是进行扩容，扩容的原因是，如果数组长度还比较小，就先利用扩容来缩小链表的长度

因此，**只有当链表中的元素个数大于8，并且数组的长度大于等于64时才会将链表转为红黑树**

### JDK8中HashMap红黑树实现原理

### JDK7与JDK8中HashMap的不同点

### JDK7中HashMap多线程循环链表问题

### JDK8中HashMap线程不安全问题

### JDK7及JDK8中ConcurrentHashMap实现原理

### JDK7中ConcurrentHashMap如何保证线程安全

### JDK8中ConcurrentHashMap如何保证线程安全

### JDK7与JDK8中ConcurrentHashMap的不同点
