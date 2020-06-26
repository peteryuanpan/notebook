class Solution {
public:
    int n, m;
    vector<vector<int>> grid;
    int ** ns, ** ls, ** rs;
    int * nn, * ln, * rn;
    void build() {
        ns = new int * [n];
        ls = new int * [n];
        rs = new int * [n];
        for (int i = 0; i < n; i ++) {
            ns[i] = new int [m];
            ls[i] = new int [m];
            rs[i] = new int [m];
            for (int j = 0; j < m; j ++) {
                ns[i][j] = ls[i][j] = rs[i][j] = 0;
            }
        }
        nn = new int [m];
        ln = new int [m];
        rn = new int [m];
        for (int j = 0; j < m; j ++) {
            nn[j] = ln[j] = rn[j] = 0;
        }
    }
    void init() {
        for (int j = 0; j < m; j ++) {
            int sum = 0, num = 0, is_one = 0;
            for (int i = 0; i < n; i ++) {
                sum += num + is_one;
                num += is_one;
                ns[i][j] += sum;
                is_one = grid[i][j];
            }
            sum = 0, num = 0, is_one = 0;
            for (int i = n - 1; i >= 0; i --) {
                sum += num + is_one;
                num += is_one;
                ns[i][j] += sum;
                is_one = grid[i][j];
            }
            for (int i = 0; i < n; i ++) {
                nn[j] += grid[i][j];
            }
        }
    }
    int solve() {
        int ans = INT_MAX;
        for (int i = 0; i < n; i ++) {
            for (int j = 1; j < m; j ++) {
                ls[i][j] = ls[i][j-1] + ln[j-1] + ns[i][j-1] + nn[j-1];
                ln[j] = ln[j-1] + nn[j-1];
            }
            for (int j = m - 2 ; j >= 0; j --) {
                rs[i][j] = rs[i][j+1] + rn[j+1] + ns[i][j+1] + nn[j+1];
                rn[j] = rn[j+1] + nn[j+1];
            }
            for (int j = 0; j < m; j ++) {
                ans = min(ans, ls[i][j] + ns[i][j] + rs[i][j]);
            }
        }
        return ans;
    }
    int minTotalDistance(vector<vector<int>>& grid_) {
        grid = grid_;
        n = grid.size();
        if (n == 0) return 0;
        m = grid[0].size();
        build();
        init();
        int ans = solve();
        return ans;
    }
};

/*
维护一个ns, nn, ls, ln, rs, rn
ns: nowsum 当前位置列所有1到达当前位置的步数
nn: nownum 当前位置列包含1的个数
ls: leftsum 当前位置列左边（不包含当前列，下同）所有1达到当前位置的步数
ln: leftnow 当前位置列左边包含1的个数
rs: rightsum 当前位置列右边所有1达到当前位置的步数
rn: rightnum 当前位置列右边包含1的个数
先求出 ns 和 nn
对于每一列，从上往下扫，再从下往上扫，就可以求出ns
对于每一列，从上往下扫，就可以求出nn
对于每一行从左往右扫求出 ls ln
起初 ls = 0, ln = 0
ls = ls + ln + ns + nn
ln = ln + nn
对于每一行从右往左扫求出 rs rn
起初 rs = 0, rn = 0
rs = rs + rn + ns + nn
rn = rn + nn
对于每一行
ans = min(ans, ls + ns + rs)
*/
