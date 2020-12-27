
# 吃苹果的最大数目

### 题意
```
有一棵特殊的苹果树，一连 n 天，每天都可以长出若干个苹果。在第 i 天，树上会长出 apples[i] 个苹果，这些苹果将会在 days[i] 天后（也就是说，第 i + days[i] 天时）腐烂，变得无法食用。也可能有那么几天，树上不会长出新的苹果，此时用 apples[i] == 0 且 days[i] == 0 表示。

你打算每天 最多 吃一个苹果来保证营养均衡。注意，你可以在这 n 天之后继续吃苹果。

给你两个长度为 n 的整数数组 days 和 apples ，返回你可以吃掉的苹果的最大数目。
```

### 条件范围
```
apples.length == n
days.length == n
1 <= n <= 2 * 10e4
0 <= apples[i], days[i] <= 2 * 10e4
只有在 apples[i] = 0 时，days[i] = 0 才成立
```

### 样例输入
```
[1,2,3,5,2]
[3,2,1,4,2]
[3,0,0,0,0,2]
[3,0,0,0,0,2]
```

### 样例输出
```
7
5
```

### 关联链接
原题：https://leetcode-cn.com/contest/weekly-contest-221/problems/maximum-number-of-eaten-apples/
