class Solution {
public:
    static const int N = 1000010;
    static const int M = 505;
    //vector<int> rb[N];
    map<int, set<int>> rb;
    queue<int> q;
    bool vis[M];
    int numBusesToDestination(vector<vector<int>>& routes, int S, int T) {
        if (S == T) return 0;
        for (int i = 0; i < routes.size(); i ++) {
            for (auto j : routes[i]) {
                //rb[j].push_back(i);
                rb[j].insert(i);
            }
        }
        for (int i = 0; i < M; i ++) {
            vis[i] = false;
        }
        for (auto i : rb[S]) {
            q.push(i);
            vis[i] = true;
        }
        int step = 0;
        while (!q.empty()) {
            step ++;
            int sz = q.size();
            for (int i = 0; i < sz; i ++) {
                int x = q.front();
                q.pop();
                for (auto j : routes[x]) {
                    if (j == T) return step;
                    for (auto k : rb[j]) {
                        if (!vis[k]) {
                            q.push(k);
                            vis[k] = true;
                        }
                    }
                }
            }
        }
        return -1;
    }
};

// 换一个思路
// routes[i] 表示第i条公交车线路
// queue<int> q 表示正在枚举的公交车线路
// vis[i] 表示第i条线路被访问过
// rb[i] 第i个点拥有的线路
// 不是很明白为何rb用map就能过，用vector就会T，也许是因为N太大了？

//结果
/*
45 / 45 个通过测试用例
状态：通过
执行用时：340 ms
内存消耗：40.5 MB
*/
