class Solution {
public:
    string sub_one(string s) {
        for (int i = s.length() - 1; i >= 0; i --) {
            if (s[i] == '0') {
                s[i] = '9';
            } else {
                s[i] = (s[i] - '0' - 1) + '0';
                break;
            }
        } 
        while (s.length() > 1 && s[0] == '0') s.erase(0, 1);
        return s;
    }

    long long f[30][2];
    void init() {
        f[1][1] = 2;  // (1,8)
        f[1][0] = 3;  // (0,1,8)
        f[2][1] = 4;  // (11,88,69,96)
        f[2][0] = 5;  // (11,88,69,96,00)
        for (int i = 3; i < 30; i ++) {
            f[i][1] = f[i-2][0] * 4;
            f[i][0] = f[i-2][0] * 5;
        }
    }

    int solve(string s) {
        int n = s.length();
        long long ans = 0;
        for (int i = 1; i < n; i ++) {
            if (i == 1) ans = ans + f[i][0];
            else ans = ans + f[i][1];
        }
        bool ok = true;
        for (int i = 0; i <= (n - 1) / 2; i ++) {
            for (int j = 0; j < (s[i] - '0'); j ++) {
                if (n > 1 && i == 0 && j == 0) continue;
                if (i == (n - 1) / 2) {
                    if (n % 2 == 1) {
                        if (j == 0 || j == 1 || j == 8) ans = ans + 1;
                    } else {
                        if (j == 0 || j == 1 || j == 6 || j == 8 || j == 9) ans = ans + 1;
                    }
                } else {
                    if (j == 0 || j == 1 || j == 6 || j == 8 || j == 9) {
                        ans = ans + f[n-2*(i+1)][0];
                    }
                }
            }
            int d1 = (s[i] - '0');
            if (d1 != 0 && d1 != 1 && d1 != 6 && d1 != 8 && d1 != 9) {
                ok = false;
                break;
            }
            int d2 = (s[n-i-1] - '0');
            if (d1 == 0 || d1 == 1 || d1 == 8) {
                if (d1 > d2) ok = false;
            }
            if (d1 == 6 && 9 > d2) ok = false;
            if (d1 == 9 && 6 > d2) ok = false;
            if (ok && i == (n - 1) / 2) {
                if (n % 2 == 1) {
                    if (d1 == 0 || d1 == 1 || d1 == 8) ans = ans + 1;
                } else {
                    ans = ans + 1;
                }
            }
        }
        return ans;
    }

    int strobogrammaticInRange(string low, string high) {
        init();
        long long ans;
        if (low == "0") ans = solve(high);
        else ans = solve(high) - solve(sub_one(low));
        return (int) max(ans, 0LL);
    }
};

/*
这题，整体思路：由中心向两边拓展时动态规划

0 1 6 8 9 是关键数字，其余2 3 4 5 7都可忽略
可以考虑用DP的方法求解
ans[low,high]=ans[0,high] - ans[0,low-1]
ans[i]表示[0,i]中的答案

假设求1-X的答案，X的长度为n
那么长度为[1,n)，即数值为[0,10^n)的数答案和都能很快求出来
dp[i]表示长度为i时的答案和
dp[1]和dp[2]可以拼接算出来
当i>=3时，可以通过dp[i-2]推得出dp[i]，注意0的情况即可
为了考虑0的情况，我们多引入一维
f[i][0]表示长度为i时包含前后导0的答案
f[i][1]标示长度为i时不包含前后导0的答案
对于奇数
f[1][1]=2 (1,8)
f[1][0]=3 (0,1,8)
f[3][1]=12 (101,609,808,906,111,619,818,916,181,689,888,986)
f[3][0]=15 (上面的基础上，000,010,080)
对于偶数
f[2][1]=4 (11,88,69,96)
f[2][0]=5 (11,88,69,96,00)
那么
f[i][1]=f[i-2][0]*4
f[i][0]=f[i-2][0]*5
dp[i]=f[1][0]+f[2][1]+...+f[i][1]

对于长度为n，且小于high的情况怎么办呢？
其实就是在上面的思路基础上，限制一下每个位置的字符集上限
相当于，之前的字符集是{0,1,...,9}，现在是{0,1,...,x}，x是对称两个位置数的最小值
需要分奇偶来判断，看代码吧
*/
