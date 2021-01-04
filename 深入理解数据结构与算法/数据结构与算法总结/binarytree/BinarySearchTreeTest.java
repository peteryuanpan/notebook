package binarytree;

import java.util.*;
import java.util.stream.Collectors;

public class BinarySearchTreeTest {

    private static final BinarySearchTree<Integer, Integer> BSTTree = new BSTTree<>();
    private static final BinarySearchTree<Integer, Integer> AVLTree = new AVLTree<>();
    private static final BinarySearchTree<Integer, Integer> RBTree = new RBTree<>();
    private static final BinarySearchTree<Integer, Integer> treeMap = new TreeMapDecorator<>();

    private static void print(List<Integer> list) {
        list.forEach(e -> System.out.print(e + " "));
        System.out.println();
    }

    private static void print(List<Integer> putValues, List<Integer> removeValues, List<Integer> checkValues, List<Integer> keys) {
        print(putValues);
        print(removeValues);
        print(checkValues);
        print(keys);
    }

    private static void check(BinarySearchTree<Integer, Integer> tree, List<Integer> putValues, List<Integer> removeValues, List<Integer> checkValues) {
        tree.clear();
        putValues.forEach(tree::put);
        removeValues.forEach(tree::remove);
        List<BinarySearchTree.Entry<Integer, Integer>> entries = tree.entryList();
        List<Integer> keys = entries.stream().map(BinarySearchTree.Entry::key).collect(Collectors.toList());
        if (entries.size() != checkValues.size()) {
            print(putValues, removeValues, checkValues, keys);
            throw new RuntimeException("entries.size() " + entries.size() + " not equals checkValues.size() " + checkValues.size());
        }
        if (tree.size() != checkValues.size()) {
            print(putValues, removeValues, checkValues, keys);
            throw new RuntimeException("tree.size() " + tree.size() + " not equals checkValues.size() " + checkValues.size());
        }
        for (int i = 0; i < entries.size(); i ++) {
            if (!checkValues.get(i).equals(entries.get(i).key())) {
                print(putValues, removeValues, checkValues, keys);
                throw new RuntimeException("i " + i + " checkValues.get(i) " + checkValues.get(i) + " not equals entries.get(i).getKey() " + entries.get(i).key());
            }
        }
        System.out.println("check done");
    }

    public static void simpleTestBSTTree() {
        System.out.println("--------SimpleTest(BSTTree)--------");
        check(BSTTree, Arrays.asList(3, 2, 1), Collections.emptyList(), Arrays.asList(3, 2, 1));
        check(BSTTree, Arrays.asList(3, 1, 2), Collections.emptyList(), Arrays.asList(3, 1, 2));
        check(BSTTree, Arrays.asList(1, 2, 3), Collections.emptyList(), Arrays.asList(1, 2, 3));
        check(BSTTree, Arrays.asList(1, 3, 2), Collections.emptyList(), Arrays.asList(1, 3, 2));
        check(BSTTree,
            Arrays.asList(7, 4, 8, 9, 2, 1, 6, 5, 3),
            Arrays.asList(4, 3),
            Arrays.asList(7, 2, 1, 6, 5, 8, 9)
        );
        check(BSTTree,
            Arrays.asList(7, 3, 5, 2, 1, 8, 6, 9, 10),
            Arrays.asList(8, 2, 3, 7),
            Arrays.asList(6, 1, 5, 9, 10)
        );
    }

