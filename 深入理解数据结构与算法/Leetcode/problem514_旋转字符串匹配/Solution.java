class Solution {

    private int lenRing;
    private int lenKey;
    private int[][] dp;

    private int min(int a, int b) {
        return a == -1 ? b : (b == -1 ? a : Integer.min(a, b));
    }

    private int dis(int i, int j) {
        return min(Math.abs(i - j), lenRing - Math.abs(i - j));
    }

    public int findRotateSteps(String ring, String key) {
        lenRing = ring.length();
        lenKey = key.length();
        dp = new int[lenKey][];
        for (int i = 0; i < lenKey; i ++)
            dp[i] = new int[lenRing];
        for (int i = 0; i < lenKey; i ++) {
            for (int j = 0; j < lenRing; j ++)
                dp[i][j] = -1;
        }

        for (int j = 0; j < lenRing; j ++) {
            if (key.charAt(0) == ring.charAt(j))
                dp[0][j] = dis(0, j);
        }
        for (int i = 1; i < lenKey; i ++) {
            for (int j0 = 0; j0 < lenRing; j0 ++) {
                if (dp[i-1][j0] != -1) {
                    for (int j1 = 0; j1 < lenRing; j1 ++) {
                        if (key.charAt(i) == ring.charAt(j1))
                            dp[i][j1] = min(dp[i][j1], dp[i-1][j0] + dis(j0, j1));
                    }
                }
            }
        }

        int ans = -1;
        for (int j = 0; j < lenRing; j ++) {
            ans = min(ans, dp[lenKey-1][j]);
        }
        ans = ans + lenKey;
        return ans;
    }
}
