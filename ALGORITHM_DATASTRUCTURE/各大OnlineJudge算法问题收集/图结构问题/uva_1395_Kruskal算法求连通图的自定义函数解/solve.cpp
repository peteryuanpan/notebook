#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <algorithm>
#include <climits>
using namespace std;

const int N = 105;

struct Edge {
    int a;
    int b;
    int w;
} edge[N * N];

int compare(Edge e1, Edge e2) {
    return e1.w < e2.w;
}

int fa[N];

int get_father(int x) {
    if (fa[x] == x) return fa[x];
    else {
        fa[x] = get_father(fa[x]);
        return fa[x];
    }
}

void union_xy(int x, int y) {
    int fax = get_father(x);
    int fay = get_father(y);
    fa[fax] = fay;
}

int main () {
    int n, m;
    while (scanf("%d %d", &n, &m) != EOF) {
        if (n == 0 && m == 0) break;

        for (int i = 1; i <= m; i ++) {
            int a, b, w;
            scanf("%d %d %d", &a, &b, &w);
            edge[i] = Edge {a, b, w};
        }

        sort(edge + 1, edge + 1 + m, compare);

        int ans = INT_MAX;
        for (int i = 1 ; i <= m; i += 1) {
            for (int j = 1; j <= n; j ++) fa[j] = j;
            int minw = INT_MAX;
            int maxw = INT_MIN;
            for (int j = i; j <= m; j += 1) {
                int faa = get_father(edge[j].a);
                int fab = get_father(edge[j].b);
                if (faa != fab) {
                    union_xy(edge[j].a, edge[j].b);
                    minw = min(minw, edge[j].w);
                    maxw = max(maxw, edge[j].w);
                }
            }
            bool flag = true;
            for (int j = 1; j <= n; j ++) {
                if (get_father(j) != get_father(1)) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                int minusw = maxw - minw;
                ans = min(ans, minusw);
            }
        }
        if (ans == INT_MAX) ans = -1;
        printf("%d\n", ans);
    }
    return 0;
}
