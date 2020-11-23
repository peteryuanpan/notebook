- [JDK8.ConcurrentHashMap](#JDK8.ConcurrentHashMap)
  - [应用场景](#应用场景)
  - [核心思想](#核心思想)
  - [类结构分析](#类结构分析)
    - [数据结构](#数据结构)
    - [核心方法](#核心方法)
      - [hash方法](#hash方法)
      - [put方法](#put方法)

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

    private static final long serialVersionUID = 7249069246763182397L;

    private static final int MAXIMUM_CAPACITY = 1 << 30;
    
    private static final int DEFAULT_CAPACITY = 16;
    
    static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;
    
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;
    
    private static final float LOAD_FACTOR = 0.75f;
    
    static final int TREEIFY_THRESHOLD = 8;
    
    static final int UNTREEIFY_THRESHOLD = 6;
    
    static final int MIN_TREEIFY_CAPACITY = 64;
    
    private static final int MIN_TRANSFER_STRIDE = 16;
    
    private static int RESIZE_STAMP_BITS = 16;
    
    private static final int MAX_RESIZERS = (1 << (32 - RESIZE_STAMP_BITS)) - 1;
    
    private static final int RESIZE_STAMP_SHIFT = 32 - RESIZE_STAMP_BITS;
    
    static final int MOVED     = -1; // hash for forwarding nodes
    static final int TREEBIN   = -2; // hash for roots of trees
    static final int RESERVED  = -3; // hash for transient reservations
    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node has
    
    /** Number of CPUS, to place bounds on some sizings */
    static final int NCPU = Runtime.getRuntime().availableProcessors();
    
    static class Node<K,V> implements Map.Entry<K,V> {
        final int hash;
        final K key;
        volatile V val;
        volatile Node<K,V> next;
    }
    
    /* ---------------- Fields -------------- */
    
    transient volatile Node<K,V>[] table;
    
    private transient volatile Node<K,V>[] nextTable;
    
    private transient volatile long baseCount;
    
    private transient volatile int sizeCtl;
    
    private transient volatile int transferIndex;
    
    private transient volatile int cellsBusy;
    
    private transient volatile CounterCell[] counterCells;
    
    private transient KeySetView<K,V> keySet;
    private transient ValuesView<K,V> values;
    private transient EntrySetView<K,V> entrySet;
    
     /**
     * Stripped-down version of helper class used in previous version,
     * declared for the sake of serialization compatibility
     */
    static class Segment<K,V> extends ReentrantLock implements Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        final float loadFactor;
        Segment(float lf) { this.loadFactor = lf; }
    }
    
    static final class ForwardingNode<K,V> extends Node<K,V> {
        final Node<K,V>[] nextTable;
        ForwardingNode(Node<K,V>[] tab) {
            super(MOVED, null, null, null);
            this.nextTable = tab;
        }
        Node<K,V> find(int h, Object k) {
        ...
        }
    }
    
    static final class ReservationNode<K,V> extends Node<K,V> {
        ReservationNode() {
            super(RESERVED, null, null, null);
        }

        Node<K,V> find(int h, Object k) {
            return null;
        }
    }
    
    @sun.misc.Contended static final class CounterCell {
        volatile long value;
        CounterCell(long x) { value = x; }
    }
    
    static final class TreeNode<K,V> extends Node<K,V> {
        TreeNode<K,V> parent;  // red-black tree links
        TreeNode<K,V> left;
        TreeNode<K,V> right;
        TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;
    }
    
    static final class TreeBin<K,V> extends Node<K,V> {
        TreeNode<K,V> root;
        volatile TreeNode<K,V> first;
        volatile Thread waiter;
        volatile int lockState;
        // values for lockState
        static final int WRITER = 1; // set while holding write lock
        static final int WAITER = 2; // set when waiting for write lock
        static final int READER = 4; // increment value for setting read lock
    }
    
    static final class TableStack<K,V> {
        int length;
        int index;
        Node<K,V>[] tab;
        TableStack<K,V> next;
    }
    
    static class Traverser<K,V> {
        Node<K,V>[] tab;        // current table; updated if resized
        Node<K,V> next;         // the next entry to use
        TableStack<K,V> stack, spare; // to save/restore on ForwardingNodes
        int index;              // index of bin to use next
        int baseIndex;          // current index of initial table
        int baseLimit;          // index bound for initial table
        final int baseSize;     // initial table siz
    }
     
    static class BaseIterator<K,V> extends Traverser<K,V> {}
    static final class KeyIterator<K,V> extends BaseIterator<K,V> implements Iterator<K>, Enumeration<K> {}
    static final class ValueIterator<K,V> extends BaseIterator<K,V> implements Iterator<V>, Enumeration<V> {}
    static final class EntryIterator<K,V> extends BaseIterator<K,V> implements Iterator<Map.Entry<K,V>> {}
    static final class MapEntry<K,V> implements Map.Entry<K,V> {}
    static final class KeySpliterator<K,V> extends Traverser<K,V> implements Spliterator<K> {}
    static final class ValueSpliterator<K,V> extends Traverser<K,V> implements Spliterator<V> {}
    static final class EntrySpliterator<K,V> extends Traverser<K,V> implements Spliterator<Map.Entry<K,V>> {}
    
    abstract static class CollectionView<K,V,E>
        implements Collection<E>, java.io.Serializable {
        private static final long serialVersionUID = 7249069246763182397L;
        final ConcurrentHashMap<K,V> map;
        CollectionView(ConcurrentHashMap<K,V> map)  { this.map = map; }
    }
    public static class KeySetView<K,V> extends CollectionView<K,V,K>
        implements Set<K>, java.io.Serializable {
        private static final long serialVersionUID = 7249069246763182397L;
        private final V value;
        KeySetView(ConcurrentHashMap<K,V> map, V value) {  // non-public
            super(map);
            this.value = value;
        }
    }
    static final class ValuesView<K,V> extends CollectionView<K,V,V>
        implements Collection<V>, java.io.Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        ValuesView(ConcurrentHashMap<K,V> map) { super(map); }
    }
    static final class EntrySetView<K,V> extends CollectionView<K,V,Map.Entry<K,V>>
        implements Set<Map.Entry<K,V>>, java.io.Serializable {
        private static final long serialVersionUID = 2249069246763182397L;
        EntrySetView(ConcurrentHashMap<K,V> map) { super(map); }
    }
    
    abstract static class BulkTask<K,V,R> extends CountedCompleter<R> {
        Node<K,V>[] tab;        // same as Traverser
        Node<K,V> next;
        TableStack<K,V> stack, spare;
        int index;
        int baseIndex;
        int baseLimit;
        final int baseSize;
        int batch;
    }
    
    // 以下是非常多的一套内部类，作用 ??
    
    static final class ForEachKeyTask<K,V> extends BulkTask<K,V,Void> {
        final Consumer<? super K> action;
    }
    static final class ForEachValueTask<K,V> extends BulkTask<K,V,Void> {
        final Consumer<? super V> action;
    }
    static final class ForEachEntryTask<K,V> extends BulkTask<K,V,Void> {
        final Consumer<? super Entry<K,V>> action;
    }
    static final class ForEachMappingTask<K,V> extends BulkTask<K,V,Void> {
        final BiConsumer<? super K, ? super V> act
    }
    static final class ForEachTransformedKeyTask<K,V,U> extends BulkTask<K,V,Void> {
        final Function<? super K, ? extends U> transformer;
        final Consumer<? super U> action;
    }
    static final class ForEachTransformedValueTask<K,V,U> extends BulkTask<K,V,Void> {
        final Function<? super V, ? extends U> transformer;
        final Consumer<? super U> action;
    }
    static final class ForEachTransformedEntryTask<K,V,U> extends BulkTask<K,V,Void> {
        final Function<Map.Entry<K,V>, ? extends U> transformer;
        final Consumer<? super U> action;
    }
    static final class ForEachTransformedMappingTask<K,V,U> extends BulkTask<K,V,Void> {
        final BiFunction<? super K, ? super V, ? extends U> transformer;
        final Consumer<? super U> action;
    }
    static final class SearchKeysTask<K,V,U> extends BulkTask<K,V,U> {
        final Function<? super K, ? extends U> searchFunction;
        final AtomicReference<U> result;
    }
    static final class SearchValuesTask<K,V,U> extends BulkTask<K,V,U> {
        final Function<? super V, ? extends U> searchFunction;
        final AtomicReference<U> result;
    }
    static final class SearchEntriesTask<K,V,U> extends BulkTask<K,V,U> {
        final Function<Entry<K,V>, ? extends U> searchFunction;
        final AtomicReference<U> result
    }
    static final class SearchMappingsTask<K,V,U> extends BulkTask<K,V,U> {
        final BiFunction<? super K, ? super V, ? extends U> searchFunction;
        final AtomicReference<U> result;
    }
    static final class ReduceKeysTask<K,V> extends BulkTask<K,V,K> {
        final BiFunction<? super K, ? super K, ? extends K> reducer;
        K result;
        ReduceKeysTask<K,V> rights, nextRight;
    }
    static final class ReduceValuesTask<K,V> extends BulkTask<K,V,V> {
        final BiFunction<? super V, ? super V, ? extends V> reducer;
        V result;
        ReduceValuesTask<K,V> rights, nextRight;
    }
    static final class ReduceEntriesTask<K,V> extends BulkTask<K,V,Map.Entry<K,V>> {
        final BiFunction<Map.Entry<K,V>, Map.Entry<K,V>, ? extends Map.Entry<K,V>> reducer;
        Map.Entry<K,V> result;
        ReduceEntriesTask<K,V> rights, nextRight;
    }
    static final class MapReduceKeysTask<K,V,U> extends BulkTask<K,V,U> {
        final Function<? super K, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceKeysTask<K,V,U> rights, nextRight;
    }
    static final class MapReduceValuesTask<K,V,U> extends BulkTask<K,V,U> {
        final Function<? super V, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceValuesTask<K,V,U> rights, nextRight;
    }
    static final class MapReduceEntriesTask<K,V,U> extends BulkTask<K,V,U> {
        final Function<Map.Entry<K,V>, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceEntriesTask<K,V,U> rights, nextRight;
    }
    static final class MapReduceMappingsTask<K,V,U> extends BulkTask<K,V,U> {
        final BiFunction<? super K, ? super V, ? extends U> transformer;
        final BiFunction<? super U, ? super U, ? extends U> reducer;
        U result;
        MapReduceMappingsTask<K,V,U> rights, nextRight;
    }
    static final class MapReduceKeysToDoubleTask<K,V> extends BulkTask<K,V,Double> {
        final ToDoubleFunction<? super K> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        MapReduceKeysToDoubleTask<K,V> rights, nextRight;
    }
    static final class MapReduceValuesToDoubleTask<K,V> extends BulkTask<K,V,Double> {
        final ToDoubleFunction<? super V> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        MapReduceValuesToDoubleTask<K,V> rights, nextRight;
    }
    static final class MapReduceEntriesToDoubleTask<K,V> extends BulkTask<K,V,Double> {
        final ToDoubleFunction<Map.Entry<K,V>> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        MapReduceEntriesToDoubleTask<K,V> rights, nextRight;
    }
    static final class MapReduceMappingsToDoubleTask<K,V> extends BulkTask<K,V,Double> {
        final ToDoubleBiFunction<? super K, ? super V> transformer;
        final DoubleBinaryOperator reducer;
        final double basis;
        double result;
        MapReduceMappingsToDoubleTask<K,V> rights, nextRight;
    }
    static final class MapReduceKeysToLongTask<K,V> extends BulkTask<K,V,Long> {
        final ToLongFunction<? super K> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        MapReduceKeysToLongTask<K,V> rights, nextRight;
    }
    static final class MapReduceValuesToLongTask<K,V> extends BulkTask<K,V,Long> {
        final ToLongFunction<? super V> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        MapReduceValuesToLongTask<K,V> rights, nextRight;
    }
    static final class MapReduceEntriesToLongTask<K,V> extends BulkTask<K,V,Long> {
        final ToLongFunction<Map.Entry<K,V>> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        MapReduceEntriesToLongTask<K,V> rights, nextRight;
    }
    static final class MapReduceMappingsToLongTask<K,V> extends BulkTask<K,V,Long> {
        final ToLongBiFunction<? super K, ? super V> transformer;
        final LongBinaryOperator reducer;
        final long basis;
        long result;
        MapReduceMappingsToLongTask<K,V> rights, nextRight;
    }
    static final class MapReduceKeysToIntTask<K,V> extends BulkTask<K,V,Integer> {
        final ToIntFunction<? super K> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        MapReduceKeysToIntTask<K,V> rights, nextRight;
    }
    static final class MapReduceValuesToIntTask<K,V> extends BulkTask<K,V,Integer> {
        final ToIntFunction<? super V> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        MapReduceValuesToIntTask<K,V> rights, nextRight;
    }
    static final class MapReduceEntriesToIntTask<K,V> extends BulkTask<K,V,Integer> {
        final ToIntFunction<Map.Entry<K,V>> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        MapReduceEntriesToIntTask<K,V> rights, nextRight;
    }
    static final class MapReduceMappingsToIntTask<K,V> extends BulkTask<K,V,Integer> {
        final ToIntBiFunction<? super K, ? super V> transformer;
        final IntBinaryOperator reducer;
        final int basis;
        int result;
        MapReduceMappingsToIntTask<K,V> rights, nextRight;
    }
    
    // 以下是内存偏移量，给Unsafe类处理使用
    
    private static final sun.misc.Unsafe U;
    private static final long SIZECTL;
    private static final long TRANSFERINDEX;
    private static final long BASECOUNT;
    private static final long CELLSBUSY;
    private static final long CELLVALUE;
    private static final long ABASE;
    private static final int ASHIFT;

    static {
        try {
            U = sun.misc.Unsafe.getUnsafe();
            Class<?> k = ConcurrentHashMap.class;
            SIZECTL = U.objectFieldOffset
                (k.getDeclaredField("sizeCtl"));
            TRANSFERINDEX = U.objectFieldOffset
                (k.getDeclaredField("transferIndex"));
            BASECOUNT = U.objectFieldOffset
                (k.getDeclaredField("baseCount"));
            CELLSBUSY = U.objectFieldOffset
                (k.getDeclaredField("cellsBusy"));
            Class<?> ck = CounterCell.class;
            CELLVALUE = U.objectFieldOffset
                (ck.getDeclaredField("value"));
            Class<?> ak = Node[].class;
            ABASE = U.arrayBaseOffset(ak);
            int scale = U.arrayIndexScale(ak);
            if ((scale & (scale - 1)) != 0)
                throw new Error("data type scale not a power of two");
            ASHIFT = 31 - Integer.numberOfLeadingZeros(scale);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
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
