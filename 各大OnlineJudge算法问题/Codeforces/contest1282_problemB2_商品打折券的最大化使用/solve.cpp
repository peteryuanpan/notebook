#include <iostream>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <cmath>
#include <algorithm>
#include <climits>
using namespace std;

const int N = 200010;

int a[N];
int n, p, k;

int dp[N];

int solve() {
    sort(a + 1, a + 1 + n);
    for (int i = 1; i <= n; i ++) {
        dp[i] = -1;
    }
    dp[0] = p;
    for (int i = 1; i <= n; i ++) {
        int t;
        t = dp[i-1] - a[i];
        if (t >= 0) dp[i] = max(dp[i], t);
        if (i-k >= 0) {
            t = dp[i-k] - a[i];
            if (t >= 0) dp[i] = max(dp[i], t);
        }
    }
    int m = 0;
    for (int i = n; i >= 1; i --) {
        if (dp[i] >= 0) m = max(m, i);
    }
    return m;
}

int main() {
    int t;
    cin >> t;
    while (t --) {
        cin >> n >> p >> k;
        for (int i = 1; i <= n; i ++) {
            scanf("%d", a + i);
        }
        cout << solve() << endl;
    }
    return 0;
}
