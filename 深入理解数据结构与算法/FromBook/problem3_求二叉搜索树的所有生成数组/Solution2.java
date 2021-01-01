/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution2 {

    private final int N = 11;

    private class Mask {
        List<Integer> mask = new ArrayList<>();
    };
    private Mask[][] masks = new Mask[N][N];

    public Solution() {
        for (int i = 0; i < N; i ++) {
            for (int j = 0; j < N; j ++) {
                masks[i][j] = new Mask();
            }
        }
        for (int l = 1; l < N; l ++) {
            for (int i = 0; i < (1<<l); i ++) {
                int n0 = 0;
                for (int j = 0; j < l; j ++) {
                    if ((i & (1<<j)) == 0)
                        n0 ++;
                }
                masks[n0][l-n0].mask.add(i);
            }
        }
    }

    private class Result {
        List<List<Integer>> dd;
        public Result() {
            dd = new ArrayList<>();
            dd.add(new ArrayList<>());
        }
    };

    private Result dfs(TreeNode root) {
        Result res = new Result();
        if (root != null) {
            Result left = dfs(root.left);
            Result right = dfs(root.right);
            int ln = left.dd.get(0).size();
            int rn = right.dd.get(0).size();
            res.dd.clear(); // important!
            if (ln == 0 && rn == 0) {
                List<Integer> d = new ArrayList<>();
                d.add(root.val);
                res.dd.add(d);
            } else if (ln == 0) {
                for (List<Integer> rd : right.dd) {
                    List<Integer> d = new ArrayList<>();
                    d.add(root.val);
                    d.addAll(rd);
                    res.dd.add(d);
                }
            } else if (rn == 0) {
                for (List<Integer> ld : left.dd) {
                    List<Integer> d = new ArrayList<>();
                    d.add(root.val);
                    d.addAll(ld);
                    res.dd.add(d);
                }
            } else {
                List<Integer> mask = masks[ln][rn].mask;
                for (List<Integer> ld : left.dd) {
                    for (List<Integer> rd : right.dd) {
                        for (Integer i : mask) {
                            List<Integer> d = new ArrayList<>();
                            d.add(root.val);
                            int tl = 0, tr = 0;
                            for (int j = 0; j < (ln + rn); j ++) {
                                if ((i & (1<<j)) == 0) {
                                    d.add(ld.get(tl));
                                    tl ++;
                                } else {
                                    d.add(rd.get(tr));
                                    tr ++;
                                }
                            }
                            res.dd.add(d);
                        }
                    }
                }
            }
        }
        return res;
    }

    public List<List<Integer>> BSTSequences(TreeNode root) {
        return dfs(root).dd;
    }
}
