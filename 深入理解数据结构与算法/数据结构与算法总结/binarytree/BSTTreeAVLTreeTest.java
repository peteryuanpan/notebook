package binarytree;

import java.util.List;

public interface BinarySearchTree<K, V> {

    /**
     * Get value if key exists or return null
     * @param key
     * @return
     */
    V get(K key);

    /**
     * If key not exists, put value and return<br/>
     * If key exists, overwrite old value and return new value<br/>
     * It is not allowed to put null key
     * @param key
     * @param value
     * @return
     */
    V put(K key, V value);

    /**
     * Put only if key is absent
     * @param key
     * @param value
     * @return
     */
    V putIfAbsent(K key, V value);

    /**
     * Put key, key
     * @param key
     * @return V
     */
    default V put(K key) {
        return put(key, (V) key);
    }

    /**
     * Put key, key only if key is absent
     * @param key
     * @return
     */
    default V putIfAbsent(K key) {
        return putIfAbsent(key, (V) key);
    }

    /**
     * Remove key and return value if key exists or return null
     * @param key
     * @return
     */
    V remove(K key);

    /**
     * Clear all nodes, size -> 0
     */
    void clear();

    /**
     * Return numbers of nodes
     * @return
     */
    int size();

    /**
     * Return height of tres
     * @return
     */
    int height();

    /**
     * Return list of Entry K, V in Pre-order
     * @return
     */
    List<Entry<K, V>> entryList();

    /**
     * Entry K, V
     * @param <K>
     * @param <V>
     */
    interface Entry<K, V> {

        K key();

        V value();

        Entry<K, V> left();

        Entry<K, V> right();

        int size();

        int height();
    }
}
