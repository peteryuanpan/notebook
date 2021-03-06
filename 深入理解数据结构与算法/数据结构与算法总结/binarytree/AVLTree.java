package binarytree;

import java.util.ArrayList;
import java.util.List;

public class AVLTree<K, V> implements BinarySearchTree<K, V> {

    @Override
    public V get(K key) {
        return get(root, key);
    }

    private V get(Node x, K key) {
        if (x != null) {
            int cmp = compare(key, x.key);
            if (cmp == 0)
                return x.value;
            if (cmp < 0)
                return get(x.left, key);
            else
                return get(x.right, key);
        }
        return null;
    }

    private Node getMaxNode(Node x) {
        if (x != null) {
            if (x.right != null)
                return getMaxNode(x.right);
            else
                return x;
        }
        return null;
    }

    private void updateHeight(Node x) {
        x.height = 1 + Integer.max(heightOf(x.left), heightOf(x.right));
    }

    private void updateRoot(Node x) {
        root = x;
        if (x != null)
            x.father = null;
    }

    private void updateLeft(Node f, Node x) {
        if (f != null)
            f.left = x;
        if (x != null)
            x.father = f;
    }

    private void updateRight(Node f, Node x) {
        if (f != null)
            f.right = x;
        if (x != null)
            x.father = f;
    }

    private void updateFather(Node x, Node v) {
        if (x.father == null)
            updateRoot(v);
        else if (x.father.left == x)
            updateLeft(x.father, v);
        else
            updateRight(x.father, v);
    }

    private void rotateLeft(Node x) {
        Node right = x.right;
        Node right_left = x.right.left;
        updateFather(x, right);
        updateLeft(right, x);
        updateRight(x, right_left);
        updateHeight(x);
        updateHeight(right);
        rotateCount ++;
    }

    private void rotateRight(Node x) {
        Node left = x.left;
        Node left_right = x.left.right;
        updateFather(x, left);
        updateRight(left, x);
        updateLeft(x, left_right);
        updateHeight(x);
        updateHeight(left);
        rotateCount ++;
    }

    @Override
    public V put(K key, V value) {
        return put(key, value, false);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return put(key, value, true);
    }

    private V put(K key, V value, boolean onlyIfAbsent) {
        if (key == null)
            throw new NullPointerException("key can not be null");
        Node x = root;
        if (x == null) {
            root = new Node(key, value, null);
            size ++;
            return value;
        }
        while (true) {
            int cmp = compare(key, x.key);
            if (cmp == 0) {
                if (!onlyIfAbsent)
                    x.value = value;
                return value;
            }
            if (cmp < 0) {
                if (x.left == null) {
                    x.left = new Node(key, value, x);
                    size ++;
                    fixAfterPut(x);
                    return value;
                }
                x = x.left;
            } else {
                if (x.right == null) {
                    x.right = new Node(key, value, x);
                    size ++;
                    fixAfterPut(x);
                    return value;
                }
                x = x.right;
            }
        }
    }

    private void fixAfterPut(Node x) {
        while (x != null) {
            int oldH = heightOf(x);
            updateHeight(x);
            if (oldH == heightOf(x))
                break;
            Node f = x.father;
            int lh = heightOf(x.left);
            int rh = heightOf(x.right);
            if (lh - rh > 1) {
                int cmp = heightOf(x.left.left) - heightOf(x.left.right);
                if (cmp >= 0)
                    rotateRight(x);
                else {
                    rotateLeft(x.left);
                    rotateRight(x);
                }
            } else if (lh - rh < -1) {
                int cmp = heightOf(x.right.right) - heightOf(x.right.left);
                if (cmp >= 0)
                    rotateLeft(x);
                else {
                    rotateRight(x.right);
                    rotateLeft(x);
                }
            }
            x = f;
        }
    }

    @Override
    public V remove(K key) {
        Node x = root;
        while (x != null) {
            int cmp = compare(key, x.key);
            if (cmp == 0) {
                V v = x.value;
                if (x.left == null && x.right == null)
                    updateFather(x, null);
                else if (x.left == null)
                    updateFather(x, x.right);
                else if (x.right == null)
                    updateFather(x, x.left);
                else {
                    Node prev = getMaxNode(x.left);
                    updateFather(prev, prev.left); // prev.right must be null
                    x.key = prev.key;
                    x.value = prev.value;
                    x = prev;
                }
                size --;
                fixAfterRemove(x.father);
                return v;
            }
            if (cmp < 0)
                x = x.left;
            else
                x = x.right;
        }
        return null;
    }

    private void fixAfterRemove(Node x) {
        while (x != null) {
            int oldH = heightOf(x);
            updateHeight(x);
            Node f = x.father;
            int lh = heightOf(x.left);
            int rh = heightOf(x.right);
            if (lh - rh > 1) {
                int cmp = heightOf(x.left.left) - heightOf(x.left.right);
                if (cmp >= 0)
                    rotateRight(x);
                else {
                    rotateLeft(x.left);
                    rotateRight(x);
                }
            } else if (lh - rh < -1) {
                int cmp = heightOf(x.right.right) - heightOf(x.right.left);
                if (cmp >= 0)
                    rotateLeft(x);
                else {
                    rotateRight(x.right);
                    rotateLeft(x);
                }
            } else if (oldH == heightOf(x))
                break;
            x = f;
        }
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
        rotateCount = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int height() {
        return heightOf(root);
    }

    private final List<Entry<K, V>> entryList = new ArrayList<>();

    @Override
    public List<Entry<K, V>> entryList() {
        entryList.clear();
        checkAll(null, root);
        return new ArrayList<>(entryList);
    }

    private void checkAll(Node f, Node x) {
        check(f, x);
        if (x != null) {
            entryList.add(x);
            checkAll(x, x.left);
            checkAll(x, x.right);
        }
    }

    private void check(Node f, Node x) {
        if (x != null) {
            if (f == null && x.father != null)
                throw new RuntimeException("f == null && x.father != null");
            if (f != null && x.father != f)
                throw new RuntimeException("f != null && x.father != f");
            if (x.left != null && compare(x.key, x.left.key) < 0)
                throw new RuntimeException("compare(x.key, x.left.key) < 0");
            if (x.right != null && compare(x.key, x.right.key) > 0)
                throw new RuntimeException("compare(x.key, x.right.key) > 0");
            int lh = heightOf(x.left);
            int rh = heightOf(x.right);
            if (x.height != (1 + Integer.max(lh, rh)))
                throw new RuntimeException("x.height != (1 + Integer.max(lh, rh))");
            if (lh - rh > 1)
                throw new RuntimeException("lh - rh > 1");
            if (lh - rh < -1)
                throw new RuntimeException("lh - rh < -1");
        }
    }

    @Override
    public int rotateCount() {
        return rotateCount;
    }

    private Node root = null;
    private int size = 0;
    private int rotateCount = 0;

    private int heightOf(Node x) {
        return x == null ? 0 : x.height;
    }

    @SuppressWarnings("unchecked")
    private int compare(Object k1, Object k2) {
        return ((Comparable<? super K>) k1).compareTo((K)k2);
    }

    public class Node implements Entry<K, V> {

        private K key;
        private V value;
        private Node left;
        private Node right;
        private Node father;
        private int height;

        Node(K key, V value, Node father) {
            this.key = key;
            this.value = value;
            this.father = father;
            this.height = 1;
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public V value() {
            return value;
        }

        @Override
        public Entry<K, V> left() {
            return left;
        }

        @Override
        public Entry<K, V> right() {
            return right;
        }

        @Override
        public Entry<K, V> father() {
            return father;
        }
    }
}
