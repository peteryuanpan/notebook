# codeforces_contest1266_problemA_排列能否被60整除

### 题意

T组输入，给一串数字，长度为N，字符集集合为C，请问全排列中是否存在一个排列能被60整除

### 条件范围

T : [1, 418]

N : [2, 100]

C : {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}

### 样例输入1

```
6
603
006
205
228
1053
0000000000000000000000000000000000000000000000
```

### 样例输出1

```
red
red
cyan
cyan
cyan
red
```

### 样例解释1

```
In the first example, there is one rearrangement that yields a number divisible by 60, and that is 360.
In the second example, there are two solutions. One is 060 and the second is 600.
In the third example, there are 6 possible rearrangments: 025, 052, 205, 250, 502, 520. None of these numbers is divisible by 60.
In the fourth example, there are 3 rearrangements: 228, 282, 822.
In the fifth example, none of the 24 rearrangements result in a number divisible by 60.
In the sixth example, note that 000…0 is a valid solution.
```

### 关联链接

原题：http://codeforces.com/contest/1266/problem/A
