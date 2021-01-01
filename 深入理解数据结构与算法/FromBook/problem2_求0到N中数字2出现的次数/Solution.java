class Solution {

    final int N = 12;
    int[][] dp = new int[N][N];
    int[] a = new int[N];
    int[] ten = new int[N];

    public Solution() {
        for (int i = 0; i < N; i ++) {
            for (int j = 0; j < N; j ++)
                dp[i][j] = 0;
        }
        for (int i = 0; i < N; i ++) {
            a[i] = 0;
        }
        int t = 1;
        for (int i = 0; i < N; i ++) {
            ten[i] = t;
            t = t * 10;
        }
    }

    private int length(int n) {
        if (n == 0)
            return 1;
        int res = 0;
        while (n != 0) {
            res ++;
            a[res] = n % 10;
            n = n / 10;
        }
        return res;
    }

    public int numberOf2sInRange(int n) {
        // init
        dp[1][2] = 1;
        // dp
        for (int i = 2; i <= 10; i ++) {
            int sum = 0;
            for (int k = 0; k <= 9; k ++) {
                sum += dp[i-1][k];
            }
            for (int j = 0; j <= 9; j ++) {
                dp[i][j] = sum + (j == 2 ? ten[i-1] : 0);
            }
        }
        // ans
        int ans = 0;
        int l = length(n);
        int num2 = 0;
        for (int i = l; i >= 1; i --) {
            for (int j = 0; j < a[i]; j ++) {
                ans += dp[i][j];
                ans += num2 * ten[i-1];
            }
            if (a[i] == 2) {
                num2 += 1;
            }
        }
        ans += num2;
        return ans;
    }
}
