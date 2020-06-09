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

char s1[N], s2[N];
int l1, l2;

void update(int t, int i, int j, int k) {
    if (t < dp[i][j][k]) {
        dp[i][j][k] = t;
        if (in_q[i][j][k] == 0) {
            struct DP x = {i, j, k};
            q.push(x);
            in_q[i][j][k] = 1;
        }
    }
}

void spfa() {
    struct DP x = {0, 0, 0};
    q.push(x);
    dp[0][0][0] = 0;
    in_q[0][0][0] = 1;

    while (!q.empty()) {
        DP x = q.front();
        q.pop();
        in_q[x.i][x.j][x.k] = 0;

        int t = dp[x.i][x.j][x.k] + 1;

        /* for ( */
        if (x.i < l1 && s1[x.i] == '(' && x.j < l2 && s2[x.j] == '(') {
            update(t, x.i+1, x.j+1, x.k+1);
        }
        if (x.i < l1 && s1[x.i] == '(') {
            update(t, x.i+1, x.j, x.k+1);
        }
        if (x.j < l2 && s2[x.j] == '(') {
            update(t, x.i, x.j+1, x.k+1);
        }
        update(t, x.i, x.j, x.k+1);

        /* for ) */
        if (x.k > 0) {
            if (x.i < l1 && s1[x.i] == ')' && x.j < l2 && s2[x.j] == ')') {
                update(t, x.i+1, x.j+1, x.k-1);
            }
            if (x.i < l1 && s1[x.i] == ')') {
                update(t, x.i+1, x.j, x.k-1);
            }
            if (x.j < l2 && s2[x.j] == ')') {
                update(t, x.i, x.j+1, x.k-1);
            }
            update(t, x.i, x.j, x.k-1);
        }
    }
}

void find_ans(int i, int j, int k) {
    if (i == 0 && j == 0 && k == 0) {
        return;
    }

    /* for ( */
    if (k > 0) {
        if (i > 0 && j > 0 && dp[i-1][j-1][k-1] + 1 == dp[i][j][k] && s1[i-1] == '(' && s2[j-1] == '(') {
            find_ans(i-1, j-1, k-1);
            printf("(");
            return;
        }
        if (i > 0 && dp[i-1][j][k-1] + 1 == dp[i][j][k] && s1[i-1] == '(') {
            find_ans(i-1, j, k-1);
            printf("(");
            return;
        }
        if (j > 0 && dp[i][j-1][k-1] + 1 == dp[i][j][k] && s2[j-1] == '(') {
            find_ans(i, j-1, k-1);
            printf("(");
            return;
        }
        if (dp[i][j][k-1] + 1 == dp[i][j][k]) {
            find_ans(i, j, k-1);
            printf("(");
            return;
        }
    }

    /* for ) */
    if (i > 0 && j > 0 && dp[i-1][j-1][k+1] + 1 == dp[i][j][k] && s1[i-1] == ')' && s2[j-1] == ')') {
        find_ans(i-1, j-1, k+1);
        printf(")");
        return;
    }
    if (i > 0 && dp[i-1][j][k+1] + 1 == dp[i][j][k] && s1[i-1] == ')') {
        find_ans(i-1, j, k+1);
        printf(")");
        return;
    }
    if (j > 0 && dp[i][j-1][k+1] + 1 == dp[i][j][k] && s2[j-1] == ')') {
        find_ans(i, j-1, k+1);
        printf(")");
        return;
    }
    if (dp[i][j][k+1] + 1 == dp[i][j][k]) {
        find_ans(i, j, k+1);
        printf(")");
        return;
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

    find_ans(l1, l2, 0);
    
    return 0;
}
