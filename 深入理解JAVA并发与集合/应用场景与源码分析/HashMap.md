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
      - [treeifyBin方法](#treeifyBin方法)
      - [resize方法](#resize方法)

# HashMap

参考
- [HashMap(JDK1.8)源码+底层数据结构分析](https://github.com/Snailclimb/JavaGuide/blob/master/docs/java/collection/HashMap(JDK1.8)%E6%BA%90%E7%A0%81+%E5%BA%95%E5%B1%82%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E5%88%86%E6%9E%90.md)
- [一文看懂HashMap](https://segmentfault.com/a/1190000022184751)

### 应用场景

需要使用一种时间复杂度低的键值对的数据结构，keySet不允许重复，支持插入、查询、删除、替换、自动扩容等操作，且是在单线程或不存在考虑线程安全问题的场景下，HashMap非常适用

应用举例
- Spring中的单例池，以< String, Object >结构，存储BeanName到单例Bean对象的映射关系
- HttpServletRequest中，以< String, String[] >结构，存储参数名到参数值的映射关系（实际上是LinkedHashMap，参考org.apache.catalina.util.ParameterMap）

### 核心思想

数组负责存储链表，通过hash&(数组长度-1)确定位置，不同元素需要存储在一个位置上时，通过链表存储

数组长度一定为2的幂次方，适当的时候自动扩容

链表负责存储具体元素，JDK7中，hash更散列，但链表可能会退化，JDK8中，链表长度和数组长度达到一定条件时，将链表转为红黑树，提高查询效率

插入元素，JDK7采用的是头插法，JDK8采用的是尾插法

扩容数组，JDK7是将旧数组每个链表的元素重新计算位置，插入到新数组中，JDK8是将旧数组链表的元素分别插入一个低位链表数组或者一个高位链表数组（扩容2倍的思想），然后再插入到新数组中

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
    // 节点个数
    transient int size;
    // 每次扩容和更改map结构的计数器
    transient int modCount;
    // 扩容阈值，一般情况下，当节点个数大于扩容阈值时会进行扩容，扩容阈值等于数组长度乘以扩容引子
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
    // 节点个数
    transient int size;
    // 扩容阈值，一般情况下，当节点个数大于扩容阈值时会进行扩容，扩容阈值等于数组长度乘以扩容引子
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

JDK8，尾插法

```java
    // 插入一个<key, value>，若节点已存在则覆盖之
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
    // 插入一个<key, value>，若节点已存在则不覆盖！
    public V putIfAbsent(K key, V value) {
        return putVal(hash(key), key, value, true, true);
    }
    // 插入一个<key, value>
    // 如果onlyIfAbsent是true，不覆盖已存在的节点
    // 如果evict是false，数组是在creation mode
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
        // 节点个数加1，当元素个数大于等于8（条件一），则尝试转为红黑树
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
    // 插入一个<key, value>，若节点已存在则覆盖之
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
        // 节点个数加1
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
    // 查找key对应的value值，找不到则返回null
    public V get(Object key) {
        Node<K,V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }
    // 查找hash，key对应的节点，找不到则返回null
    final Node<K,V> getNode(int hash, Object key) {
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        // 三个条件：数组不为null，数组长度大于0，数组index位置（index=(n-1)&hash）已存在链表
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (first = tab[(n - 1) & hash]) != null) {
            // 检查头节点，如果hash和key一样则返回
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
    // 查找key对应的value值，找不到则返回null
    public V get(Object key) {
        // key为null
        if (key == null)
            return getForNullKey();
        // 查找节点
        Entry<K,V> entry = getEntry(key);
        // 若找到节点则返回之，找不到则返回null
        return null == entry ? null : entry.getValue();
    }
    // 查找key为null对应的value值，找不到则返回null
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
    // 查找key对应的节点，找不到则返回null
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
    // 删除key对应的节点，若找到则返回value，否则返回null
    public V remove(Object key) {
        Node<K,V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
            null : e.value;
    }
    // 删除<key, value>对应的节点，删除则返回true，否则返回false
    public boolean remove(Object key, Object value) {
        return removeNode(hash(key), key, value, true, true) != null;
    }
    // 删除节点，若找到则返回之，否则返回null
    // 若matchValue为true，删除hash, key, value对应的节点
    // 若matchValue为false，删除hash, key对应的节点
    // movable与红黑树操作有关
    final Node<K,V> removeNode(int hash, Object key, Object value,
                               boolean matchValue, boolean movable) {
        Node<K,V>[] tab; Node<K,V> p; int n, index;
        // 三个条件：数组不为null，数组长度大于0，数组index位置（index=(n-1)&hash）已存在链表
        if ((tab = table) != null && (n = tab.length) > 0 &&
            (p = tab[index = (n - 1) & hash]) != null) {
            Node<K,V> node = null, e; K k; V v;
            // 头节点的hash和key一样，则找到node
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                node = p;
            else if ((e = p.next) != null) {
                // 已经是红黑树，则在树上找node
                if (p instanceof TreeNode)
                    node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
                // 在链表上找node
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
            // node不为null，表示找到node，根据matchValue判断是否进行value的匹配
            if (node != null && (!matchValue || (v = node.value) == value ||
                                 (value != null && value.equals(v)))) {
                // 已经是红黑树，在树上删除node
                if (node instanceof TreeNode)
                    ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
                // 去除要删除的链表节点
                else if (node == p)
                    tab[index] = node.next;
                else
                    p.next = node.next;
                // modCound加1
                ++modCount;
                // 节点个数减1
                --size;
                // 自定义回调方法，给子类使用
                afterNodeRemoval(node);
                // 返回删除的节点
                return node;
            }
        }
        // 没找到对应的node，不删除，返回null
        return null;
    }
```

JDK7

```java
    // 删除节点，若找到则返回value，否则返回null
    public V remove(Object key) {
        Entry<K,V> e = removeEntryForKey(key);
        return (e == null ? null : e.value);
    }
    // 删除节点，若找到则返回之，否则返回null
    final Entry<K,V> removeEntryForKey(Object key) {
        // 节点个数为0
        if (size == 0) {
            return null;
        }
        // 计算hash值
        int hash = (key == null) ? 0 : hash(key);
        // 计算index值
        int i = indexFor(hash, table.length);
        // 获取头节点
        Entry<K,V> prev = table[i];
        Entry<K,V> e = prev;
        // 链表循环
        while (e != null) {
            Entry<K,V> next = e.next;
            Object k;
            // hash与key一样，则找到节点
            if (e.hash == hash &&
                ((k = e.key) == key || (key != null && key.equals(k)))) {
                // modCound加1
                modCount++;
                // 节点个数减1
                size--;
                // 去除找到的节点
                if (prev == e)
                    table[i] = next;
                else
                    prev.next = next;
                // 自定义回调方法，给子类使用
                e.recordRemoval(this);
                // 返回找到的节点
                return e;
            }
            prev = e;
            e = next;
        }
        // 链表循环完了，返回null
        return e;
    }
```

### treeifyBin方法

JDK8独有，该方法在putVal、computeIfAbsent、compute、merge方法中被调用

```java
    // 将链表转为红黑树
    final void treeifyBin(Node<K,V>[] tab, int hash) {
        int n, index; Node<K,V> e;
        // 数组为null，或者数组长度小于64（反之是转为红黑树的第二个条件），进行扩容，不转红黑树
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
            resize();
        // 数组链表不为null
        else if ((e = tab[index = (n - 1) & hash]) != null) {
            TreeNode<K,V> hd = null, tl = null;
            // 构造红黑树链表
            do {
                TreeNode<K,V> p = replacementTreeNode(e, null);
                if (tl == null)
                    hd = p;
                else {
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            // 数组index位置更新为红黑树链表，并且正式转为红黑树
            if ((tab[index] = hd) != null)
                hd.treeify(tab);
        }
    }
    TreeNode<K,V> replacementTreeNode(Node<K,V> p, Node<K,V> next) {
        return new TreeNode<>(p.hash, p.key, p.value, next);
    }
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        TreeNode<K,V> parent;  // red-black tree links
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;
        TreeNode(int hash, K key, V val, Node<K,V> next) {
            super(hash, key, val, next);
        }
        /**
         * Forms tree of the nodes linked from this node.
         */
        final void treeify(Node<K,V>[] tab) {
            TreeNode<K,V> root = null;
            for (TreeNode<K,V> x = this, next; x != null; x = next) {
                next = (TreeNode<K,V>)x.next;
                x.left = x.right = null;
                if (root == null) {
                    x.parent = null;
                    x.red = false;
                    root = x;
                }
                else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    for (TreeNode<K,V> p = root;;) {
                        int dir, ph;
                        K pk = p.key;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((kc == null &&
                                  (kc = comparableClassFor(k)) == null) ||
                                 (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);

                        TreeNode<K,V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            root = balanceInsertion(root, x);
                            break;
                        }
                    }
                }
            }
            moveRootToFront(tab, root);
        }
...
     }
```

### resize方法

JDK8

```java
    // 扩容，返回新的数组
    final Node<K,V>[] resize() {
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        // 旧数组长度大于0
        if (oldCap > 0) {
            // 旧数组长度太大了，不进行扩容，直接返回旧数组
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            // 一般情况下，新数组长度为旧数组长度两倍，新扩容阈值也为旧扩容阈值两倍
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        // 旧数组长度等于0，旧扩容阈值大于0
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        // 旧数组长度等于0，旧扩容阈值等于0，按默认策略分配值
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        // 新扩容阈值等于0，按默认策略分配值
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor; // 一般情况下，扩容阈值等于数组长度乘以扩容引子
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        // 确定了扩容阈值
        threshold = newThr;
        // 初始化新数组
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
        // 旧数组不为null，开始转移数据
        if (oldTab != null) {
            // 从0开始枚举每个旧数组的位置
            for (int j = 0; j < oldCap; ++j) {
                Node<K,V> e;
                // 数组位置上有元素，赋值给e
                if ((e = oldTab[j]) != null) {
                    // 旧数组元素清空
                    oldTab[j] = null;
                    // e的next是null，说明就一个元素，直接插入到新数组中
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    // 已经是红黑树了，将红黑树分为两个子红黑树，插入到新数组中
                    else if (e instanceof TreeNode)
                        ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    // 还是链表
                    else { // preserve order
                        Node<K,V> loHead = null, loTail = null;
                        Node<K,V> hiHead = null, hiTail = null;
                        Node<K,V> next;
                        // 根据 e.hash & oldCap 结果是0或1，分别构建两个新的链表，一个是低位链表，一个是高位链表
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
                        // 低位链表插入新数组，位置与旧数组位置一致
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        // 高位链表插入新数组，位置是旧数组位置+旧数组长度（新数组正好是有一半是旧数据，一半是新数据）
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        // 返回新数组
        return newTab;
    }
    static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
        // 这是“将红黑树分为两个子红黑树，插入到新数组中”的过程
        // 与上面“构建两个新的链表，一个是低位链表，一个是高位链表，分别插入数组”的思想是一样的
        final void split(HashMap<K,V> map, Node<K,V>[] tab, int index, int bit) {
            TreeNode<K,V> b = this;
            // Relink into lo and hi lists, preserving order
            TreeNode<K,V> loHead = null, loTail = null;
            TreeNode<K,V> hiHead = null, hiTail = null;
            int lc = 0, hc = 0;
            for (TreeNode<K,V> e = b, next; e != null; e = next) {
                next = (TreeNode<K,V>)e.next;
                e.next = null;
                if ((e.hash & bit) == 0) {
                    if ((e.prev = loTail) == null)
                        loHead = e;
                    else
                        loTail.next = e;
                    loTail = e;
                    ++lc;
                }
                else {
                    if ((e.prev = hiTail) == null)
                        hiHead = e;
                    else
                        hiTail.next = e;
                    hiTail = e;
                    ++hc;
                }
            }

            if (loHead != null) {
                if (lc <= UNTREEIFY_THRESHOLD)
                    tab[index] = loHead.untreeify(map);
                else {
                    tab[index] = loHead;
                    if (hiHead != null) // (else is already treeified)
                        loHead.treeify(tab);
                }
            }
            if (hiHead != null) {
                if (hc <= UNTREEIFY_THRESHOLD)
                    tab[index + bit] = hiHead.untreeify(map);
                else {
                    tab[index + bit] = hiHead;
                    if (loHead != null)
                        hiHead.treeify(tab);
                }
            }
        }
    }
```

JDK7

```java
    // 扩容，新数组长度为newCapacity
    // 从其他代码来看，不会出现newCapacity比oldCapacity小的情况
    void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        // oldCapacity太大了，不扩容了
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }
        // 初始化新的数组
        Entry[] newTable = new Entry[newCapacity];
        // 转移旧数组元素到新数组中，并决定是否rehash
        transfer(newTable, initHashSeedAsNeeded(newCapacity));
        // 新数组赋值
        table = newTable;
        // 计算新的扩容阈值
        threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
    }
    // 决定是否进行rehash，逻辑比较复杂
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
    // 转移旧数组元素到新数组中
    void transfer(Entry[] newTable, boolean rehash) {
        // 获取新数组长度
        int newCapacity = newTable.length;
        // 枚举旧数组每个位置
        for (Entry<K,V> e : table) {
            // 链表循环
            while(null != e) {
                Entry<K,V> next = e.next;
                // 如果进行rehash，则对每个元素重新hash（大量做这一步是比较耗时的）
                if (rehash) {
                    e.hash = null == e.key ? 0 : hash(e.key);
                }
                // 重新计算index
                int i = indexFor(e.hash, newCapacity);
                // 将元素插入链表（头插法）
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            }
        }
    }
    // 使用h & (length-1)来计算index，length一定是2的幂次方，因此相当于h%length
    static int indexFor(int h, int length) {
        // assert Integer.bitCount(length) == 1 : "length must be a non-zero power of 2";
        return h & (length-1);
    }
```
