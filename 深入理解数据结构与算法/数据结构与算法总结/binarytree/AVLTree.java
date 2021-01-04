package binarytree;

import java.util.ArrayList;
import java.util.List;

public class AVLTree<K, V> implements BinarySearchTree<K, V> {

    @Override
    public V get(K key) {
        return get(root, key);
    }

    @SuppressWarnings("unchecked")
    private V get(Node x, K key) {
        if (x != null) {
            Comparable<? super K> k = (Comparable<? super K>) key;
            int cmp = k.compareTo(x.key);
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

    private int getSize(Node x) {
        if (x == null)
            return 0;
        int ln = x.left == null ? 0 : x.left.size;
        int rn = x.right == null ? 0 : x.right.size;
        return 1 + ln + rn;
    }

    private int getHeight(Node x) {
        if (x == null)
            return 0;
        int lh = x.left == null ? 0 : x.left.height;
        int rh = x.right == null ? 0 : x.right.height;
        return 1 + Integer.max(lh, rh);
    }

    private void update(Node x) {
        x.size = getSize(x);
        x.height = getHeight(x);
    }

    private void rotateLeft(Node f, Node x) {
        Node right = x. right;
        Node right_left = x.right.left;
        if (f == null)
            root = right;
        else if (f.left == x)
            f.left = right;
        else
            f.right = right;
        right.left = x;
        x.right = right_left;
        update(x);
        update(right);
    }

    private void rotateRight(Node f, Node x) {
        Node left = x.left;
        Node left_right = x.left.right;
        if (f == null)
            root = left;
        else if (f.left == x)
            f.left = left;
        else
            f.right = left;
        left.right = x;
        x.left = left_right;
        x.size = getSize(x);
        x.height = getHeight(x);
        update(x);
        update(left);
    }

    private void rotate(Node f, Node x) {
        int lh = getHeight(x.left);
        int rh = getHeight(x.right);
        if (lh - rh > 1) {
            int cmp = getHeight(x.left.left) - getHeight(x.left.right);
            if (cmp >= 0)
                rotateRight(f, x);
            else {
                rotateLeft(x, x.left);
                rotateRight(f, x);
            }
        } else if (lh - rh < -1) {
            int cmp = getHeight(x.right.right) - getHeight(x.right.left);
            if (cmp >= 0)
                rotateLeft(f, x);
            else {
                rotateRight(x, x.right);
                rotateLeft(f, x);
            }
        } else
            update(x);
    }

    @Override
    public V put(K key, V value) {
        if (key == null)
            throw new NullPointerException("key can not be null");
        if (root == null) {
            root = new Node(key, value);
            return root.value;
        }
        return put(null, root, key, value, false);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (key == null)
            throw new NullPointerException("key can not be null");
        if (root == null) {
            root = new Node(key, value);
            return root.value;
        }
        return put(null, root, key, value, true);
    }

    @SuppressWarnings("unchecked")
    private V put(Node f, Node x, K key, V value, boolean onlyIfAbsent) {
        Comparable<? super K> k = (Comparable<? super K>) key;
        int cmp = k.compareTo(x.key);
        if (cmp == 0) {
            if (!onlyIfAbsent)
                x.value = value;
            return x.value;
        }
        try {
            if (cmp < 0) {
                if (x.left != null)
                    return put(x, x.left, key, value, onlyIfAbsent);
                else {
                    x.left = new Node(key, value);
                    return x.left.value;
                }
            } else {
                if (x.right != null)
                    return put(x, x.right, key, value, onlyIfAbsent);
                else {
                    x.right = new Node(key, value);
                    return x.right.value;
                }
            }
        } finally {
            rotate(f, x);
        }
    }

    @Override
    public V remove(K key) {
        if (root == null)
            return null;
        return remove(null, root, key);
    }

    @SuppressWarnings("unchecked")
    private V remove(Node f, Node x, K key) {
        try {
            Comparable<? super K> k = (Comparable<? super K>) key;
            int cmp = k.compareTo(x.key);
            if (cmp == 0) {
                if (x.left == null && x.right == null) {
                    if (f == null)
                        root = null;
                    else if (f.left == x)
                        f.left = null;
                    else
                        f.right = null;
                } else if (x.left == null) { // x.right != null
                    if (f == null)
                        root = x.right;
                    else if (f.left == x)
                        f.left = x.right;
                    else
                        f.right = x.right;
                } else if (x.right == null) { // x.left != null
                    if (f == null)
                        root = x.left;
                    else if (f.left == x)
                        f.left = x.left;
                    else
                        f.right = x.left;
                } else { // x.left != null && x.right != null
                    Node prev = getMaxNode(x.left);
                    remove(prev.key);
                    x.key = prev.key;
                    x.value = prev.value;
                }
                return x.value;
            }
            if (cmp < 0)
                return remove(x, x.left, key);
            else
                return remove(x, x.right, key);
        } finally {
            rotate(f, x);
        }
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public int size() {
        return root == null ? 0 : root.size;
    }

    @Override
    public int height() {
        return root == null ? 0 : root.height;
    }

    @Override
    public List<Entry<K, V>> entryList() {
        return entryList(root);
    }

    private List<Entry<K, V>> entryList(Node x) {
        check(x);
        List<Entry<K, V>> list = new ArrayList<>();
        if (x != null) {
            list.add(x);
            list.addAll(entryList(x.left));
            list.addAll(entryList(x.right));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private void check(Node x) {
        if (x != null) {
            Comparable<? super K> k = (Comparable<? super K>) x.key;
            int ln = 0;
            int lh = 0;
            if (x.left != null) {
                if (k.compareTo(x.left.key) < 0)
                    throw new RuntimeException("leftKey.compareTo(x.key) > 0");
                ln = x.left.size;
                lh = x.left.height;
            }
            int rn = 0;
            int rh = 0;
            if (x.right != null) {
                if (k.compareTo(x.right.key) > 0)
                    throw new RuntimeException("rightKey.compareTo(x.key) < 0");
                rn = x.right.size;
                rh = x.right.height;
            }
            if (x.size != (1 + ln + rn))
                throw new RuntimeException("x.size != (1 + ln + rn)");
            if (x.height != (1 + Integer.max(lh, rh)))
                throw new RuntimeException("x.height != (1 + Integer.max(lh, rh))");
            if (lh - rh > 1)
                throw new RuntimeException("lh - rh > 1");
            if (lh - rh < -1)
                throw new RuntimeException("lh - rh < -1");
        }
    }

    private Node root = null;

    class Node implements Entry<K, V> {

        private K key;
        private V value;
        private Node left;
        private Node right;
        private int size;
        private int height;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.size = 1;
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
        public int size() {
            return size;
        }

        @Override
        public int height() {
            return height;
        }
    }
}
