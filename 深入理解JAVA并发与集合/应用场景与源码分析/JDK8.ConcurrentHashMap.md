
# JDK8.ConcurrentHashMap

参考
- [ConcurrentHashMap源码+底层数据结构分析](https://github.com/Snailclimb/JavaGuide/blob/master/docs/java/collection/ConcurrentHashMap%E6%BA%90%E7%A0%81+%E5%BA%95%E5%B1%82%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E5%88%86%E6%9E%90.md)
- [ConcurrentHashMap源码分析(1.8)](https://www.cnblogs.com/zerotomax/p/8687425.html)
- [简书：深入浅出ConcurrentHashMap1.8](https://www.jianshu.com/p/c0642afe03e0)
- [简书：深入分析ConcurrentHashMap1.8的扩容实现](https://www.jianshu.com/p/f6730d5784ad)

### 应用场景

### 核心思想

数据结构上，JDK8 中的 ConcurrentHashMap 不再使用如 JDK7 中 ConcurrentHashMap 一样 Segment数组 + HashEntry 数组 的形式了，而是用了如 JDK8 中 HashMap 的 Node数组 + 链表 / 红黑树 一样的形式

线程安全上，JDK8 中并没有完全摒弃 JDK7 中分段锁的思想，JDK7 中是采用 Segment分段 + ReetrantLock（自旋加锁）+ CAS 来保证线程安全，JDK8 中是采用 Node数组分段 + synchonized + CAS 来保证线程安全

可以简单一点的记忆，JDK8中 ConcurrentHashMap 的数据结构沿用了 HashMap 的，并在此基础上，通过 每个数组位置上的操作加 synchonized 监视器锁 以及 CAS 指令保证原子性，volatile 保证可见性和有序性

![image](https://user-images.githubusercontent.com/10209135/99938125-22661780-2da2-11eb-8824-7fe2e6ff2cf7.png)

### 类结构分析

#### 类图

![image](https://user-images.githubusercontent.com/10209135/99937247-34df5180-2da0-11eb-869f-883c29eb3e01.png)

#### 数据结构

```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V> implements ConcurrentMap<K,V>, Serializable {

}
```

#### 核心方法

##### hash方法

JDK8的ConcurrentHashMap中hash方法也是扰动函数，但计算复杂度少了很多，是因为JDK8中主要依靠红黑树来降低操作复杂度，不需要靠hash散列

```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V> implements ConcurrentMap<K,V>, Serializable {
    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }
}
```

##### put方法

```java
public class ConcurrentHashMap<K,V> extends AbstractMap<K,V> implements ConcurrentMap<K,V>, Serializable {
    // put 一个 < key, value >
    public V put(K key, V value) {
        return putVal(key, value, false);
    }
    // putIfAbsent 与 put 类似，但 onlyIfAbsent = true，含义是若已存在元素则不覆盖
    public V putIfAbsent(K key, V value) {
        return putVal(key, value, true);
    }
    /** Implementation for put and putIfAbsent */
    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null) 
            throw new NullPointerException();
        // 计算hash值
        int hash = spread(key.hashCode());
        int binCount = 0;
        // 
        for (Node<K,V>[] tab = table;;) {
            Node<K,V> f; int n, i, fh;
            if (tab == null || (n = tab.length) == 0)
                tab = initTable();
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                if (casTabAt(tab, i, null,
                             new Node<K,V>(hash, key, value, null)))
                    break;                   // no lock when adding to empty bin
            }
            else if ((fh = f.hash) == MOVED)
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null;
                synchronized (f) {
                    if (tabAt(tab, i) == f) {
                        if (fh >= 0) {
                            binCount = 1;
                            for (Node<K,V> e = f;; ++binCount) {
                                K ek;
                                if (e.hash == hash &&
                                    ((ek = e.key) == key ||
                                     (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K,V> pred = e;
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K,V>(hash, key,
                                                              value, null);
                                    break;
                                }
                            }
                        }
                        else if (f instanceof TreeBin) {
                            Node<K,V> p;
                            binCount = 2;
                            if ((p = ((TreeBin<K,V>)f).putTreeVal(hash, key,
                                                           value)) != null) {
                                oldVal = p.val;
                                if (!onlyIfAbsent)
                                    p.val = value;
                            }
                        }
                    }
                }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        addCount(1L, binCount);
        return null;
    }
}
```
