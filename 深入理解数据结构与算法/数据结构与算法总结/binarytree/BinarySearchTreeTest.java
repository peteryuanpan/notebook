package binarytree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * VM option: -Xss100M
 */
public class BinarySearchTreeTest {

    private static final BinarySearchTree<Integer, Integer> BSTTree = new BSTTree<>();
    private static final BinarySearchTree<Integer, Integer> AVLTree = new AVLTree<>();
    private static BinarySearchTree<Integer, Integer> RBTree = new RBTree<>();
    private static final BinarySearchTree<Integer, Integer> TreeMap = new TreeMapDecorator<>();

    private static void print(List<Integer> list) {
        list.forEach(e -> System.out.print(e + " "));
        System.out.println();
    }

    @SafeVarargs
    private static void print(List<Integer>... lists) {
        for (List<Integer> list : lists)
            print(list);
    }

    private static void simpleDataCheck(BinarySearchTree<Integer, Integer> tree, List<Integer> putKeys, List<Integer> removeKeys, List<Integer> checkKeys) {
        tree.clear();
        putKeys.forEach(tree::put);
        removeKeys.forEach(tree::remove);
        List<BinarySearchTree.Entry<Integer, Integer>> entries = tree.entryList();
        List<Integer> keys = entries.stream().map(BinarySearchTree.Entry::key).collect(Collectors.toList());
        try {
            if (entries.size() != checkKeys.size())
                throw new RuntimeException("entries.size() " + entries.size() + " not equals checkValues.size() " + checkKeys.size());
            if (tree.size() != checkKeys.size())
                throw new RuntimeException("tree.size() " + tree.size() + " not equals checkValues.size() " + checkKeys.size());
            for (int i = 0; i < entries.size(); i ++) {
                if (!checkKeys.get(i).equals(entries.get(i).key()))
                    throw new RuntimeException("i " + i + " checkValues.get(i) " + checkKeys.get(i) + " not equals entries.get(i).getKey() " + entries.get(i).key());
            }
            System.out.println("check done");
        } catch (Exception e) {
            print(putKeys, removeKeys, checkKeys, keys);
            throw e;
        }
    }

    public static void simpleDataTestBSTTree() {
        System.out.println("--------SimpleDataTest(BSTTree)--------");
        simpleDataCheck(BSTTree, Arrays.asList(3, 2, 1), Collections.emptyList(), Arrays.asList(3, 2, 1));
        simpleDataCheck(BSTTree, Arrays.asList(3, 1, 2), Collections.singletonList(4), Arrays.asList(3, 1, 2));
        simpleDataCheck(BSTTree, Arrays.asList(1, 2, 3), Collections.emptyList(), Arrays.asList(1, 2, 3));
        simpleDataCheck(BSTTree, Arrays.asList(1, 3, 2), Collections.singletonList(4), Arrays.asList(1, 3, 2));
        simpleDataCheck(BSTTree,
            Arrays.asList(7, 4, 8, 9, 2, 1, 6, 5, 3),
            Arrays.asList(4, 3),
            Arrays.asList(7, 2, 1, 6, 5, 8, 9)
        );
        simpleDataCheck(BSTTree,
            Arrays.asList(7, 3, 5, 2, 1, 8, 6, 9, 10),
            Arrays.asList(8, 2, 3, 7),
            Arrays.asList(6, 1, 5, 9, 10)
        );
    }