    public static void simpleTestAVLTree() {
        System.out.println("--------SimpleTest(AVLTree)--------");
        check(AVLTree, Arrays.asList(3, 2, 1), Collections.emptyList(), Arrays.asList(2, 1, 3));
        check(AVLTree, Arrays.asList(3, 1, 2), Collections.emptyList(), Arrays.asList(2, 1, 3));
        check(AVLTree, Arrays.asList(1, 2, 3), Collections.emptyList(), Arrays.asList(2, 1, 3));
        check(AVLTree, Arrays.asList(1, 3, 2), Collections.emptyList(), Arrays.asList(2, 1, 3));
        check(AVLTree,
            Arrays.asList(7, 4, 8, 9, 2, 1, 6, 5, 3),
            Arrays.asList(4, 3),
            Arrays.asList(7, 2, 1, 5, 6, 8, 9)
        );
        check(AVLTree,
            Arrays.asList(7, 3, 5, 2, 1, 8, 6, 9, 10),
            Arrays.asList(8, 2, 3, 7),
            Arrays.asList(6, 5, 1, 9, 10)
        );
    }

    private static void simpleTestRBTree() {
        System.out.println("--------SimpleTest(RBTree)--------");

    }

    private static final Map<Integer, int[]> randomDataMap = new HashMap<>();

    private static int[] getRandomData(int deep) {
        return randomDataMap.computeIfAbsent(deep, d -> new int[d + 1]);
    }

    private static void randomDataCheck(BinarySearchTree<Integer, Integer> tree, int deep) {
        int[] randomData = getRandomData(deep);
        for (int i = 1; i <= deep; i ++) {
            randomData[i] = i;
        }
        Random random = new Random(new Random().nextInt(100));
        for (int i = 1; i <= deep; i ++) {
            int t1 = 1 + random.nextInt(deep);
            int t2 = 1 + random.nextInt(deep);
            int t = randomData[t1];
            randomData[t1] = randomData[t2];
            randomData[t2] = t;
        }
        long begin = System.currentTimeMillis();
        tree.clear();
        for (int i = 1; i <= deep; i ++) {
            tree.put(randomData[i]);
        }
        long duration = System.currentTimeMillis() - begin;
        tree.entryList(); // check
        System.out.printf("Test put\t[deep: %d, size: %d, height: %d, rotateCount: %d, duration: %dms]\n",
            deep, tree.size(), tree.height(), tree.rotateCount(), duration);
        begin = System.currentTimeMillis();
        for (int i = deep; i >= 1; i --) {
            tree.remove(randomData[i]);
        }
        duration = System.currentTimeMillis() - begin;
        tree.entryList(); // check
        System.out.printf("Test remove\t[deep: %d, size: %d, height: %d, rotateCount: %d, duration: %dms]\n",
            deep, tree.size(), tree.height(), tree.rotateCount(), duration);
    }

    public static void randomDataTestBSTree() {
        System.out.println("--------randomDataTest(BSTTree)--------");
        randomDataCheck(BSTTree, 10000);
        randomDataCheck(BSTTree, 50000);
        randomDataCheck(BSTTree, 100000);
        randomDataCheck(BSTTree, 1000000);
        randomDataCheck(BSTTree, 10000000);
    }

    public static void randomDataTestAVLTree() {
        System.out.println("--------randomDataTest(AVLTree)--------");
        randomDataCheck(AVLTree, 10000);
        randomDataCheck(AVLTree, 50000);
        randomDataCheck(AVLTree, 100000);
        randomDataCheck(AVLTree, 1000000);
        randomDataCheck(AVLTree, 10000000);
    }

    public static void randomDataTestTreeMap() {
        System.out.println("--------randomDataTest(TreeMap)--------");
        randomDataCheck(treeMap, 10000);
        randomDataCheck(treeMap, 50000);
        randomDataCheck(treeMap, 100000);
        randomDataCheck(treeMap, 1000000);
        randomDataCheck(treeMap, 10000000);
    }

