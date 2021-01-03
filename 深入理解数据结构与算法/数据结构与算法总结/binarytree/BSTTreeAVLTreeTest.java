package binarytree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BSTTreeAVLTreeTest {

    static BinarySearchTree<Integer, Integer> BSTTree = new BSTTree<>();
    static BinarySearchTree<Integer, Integer> AVLTree = new AVLTree<>();

    static void print(List<Integer> list) {
        list.forEach(e -> System.out.print(e + " "));
        System.out.println();
    }

    static void print(List<Integer> putValues, List<Integer> removeValues, List<Integer> checkValues, List<Integer> keys) {
        print(putValues);
        print(removeValues);
        print(checkValues);
        print(keys);
    }

    static void check(BinarySearchTree<Integer, Integer> tree, List<Integer> putValues, List<Integer> removeValues, List<Integer> checkValues) {
        tree.clear();
        putValues.forEach(tree::put);
        removeValues.forEach(tree::remove);
        List<BinarySearchTree.Entry<Integer, Integer>> entries = tree.entryList();
        List<Integer> keys = entries.stream().map(BinarySearchTree.Entry::getKey).collect(Collectors.toList());
        if (entries.size() != checkValues.size()) {
            print(putValues, removeValues, checkValues, keys);
            throw new RuntimeException("entries.size() " + entries.size() + " not equals checkValues.size() " + checkValues.size());
        }
        if (tree.size() != checkValues.size()) {
            print(putValues, removeValues, checkValues, keys);
            throw new RuntimeException("tree.size() " + tree.size() + " not equals checkValues.size() " + checkValues.size());
        }
        for (int i = 0; i < entries.size(); i ++) {
            if (!checkValues.get(i).equals(entries.get(i).getKey())) {
                print(putValues, removeValues, checkValues, keys);
                throw new RuntimeException("i " + i + " checkValues.get(i) " + checkValues.get(i) + " not equals entries.get(i).getKey() " + entries.get(i).getKey());
            }
        }
        System.out.println("check done");
    }

    static void BSTTreeSimpleTest() {
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

    static void AVLTreeSimpleTest() {
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

    static void linearDataCheck(BinarySearchTree<Integer, Integer> tree, int deep) {
        long begin = System.currentTimeMillis();
        tree.clear();
        for (int i = 1; i <= deep; i ++) {
            tree.put(i);
        }
        long duration = System.currentTimeMillis() - begin;
        System.out.println("deep: " + deep);
        System.out.println("duration: " + duration + "ms");
    }

    static void linearDataTest() {
        System.out.println("--------linearDataCheck(AVLTree)--------");
        linearDataCheck(AVLTree, 10000);
        linearDataCheck(AVLTree, 50000);
        linearDataCheck(AVLTree, 100000);
        System.out.println("--------linearDataCheck(BSTTree)--------");
        linearDataCheck(BSTTree, 10000);
        linearDataCheck(BSTTree, 50000);
        linearDataCheck(BSTTree, 100000);
    }

    public static void main(String[] args) {
        BSTTreeSimpleTest();
        AVLTreeSimpleTest();
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
--------linearDataCheck(AVLTree)--------
deep: 10000
duration: 7ms
deep: 50000
duration: 17ms
deep: 100000
duration: 15ms
--------linearDataCheck(BSTTree)--------
deep: 10000
duration: 276ms
deep: 50000
duration: 7232ms
deep: 100000
duration: 34885ms
*/
