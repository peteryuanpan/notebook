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

![image](https://user-images.githubusercontent.com/10209135/97936983-41cbdf00-1db8-11eb-9bd5-325e3c645377.png)

#### 数据结构

JDK8

```java
public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable {
    // 序列号
    private static final long serialVersionUID = 362498820763181265L;    
    // 默认数组的初始长度是16
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;   
    // 数组的最大长度
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
    // 存放<KEY, VALUE>的集合
    transient Set<map.entry<k,v>> entrySet;
    // 数据元素的个数，注意这个不等于数组的长度
    transient int size;
    // 每次扩容和更改map结构的计数器
    transient int modCount;
    // 当数组长度*扩容引子超过临界值时，会对数组进行扩容
    int threshold;
    // 扩容引子
    final float loadFactor;
    // 链表的数据结构
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        V value;
        Node<K,V> next;
    }
    // 红黑数的数据结构
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent;  // red-black tree links
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;
    }
    // 存放KEY的集合
    final class KeySet extends AbstractSet<K> {}
    // 存放VALUE的集合
    final class Values extends AbstractCollection<V> {}
    // 存放<KEY, VALUE>的集合
    final class EntrySet extends AbstractSet<Map.Entry<K,V>> {}
    // 集合遍历器
    abstract class HashIterator {
        Node<K,V> next;        // next entry to return
        Node<K,V> current;     // current entry
        int expectedModCount;  // for fast-fail
        int index;             // current slot
    }
    // KEY集合的遍历器
    final class KeyIterator extends HashIterator implements Iterator<K> {}
    // VALUE集合的遍历器
    final class ValueIterator extends HashIterator implements Iterator<V> {}
    // <KEY, VALUE>集合的遍历器
    final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K,V>> {}
    static class HashMapSpliterator<K,V> {
        final HashMap<K,V> map;
        Node<K,V> current;          // current node
        int index;                  // current index, modified on advance/split
        int fence;                  // one past last index
        int est;                    // size estimate
        int expectedModCount;       // for comodification checks
    }
    static final class KeySpliterator<K,V> extends HashMapSpliterator<K,V> implements Spliterator<K> {}
    static final class ValueSpliterator<K,V> extends HashMapSpliterator<K,V> implements Spliterator<V> {}
    static final class EntrySpliterator<K,V> extends HashMapSpliterator<K,V> implements Spliterator<Map.Entry<K,V>> {}
}
```

JDK7，可以看得出来数据结构简单了很多

```
public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable {
    // 默认数组的初始长度是16
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    // 数组的最大长度
    static final int MAXIMUM_CAPACITY = 1 << 30;
    // 默认的扩容引子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 空链表数组
    static final Entry<?,?>[] EMPTY_TABLE = {};
    // 数组默认是空链表数组
    transient Entry<K,V>[] table = (Entry<K,V>[]) EMPTY_TABLE;
    // 存放<KEY, VALUE>的集合
    private transient Set<Map.Entry<K,V>> entrySet = null;
    // 数据元素的个数，注意这个不等于数组的长度
    transient int size;
    // 当数组长度*扩容引子超过临界值时，会对数组进行扩容
    int threshold;
    // 扩容引子
    final float loadFactor;
    // 每次扩容和更改map结构的计数器
    transient int modCount;
    // hash种子
    transient int hashSeed = 0;
    static final int ALTERNATIVE_HASHING_THRESHOLD_DEFAULT = Integer.MAX_VALUE;
    // 链表的数据结构
    static class Entry<K,V> implements Map.Entry<K,V> {
        final K key;
        V value;
        Entry<K,V> next;
        int hash;
    }
    // 存放KEY的集合
    private final class KeySet extends AbstractSet<K> {}
    // 存放VALUE的集合
    private final class Values extends AbstractCollection<V> {}
    // 存放<KEY, VALUE>的集合
    private final class EntrySet extends AbstractSet<Map.Entry<K,V>> {}
    // 集合遍历器
    private abstract class HashIterator<E> implements Iterator<E> {
        Entry<K,V> next;        // next entry to return
        int expectedModCount;   // For fast-fail
        int index;              // current slot
        Entry<K,V> current;     // current entry
    }
    // KEY集合的遍历器
    private final class ValueIterator extends HashIterator<V> {}
    // VALUE集合的遍历器
    private final class KeyIterator extends HashIterator<K> {}
    // <KEY, VALUE>集合的遍历器
    private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {}
}
```

#### 核心方法

##### put方法

##### get方法

