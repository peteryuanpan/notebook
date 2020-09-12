# 计算三次最小生成树
### 题意
```
Alice 和 Bob 共有一个无向图，其中包含 n 个节点和 3 种类型的边：

类型 1：只能由 Alice 遍历。
类型 2：只能由 Bob 遍历。
类型 3：Alice 和 Bob 都可以遍历。

给你一个数组 edges ，其中 edges[i] = [typei, ui, vi] 表示节点 ui 和 vi 之间存在类型为 typei 的双向边。请你在保证图仍能够被 Alice和 Bob 完全遍历的前提下，找出可以删除的最大边数。如果从任何节点开始，Alice 和 Bob 都可以到达所有其他节点，则认为图是可以完全遍历的。

返回可以删除的最大边数，如果 Alice 和 Bob 无法完全遍历图，则返回 -1 。
```
### 条件范围
```
1 <= n <= 10^5
1 <= edges.length <= min(10^5, 3 * n * (n-1) / 2)
edges[i].length == 3
1 <= edges[i][0] <= 3
1 <= edges[i][1] < edges[i][2] <= n
所有元组 (typei, ui, vi) 互不相同
```
### 样例输入1
```
4
[[3,1,2],[3,2,3],[1,1,3],[1,2,4],[1,1,2],[2,3,4]]
```
### 样例输出1
```
2
```
### 样例解释1
![image](https://user-images.githubusercontent.com/10209135/92988571-433b0280-f4ff-11ea-9200-1cc4b8101191.png)
```
如果删除 [1,1,2] 和 [1,1,3] 这两条边，Alice 和 Bob 仍然可以完全遍历这个图。再删除任何其他的边都无法保证图可以完全遍历。所以可以删除的最大边数是 2 。
```
### 样例输入2
```
4
[[3,1,2],[3,2,3],[1,1,4],[2,1,4]]
```
### 样例输出2
```
0
```
### 样例解释2
![image](https://user-images.githubusercontent.com/10209135/92988579-5057f180-f4ff-11ea-82d4-bbcf62f0e190.png)
```
注意，删除任何一条边都会使 Alice 和 Bob 无法完全遍历这个图。
```
### 样例输入3
```
4
[[3,2,3],[1,1,2],[2,3,4]]
```
### 样例输出3
```
-1
```
### 样例解释3
![image](https://user-images.githubusercontent.com/10209135/92988581-58b02c80-f4ff-11ea-9796-61e0e8d3aa24.png)
```
在当前图中，Alice 无法从其他节点到达节点 4 。类似地，Bob 也不能达到节点 1 。因此，图无法完全遍历。
```
### 关联链接
原题：https://leetcode-cn.com/contest/weekly-contest-205/problems/remove-max-number-of-edges-to-keep-graph-fully-traversable/
