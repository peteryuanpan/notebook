/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode(int x) { val = x; }
 * }
 */
class Solution {

    private List<List<Integer>> res = new ArrayList<>();
    private Deque<TreeNode> deque = new ArrayDeque<>();
    private Stack<Integer> stack = new Stack<>();

    private void dfs() {
        if (deque.isEmpty()) {
            res.add(new ArrayList<>(stack));
            return;
        }
        int size = deque.size();
        for (int i = 0; i < size; i ++) {
            TreeNode node = deque.pollFirst();
            if (node.left != null)
                deque.addLast(node.left);
            if (node.right != null)
                deque.addLast(node.right);
            stack.add(node.val);

            dfs();

            stack.pop();
            if (node.right != null)
                deque.pollLast();
            if (node.left != null)
                deque.pollLast();
            deque.addLast(node);
        }
    }

    public List<List<Integer>> BSTSequences(TreeNode root) {
        if (root == null)
            res.add(new ArrayList<>());
        else {
            deque.addLast(root);
            dfs();
        }
        return res;
    }
}