    private static void linearDataCheck(BinarySearchTree<Integer, Integer> tree, int deep) {
        long begin = System.currentTimeMillis();
        tree.clear();
        for (int i = 1; i <= deep; i ++) {
            tree.put(i);
        }
        long duration = System.currentTimeMillis() - begin;
        tree.entryList(); // check
        System.out.printf("Test put\t[deep: %d, size: %d, height: %d, rotateCount: %d, duration: %dms]\n",
            deep, tree.size(), tree.height(), tree.rotateCount(), duration);
        begin = System.currentTimeMillis();
        for (int i = deep; i >= 1; i --) {
            tree.remove(i);
        }
        duration = System.currentTimeMillis() - begin;
        tree.entryList(); // check
        System.out.printf("Test remove\t[deep: %d, size: %d, height: %d, rotateCount: %d, duration: %dms]\n",
            deep, tree.size(), tree.height(), tree.rotateCount(), duration);
    }

    public static void linearDataTestBSTTree() {
        System.out.println("--------linearDataCheck(BSTTree)--------");
        linearDataCheck(BSTTree, 10000);
        linearDataCheck(BSTTree, 50000);
        linearDataCheck(BSTTree, 100000);
    }

    public static void linearDataTestAVLTree() {
        System.out.println("--------linearDataCheck(AVLTree)--------");
        linearDataCheck(AVLTree, 10000);
        linearDataCheck(AVLTree, 50000);
        linearDataCheck(AVLTree, 100000);
        linearDataCheck(AVLTree, 1000000);
        linearDataCheck(AVLTree, 10000000);
    }

    public static void linearDataTestTreeMap() {
        System.out.println("--------linearDataCheck(TreeMap)--------");
        linearDataCheck(treeMap, 10000);
        linearDataCheck(treeMap, 50000);
        linearDataCheck(treeMap, 100000);
        linearDataCheck(treeMap, 1000000);
        linearDataCheck(treeMap, 10000000);
    }

    public static void main(String[] args) {
        simpleTestBSTTree();
        simpleTestAVLTree();
        simpleTestRBTree();
        randomDataTestBSTree();
        randomDataTestAVLTree();
        randomDataTestTreeMap();
        linearDataTestBSTTree();
        linearDataTestAVLTree();
        linearDataTestTreeMap();
    }
}