    public static void simpleDataTestAVLTree() {
        System.out.println("--------SimpleDataTest(AVLTree)--------");
        simpleDataCheck(AVLTree, Arrays.asList(3, 2, 1), Collections.emptyList(), Arrays.asList(2, 1, 3));
        simpleDataCheck(AVLTree, Arrays.asList(3, 1, 2), Collections.singletonList(4), Arrays.asList(2, 1, 3));
        simpleDataCheck(AVLTree, Arrays.asList(1, 2, 3), Collections.emptyList(), Arrays.asList(2, 1, 3));
        simpleDataCheck(AVLTree, Arrays.asList(1, 3, 2), Collections.singletonList(4), Arrays.asList(2, 1, 3));
        simpleDataCheck(AVLTree,
            Arrays.asList(7, 4, 8, 9, 2, 1, 6, 5, 3),
            Arrays.asList(4, 3),
            Arrays.asList(7, 2, 1, 5, 6, 8, 9)
        );
        simpleDataCheck(AVLTree,
            Arrays.asList(7, 3, 5, 2, 1, 8, 6, 9, 10),
            Arrays.asList(8, 2, 3, 7),
            Arrays.asList(6, 5, 1, 9, 10)
        );
    }

    private static void simpleDataCheckRBTree(List<Integer> putKeys, List<Integer> removeKeys, List<Integer> checkKeys, List<Integer> checkColors) {
        RBTree.clear();
        putKeys.forEach(RBTree::put);
        removeKeys.forEach(RBTree::remove);
        List<BinarySearchTree.Entry<Integer, Integer>> entries = RBTree.entryList();
        List<Integer> keys = entries.stream().map(BinarySearchTree.Entry::key).collect(Collectors.toList());
        List<Integer> colors = entries.stream().map(e -> ((RBTree<Integer, Integer>.Node) e).getColor() ? 1 : 0).collect(Collectors.toList());
        try {
            if (entries.size() != checkKeys.size())
                throw new RuntimeException("entries.size() " + entries.size() + " not equals checkValues.size() " + checkKeys.size());
            if (RBTree.size() != checkKeys.size())
                throw new RuntimeException("tree.size() " + RBTree.size() + " not equals checkValues.size() " + checkKeys.size());
            for (int i = 0; i < entries.size(); i ++) {
                if (!checkKeys.get(i).equals(entries.get(i).key()))
                    throw new RuntimeException("i " + i + " checkValues.get(i) " + checkKeys.get(i) + " not equals entries.get(i).getKey() " + entries.get(i).key());
            }
            for (int i = 0; i < entries.size(); i ++) {
                if (!checkColors.get(i).equals(colors.get(i)))
                    throw new RuntimeException("i " + i + " checkColors.get(i) " + checkColors.get(i) + " not equals colors.get(i) " + colors.get(i));
            }
            System.out.println("check done");
        } catch (Exception e) {
            print(putKeys, removeKeys, checkKeys, keys, checkColors, colors);
            throw e;
        }
    }

    public static void simpleDataTestRBTree() {
        System.out.println("--------SimpleDataTest(RBTree)--------");
        simpleDataCheckRBTree(Arrays.asList(3, 2, 1), Collections.emptyList(), Arrays.asList(2, 1, 3), Arrays.asList(1, 0, 0));
        simpleDataCheckRBTree(Arrays.asList(3, 1, 2), Collections.singletonList(4), Arrays.asList(2, 1, 3), Arrays.asList(1, 0, 0));
        simpleDataCheckRBTree(Arrays.asList(1, 2, 3), Collections.emptyList(), Arrays.asList(2, 1, 3), Arrays.asList(1, 0, 0));
        simpleDataCheckRBTree(Arrays.asList(1, 3, 2), Collections.singletonList(4), Arrays.asList(2, 1, 3), Arrays.asList(1, 0, 0));
        simpleDataCheckRBTree(
            Arrays.asList(7, 4, 8, 9, 2, 1, 6, 5, 3),
            Collections.emptyList(),
            Arrays.asList(5, 2, 1, 4, 3, 7, 6, 8, 9),
            Arrays.asList(1, 0, 1, 1, 0, 0, 1, 1, 0)
        );
        simpleDataCheckRBTree(
            Arrays.asList(11, 14, 2, 15, 1, 7, 5, 8, 4),
            Collections.emptyList(),
            Arrays.asList(7, 2, 1, 5, 4, 11, 8, 14, 15),
            Arrays.asList(1, 0, 1, 1, 0, 0, 1, 1, 0)
        );
        simpleDataCheckRBTree(
            Arrays.asList(11, 14, 2, 15, 1, 7, 5, 8, 4),
            Arrays.asList(7, 5, 14),
            Arrays.asList(4, 2, 1, 11, 8, 15),
            Arrays.asList(1, 1, 0, 0, 1, 1)
        );
        simpleDataCheckRBTree(
            Arrays.asList(15, 10, 17, 16, 18, 19, 6, 12, 3, 8, 1),
            Collections.emptyList(),
            Arrays.asList(15, 10, 6, 3, 1, 8, 12, 17, 16, 18, 19),
            Arrays.asList(1, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0)
        );
        simpleDataCheckRBTree(
            Arrays.asList(15, 10, 17, 16, 18, 19, 6, 12, 3, 8, 1),
            Arrays.asList(10, 19, 17, 18),
            Arrays.asList(8, 3, 1, 6, 15, 12, 16),
            Arrays.asList(1, 1, 1, 1, 1, 1, 1)
        );
    }

