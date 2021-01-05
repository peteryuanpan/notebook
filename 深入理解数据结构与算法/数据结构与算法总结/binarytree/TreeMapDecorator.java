package binarytree;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class TreeMapDecorator<K, V> implements BinarySearchTree<K, V> {

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
        return heightOf(buildTree(root()));
    }

    private int heightOf(Node x) {
        return x == null ? 0 : 1 + Integer.max(heightOf(x.left), heightOf(x.right));
    }

    private final List<Entry<K, V>> entryList = new ArrayList<>();

    @Override
    public List<Entry<K, V>> entryList() {
        Node root = buildTree(root());
        entryList.clear();
        checkAll(root);
        return new ArrayList<>(entryList);
    }

    private void checkAll(Node x) {
        if (x != null) {
            entryList.add(x);
            checkAll(x.left);
            checkAll(x.right);
        }
    }

    private Node buildTree(Object x) {
        if (x != null) {
            Node now = new Node(keyOf(x), valueOf(x), colorOf(x));
            Node left = buildTree(leftOf(x));
            Node right = buildTree(rightOf(x));
            now.left = left;
            now.right = right;
            if (left != null)
                left.father = now;
            if (right != null)
                right.father = now;
            return now;
        }
        return null;
    }

    @Override
    public int rotateCount() {
        return -1;
    }

    public class Node implements Entry<K, V> {

        private K key;
        private V value;
        private Node left;
        private Node right;
        private Node father;
        private boolean color;

        Node(K key, V value, Boolean color) {
            this.key = key;
            this.value = value;
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

    private final TreeMap<K, V> treeMap = new TreeMap<>();
    
    private static Field rootField;
    private static Field keyField;
    private static Field valueField;
    private static Field leftField;
    private static Field rightField;
    private static Field colorField;

    private Object root() {
        try {
            return rootField.get(treeMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private K keyOf(Object x) {
        try {
            return (K) keyField.get(x);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private V valueOf(Object x) {
        try {
            return (V) valueField.get(x);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object leftOf(Object x) {
        try {
            return leftField.get(x);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object rightOf(Object x) {
        try {
            return rightField.get(x);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Boolean colorOf(Object x) {
        try {
            return (Boolean) colorField.get(x);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    static {
        try {
            Class<?> treeMapClass = Class.forName("java.util.TreeMap");
            rootField = treeMapClass.getDeclaredField("root");
            rootField.setAccessible(true);
            Class<?>[] declaredClasses = treeMapClass.getDeclaredClasses();
            for (Class<?> clazz : declaredClasses) {
                if ("java.util.TreeMap$Entry".equals(clazz.getName())) {
                    keyField = clazz.getDeclaredField("key");
                    keyField.setAccessible(true);
                    valueField = clazz.getDeclaredField("value");
                    valueField.setAccessible(true);
                    leftField = clazz.getDeclaredField("left");
                    leftField.setAccessible(true);
                    rightField = clazz.getDeclaredField("right");
                    rightField.setAccessible(true);
                    colorField = clazz.getDeclaredField("color");
                    colorField.setAccessible(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
