class Solution {
public:
    static const int N = 100010;
    int fa1[N];
    int fa2[N];
    int get_fa(int * fa, int v) {
        if (fa[v] == v) return v;
        else {
            fa[v] = get_fa(fa, fa[v]);
            return fa[v];
        }
    }
    int maxNumEdgesToRemove(int n, vector<vector<int>>& edges) {
        for (int i = 1; i <= n; i ++) {
            fa1[i] = i;
        }
        int max_remove = 0;
        for (int i = 0; i < edges.size(); i ++) {
            if (edges[i][0] == 3) {
                int f1 = get_fa(fa1, edges[i][1]);
                int f2 = get_fa(fa1, edges[i][2]);
                if (f1 != f2) {
                    fa1[f1] = f2;
                } else {
                    max_remove += 1;
                }
            }
        }
        for (int i = 1; i <= n; i ++) {
            fa2[i] = fa1[i];   
        }
        for (int i = 0; i < edges.size(); i ++) {
            if (edges[i][0] == 1) {
                int f1 = get_fa(fa1, edges[i][1]);
                int f2 = get_fa(fa1, edges[i][2]);
                if (f1 != f2) {
                    fa1[f1] = f2;
                } else {
                    max_remove += 1;
                }
            }
        }
        for (int i = 1; i <= n; i ++) {
            if (get_fa(fa1, 1) != get_fa(fa1, i)) return -1;
        }
        for (int i = 0; i < edges.size(); i ++) {
            if (edges[i][0] == 2) {
                int f1 = get_fa(fa2, edges[i][1]);
                int f2 = get_fa(fa2, edges[i][2]);
                if (f1 != f2) {
                    fa2[f1] = f2;
                } else {
                    max_remove += 1;
                }
            }
        }
        for (int i = 1; i <= n; i ++) {
            if (get_fa(fa2, 1) != get_fa(fa2, i)) return -1;
        }
        return max_remove;
    }
};
