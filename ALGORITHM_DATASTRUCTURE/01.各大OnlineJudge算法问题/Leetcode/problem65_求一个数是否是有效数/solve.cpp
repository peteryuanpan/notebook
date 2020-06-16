class Solution {
public:
    bool isNumber(string s) {
        //cout << s << endl;
        int c, p, d;
        int n = (int) s.length();
        // 空字符串
        if (n == 0) return false;
        // 去掉前导后导空格
        while (!s.empty() && s[0] == ' ') s.erase(0, 1);
        while (!s.empty() && s.back() == ' ') s.erase(s.length() - 1, 1);
        if (s.length() != n) return isNumber(s);
        // 判断字符集
        for (int i = 0; i < n; i ++) {
            if (!isdigit(s[i]) && s[i] != 'e' && s[i] != '.' && s[i] != '+' && s[i] != '-') {
                return false;
            }
        }
        // 求e出现的次数及位置
        c = 0;
        for (int i = 0; i < n; i ++) {
            if (s[i] == 'e') {
                c ++;
                p = i;
            }
        }
        // e最多出现一次
        if (c > 1) return false;
        if (c == 1) {
            // e后面不能出现小数点了
            for (int i = p + 1; i < n; i ++) {
                if (s[i] == '.') return false;
            }
        }
        if (c == 1) {
            // 根据 e 拆分
            // e前后必须有字符，不能为空：由于最开始判断了空字符串，这种情况也考虑了
            return isNumber(s.substr(0, p)) && isNumber(s.substr(p + 1, n));
        }
        // 往下就没有 e 了
        // 求小数点出现的次数及位置
        c = 0;
        p = 0;
        for (int i = 0; i < n; i ++) {
            if (s[i] == '.') {
                c ++;
                p = i;
            }
        }
        // 小数点最多出现一次
        if (c > 1) return false;
        if (c == 1) {
            // 小数点前后至少有一个数字
            if (p == 0 && p == n - 1) return false; // 只有一个点
            else if ((p == 0 || !isdigit(s[p-1])) && (p == n - 1 || !isdigit(s[p+1]))) return false;
            // 如果只有一个时，前后缺的位置补上0
            if (p == 0 || !isdigit(s[p-1])) { // 往前补0
                s.insert(p, "0");
                return isNumber(s);
            }
            if (p == n - 1 || !isdigit(s[p+1])) { // 往后补0
                s.insert(p + 1, "0");
                return isNumber(s);
            }
        }
        // 求+-出现的次数及状态
        c = 0;
        d = 0;
        for (int i = 0; i < n; i ++) {
            if (s[i] == '+') {
                d |= 1;
                c ++;
            }
            else if (s[i] == '-') {
                d |= 2;
                c ++;
            }
        }
        // +-不能同时出现
        if (d == 3) return false;
        if (d == 1 || d == 2) {
            // +或-最多只能出现一次
            if (c > 1) return false;
            // +-如果出现，必须在第一位
            if (s[0] != '+' && s[0] != '-') return false;
            // 只有+-也不行
            if (s.length() == 1) return false;
        }
        // 都判断完了
        return true;
    }
};

/*
0. 前导0是可以的
1. 空字符串不允许
2. 空格情况，去掉前导及后导空格再判断
3. 判断字符集[0,1,2,3,4,5,6,7,8,9,e,+,-,.]，其他超过这个字符集的，都算false
4. e最多出现1次吗？？是的，e的含义是指数，而不是数学中的那个e=2.71828...
5. e后面不能再出现小数点了，比如[2e0.1、3e1.1]，都是false

考虑
一，+-如果出现，必须在第一位？不一定
比如[6e-1、+6e-2、-0e-2、-0.e-2]，都是true，这就需要找到e的位置，拆分前后来判断了
二，+-不能同时出现？不一定
三，e前后必须有字符，且为数字？不一定
四，小数点最多出现一次？不一定
如下
6.首先，找到e的位置，前后拆分，独立判断
此时
7. e前后必须有字符，不能为空
8. 小数点最多出现一次
9. 小数点前后必须有字符，且为数字？？？不一定
有几种特殊情况
一、[.0、0.、-.0、+0.e1、-.1e2]，都是true，可以有一个办法，即小数点前后不是数字时，默认补个0，这样就变为[0.0、0.0、-0.0、+0.0e1、-0.1e2]
二，但不能只有一个点，比如[.、+.、-.]，结果都是false
因此可以总结为：小数点前后至少有一个数字，如果只有一个时，前后缺的位置补上0

10. +-不能同时出现
11. +或-最多只能出现一次
12. +-如果出现，必须在第一位
13. 只有+-也不行

测试集
true:
[0.1、123、01、001、000、000e0、+0、+1、-0、+2e2、.0、0.、-.0、-.1e2、1e+0、-.0e-0、+0.e1、-.1e2]
["1 "、" -.0e-0"]

false:
[a1、1+、+-1、0.1e、+e、-e、+1e、ee、1e1e2、.e1、e1、2e0.1、3e1.1、.、+.、-.、+、-、4e+]
["1 e 2"、" -.0e -0"、"  "、""]

*/
