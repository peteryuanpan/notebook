package binarytree;

import java.util.ArrayList;
import java.util.List;

public class RBTree<K, V> implements BinarySearchTree<K, V> {

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

    private Node getMinNode(Node x) {
        if (x != null) {
            if (x.left != null)
                return getMinNode(x.left);
            else
                return x;
        }
        return null;
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
        rotateCount ++;
    }

    private void rotateRight(Node x) {
        Node left = x.left;
        Node left_right = x.left.right;
        updateFather(x, left);
        updateRight(left, x);
        updateLeft(x, left_right);
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
            root = new Node(key, value, null, BLACK);
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
                    x.left = new Node(key, value, x, RED);
                    size ++;
                    fixAfterPut(x.left);
                    return value;
                }
                x = x.left;
            } else {
                if (x.right == null) {
                    x.right = new Node(key, value, x, RED);
                    size ++;
                    fixAfterPut(x.right);
                    return value;
                }
                x = x.right;
            }
        }
    }

    private void fixAfterPut(Node x) {
        while (x.father != null && x.father.color == RED) {
            if (x.father.father.left == x.father) {
                Node uncle = x.father.father.right;
                if (uncle == null || uncle.color == BLACK) {
                    if (x.father.right == x) {
                        x = x.father;
                        rotateLeft(x);
                    }
                    x.father.father.color = RED;
                    x.father.color = BLACK;
                    rotateRight(x.father.father);
                    break;
                } else {
                    x.father.father.color = RED;
                    x.father.color = BLACK;
                    uncle.color = BLACK;
                    x = x.father.father;
                }
            } else {
                Node uncle = x.father.father.left;
                if (uncle == null || uncle.color == BLACK) {
                    if (x.father.left == x) {
                        x = x.father;
                        rotateRight(x);
                    }
                    x.father.father.color = RED;
                    x.father.color = BLACK;
                    rotateLeft(x.father.father);
                    break;
                } else {
                    x.father.father.color = RED;
                    x.father.color = BLACK;
                    uncle.color = BLACK;
                    x = x.father.father;
                }
            }
        }
        if (root != null)
            root.color = BLACK;
    }

    @Override
    public V remove(K key) {
        Node x = root;
        while (x != null) {
            int cmp = compare(key, x.key);
            if (cmp == 0) {
                V v = x.value;
                if (x.left == null && x.right == null){
                    fixAfterRemove(x);
                    updateFather(x, null);
                } else if (x.left == null) { // x must be BLACK Node, x.left must be RED Node
                    updateFather(x, x.right);
                    x.right.color = x.color;
                } else if (x.right == null) { // x must be BLACK Node, x.right must be RED Node
                    updateFather(x, x.left);
                    x.left.color = x.color;
                } else if (this.prev) {
                    Node prev = getMaxNode(x.left); // previous.right must be null
                    x.key = prev.key;
                    x.value = prev.value;
                    if (prev.left != null) { // previous must be BLACK Node, previous.left must be RED Node
                        updateFather(prev, prev.left);
                        prev.left.color = prev.color;
                    } else {
                        fixAfterRemove(prev);
                        updateFather(prev, null);
                    }
                } else {
                    Node suc = getMinNode(x.right); // successor.left must be null
                    x.key = suc.key;
                    x.value = suc.value;
                    if (suc.right != null) { // successor must be BLACK Node, successor.right must be RED Node
                        updateFather(suc, suc.right);
                        suc.right.color = suc.color;
                    } else {
                        fixAfterRemove(suc);
                        updateFather(suc, null);
                    }
                }
                size --;
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
        while (x.father != null && x.color == BLACK) {
            if (x.father.left == x) {
                Node bro = x.father.right; // BLACK Node x must have a brother
                if (bro.color == RED) { // change to brother.color == BLACK
                    x.father.color = RED;
                    bro.color = BLACK;
                    rotateLeft(x.father);
                    bro = x.father.right;
                }
                boolean bro_left_black = (bro.left == null || bro.left.color == BLACK);
                boolean bro_right_black = (bro.right == null || bro.right.color == BLACK);
                if (bro_left_black && bro_right_black) {
                    bro.color = RED;
                    if (x.father.color == RED) {
                        x.father.color = BLACK;
                        break;
                    }
                    x = x.father; // continue because nBlocks has changed
                } else {
                    if (bro_right_black) { // change to brother.right.color == RED
                        bro.color = RED;
                        bro.left.color = BLACK;
                        rotateRight(bro);
                        bro = x.father.right;
                    }
                    bro.color = x.father.color;
                    bro.right.color = BLACK;
                    x.father.color = BLACK;
                    rotateLeft(x.father);
                    break;
                }
            } else {
                Node bro = x.father.left; // BLACK Node x must have a brother
                if (bro.color == RED) { // change to brother.color == BLACK
                    x.father.color = RED;
                    bro.color = BLACK;
                    rotateRight(x.father);
                    bro = x.father.left;
                }
                boolean bro_left_black = (bro.left == null || bro.left.color == BLACK);
                boolean bro_right_black = (bro.right == null || bro.right.color == BLACK);
                if (bro_left_black && bro_right_black) {
                    bro.color = RED;
                    if (x.father.color == RED) {
                        x.father.color = BLACK;
                        break;
                    }
                    x = x.father; // continue because nBlocks has changed
                } else {
                    if (bro_left_black) { // change to brother.left.color == RED
                        bro.color = RED;
                        bro.right.color = BLACK;
                        rotateLeft(bro);
                        bro = x.father.left;
                    }
                    bro.color = x.father.color;
                    bro.left.color = BLACK;
                    x.father.color = BLACK;
                    rotateRight(x.father);
                    break;
                }
            }
        }
        if (root != null)
            root.color = BLACK;
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

    private int heightOf(Node x) {
        return x == null ? 0 : 1 + Integer.max(heightOf(x.left), heightOf(x.right));
    }

    private final List<Entry<K, V>> entryList = new ArrayList<>();

    @Override
    public List<Entry<K, V>> entryList() {
        entryList.clear();
        checkAll(null, root);
        return new ArrayList<>(entryList);
    }

    private int checkAll(Node f, Node x) {
        check(f, x);
        if (x != null) {
            entryList.add(x);
            int lnb = checkAll(x, x.left);
            int rnb = checkAll(x, x.right);
            if (lnb != rnb)
                throw new RuntimeException("lnb " + lnb + " != rnb " + rnb);
            return lnb + (x.color == BLACK ? 1 : 0);
        }
        return 0;
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
            if (f != null && f.color == RED && x.color == RED)
                throw new RuntimeException("f.color == RED && x.color == RED");
        }
    }

    @Override
    public int rotateCount() {
        return rotateCount;
    }

    private Node root = null;
    private int size = 0;
    private int rotateCount = 0;
    private final boolean prev;

    private static final boolean BLACK = true;
    private static final boolean RED = false;

    public RBTree() {
        this.prev = true;
    }

    public RBTree(boolean prev) {
        this.prev = prev;
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
        private boolean color;

        Node(K key, V value, Node father, boolean color) {
            this.key = key;
            this.value = value;
            this.father = father;
            this.color = color;
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

        public boolean getColor() {
            return color;
        }
    }
}
