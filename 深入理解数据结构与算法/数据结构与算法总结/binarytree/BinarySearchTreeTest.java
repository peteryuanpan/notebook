package binarytree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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

    public static void BSTTreeSimpleTest() {
        System.out.println("--------BSTTreeSimpleTest--------");
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

    public static void AVLTreeSimpleTest() {
        System.out.println("--------AVLTreeSimpleTest--------");
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

    private static void randomDataCheck(BinarySearchTree<Integer, Integer> tree, int deep) {
        int[] randomData = new int[deep + 1];
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
        System.out.println("deep: " + deep);
        System.out.println("size: " + tree.size());
        System.out.println("height: " + tree.height());
        System.out.println("duration: " + duration + "ms");
    }

    public static void randomDataTest() {
        System.out.println("--------randomDataTest(BSTTree)--------");
        randomDataCheck(BSTTree, 10000);
        randomDataCheck(BSTTree, 50000);
        randomDataCheck(BSTTree, 100000);
        linearDataCheck(AVLTree, 1000000);
        linearDataCheck(AVLTree, 10000000);
        System.out.println("--------randomDataTest(AVLTree)--------");
        randomDataCheck(AVLTree, 10000);
        randomDataCheck(AVLTree, 50000);
        randomDataCheck(AVLTree, 100000);
        linearDataCheck(AVLTree, 1000000);
        linearDataCheck(AVLTree, 10000000);
    }

    private static void linearDataCheck(BinarySearchTree<Integer, Integer> tree, int deep) {
        long begin = System.currentTimeMillis();
        tree.clear();
        for (int i = 1; i <= deep; i ++) {
            tree.put(i);
        }
        long duration = System.currentTimeMillis() - begin;
        tree.entryList(); // check
        System.out.println("deep: " + deep);
        System.out.println("size: " + tree.size());
        System.out.println("height: " + tree.height());
        System.out.println("duration: " + duration + "ms");
    }

    public static void linearDataTest() {
        System.out.println("--------linearDataCheck(BSTTree)--------");
        linearDataCheck(BSTTree, 10000);
        linearDataCheck(BSTTree, 50000);
        linearDataCheck(BSTTree, 100000);
        System.out.println("--------linearDataCheck(AVLTree)--------");
        linearDataCheck(AVLTree, 10000);
        linearDataCheck(AVLTree, 50000);
        linearDataCheck(AVLTree, 100000);
        linearDataCheck(AVLTree, 1000000);
        linearDataCheck(AVLTree, 10000000);
    }

    public static void main(String[] args) {
        BSTTreeSimpleTest();
        AVLTreeSimpleTest();
        randomDataTest();
        linearDataTest();
    }
}

/**
 --------BSTTreeSimpleTest--------
 check done
 check done
 check done
 check done
 check done
 check done
 --------AVLTreeSimpleTest--------
 check done
 check done
 check done
 check done
 check done
 check done
 --------randomDataTest(BSTTree)--------
 deep: 10000
 size: 10000
 height: 48
 duration: 7ms
 deep: 50000
 size: 50000
 height: 96
 duration: 16ms
 deep: 100000
 size: 100000
 height: 122
 duration: 45ms
 deep: 1000000
 size: 1000000
 height: 20
 duration: 294ms
 deep: 10000000
 size: 10000000
 height: 24
 duration: 3484ms
 --------randomDataTest(AVLTree)--------
 deep: 10000
 size: 10000
 height: 16
 duration: 7ms
 deep: 50000
 size: 50000
 height: 19
 duration: 31ms
 deep: 100000
 size: 100000
 height: 20
 duration: 39ms
 deep: 1000000
 size: 1000000
 height: 20
 duration: 294ms
 deep: 10000000
 size: 10000000
 height: 24
 duration: 3973ms
 --------linearDataCheck(BSTTree)--------
 deep: 10000
 size: 10000
 height: 10000
 duration: 318ms
 deep: 50000
 size: 50000
 height: 50000
 duration: 8688ms
 deep: 100000
 size: 100000
 height: 100000
 duration: 42953ms
 --------linearDataCheck(AVLTree)--------
 deep: 10000
 size: 10000
 height: 14
 duration: 2ms
 deep: 50000
 size: 50000
 height: 16
 duration: 14ms
 deep: 100000
 size: 100000
 height: 17
 duration: 27ms
 deep: 1000000
 size: 1000000
 height: 20
 duration: 379ms
 deep: 10000000
 size: 10000000
 height: 24
 duration: 4151ms
 */
