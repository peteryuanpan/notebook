
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
    // Segment 类，其内部结构类似于 JDK7的 HashMap
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

##### xxx方法
