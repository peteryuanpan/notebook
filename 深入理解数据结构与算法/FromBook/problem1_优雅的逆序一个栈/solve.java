package algorithm;

import java.util.Stack;

public class StackReverse {

    public static void dfs(Stack<Integer> s) {
        if (s.empty())
            return;
        int t = dfs2(s);
        dfs(s);
        s.push(t);
    }

    public static int dfs2(Stack<Integer> s) {
        int t = s.pop();
        if (!s.empty()) {
            int r = dfs2(s);
            s.push(t);
            return r;
        } else
            return t;
    }

    public static void main(String[] args) {
        Stack<Integer> s = new Stack<>();
        s.push(1);
        s.push(2);
        s.push(3);
        s.push(4);
        s.push(5);

        dfs(s);

        while (!s.empty()) {
            System.out.println(s.pop());
        }
    }
}

/* output
1
2
3
4
5
* */
