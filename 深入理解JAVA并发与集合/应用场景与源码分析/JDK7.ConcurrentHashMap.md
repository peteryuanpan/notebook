- [JDK7.ConcurrentHashMap](#JDK7.ConcurrentHashMap)
  - [应用场景](#应用场景)
  - [核心思想](#核心思想)
  - [类结构分析](#类结构分析)
    - [数据结构](#数据结构)
    - [核心方法](#核心方法)
      - [构造方法](#构造方法)
      - [put方法](#put方法)
      - [get方法](#get方法)
      - [remove方法](#remove方法)
      - [rehash方法](#rehash方法)
      - [scanAndLock方法](#scanAndLock方法)

# JDK7.ConcurrentHashMap

参考
- [ConcurrentHashMap源码+底层数据结构分析](https://github.com/Snailclimb/JavaGuide/blob/master/docs/java/collection/ConcurrentHashMap%E6%BA%90%E7%A0%81+%E5%BA%95%E5%B1%82%E6%95%B0%E6%8D%AE%E7%BB%93%E6%9E%84%E5%88%86%E6%9E%90.md)

### 应用场景

需要使用一种键值对的数据结构，keySet不允许重复，支持插入、查询、删除、替换、自动扩容等操作，操作时间复杂度低，且需要保证线程安全的场景下，ConcurrentHashMap非常适用

### 核心思想

ConcurrnetHashMap 由很多个 Segment 组合，而每一个 Segment 是一个类似于 HashMap 的结构，所以每一个 HashMap 的内部可以进行扩容。但是 Segment 的个数一旦初始化就不能改变，默认 Segment 的个数是 16 个（相当于16 并发）

ConcurrnetHashMap 中的分段是一种思想，多线程只要操作的不是同一个段，就不存在线程安全问题，而操作同一个段时需要使用 ReenrantLock 来加锁

![image](https://user-images.githubusercontent.com/10209135/99253611-01567180-284c-11eb-9de1-f4bad5284942.png)

### 类结构分析

#### 类图

![image](https://user-images.githubusercontent.com/10209135/99254711-be959900-284d-11eb-92e4-f40f5bca6d1e.png)

#### 数据结构

```java
public class HashMap<K,V> extends AbstractMap<K,V> implements Map<K,V>, Cloneable, Serializable {
    // 默认Entry数组的初始长度是16
    static final int DEFAULT_INITIAL_CAPACITY = 16;
    // 默认的扩容引子
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    // 默认的Segment数组长度是16（默认并发量）
    static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    // Entry数组最大长度
    static final int MAXIMUM_CAPACITY = 1 << 30;
    // Segment数组的最小长度是2（最小并发量）
    static final int MIN_SEGMENT_TABLE_CAPACITY = 2;
    // Segment数组的最大长度是 2^16（最大并发量）
    static final int MAX_SEGMENTS = 1 << 16; // slightly conservative
    // TODO：干嘛用的?
    static final int RETRIES_BEFORE_LOCK = 2;
    // 哈希种子
    private transient final int hashSeed = randomHashSeed(this);
    // 用于定位数据属于哪个段（2的Segment数组长度次方减1）
    final int segmentMask;
    // 用于定位数据属于哪个段
    final int segmentShift;
    // Segment数组，是final的，一旦初始化不可变
    final Segment<K,V>[] segments;
    // KEY集合
    transient Set<K> keySet;
    // KEY-VALUE 键值对的集合
    transient Set<Map.Entry<K,V>> entrySet;
    // VALUE集合
    transient Collection<V> values;
    // Segment 中 table 数组对应的类，是一个单向链表结构
    static final class HashEntry<K,V> {
        final int hash;
        final K key;
        volatile V value;
        volatile HashEntry<K,V> next;
    }
    // Segment 类，其内部结构类似于 JDK7的 HashMap，它集成 ReentrantLock，是一把独占锁
    static final class Segment<K,V> extends ReentrantLock implements Serializable {
        // 序列号
        private static final long serialVersionUID = 2249069246763182397L;
        // 竞争分段锁的最多重试次数，一般是64次
        static final int MAX_SCAN_RETRIES = Runtime.getRuntime().availableProcessors() > 1 ? 64 : 1;
        // 链表数组（核心数据）
        transient volatile HashEntry<K,V>[] table;
        // 链表数组中实际的元素个数
        transient int count;
        // 安全失败用的modCount
        transient int modCount;
        // Segment中数组元素个数超过该阈值后，进行扩容
        transient int threshold;
        // 扩容引子，final修饰的
        final float loadFactor;
    }
    // 抽象迭代器
    abstract class HashIterator {
        int nextSegmentIndex;
        int nextTableIndex;
        HashEntry<K,V>[] currentTable;
        HashEntry<K, V> nextEntry;
        HashEntry<K, V> lastReturned;
    }
    // KEY集合迭代器
    final class KeyIterator extends HashIterator implements Iterator<K>, Enumeration<K> {}
    // VALUE集合迭代器
    final class ValueIterator extends HashIterator implements Iterator<V>, Enumeration<V> {}
    // KEY-VALUE集合的迭代器
    final class EntryIterator extends HashIterator implements Iterator<Entry<K,V>> {}
    // keySet 实例对应的类
    final class KeySet extends AbstractSet<K> {}
    // entrySet 实例对应的类
    final class EntrySet extends AbstractSet<Map.Entry<K,V>> {}
    // values 实例对应的类
    final class Values extends AbstractCollection<V> {}
    // UNSAFE类实例
    private static final sun.misc.Unsafe UNSAFE;
    // 下面都是内存偏移量，具体对应实例见 clinit方法
    private static final long SBASE;
    private static final int SSHIFT;
    private static final long TBASE;
    private static final int TSHIFT;
    private static final long HASHSEED_OFFSET;
    private static final long SEGSHIFT_OFFSET;
    private static final long SEGMASK_OFFSET;
    private static final long SEGMENTS_OFFSET;
    // clinit方法
    static {
        int ss, ts;
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class tc = HashEntry[].class;
            Class sc = Segment[].class;
            TBASE = UNSAFE.arrayBaseOffset(tc);
            SBASE = UNSAFE.arrayBaseOffset(sc);
            ts = UNSAFE.arrayIndexScale(tc);
            ss = UNSAFE.arrayIndexScale(sc);
            HASHSEED_OFFSET = UNSAFE.objectFieldOffset(
                ConcurrentHashMap.class.getDeclaredField("hashSeed"));
            SEGSHIFT_OFFSET = UNSAFE.objectFieldOffset(
                ConcurrentHashMap.class.getDeclaredField("segmentShift"));
            SEGMASK_OFFSET = UNSAFE.objectFieldOffset(
                ConcurrentHashMap.class.getDeclaredField("segmentMask"));
            SEGMENTS_OFFSET = UNSAFE.objectFieldOffset(
                ConcurrentHashMap.class.getDeclaredField("segments"));
        } catch (Exception e) {
            throw new Error(e);
        }
        if ((ss & (ss-1)) != 0 || (ts & (ts-1)) != 0)
            throw new Error("data type scale not a power of two");
        SSHIFT = 31 - Integer.numberOfLeadingZeros(ss);
        TSHIFT = 31 - Integer.numberOfLeadingZeros(ts);
    }
}
```

#### 核心方法

##### 构造方法

```java
    // 默认的构造方法，DEFAULT_INITIAL_CAPACITY = 16，DEFAULT_LOAD_FACTOR = 0.75，DEFAULT_CONCURRENCY_LEVEL = 16
    public ConcurrentHashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
    }
    // 构造方法，指定initialCapacity
    public ConcurrentHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
    }
    // 构造方法，指定initialCapacity，loadFactor
    public ConcurrentHashMap(int initialCapacity, float loadFactor) {
        this(initialCapacity, loadFactor, DEFAULT_CONCURRENCY_LEVEL);
    }
    // 构造方法，通过Map m构造
    public ConcurrentHashMap(Map<? extends K, ? extends V> m) {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
                      DEFAULT_INITIAL_CAPACITY),
             DEFAULT_LOAD_FACTOR, DEFAULT_CONCURRENCY_LEVEL);
        putAll(m);
    }
    // 核心构造方法
    public ConcurrentHashMap(int initialCapacity,
                             float loadFactor, int concurrencyLevel) {
        // 参数不符合范围
        if (!(loadFactor > 0) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();
        // 最大并发数为 MAX_SEGMENTS
        if (concurrencyLevel > MAX_SEGMENTS)
            concurrencyLevel = MAX_SEGMENTS;
        // Find power-of-two sizes best matching arguments
        int sshift = 0;
        int ssize = 1;
        // ssize 最小的不小于 concurrencyLevel 的数字，比如 cLevel = 15，ssize = 16
        // 2^sshift 等于 ssize
        while (ssize < concurrencyLevel) {
            ++sshift;
            ssize <<= 1;
        }
        // segmentShift + sshift = 32
        this.segmentShift = 32 - sshift;
        // segmentMask 用于与预算
        this.segmentMask = ssize - 1;
        
        // 到了这里，ssize、sshift、segmentShift、segmentMask 就确定好了，它们各有用途
        // ssize 是 Segment 数组的长度，下文会用到
        // segmentShift 和 segmentMask 在 put 方法中等会用到，用于确认数据属于哪个 Segment
        
        // HashEntry 最大长度为 MAXIMUM_CAPACITY
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        // 下面这一步的理解非常重要
        // c 保证了 每个 Segment 中的 HashEntry 数组尽量平分数据，且 c * ssize >= initialCapcity
        int c = initialCapacity / ssize;
        if (c * ssize < initialCapacity)
            ++c;
        // cap 是 每个 Segment 中 HashEntry 数组的初始长度
        // cap 是最小的不小于 c 的数字，cap 最小为 2
        int cap = MIN_SEGMENT_TABLE_CAPACITY;
        while (cap < c)
            cap <<= 1;
        // create segments and segments[0]
        // 但是，只初始化 Segment[0]，其他位置用了懒初始化
        Segment<K,V> s0 =
            new Segment<K,V>(loadFactor, (int)(cap * loadFactor),
                             (HashEntry<K,V>[])new HashEntry[cap]);
        // 初始化 Segment 数组
        Segment<K,V>[] ss = (Segment<K,V>[])new Segment[ssize];
        // 使用 CAS 指令给 Segment[0] 位置赋值
        UNSAFE.putOrderedObject(ss, SBASE, s0); // ordered write of segments[0]
        this.segments = ss;
    }
```

##### hash方法

类似于JDK7中HashMap的hash方法，hash方法是扰动函数，在key的hashCode方法基础上进行强扰动计算，保证hash散列

```java
    private int hash(Object k) {
        int h = hashSeed;

        if ((0 != h) && (k instanceof String)) {
            return sun.misc.Hashing.stringHash32((String) k);
        }

        h ^= k.hashCode();

        // Spread bits to regularize both segment and index locations,
        // using variant of single-word Wang/Jenkins hash.
        h += (h <<  15) ^ 0xffffcd7d;
        h ^= (h >>> 10);
        h += (h <<   3);
        h ^= (h >>>  6);
        h += (h <<   2) + (h << 14);
        return h ^ (h >>> 16);
    }
```

##### put方法

JDK7 中 ConcurrentHashMap 的 put、remove、replace、clear 方法最终都会调用 Segment 中的对应方法。Segment 继承了 ReentrantLock，是一把独占锁（下面就不重复说明了）

```java
public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    static final class Segment<K,V> extends ReentrantLock implements Serializable {
        // put 一个 < key, value >，已知其hash值，onlyIfAbsent为false表示强制覆盖
        final V put(K key, int hash, V value, boolean onlyIfAbsent) {
            // 先tryLock尝试获取锁，若获取成功，则 node 为 null
            // 若获取失败，调用 scanAndLockForPut 最多循环64次获取锁
            HashEntry<K,V> node = tryLock() ? null :
                scanAndLockForPut(key, hash, value);
            V oldValue;
            try {
                // 获取段的数组table
                HashEntry<K,V>[] tab = table;
                // 通过hash计算出index
                int index = (tab.length - 1) & hash;
                // CAS指令获取table[index]的first节点
                HashEntry<K,V> first = entryAt(tab, index);
                // 遍历链表
                for (HashEntry<K,V> e = first;;) {
                    // 链表节点不为null
                    if (e != null) {
                        K k;
                        // 发现一个元素能匹配上
                        if ((k = e.key) == key ||
                            (e.hash == hash && key.equals(k))) {
                            oldValue = e.value;
                            // onlyIfAbsent为false时，使用新VALUE覆盖旧VALUE，并且modCount加1
                            if (!onlyIfAbsent) {
                                e.value = value;
                                ++modCount;
                            }
                            break;
                        }
                        e = e.next;
                    }
                    // 遍历了所有链表节点都没有匹配上，说明要插入一个新节点
                    else {
                        // node不为null，设置node的next为first节点（头插法）
                        if (node != null)
                            node.setNext(first);
                        // node为null，新建节点，并设置node的next为first节点（头插法）
                        else
                            node = new HashEntry<K,V>(hash, key, value, first);
                        
                        // 注意，上面的操作，还没有将table[index]指向node
                        
                        // count加1
                        int c = count + 1;
                        // 元素个数超过阈值，且table长度未达到最大，进行扩容
                        // 扩容过程中会重新构建table，还会将node插入新位置
                        if (c > threshold && tab.length < MAXIMUM_CAPACITY)
                            rehash(node);
                        // 不需要扩容，把table[index]指向node，此时node是头节点了（头插法）
                        else
                            setEntryAt(tab, index, node);
                        // modCount加1
                        ++modCount;
                        count = c;
                        oldValue = null;
                        break;
                    }
                }
            } finally {
                // 释放锁
                unlock();
            }
            // 返回旧VALUE或null
            return oldValue;
        }
    }
    // 传入 map m，调用 put 方法将 < key, value > 全部打入
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet())
            put(e.getKey(), e.getValue());
    }
    // put 一个 < key, value >
    public V put(K key, V value) {
        Segment<K,V> s;
        if (value == null)
            throw new NullPointerException();
        // 计算key的hash值
        int hash = hash(key);
        // 通过 SegmentShift、SegmentMask 计算出 j
        int j = (hash >>> segmentShift) & segmentMask;
        // (j << SSHIFT) + SBASE 表示内存偏移量
        // 如果 Segment[j] 位置为 null，则进行初始化
        if ((s = (Segment<K,V>)UNSAFE.getObject          // nonvolatile; recheck
             (segments, (j << SSHIFT) + SBASE)) == null) //  in ensureSegment
            s = ensureSegment(j);
        // 调用Segment#put方法，将元素put进该端的HashEntry数组中
        // 不同段的put操作是不互斥的，可以并行进行
        return s.put(key, hash, value, false);
    }
    // putIfAbsent 与 put 类似，但 onlyIfAbsent = true，含义是若已存在元素则不覆盖
    public V putIfAbsent(K key, V value) {
        Segment<K,V> s;
        if (value == null)
            throw new NullPointerException();
        int hash = hash(key);
        int j = (hash >>> segmentShift) & segmentMask;
        if ((s = (Segment<K,V>)UNSAFE.getObject
             (segments, (j << SSHIFT) + SBASE)) == null)
            s = ensureSegment(j);
        return s.put(key, hash, value, true);
    }
    // 初始化 Segments[k] 位置
    private Segment<K,V> ensureSegment(int k) {
        final Segment<K,V>[] ss = this.segments;
        // u为内存偏移量
        long u = (k << SSHIFT) + SBASE; // raw offset
        Segment<K,V> seg;
        // CAS指令确认 Segment[k] 位置为 null，则进行初始化
        if ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u)) == null) {
            // 获取 Segment[0]，使用它的 length，loadFator，threshold
            Segment<K,V> proto = ss[0]; // use segment 0 as prototype
            int cap = proto.table.length;
            float lf = proto.loadFactor;
            int threshold = (int)(cap * lf);
            // 初始化出一个 HashEntry 数组，长度等于 Segment[0] 的 HashEntry 数组
            HashEntry<K,V>[] tab = (HashEntry<K,V>[])new HashEntry[cap];
            // 使用CAS自旋的方法，初始化 Segment[k]，赋值于 seg引用
            if ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u)) == null) { // recheck
                Segment<K,V> s = new Segment<K,V>(lf, threshold, tab);
                while ((seg = (Segment<K,V>)UNSAFE.getObjectVolatile(ss, u)) == null) {
                    if (UNSAFE.compareAndSwapObject(ss, u, null, seg = s))
                        break;
                }
            }
        }
        // 返回seg引用
        return seg;
    }
    // CAS指令获取 table[i] 的 first 节点
    static final <K,V> HashEntry<K,V> entryAt(HashEntry<K,V>[] tab, int i) {
        return (tab == null) ? null :
            (HashEntry<K,V>) UNSAFE.getObjectVolatile
            (tab, ((long)i << TSHIFT) + TBASE);
    }
    // CAS指令赋值 e 于 table[i]
    static final <K,V> void setEntryAt(HashEntry<K,V>[] tab, int i,
                                       HashEntry<K,V> e) {
        UNSAFE.putOrderedObject(tab, ((long)i << TSHIFT) + TBASE, e);
    }
    static final class HashEntry<K,V> {
        final int hash;
        final K key;
        volatile V value;
        volatile HashEntry<K,V> next;
        
        // CAS指令设置next
        final void setNext(HashEntry<K,V> n) {
            UNSAFE.putOrderedObject(this, nextOffset, n);
        }
    }
}
```

##### get方法

```java
public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    // 通过KEY获取VALUE值
    public V get(Object key) {
        Segment<K,V> s; // manually integrate access methods to reduce overhead
        HashEntry<K,V>[] tab;
        // 获取KEY的hash值
        int h = hash(key);
        // 获取内存偏移量
        long u = (((h >>> segmentShift) & segmentMask) << SSHIFT) + SBASE;
        // Segment[u]不为null，且table不为null
        if ((s = (Segment<K,V>)UNSAFE.getObjectVolatile(segments, u)) != null &&
            (tab = s.table) != null) {
            // 通过 (tab.length - 1) & h 计算出 index
            // 获取 table[index] 的 first 节点，遍历链表
            for (HashEntry<K,V> e = (HashEntry<K,V>) UNSAFE.getObjectVolatile
                     (tab, ((long)(((tab.length - 1) & h)) << TSHIFT) + TBASE);
                 e != null; e = e.next) {
                K k;
                // 发现匹配节点，返回之
                if ((k = e.key) == key || (e.hash == h && key.equals(k)))
                    return e.value;
            }
        }
        // 未匹配节点，返回null
        return null;
    }
    
}
```

##### remove方法

```java
public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    // 删除KEY对应的键值对，返回删除的VALUE或者null
    public V remove(Object key) {
        int hash = hash(key);
        Segment<K,V> s = segmentForHash(hash);
        return s == null ? null : s.remove(key, hash, null);
    }
    // 删除<KEY, VALUE>键值对，返回删除成功与否
    public boolean remove(Object key, Object value) {
        int hash = hash(key);
        Segment<K,V> s;
        return value != null && (s = segmentForHash(hash)) != null &&
            s.remove(key, hash, value) != null;
    }
    static final class Segment<K,V> extends ReentrantLock implements Serializable {
        // 删除<KEY, VALUE>键值对，VALUE可能为null，其hash值已知
        final V remove(Object key, int hash, Object value) {
            // 尝试获取锁，如果获取失败，调用 scanAndLockForPut 最多循环64次获取锁
            if (!tryLock())
                scanAndLock(key, hash);
            V oldValue = null;
            try {
                HashEntry<K,V>[] tab = table;
                // 通过hash获取index
                int index = (tab.length - 1) & hash;
                // CAS指令获取 table[index] 的 first 节点
                HashEntry<K,V> e = entryAt(tab, index);
                // e的前驱节点
                HashEntry<K,V> pred = null;
                // 遍历链表
                while (e != null) {
                    K k;
                    HashEntry<K,V> next = e.next;
                    // 匹配节点
                    if ((k = e.key) == key ||
                        (e.hash == hash && key.equals(k))) {
                        V v = e.value;
                        // value为null，表示不需要匹配value
                        // value不为null，则进行匹配
                        if (value == null || value == v || value.equals(v)) {
                            // e的前驱节点为null，则将table[index]指向e的后继节点（删除e）
                            if (pred == null)
                                setEntryAt(tab, index, next);
                            // 否则，将e的前驱节点next指向e的后继节点（删除e）
                            else
                                pred.setNext(next);
                            // modCount加1，count减1
                            ++modCount;
                            --count;
                            oldValue = v;
                        }
                        break;
                    }
                    pred = e;
                    e = next;
                }
            } finally {
                // 释放锁
                unlock();
            }
            return oldValue;
        }
    }
    // 通过hash获取Segment[index]
    private Segment<K,V> segmentForHash(int h) {
        long u = (((h >>> segmentShift) & segmentMask) << SSHIFT) + SBASE;
        return (Segment<K,V>) UNSAFE.getObjectVolatile(segments, u);
    }
}
```

##### rehash方法

rehash方法实际上是扩容，没有重新hash

rehash方法只在Segment#put方法中被调用了，put方法获取了锁，因此是保证原子性的

```java
public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    static final class Segment<K,V> extends ReentrantLock implements Serializable {
        // 扩容，且将node插入新位置
        private void rehash(HashEntry<K,V> node) {
            HashEntry<K,V>[] oldTable = table;
            // 旧数组长度
            int oldCapacity = oldTable.length;
            // 新数组长度固定为旧数组长度的2倍
            int newCapacity = oldCapacity << 1;
            // 新的扩容阈值（新数组长度乘以扩容引子）
            threshold = (int)(newCapacity * loadFactor);
            // 初始化一个新的HashEntry数组
            HashEntry<K,V>[] newTable =
                (HashEntry<K,V>[]) new HashEntry[newCapacity];
            // 用于定位元素在新数组的位置
            int sizeMask = newCapacity - 1;
            // 枚举旧数组的每个位置，将元素转移到新数组上
            for (int i = 0; i < oldCapacity ; i++) {
                // 获取index=i的头节点
                HashEntry<K,V> e = oldTable[i];
                if (e != null) {
                    // 获取e的后继节点
                    HashEntry<K,V> next = e.next;
                    // 通过hash重新计算新的index
                    int idx = e.hash & sizeMask;
                    // e的后继节点为null，则将e赋值于新数组的新index位置
                    if (next == null)   //  Single node on list
                        newTable[idx] = e;
                    // 否则，枚举链表，将元素转移到新数组上
                    else { // Reuse consecutive sequence at same slot
                        HashEntry<K,V> lastRun = e;
                        int lastIdx = idx;
                        // 找到一个节点 lastRun，lastRun 往后的后继节点 hash 值计算出来的 index 都与 lastRun 一样
                        // lastIdx 为 lastRun 通过 hash 计算出来的 index
                        for (HashEntry<K,V> last = next;
                             last != null;
                             last = last.next) {
                            int k = last.hash & sizeMask;
                            if (k != lastIdx) {
                                lastIdx = k;
                                lastRun = last;
                            }
                        }
                        // 将新数组的 lastIndex 位置指向 lastRun（lastRun后继可能还有节点，一并属于lastIdx位置）
                        newTable[lastIdx] = lastRun;
                        // Clone remaining nodes
                        // 从 e 枚举到 lastRun
                        for (HashEntry<K,V> p = e; p != lastRun; p = p.next) {
                            V v = p.value;
                            // 沿用旧的hash值
                            int h = p.hash;
                            // 获取新的位置 K
                            // 重要！可以发现，K 要不然等于旧数组位置，要不然等于旧数组位置加上旧数组长度
                            int k = h & sizeMask;
                            // 采用头插法，将元素插入新数组
                            HashEntry<K,V> n = newTable[k];
                            newTable[k] = new HashEntry<K,V>(h, p.key, v, n);
                        }
                    }
                }
            }
            // 使用头插法，将node插入新位置
            int nodeIndex = node.hash & sizeMask; // add the new node
            node.setNext(newTable[nodeIndex]);
            newTable[nodeIndex] = node;
            // 新表替换旧表
            table = newTable;
        }
    }
}
```

##### scanAndLock方法

下面的方法是整个JDK7.ConcurrentHashMap的核心方法，因为它诠释了是如何保证线程安全的
- put方法中在tryLock失败后会调用scanAndLockForPut方法，remove和replace方法中在tryLock失败后会调用scanAndLock方法
- scanAndLockForPut方法和scanAndLock方法上层都没有加互斥锁，所以entryForHash方法中使用了CAS指令来保证原子性，seg.table使用了volatile修饰来保证可见性与有序性
- scanAndLockForPut方法和scanAndLock方法目的都是获取独占锁，若尝试64次后依然未获取到，会调用lock()方法进入AQS等待队列

```java
public class ConcurrentHashMap<K, V> extends AbstractMap<K, V> implements ConcurrentMap<K, V>, Serializable {
    static final class Segment<K,V> extends ReentrantLock implements Serializable {
        // 尝试在put方法前获取锁，并合适时候初始化节点
        // 若尝试64次后获取不成功，会强制lock，进入等待队列
        private HashEntry<K,V> scanAndLockForPut(K key, int hash, V value) {
            // 根据hash获取Segment[index]的头节点
            HashEntry<K,V> first = entryForHash(this, hash);
            // 将头节点赋值给e
            HashEntry<K,V> e = first;
            // node是需要返回的节点，可能会初始化
            HashEntry<K,V> node = null;
            // 重试次数
            int retries = -1; // negative while locating node
            // 尝试获取锁，获取不到则进入while循环
            // while循环中，会做三件事
            // (1) 遍历链表，尝试将retires清0，并合适时候初始化节点
            // (2) ++retries，当超过64时，强制lock，进入等待队列
            // (3) 检测hash获取的Segment[index]的头节点是否发生了变化，若发生了则retires清为-1
            while (!tryLock()) {
                HashEntry<K,V> f; // to recheck first below
                // (1) 遍历链表，尝试将retires清0，并合适时候初始化节点
                if (retries < 0) {
                    // retries为-1时进入，可以发现第一次while循环一定进入
                    // e和node皆为null时，初始化节点，赋值给node
                    // e为null时，retries清0
                    if (e == null) {
                        if (node == null) // speculatively create node
                            node = new HashEntry<K,V>(hash, key, value, null);
                        retries = 0;
                    }
                    // e不为null，key等于e.key，匹配到节点了，retires清0
                    else if (key.equals(e.key))
                        retries = 0;
                    // e不为null，key不等于e.key，e赋值为e的后继节点
                    else
                        e = e.next;
                }
                // (2) ++retries，当超过64时，强制lock，进入等待队列
                else if (++retries > MAX_SCAN_RETRIES) {
                    lock();
                    break;
                }
                // (3) 检测hash获取的Segment[index]的头节点是否发生了变化，若发生了则retires清为-1
                else if ((retries & 1) == 0 &&
                         (f = entryForHash(this, hash)) != first) {
                    e = first = f; // re-traverse if entry changed
                    retries = -1;
                }
            }
            return node;
        }
        // 尝试在remove、replace方法前获取锁
        // 若尝试64次后获取不成功，会强制lock，进入等待队列
        // 与 scanAndLockForPut 方法很相似，就 retries < 0 时策略略有区别
        private void scanAndLock(Object key, int hash) {
            // similar to but simpler than scanAndLockForPut
            HashEntry<K,V> first = entryForHash(this, hash);
            HashEntry<K,V> e = first;
            int retries = -1;
            while (!tryLock()) {
                HashEntry<K,V> f;
                if (retries < 0) {
                    if (e == null || key.equals(e.key))
                        retries = 0;
                    else
                        e = e.next;
                }
                else if (++retries > MAX_SCAN_RETRIES) {
                    lock();
                    break;
                }
                else if ((retries & 1) == 0 &&
                         (f = entryForHash(this, hash)) != first) {
                    e = first = f;
                    retries = -1;
                }
            }
        }
    }
    // 通过hash获取Segment[index]
    static final <K,V> HashEntry<K,V> entryForHash(Segment<K,V> seg, int h) {
        HashEntry<K,V>[] tab;
        return (seg == null || (tab = seg.table) == null) ? null :
            (HashEntry<K,V>) UNSAFE.getObjectVolatile
            (tab, ((long)(((tab.length - 1) & h)) << TSHIFT) + TBASE);
    }
}
```
