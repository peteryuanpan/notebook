class Solution {
public:
    static const int N = 1000010;
    int a[N];
    vector<int> b[N];
    queue<int> q;
    static const int M = 505;
    bool vis[M];
    void build(vector<vector<int>>& routes) {
        for (int i = 0; i < routes.size(); i ++) {
            for (int j = 0; j < routes[i].size(); j ++) {
                int x = routes[i][j];
                b[x].push_back(i);
            }
        }
    }
    void init() {
        for (int i = 0; i < N; i ++) {
            a[i] = -1;
        }
        for (int i = 0; i < M; i ++) {
            vis[i] = false;
        }
    }
    int numBusesToDestination(vector<vector<int>>& routes, int s, int t) {
        if (s == t) return 0;
        build(routes);
        init();
        a[s] = 0;
        q.push(s);
        while(!q.empty()) {
            int x = q.front();
            if (x == t) return a[x];
            q.pop();
            for (int i = 0; i < b[x].size(); i ++) {
                int bxi = b[x][i];
                if (!vis[bxi]) {
                    vis[bxi] = true;
                    int ax = a[x];
                    for (int j = 0; j < routes[bxi].size(); j ++) {
                        int y = routes[bxi][j];
                        if (a[y] == -1 || ax + 1 < a[y]) {
                            a[y] = ax + 1;
                            q.push(y);
                        }
                    }
                }
            }
        }
        a[s] = 1;
        return a[t];
    }
};

// a[x]表示起点S到节点x所需要乘坐的最少公交车数量
// 假设起点不存在公交线路，则为-1
// 假设起点存在公交线路，公交线路上的所有点到达起点的A值都为1
// routes[i] => r[i]
// i 表示第i辆公交车
// r[i]第i辆公交车的路线
// r[i][j]第i辆公交车第j个站点的站点标志
// s表示起点站点标志
// t表示终点站点标志
// 第一件事，建图
// 最短路问题，权重每次都最多+X（X是非负数且固定值）的话，不需要优先队列
// b[i]表示从第i点能经过的公交车标志（从下到大）

//结果
/*
45 / 45 个通过测试用例
状态：通过
执行用时：1352 ms
内存消耗：52 MB
*/
