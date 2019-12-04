#include <cstdio>
#include <cstring>
#include <cstdlib>
#include <algorithm>
using namespace std;

const int N = 100000 + 100;

int a[N];

long long tree[N * 4];

void build(int id, int l, int r) {
    if (l == r) {
        tree[id] = a[l];
        return;
    }
    int mid = (l + r) >> 1;
    build(id<<1, l, mid);
    build(id<<1|1, mid + 1, r);
    tree[id] = tree[id<<1] + tree[id<<1|1];
}

void update(int id, int l, int r, int x, int v) {
    if (x == l && x == r) {
        tree[id] += v;
        return;
    }
    int mid = (l + r) >> 1;
    if (x <= mid) update(id<<1, l, mid, x, v);
    else update(id<<1|1, mid + 1, r, x, v);
    tree[id] = tree[id<<1] + tree[id<<1|1];
}

long long query(int id, int ql ,int qr, int l, int r) {
    if (ql == l && qr == r) {
        return (long long) tree[id];
    }
    int mid = (l + r) >> 1;
    if (qr <= mid) return query(id<<1, ql, qr, l, mid);
    else if (mid + 1 <= ql) return query(id<<1|1, ql, qr, mid + 1, r);
    else {
        long long sum = 0;
        sum += query(id<<1, ql, mid, l, mid);
        sum += query(id<<1|1, mid + 1, qr, mid + 1, r);
        return sum;
    }
}

int main() {
    int n, m;
    scanf("%d%d", &n, &m);
    for (int i = 1; i <= n; i ++) {
        scanf("%d", a + i);
    }

    build(1, 1, n);

    for (int i = 1; i <= m; i ++) {
        int type;
        scanf("%d", &type);
        if (type == 0) {
            int ql, qr;
            scanf("%d%d", &ql, &qr);
            long long ans = query(1, ql, qr, 1, n);
            printf("%lld\n", ans);
        } else { // type == 1
            int x, v;
            scanf("%d%d", &x, &v);
            update(1, 1, n, x, v);
        }
    }

    return 0;
}
