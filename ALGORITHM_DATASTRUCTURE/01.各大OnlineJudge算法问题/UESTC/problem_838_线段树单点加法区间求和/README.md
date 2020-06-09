
# 线段树单点加法区间求和

### 题意

给一个数列A，长度为N

M次操作，type=0或1

type=0时，给l、r，求A[l] + A[l+1] + ... + A[r]的结果

type=1时，给x、v，令A[x] = A[x] + v

### 条件范围

N, M : [1, 100000]

A[i] : [0, 100000]

type : 0 | 1

l : [1, r]

r : [l, N]

x : [1, N]

v : [0, 10000]

### 样例输入1

```
5 4
1 2 3 4 5
1 2 3
0 2 4
1 4 1
0 1 5
```

### 样例输出1

```
12
19
```

### 关联链接

原题：https://acm.uestc.edu.cn/problem/mu-yi-tian-xia/description
