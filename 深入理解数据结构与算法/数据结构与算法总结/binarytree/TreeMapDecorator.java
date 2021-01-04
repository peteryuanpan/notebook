package binarytree;

import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class TreeMapDecorator<K, V> implements BinarySearchTree<K, V> {

    private final TreeMap<K, V> treeMap = new TreeMap<>();

    @Override
    public V get(K key) {
        return treeMap.get(key);
    }

    @Override
    public V put(K key, V value) {
        return treeMap.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return treeMap.putIfAbsent(key, value);
    }

    @Override
    public V remove(K key) {
        return treeMap.remove(key);
    }

    @Override
    public void clear() {
        treeMap.clear();
    }

    @Override
    public int size() {
        return treeMap.size();
    }

    @Override
    public int height() {
        return -1;
    }

    @Override
    public List<Entry<K, V>> entryList() {
        return treeMap.entrySet().stream().map(entry -> new Entry<K, V>() {
            @Override
            public K key() {
                return entry.getKey();
            }

            @Override
            public V value() {
                return entry.getValue();
            }

            @Override
            public Entry<K, V> father() {
                return null;
            }

            @Override
            public Entry<K, V> left() {
                return null;
            }

            @Override
            public Entry<K, V> right() {
                return null;
            }

            @Override
            public int size() {
                return 0;
            }

            @Override
            public int height() {
                return 0;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public int rotateCount() {
        return -1;
    }
}
