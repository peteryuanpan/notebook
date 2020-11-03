- [HashMap](#HashMap)
  - [应用场景](#应用场景)
  - [核心思想](#核心思想)
  - [类结构分析](#类结构分析)
    - [类图](#类图)
    - [数据结构](#数据结构)
    - [核心方法](#核心方法)
	  - [hash方法](#hash方法)
      - [put方法](#put方法)
      - [get方法](#get方法)
      - [remove方法](#remove方法)
      - [resize方法](#resize方法)

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

JDK8，链表数组、红黑树

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
    // 红黑数的数据结构，TreeNode extends LinkedHashMap.Entry extends HashMap.Node，因此TreeNode是Node的子类
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
    // 抽象迭代器
    abstract class HashIterator {
        Node<K,V> next;        // next entry to return
        Node<K,V> current;     // current entry
        int expectedModCount;  // for fast-fail
        int index;             // current slot
    }
    // KEY集合迭代器
    final class KeyIterator extends HashIterator implements Iterator<K> {}
    // VALUE集合迭代器
    final class ValueIterator extends HashIterator implements Iterator<V> {}
    // <KEY, VALUE>集合迭代器
    final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K,V>> {}
    // 抽象分割迭代器
    static class HashMapSpliterator<K,V> {
        final HashMap<K,V> map;
        Node<K,V> current;          // current node
        int index;                  // current index, modified on advance/split
        int fence;                  // one past last index
        int est;                    // size estimate
        int expectedModCount;       // for comodification checks
    }
    // KEY集合分割迭代器
    static final class KeySpliterator<K,V> extends HashMapSpliterator<K,V> implements Spliterator<K> {}
    // VALUE集合分割迭代器
    static final class ValueSpliterator<K,V> extends HashMapSpliterator<K,V> implements Spliterator<V> {}
    // <KEY, VALUE>集合分割迭代器
    static final class EntrySpliterator<K,V> extends HashMapSpliterator<K,V> implements Spliterator<Map.Entry<K,V>> {}
}
```

JDK7，链表数组

```java
public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable {
    // 默认数组的初始长度是16
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    // 数组的最大长度
    static final int MAXIMUM_CAPACITY = 1 << 30;
    // 默认的扩容引子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 空链表数组
    static final Entry<?,?>[] EMPTY_TABLE = {};
    // 存放元素的数组，长度总是2的幂次倍
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
    // 抽象迭代器
    private abstract class HashIterator<E> implements Iterator<E> {
        Entry<K,V> next;        // next entry to return
        int expectedModCount;   // For fast-fail
        int index;              // current slot
        Entry<K,V> current;     // current entry
    }
    // KEY集合迭代器
    private final class ValueIterator extends HashIterator<V> {}
    // VALUE集合迭代器
    private final class KeyIterator extends HashIterator<K> {}
    // <KEY, VALUE>集合迭代器
    private final class EntryIterator extends HashIterator<Map.Entry<K,V>> {}
}
```

#### 核心方法

#### hash方法

JDK7及JDK8中的hash方法都是扰动函数，在key的hashCode方法基础上进行扰动计算，这么做是解决hashCode方法可能不够散列的问题

JDK8，由于采用了红黑树，不需要hash特别散列，因此计算简单，降低CPU消耗

```java
    static final int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

JDK7，hash比较复杂，较为散列，计算多，CPU消耗多

```java
    final int hash(Object k) {
        int h = hashSeed;
        if (0 != h && k instanceof String) {
            return sun.misc.Hashing.stringHash32((String) k);
        }

        h ^= k.hashCode();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }
```

##### put方法

JDK8，尾插法，允许key、value为null

```java
    // 插入一个<key, value>
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
    // 插入一个<key, value>
    // onlyIfAbsent: 如果是true，不修改已存在的节点
    // evict: 如果是false，数组是在creation mode
    // 返回被覆盖节点的VALUE 或者 null（表示新建了一个节点）
    final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        // 数组为null或者长度为0，则进行扩容
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        // (n-1)&hash得到元素index，n一定是2的幂次方，数组index位置上链表是null，则初始化一个新节点
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        // 数组index位置上已存在链表，则插入
        else {
            Node<K,V> e; K k;
            // 链表第一个节点（或者红黑树头节点）的hash、key等于待插入节点的hash、key，则找到相同节点
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            // 已经转为了红黑树，则使用红黑树插入，e为null表示插入成功，e不为null表示找到相同节点
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            // 使用链表插入
            else {
                // 遍历链表
                for (int binCount = 0; ; ++binCount) {
                    // 链表节点的next为null，则初始化一个节点，插入之（尾插法）
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        // 当链表节点数大于等于8（条件一），则尝试转为红黑树
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    // 链表节点的hash、key等于待插入元素的hash、key，则找到相同节点
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            // e不为null，说明找到相同节点，但还未插入
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                // onlyIfAbsent参数为false，或者找到的节点的value值为null，则覆盖它的value值
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                // 自定义回调，给子类使用
                afterNodeAccess(e);
                // 返回找到相同节点的旧value值
                return oldValue;
            }
        }
        // 一定新建了一个节点，modCount加1
        ++modCount;
        // 数组元素个数加1，当元素个数大于等于8（条件一），则尝试转为红黑树
        if (++size > threshold)
            resize();
        // 自定义回调，给子类使用
        afterNodeInsertion(evict);
        // 由于新建了一个节点，旧value值就为null，返回之
        return null;
    }
```

JDK7，头插法

```java
    // 插入一个<key, value>
    // 返回被覆盖节点的VALUE 或者 null（表示新建了一个节点）
    public V put(K key, V value) {
        // 数组是空数组（默认就是空数组），则扩容
        if (table == EMPTY_TABLE) {
            inflateTable(threshold);
        }
        // key为null，插入节点
        if (key == null)
            return putForNullKey(value);
        // 计算hash值
        int hash = hash(key);
        // 计算index，index=hash&(table.length-1)，table.length一定是2的幂次方，因此相当于index=hash%table.length
        int i = indexFor(hash, table.length);
        // 遍历链表
        for (Entry<K,V> e = table[i]; e != null; e = e.next) {
            Object k;
            // 链表节点的hash、key等于待插入元素的hash、key，则找到相同节点
            if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
                V oldValue = e.value;
                // 替换节点的value值
                e.value = value;
                // 自定义回调方法，给子类使用
                e.recordAccess(this);
                // 返回节点的旧value值
                return oldValue;
            }
        }
        // 需要插入一个新节点，modCount加1
        modCount++;
        // 在数组的index=i位置插入节点
        addEntry(hash, key, value, i);
        // 由于新建了一个节点，旧value值就为null，返回之
        return null;
    }
    // 调整数组长度到第一个比toSize大的2的幂次方数
    private void inflateTable(int toSize) {
        // 保证数组长度为2的幂次方
        // Find a power of 2 >= toSize
        int capacity = roundUpToPowerOf2(toSize);
        // 这句话好像多余
        threshold = (int) Math.min(capacity * loadFactor, MAXIMUM_CAPACITY + 1);
        // 初始化一个新的链表数组
        table = new Entry[capacity];
        // 调整hash种子
        initHashSeedAsNeeded(capacity);
    }
    // 返回一个值，该值是第一个比number大的数，且是2的幂次方数
    private static int roundUpToPowerOf2(int number) {
        // assert number >= 0 : "number must be non-negative";
        return number >= MAXIMUM_CAPACITY
                ? MAXIMUM_CAPACITY
                : (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 1;
    }
    // 插入key为null的节点
    private V putForNullKey(V value) {
        // 遍历链表
        for (Entry<K,V> e = table[0]; e != null; e = e.next) {
            // 发现key为null的节点，则替换value
            if (e.key == null) {
                V oldValue = e.value;
                e.value = value;
                // 自定义回调，给子类使用
                e.recordAccess(this);
                // 返回null节点的旧value
                return oldValue;
            }
        }
        // 需要插入一个新节点，modCount加1
        modCount++;
        // 在数组的index=0位置插入节点
        addEntry(0, null, value, 0);
        // 由于新建了一个节点，旧value值就为null，返回之
        return null;
    }
    // 在table[bucketIndex]位置，使用头插法，插入一个节点
    void addEntry(int hash, K key, V value, int bucketIndex) {
        // 当数组长度大于等于8 且 数组在bucetIndex位置不为null 时，进行扩容
        if ((size >= threshold) && (null != table[bucketIndex])) {
            // 进行扩容到2倍的当前数组长度
            resize(2 * table.length);
            // 重新计算hash值
            hash = (null != key) ? hash(key) : 0;
            // 重新计算bucketIndex值
            bucketIndex = indexFor(hash, table.length);
        }
        // 正式插入节点
        createEntry(hash, key, value, bucketIndex);
    }
    // 使用h & (length-1)来计算index，length一定是2的幂次方，因此相当于h%length
    static int indexFor(int h, int length) {
        // assert Integer.bitCount(length) == 1 : "length must be a non-zero power of 2";
        return h & (length-1);
    }
    // 在table[bucketIndex]位置，使用头插法，正式插入一个节点
    void createEntry(int hash, K key, V value, int bucketIndex) {
        // 取得旧节点
        Entry<K,V> e = table[bucketIndex];
        // 初始化一个新节点，旧节点作为了新节点的next节点，因此是头插法
        table[bucketIndex] = new Entry<>(hash, key, value, e);
        // 数组元素个数加1
        size++;
    }
    static class Entry<K,V> implements Map.Entry<K,V> {
        Entry(int h, K k, V v, Entry<K,V> n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        }
    }
```

##### get方法

JDK8

```java
    // 返回key对应的value值，找不到则返回null
    public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }
    // 返回hash，key对应的节点，找不到则返回null
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        // 三个条件：数组不为null，数组长度大于0，数组index位置（index=(n-1)&hash）已存在链表
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            // 检查头节点，如果匹配则返回
            if (first.hash == hash && // always check first node
                ((k = first.key) == key || (key != null && key.equals(k))))
                return first;
            // 头节点的next节点不为null，则往下遍历查找
            if ((e = first.next) != null) {
                // 已经转为红黑树了，则使用红黑树查找，并返回找到的节点，找不到返回null
                if (first instanceof TreeNode)
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                // 遍历链表，若找到节点，则返回之
                do {
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        // 找不到节点，返回null
        return null;
    }
```

JDK7

```java
    // 返回key对应的value值，找不到则返回null
    public V get(Object key) {
        // key为null
        if (key == null)
            return getForNullKey();
        // 查找节点
        Entry<K,V> entry = getEntry(key);
        // 若找到节点则返回之，找不到则返回null
        return null == entry ? null : entry.getValue();
    }
    // 返回key为null对应的value值，找不到则返回null
    private V getForNullKey() {
        // 数组长度为0，说明找不到，返回null
        if (size == 0) {
            return null;
        }
        // 遍历链表（index=0位置）
        for (Entry<K,V> e = table[0]; e != null; e = e.next) {
            // 找到key为null对应的value值，返回之
            if (e.key == null)
                return e.value;
        }
        // 找不到节点，返回null
        return null;
    }
    // 返回key对应的节点，找不到则返回null
    final Entry<K,V> getEntry(Object key) {
        // 数组长度为0，说明找不到，返回null
        if (size == 0) {
            return null;
        }
        // 计算hash值
        int hash = (key == null) ? 0 : hash(key);
        // 遍历链表
        for (Entry<K,V> e = table[indexFor(hash, table.length)];
             e != null;
             e = e.next) {
            Object k;
            // 链表节点的hash、key等于查找的hash、key，则找到节点，返回之
            if (e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k))))
                return e;
        }
        // 找不到节点，返回null
        return null;
    }
```

### remove方法

JDK8

```java
    public V remove(Object key) {
        Node<K,V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
            null : e.value;
    }
    final Node<K,V> removeNode(int hash, Object key, Object value,
                               boolean matchValue, boolean movable) {
        Node<K,V>[] tab; Node<K,V> p; int n, index;
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (p = tab[index = (n - 1) & hash]) != null) {
            Node<K,V> node = null, e; K k; V v;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                node = p;
            else if ((e = p.next) != null) {
                if (p instanceof TreeNode)
                    node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
                else {
                    do {
                        if (e.hash == hash &&
                            ((k = e.key) == key ||
                             (key != null && key.equals(k)))) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
                }
            }
            if (node != null && (!matchValue || (v = node.value) == value ||
                                 (value != null && value.equals(v)))) {
                if (node instanceof TreeNode)
                    ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
                else if (node == p)
                    tab[index] = node.next;
                else
                    p.next = node.next;
                ++modCount;
                --size;
                afterNodeRemoval(node);
                return node;
            }
        }
        return null;
    }
```

JDK7

```java
    public V remove(Object key) {
        Entry<K,V> e = removeEntryForKey(key);
        return (e == null ? null : e.value);
    }
    final Entry<K,V> removeEntryForKey(Object key) {
        if (size == 0) {
            return null;
        }
        int hash = (key == null) ? 0 : hash(key);
        int i = indexFor(hash, table.length);
        Entry<K,V> prev = table[i];
        Entry<K,V> e = prev;

        while (e != null) {
            Entry<K,V> next = e.next;
            Object k;
            if (e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k)))) {
                modCount++;
                size--;
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                e.recordRemoval(this);
                return e;
            }
            prev = e;
            e = next;
        }

        return e;
    }
```

### resize方法

JDK8

```java
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }
```

JDK7

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
    final boolean initHashSeedAsNeeded(int capacity) {
        boolean currentAltHashing = hashSeed != 0;
        boolean useAltHashing = sun.misc.VM.isBooted() &&
                (capacity >= Holder.ALTERNATIVE_HASHING_THRESHOLD);
        boolean switching = currentAltHashing ^ useAltHashing;
        if (switching) {
            hashSeed = useAltHashing
                ? sun.misc.Hashing.randomHashSeed(this)
                : 0;
        }
        return switching;
    }
```