/**
 --------SimpleTest(BSTTree)--------
 check done
 check done
 check done
 check done
 check done
 check done
 --------SimpleTest(AVLTree)--------
 check done
 check done
 check done
 check done
 check done
 check done
 --------SimpleTest(RBTree)--------
 --------randomDataTest(BSTTree)--------
 Test put	[deep: 10000, size: 10000, height: 49, rotateCount: 0, duration: 6ms]
 Test remove	[deep: 10000, size: 0, height: 0, rotateCount: 0, duration: 6ms]
 Test put	[deep: 50000, size: 50000, height: 55, rotateCount: 0, duration: 15ms]
 Test remove	[deep: 50000, size: 0, height: 0, rotateCount: 0, duration: 14ms]
 Test put	[deep: 100000, size: 100000, height: 69, rotateCount: 0, duration: 40ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 0, duration: 41ms]
 Test put	[deep: 1000000, size: 1000000, height: 245, rotateCount: 0, duration: 939ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 0, duration: 599ms]
 Test put	[deep: 10000000, size: 10000000, height: 631, rotateCount: 0, duration: 11254ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 0, duration: 9288ms]
 --------randomDataTest(AVLTree)--------
 Test put	[deep: 10000, size: 10000, height: 16, rotateCount: 7089, duration: 6ms]
 Test remove	[deep: 10000, size: 0, height: 0, rotateCount: 9197, duration: 5ms]
 Test put	[deep: 50000, size: 50000, height: 19, rotateCount: 44819, duration: 20ms]
 Test remove	[deep: 50000, size: 0, height: 0, rotateCount: 55065, duration: 22ms]
 Test put	[deep: 100000, size: 100000, height: 20, rotateCount: 126373, duration: 34ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 146896, duration: 36ms]
 Test put	[deep: 1000000, size: 1000000, height: 24, rotateCount: 859172, duration: 712ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 1064669, duration: 788ms]
 Test put	[deep: 10000000, size: 10000000, height: 28, rotateCount: 8195203, duration: 11614ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 10257501, duration: 9081ms]
 --------randomDataTest(TreeMap)--------
 Test put	[deep: 10000, size: 10000, height: -1, rotateCount: -1, duration: 5ms]
 Test remove	[deep: 10000, size: 0, height: -1, rotateCount: -1, duration: 4ms]
 Test put	[deep: 50000, size: 50000, height: -1, rotateCount: -1, duration: 14ms]
 Test remove	[deep: 50000, size: 0, height: -1, rotateCount: -1, duration: 12ms]
 Test put	[deep: 100000, size: 100000, height: -1, rotateCount: -1, duration: 21ms]
 Test remove	[deep: 100000, size: 0, height: -1, rotateCount: -1, duration: 21ms]
 Test put	[deep: 1000000, size: 1000000, height: -1, rotateCount: -1, duration: 546ms]
 Test remove	[deep: 1000000, size: 0, height: -1, rotateCount: -1, duration: 538ms]
 Test put	[deep: 10000000, size: 10000000, height: -1, rotateCount: -1, duration: 8521ms]
 Test remove	[deep: 10000000, size: 0, height: -1, rotateCount: -1, duration: 6178ms]
 --------linearDataCheck(BSTTree)--------
 Test put	[deep: 10000, size: 10000, height: 10000, rotateCount: 0, duration: 310ms]
 Test remove	[deep: 10000, size: 0, height: 0, rotateCount: 0, duration: 281ms]
 Test put	[deep: 50000, size: 50000, height: 50000, rotateCount: 0, duration: 8169ms]
 Test remove	[deep: 50000, size: 0, height: 0, rotateCount: 0, duration: 8908ms]
 Test put	[deep: 100000, size: 100000, height: 100000, rotateCount: 0, duration: 41518ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 0, duration: 43753ms]
 --------linearDataCheck(AVLTree)--------
 Test put	[deep: 10000, size: 10000, height: 14, rotateCount: 10267487, duration: 2ms]
 Test remove	[deep: 10000, size: 0, height: 0, rotateCount: 10272474, duration: 1ms]
 Test put	[deep: 50000, size: 50000, height: 16, rotateCount: 10322458, duration: 11ms]
 Test remove	[deep: 50000, size: 0, height: 0, rotateCount: 10347443, duration: 7ms]
 Test put	[deep: 100000, size: 100000, height: 17, rotateCount: 10447426, duration: 20ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 10497410, duration: 13ms]
 Test put	[deep: 1000000, size: 1000000, height: 20, rotateCount: 11497390, duration: 238ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 11997371, duration: 155ms]
 Test put	[deep: 10000000, size: 10000000, height: 24, rotateCount: 21997347, duration: 3257ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 26997324, duration: 1503ms]
 --------linearDataCheck(TreeMap)--------
 Test put	[deep: 10000, size: 10000, height: -1, rotateCount: -1, duration: 1ms]
 Test remove	[deep: 10000, size: 0, height: -1, rotateCount: -1, duration: 1ms]
 Test put	[deep: 50000, size: 50000, height: -1, rotateCount: -1, duration: 6ms]
 Test remove	[deep: 50000, size: 0, height: -1, rotateCount: -1, duration: 4ms]
 Test put	[deep: 100000, size: 100000, height: -1, rotateCount: -1, duration: 12ms]
 Test remove	[deep: 100000, size: 0, height: -1, rotateCount: -1, duration: 7ms]
 Test put	[deep: 1000000, size: 1000000, height: -1, rotateCount: -1, duration: 219ms]
 Test remove	[deep: 1000000, size: 0, height: -1, rotateCount: -1, duration: 104ms]
 Test put	[deep: 10000000, size: 10000000, height: -1, rotateCount: -1, duration: 2706ms]
 Test remove	[deep: 10000000, size: 0, height: -1, rotateCount: -1, duration: 731ms]
 */
