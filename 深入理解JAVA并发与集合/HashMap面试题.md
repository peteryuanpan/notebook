- [HashMap面试题](#HashMap面试题)
  - [JDK7与JDK8中HashMap实现原理上的不同点](#JDK7与JDK8中HashMap实现原理上的不同点)
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
  - [JDK7与JDK8中ConcurrentHashMap保证线程安全实现原理上的不同点](#JDK7与JDK8中ConcurrentHashMap保证线程安全实现原理上的不同点)
  - [JDK8中Hashtable与HashMap实现原理上的不同点](#JDK8中Hashtable与HashMap实现原理上的不同点)
  - [JDK8中LinkedHashMap与HashMap实现原理上的不同点](#JDK8中LinkedHashMap与HashMap实现原理上的不同点)
  - [JDK8中TreeMap与HashMap实现原理上的不同点](#JDK8中TreeMap与HashMap实现原理上的不同点)
  
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

### JDK7与JDK8中HashMap实现原理上的不同点

回答这些问题，需要对JDK7及JDK8中HashMap底层实现原理比较清楚，可以先自行回忆一下，若不清楚可以看 [HashMap源码分析](应用场景与源码分析/HashMap.md)

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

来看下JDK7中resize时的代码

```java
    void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Entry[] newTable = new Entry[newCapacity];
        transfer(newTable, initHashSeedAsNeeded(newCapacity));
        table = newTable;
        threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
    }
    void transfer(Entry[] newTable, boolean rehash) {
        int newCapacity = newTable.length;
        for (Entry<K,V> e : table) {
            while(null != e) {
                Entry<K,V> next = e.next;
                if (rehash) {
                    e.hash = null == e.key ? 0 : hash(e.key);
                }
                int i = indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            }
        }
    }
```

会存在以下的情况

线程1执行了resize，执行到了 next = e.next;

此时，旧数组index=1位置中，存在一个链表 |->a->b，新数组还是空，然后线程1让出CPU时间片

```
1|->a->b  ====>  |(EMPTY)
```

线程2也执行了resize，并执行完了transfer（这种情况是可能发生的，CPU执行的太快，存在不确定性）

此时，新数组中生成了新的链表，由于采用的头插法（这是关键），旧数组中链表 |->a->b 变成了 |->b->a，然后线程2让出CPU时间片

```
旧数组     ====   新数组
1|->a->b   ====  |(EMPTY)
1|(EMPTY)  ====  |->b->a
```

线程1继续执行，注意e、next都是局部变量，属于线程工作内存私有的，会存在从CPU缓存中获取数据而不直接从主内存获取数据的情况（第一次除外）

即使线程2中旧数组index=1位置的链表已经是(EMPTY)了，线程1中旧数组的链表仍可能保持原样（可以这么理解），但线程1中新数组的链表可能会是从主内存获取的数据

此时，旧数组链表的元素a通过头插法插入新数组中，就可能会形成 |->a->b->a 这样的循环链表

```
旧数组    ====   新数组
1|->a->b  ====  |->b->a
1|->b     ====  |->a->b->a
```

在上面的解释中，我用了许多“可能”，这是由于并发中的不确定性导致的，一个线程执行多少行指令而让出CPU时间片，从CPU缓存还是主内存中读取数据，这些都无法有一个非常确定的结论

甚至会让人认为，那么多凑巧的情况真的会出现吗? 那我们不放测试一下

```java
package hashmap;

import java.util.HashMap;
import java.util.Map;

public class HashMapLinkedLoopProblemTest {

    static final int N = 100000;
    static String[] str_arr = new String[N];
    static Map<String, String> map = new HashMap<>();

    static {
        for (int i = 0; i < N; i ++)
            str_arr[i] = String.valueOf(i);
    }

    public static void main(String[] args) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < N; i ++) {
                    System.out.println(Thread.currentThread().getName() + " " + i);
                    map.put(str_arr[i], str_arr[i]);
                }
            }
        });
        Thread a = new Thread(t, "ThreadA");
        Thread b = new Thread(t, "ThreadB");
        a.start();
        b.start();
        try {
            a.join();
            b.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " end");
    }
}
```

输出结果（JDK7下运行，偶现，但复现率很高）
```
ThreadA 27034
ThreadA 27035
ThreadA 27036
...
ThreadB 21288
ThreadB 21289
ThreadB 21290
...
（不输出了，但程序在运行）
```

用jps查看进程号pid，再执行jstack pid，可以看到两个线程都在 at java.util.HashMap.put(HashMap.java:494) 这一行死循环了
```
"ThreadB" prio=6 tid=0x0000000012597800 nid=0x2fc0 runnable [0x0000000012ebf000]
   java.lang.Thread.State: RUNNABLE
        at java.util.HashMap.put(HashMap.java:494)
        at hashmap.HashMapLinkedLoopProblemTest$1.run(HashMapLinkedLoopProblemTest.java:23)
        at java.lang.Thread.run(Thread.java:745)
        at java.lang.Thread.run(Thread.java:745)

"ThreadA" prio=6 tid=0x0000000012597000 nid=0x2318 runnable [0x0000000012dbf000]
   java.lang.Thread.State: RUNNABLE
        at java.util.HashMap.put(HashMap.java:494)
        at hashmap.HashMapLinkedLoopProblemTest$1.run(HashMapLinkedLoopProblemTest.java:23)
        at java.lang.Thread.run(Thread.java:745)
        at java.lang.Thread.run(Thread.java:745)
```

查看HashMap.java:494行代码，如下图，可以看出是在for循环一直运行，这是resize之后出现的循环链表

加个断点后，可以看出e所表示的key->value一直是2个数字，比如21619->21619、15239->15239、21619->21619、15239->15239、...，但挺可惜，不知为何，IDEA这里debug不出e.next的信息

![image](https://user-images.githubusercontent.com/10209135/98442724-5eae3c80-2141-11eb-988b-4770557ae816.png)

### JDK8中HashMap2个线程同时get会发生什么

不会有线程安全问题，都能正常获取到数据

get方法不会触发resize，不会使链表或红黑树节点发生变动

### JDK8中HashMap2个线程同时put会发生什么

代码例子测试

会出现keySet集合元素个数变少的情况

processon画一下图

### JDK8中HashMap1个线程put1个线程get会发生什么

代码例子测试

### JDK7与JDK8中ConcurrentHashMap保证线程安全实现原理上的不同点

回答这些问题，需要对JDK7及JDK8中ConcurrentHashMap底层实现原理比较清楚，可以先自行回忆一下，若不清楚可以看 [ConcurrentHashMap源码分析](应用场景与源码分析/ConcurrentHashMap.md)

要涉及到扩容

JDK8中ConcurrentHashMap的CounterCell?

### JDK8中Hashtable与HashMap实现原理上的不同点

### JDK8中LinkedHashMap与HashMap实现原理上的不同点

### JDK8中TreeMap与HashMap实现原理上的不同点
