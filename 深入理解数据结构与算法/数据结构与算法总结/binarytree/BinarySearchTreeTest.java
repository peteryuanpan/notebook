package binarytree;

import java.util.*;
import java.util.stream.Collectors;

public class BinarySearchTreeTest {

    private static final BinarySearchTree<Integer, Integer> BSTTree = new BSTTree<>();
    private static final BinarySearchTree<Integer, Integer> AVLTree = new AVLTree<>();
    private static final BinarySearchTree<Integer, Integer> RBTree = new RBTree<>();

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

    public static void main(String[] args) {
        simpleTestBSTTree();
        simpleTestAVLTree();
        simpleTestRBTree();
        randomDataTestBSTree();
        randomDataTestAVLTree();
        linearDataTestBSTTree();
        linearDataTestAVLTree();
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
 Test put	[deep: 10000, size: 10000, height: 47, rotateCount: 0, duration: 6ms]
 Test remove	[deep: 10000, size: 0, height: 0, rotateCount: 0, duration: 4ms]
 Test put	[deep: 50000, size: 50000, height: 50, rotateCount: 0, duration: 15ms]
 Test remove	[deep: 50000, size: 0, height: 0, rotateCount: 0, duration: 16ms]
 Test put	[deep: 100000, size: 100000, height: 82, rotateCount: 0, duration: 42ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 0, duration: 40ms]
 Test put	[deep: 1000000, size: 1000000, height: 194, rotateCount: 0, duration: 889ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 0, duration: 668ms]
 Test put	[deep: 10000000, size: 10000000, height: 576, rotateCount: 0, duration: 11177ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 0, duration: 9814ms]
 --------randomDataTest(AVLTree)--------
 Test put	[deep: 10000, size: 10000, height: 16, rotateCount: 7161, duration: 7ms]
 Test remove	[deep: 10000, size: 0, height: 0, rotateCount: 9221, duration: 5ms]
 Test put	[deep: 50000, size: 50000, height: 19, rotateCount: 44763, duration: 19ms]
 Test remove	[deep: 50000, size: 0, height: 0, rotateCount: 54934, duration: 22ms]
 Test put	[deep: 100000, size: 100000, height: 20, rotateCount: 125976, duration: 32ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 146519, duration: 39ms]
 Test put	[deep: 1000000, size: 1000000, height: 24, rotateCount: 858283, duration: 755ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 1064521, duration: 613ms]
 Test put	[deep: 10000000, size: 10000000, height: 28, rotateCount: 8198214, duration: 13003ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 10264706, duration: 10120ms]
 --------linearDataCheck(BSTTree)--------
 Test put	[deep: 10000, size: 10000, height: 10000, rotateCount: 0, duration: 307ms]
 Test remove	[deep: 10000, size: 0, height: 0, rotateCount: 0, duration: 296ms]
 Test put	[deep: 50000, size: 50000, height: 50000, rotateCount: 0, duration: 8030ms]
 Test remove	[deep: 50000, size: 0, height: 0, rotateCount: 0, duration: 7828ms]
 Test put	[deep: 100000, size: 100000, height: 100000, rotateCount: 0, duration: 45064ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 0, duration: 44641ms]
 --------linearDataCheck(AVLTree)--------
 Test put	[deep: 10000, size: 10000, height: 14, rotateCount: 10274692, duration: 2ms]
 Test remove	[deep: 10000, size: 0, height: 0, rotateCount: 10279679, duration: 3ms]
 Test put	[deep: 50000, size: 50000, height: 16, rotateCount: 10329663, duration: 11ms]
 Test remove	[deep: 50000, size: 0, height: 0, rotateCount: 10354648, duration: 11ms]
 Test put	[deep: 100000, size: 100000, height: 17, rotateCount: 10454631, duration: 23ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 10504615, duration: 17ms]
 Test put	[deep: 1000000, size: 1000000, height: 20, rotateCount: 11504595, duration: 231ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 12004576, duration: 125ms]
 Test put	[deep: 10000000, size: 10000000, height: 24, rotateCount: 22004552, duration: 3037ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 27004529, duration: 1528ms]
 */