    private static final Map<Integer, int[]> randomDataMap = new HashMap<>();
    private static final Random random = new Random(new Random().nextInt(100));

    private static int[] getRandomData(int deep) {
        int[] randomData = randomDataMap.get(deep);
        if (randomData == null) {
            randomData = new int[deep+1];
            for (int i = 1; i <= deep; i ++) {
                randomData[i] = i;
            }
            for (int i = 1; i <= deep; i ++) {
                int t1 = 1 + random.nextInt(deep);
                int t2 = 1 + random.nextInt(deep);
                int t = randomData[t1];
                randomData[t1] = randomData[t2];
                randomData[t2] = t;
            }
            randomDataMap.put(deep, randomData);
        }
        return randomData;
    }

    private static void randomDataCheckRBTreeVSTreeMap(int deep) {
        int[] randomData = getRandomData(deep);
        RBTree.clear();
        TreeMap.clear();
        for (int i = 1; i <= deep; i ++) {
            RBTree.put(randomData[i]);
            TreeMap.put(randomData[i]);
        }
        for (int i = 1; i <= (deep / 4); i ++) {
            int t = 1 + random.nextInt(deep);
            RBTree.remove(randomData[t]);
            TreeMap.remove(randomData[t]);
        }
        List<BinarySearchTree.Entry<Integer, Integer>> elRBTree = RBTree.entryList();
        List<BinarySearchTree.Entry<Integer, Integer>> elTreeMap = TreeMap.entryList();
        try {
            if (elRBTree.size() != elTreeMap.size())
                throw new RuntimeException("elRBTree.size() " + elRBTree.size() + " != elTreeMap.size() " + elTreeMap.size());
            for (int i = 0; i < elRBTree.size(); i ++) {
                RBTree<Integer, Integer>.Node nodeRBTree = (RBTree<Integer, Integer>.Node) elRBTree.get(i);
                TreeMapDecorator<Integer, Integer>.Node nodeTreeMap = (TreeMapDecorator<Integer, Integer>.Node) elTreeMap.get(i);
                if (!nodeRBTree.key().equals(nodeTreeMap.key()))
                    throw new RuntimeException("nodeRBTree.key() " + nodeRBTree.key() + " != nodeTreeMap.key() " + nodeTreeMap.key());
                if (nodeRBTree.getColor() != nodeTreeMap.getColor())
                    throw new RuntimeException("nodeRBTree.getColor() " + nodeRBTree.getColor() + " != nodeTreeMap.getColor() " + nodeTreeMap.getColor());
            }
        } finally {
            System.out.println("check done with deep " + deep);
        }
    }

    public static void randomDataCrossTestRBTreeVSTreeMap() {
        System.out.println("--------RandomDataCrossTest(RBTree vs TreeMap)--------");
        BinarySearchTree<Integer, Integer> backup = RBTree;
        RBTree = new RBTree<>(false);
        for (int i = 1; i <= 10; i ++) {
            int deep = 100000 + random.nextInt(900000);
            randomDataCheckRBTreeVSTreeMap(deep);
        }
        RBTree = backup;
    }

