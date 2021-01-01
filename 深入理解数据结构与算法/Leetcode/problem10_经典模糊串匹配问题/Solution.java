class Solution {

    boolean dp[][];
    int n, m;

    public boolean isMatch(String s, String p) {
        // init
        s = "a" + s;
        p = "a" + p;
        n = s.length();
        m = p.length();
        dp = new boolean[n][];
        for (int i = 0; i < n; i ++) {
            dp[i] = new boolean[m];
            for (int j = 0; j < m; j ++)
                dp[i][j] = false;
        }
        // dp
        dp[0][0] = true;
        for (int i = 0; i < n; i ++) {
            for (int j = 0; j < m; j ++) {
                if (dp[i][j]) {
                    char s0 = s.charAt(i);
                    char p0 = p.charAt(j);
                    if (i + 1 < n && j - 1 >= 0) { // case0
                        char s1 = s.charAt(i+1);
                        char p_1 = p.charAt(j-1);
                        if (p0 == '*' && (p_1 == '.' || p_1 == s1))
                            dp[i+1][j] = true;
                    }
                    if (i + 1 < n && j + 1 < m) { // case1
                        char s1 = s.charAt(i+1);
                        char p1 = p.charAt(j+1);
                        if (p1 == '*') {
                            if (p0 != '*' && (p0 == '.' || p0 == s1))
                                dp[i+1][j+1] = true;
                        } else if (p1 == '.' || p1 == s1)
                            dp[i+1][j+1] = true;
                    }
                    if (j + 1 < m) { // case2
                        char p1 = p.charAt(j+1);
                        if (p0 != '*' && p1 == '*')
                            dp[i][j+1] = true;
                        if (j + 2 < m) { // case3
                            char p2 = p.charAt(j+2);
                            if (p1 != '*' && p2 == '*')
                                dp[i][j+2] = true;
                        }
                    }
                }
            }
        }
        // result
        return dp[n-1][m-1];
    }
}
