#include <iostream>
#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <cmath>
#include <algorithm>
#include <climits>
#include <queue>
using namespace std;

const int N = 202;

struct DP {
    int i, j, k;
};
queue<DP> q;

int dp[N][N][2*N];
int in_q[N][N][2*N];
DP from[N][N][2*N];

char s1[N], s2[N];
int l1, l2;

void update(int t, int i, int j, int k, DP f) {
    if (t < dp[i][j][k]) {
        dp[i][j][k] = t;
        from[i][j][k] = f;
        if (in_q[i][j][k] == 0) {
            struct DP x = {i, j, k};
            q.push(x);
            in_q[i][j][k] = 1;
        }
    }
}

void spfa() {
    while(!q.empty()) q.pop();

    struct DP x = {0, 0, 0};
    q.push(x);
    dp[0][0][0] = 0;
    in_q[0][0][0] = 1;

    while (!q.empty()) {
        DP x = q.front();
        q.pop();
        in_q[x.i][x.j][x.k] = 0;

        if (x.i == l1 && x.j == l2 && x.k == 0) break;

        int t = dp[x.i][x.j][x.k] + 1;

        /* for ( */
        if (x.i < l1 && s1[x.i] == '(' && x.j < l2 && s2[x.j] == '(') {
            update(t, x.i+1, x.j+1, x.k+1, x);
        }
        if (x.i < l1 && s1[x.i] == '(') {
            update(t, x.i+1, x.j, x.k+1, x);
        }
        if (x.j < l2 && s2[x.j] == '(') {
            update(t, x.i, x.j+1, x.k+1, x);
        }
        update(t, x.i, x.j, x.k+1, x);

        /* for ) */
        if (x.k > 0) {
            if (x.i < l1 && s1[x.i] == ')' && x.j < l2 && s2[x.j] == ')') {
                update(t, x.i+1, x.j+1, x.k-1, x);
            }
            if (x.i < l1 && s1[x.i] == ')') {
                update(t, x.i+1, x.j, x.k-1, x);
            }
            if (x.j < l2 && s2[x.j] == ')') {
                update(t, x.i, x.j+1, x.k-1, x);
            }
            update(t, x.i, x.j, x.k-1, x);
        }
    }
}

int main() {
    cin >> s1 >> s2;
    l1 = (int) strlen(s1);
    l2 = (int) strlen(s2);

    for (int i = 0; i < N; i ++) {
        for (int j = 0; j < N; j ++) {
            for (int k = 0; k < N; k ++) {
                dp[i][j][k] = INT_MAX;
                in_q[i][j][k] = 0;
            }
        }
    }

    spfa();

    string ans;
    int i = l1, j = l2, k = 0;
    while (i != 0 || j !=0 || k != 0) {
        DP f = from[i][j][k];
        ans = ans + (f.k < k ? "(" : ")");
        i = f.i;
        j = f.j;
        k = f.k;
    }
    reverse(ans.begin(), ans.end());
    cout << ans << endl;

    return 0;
}