    private static void randomDataCheck(BinarySearchTree<Integer, Integer> tree, int deep) {
        int[] randomData = getRandomData(deep);
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

    public static void randomDataTestBSTTree() {
        System.out.println("--------RandomDataTest(BSTTree)--------");
        randomDataCheck(BSTTree, 100000);
        randomDataCheck(BSTTree, 1000000);
        randomDataCheck(BSTTree, 10000000);
    }

    public static void randomDataTestAVLTree() {
        System.out.println("--------RandomDataTest(AVLTree)--------");
        randomDataCheck(AVLTree, 100000);
        randomDataCheck(AVLTree, 1000000);
        randomDataCheck(AVLTree, 10000000);
    }

    public static void randomDataTestRBTree() {
        System.out.println("--------RandomDataTest(RBTree)--------");
        randomDataCheck(RBTree, 100000);
        randomDataCheck(RBTree, 1000000);
        randomDataCheck(RBTree, 10000000);
    }

    public static void randomDataTestTreeMap() {
        System.out.println("--------RandomDataTest(TreeMap)--------");
        randomDataCheck(TreeMap, 100000);
        randomDataCheck(TreeMap, 1000000);
        randomDataCheck(TreeMap, 10000000);
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
        System.out.println("--------LinearDataCheck(BSTTree)--------");
        linearDataCheck(BSTTree, 10000);
        linearDataCheck(BSTTree, 50000);
        linearDataCheck(BSTTree, 100000);
    }

    public static void linearDataTestAVLTree() {
        System.out.println("--------LinearDataCheck(AVLTree)--------");
        linearDataCheck(AVLTree, 100000);
        linearDataCheck(AVLTree, 1000000);
        linearDataCheck(AVLTree, 10000000);
    }

    public static void linearDataTestRBTree() {
        System.out.println("--------LinearDataCheck(RBTree)--------");
        linearDataCheck(RBTree, 100000);
        linearDataCheck(RBTree, 1000000);
        linearDataCheck(RBTree, 10000000);
    }

    public static void linearDataTestTreeMap() {
        System.out.println("--------LinearDataCheck(TreeMap)--------");
        linearDataCheck(TreeMap, 100000);
        linearDataCheck(TreeMap, 1000000);
        linearDataCheck(TreeMap, 10000000);
    }

    public static void main(String[] args) {
        simpleDataTestBSTTree();
        simpleDataTestAVLTree();
        simpleDataTestRBTree();
        randomDataCrossTestRBTreeVSTreeMap();
        randomDataTestBSTTree();
        randomDataTestAVLTree();
        randomDataTestRBTree();
        randomDataTestTreeMap();
        linearDataTestBSTTree();
        linearDataTestAVLTree();
        linearDataTestRBTree();
        linearDataTestTreeMap();
    }
}

/*
 --------SimpleDataTest(BSTTree)--------
 check done
 check done
 check done
 check done
 check done
 check done
 --------SimpleDataTest(AVLTree)--------
 check done
 check done
 check done
 check done
 check done
 check done
 --------SimpleDataTest(RBTree)--------
 check done
 check done
 check done
 check done
 check done
 check done
 check done
 check done
 check done
 --------RandomDataCrossTest(RBTree vs TreeMap)--------
 check done with deep 460765
 check done with deep 144721
 check done with deep 615097
 check done with deep 392589
 check done with deep 691742
 check done with deep 867045
 check done with deep 787316
 check done with deep 425043
 check done with deep 262294
 check done with deep 128116
 --------RandomDataTest(BSTTree)--------
 Test put	[deep: 100000, size: 100000, height: 122, rotateCount: 0, duration: 25ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 0, duration: 24ms]
 Test put	[deep: 1000000, size: 1000000, height: 370, rotateCount: 0, duration: 556ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 0, duration: 574ms]
 Test put	[deep: 10000000, size: 10000000, height: 1054, rotateCount: 0, duration: 8374ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 0, duration: 7608ms]
 --------RandomDataTest(AVLTree)--------
 Test put	[deep: 100000, size: 100000, height: 20, rotateCount: 71189, duration: 44ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 91921, duration: 33ms]
 Test put	[deep: 1000000, size: 1000000, height: 24, rotateCount: 712645, duration: 587ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 918215, duration: 623ms]
 Test put	[deep: 10000000, size: 10000000, height: 28, rotateCount: 7131752, duration: 7649ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 9193733, duration: 6638ms]
 --------RandomDataTest(RBTree)--------
 Test put	[deep: 100000, size: 100000, height: 21, rotateCount: 59058, duration: 22ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 77929, duration: 31ms]
 Test put	[deep: 1000000, size: 1000000, height: 26, rotateCount: 593996, duration: 509ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 782581, duration: 468ms]
 Test put	[deep: 10000000, size: 10000000, height: 31, rotateCount: 5941069, duration: 7864ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 7833301, duration: 6643ms]
 --------RandomDataTest(TreeMap)--------
 Test put	[deep: 100000, size: 100000, height: 21, rotateCount: -1, duration: 21ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: -1, duration: 21ms]
 Test put	[deep: 1000000, size: 1000000, height: 26, rotateCount: -1, duration: 558ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: -1, duration: 557ms]
 Test put	[deep: 10000000, size: 10000000, height: 31, rotateCount: -1, duration: 7461ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: -1, duration: 6415ms]
 --------LinearDataCheck(BSTTree)--------
 Test put	[deep: 10000, size: 10000, height: 10000, rotateCount: 0, duration: 117ms]
 Test remove	[deep: 10000, size: 0, height: 0, rotateCount: 0, duration: 141ms]
 Test put	[deep: 50000, size: 50000, height: 50000, rotateCount: 0, duration: 2969ms]
 Test remove	[deep: 50000, size: 0, height: 0, rotateCount: 0, duration: 4523ms]
 Test put	[deep: 100000, size: 100000, height: 100000, rotateCount: 0, duration: 12494ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 0, duration: 15012ms]
 --------LinearDataCheck(AVLTree)--------
 Test put	[deep: 100000, size: 100000, height: 17, rotateCount: 99983, duration: 8ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 149967, duration: 16ms]
 Test put	[deep: 1000000, size: 1000000, height: 20, rotateCount: 999980, duration: 84ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 1499961, duration: 78ms]
 Test put	[deep: 10000000, size: 10000000, height: 24, rotateCount: 9999976, duration: 1137ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 14999953, duration: 764ms]
 --------LinearDataCheck(RBTree)--------
 Test put	[deep: 100000, size: 100000, height: 31, rotateCount: 99969, duration: 13ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: 149939, duration: 9ms]
 Test put	[deep: 1000000, size: 1000000, height: 37, rotateCount: 999963, duration: 227ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: 1499927, duration: 127ms]
 Test put	[deep: 10000000, size: 10000000, height: 44, rotateCount: 9999956, duration: 2197ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: 14999913, duration: 4178ms]
 --------LinearDataCheck(TreeMap)--------
 Test put	[deep: 100000, size: 100000, height: 31, rotateCount: -1, duration: 15ms]
 Test remove	[deep: 100000, size: 0, height: 0, rotateCount: -1, duration: 8ms]
 Test put	[deep: 1000000, size: 1000000, height: 37, rotateCount: -1, duration: 210ms]
 Test remove	[deep: 1000000, size: 0, height: 0, rotateCount: -1, duration: 104ms]
 Test put	[deep: 10000000, size: 10000000, height: 44, rotateCount: -1, duration: 2530ms]
 Test remove	[deep: 10000000, size: 0, height: 0, rotateCount: -1, duration: 736ms]
 */
