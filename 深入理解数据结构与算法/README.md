# 深入理解数据结构与算法

### 前言

大学里搞过2年ACM，基本上在解决的都是一些经典问题（基本都属于P类问题），比如线段树、树状数组、动态规划、KMP、AC自动机、BFS、DFS、最小生成树、图论问题等

ACM中的问题，对个人编程思维有很大帮助，对拿到面试offer也是一块不错的敲门砖，但是，它的实际应用价值不大，你很难在实际项目中找到一个问题，需要用到ACM中的某个具体算法去解决的，而实际应用中最常见的也不是P类问题，而是NP类问题或者NPC问题，P类问题与它们有美妙的关联，也有差距

P类问题、NP类问题、NPC问题
> P类问题：所有可以在多项式时间内求解的判定问题构成P类问题。<br>
> NP类问题：所有的非确定性多项式时间可解的判定问题构成NP类问题。<br>
> 非确定性算法：非确定性算法将问题分解成猜测和验证两个阶段。算法的猜测阶段是非确定性的，算法的验证阶段是确定性的，它验证猜测阶段给出解的正确性。<br>
> NPC问题：NP中的某些问题的复杂性与整个类的复杂性相关联，这些问题中任何一个如果存在多项式时间的算法，那么所有NP问题都是多项式时间可解的，这些问题被称为NP-完全问题（NPC问题）。

总的来说，我希望在P类问题中能做一些自己的总结，兴趣为主，以后也会有用，而也希望在NP类和NPC问题中能有所探索

写这篇文章有三个出发点
- 大学中做过的题并没有很好地记录下来，那么现在开始记录也为时不晚，我会将在各大OnlineJudge上（主要是leetcode、lintcode）刷过题，自认为有价值的，记录之
- 经典数据结构与算法问题，比如经典排序算法、红黑树、B+树、KMP等，在面试中考核较多的，我希望记录下来，有用
- NPC类问题有关算法的探索，比如拟退火算法、粒子群优化算法、遗传算法、蚁群优化算法等

## 数据结构与算法总结

TODO

## 各大OnlineJudge题目

这是我的刷题笔记本，以Leetcode、lintcode为主，其他OnlineJudge为辅，记录下刷过的问题、源码、题解等

### OnlineJudge及相关主页

|名称|全称|主页|
|--|--|--|
|ICPC|International Collegiate Programming Contest|https://icpc.global/|
|UESTC|University of Electronic Science and Technology of China|https://acm.uestc.edu.cn/home|
|Leetcode|The World's Leading Online Programming Contest|https://leetcode-cn.com/|
|UVA|UVA Online Judge|https://onlinejudge.org/index.php|

### 题目列表

|序号|题意|题目|来源|大标签|小标签|题解|
|--|--|--|--|--|--|--|
|001|任意升序排序数列A的子串能否变成B|[contest1187_problemD](Codeforces/contest1187_problemD_任意升序排序数列A的子串能否变成B)|codeforces|白题|排列||
|002|求和为零的三元组的去重解|[problem15](Leetcode/problem15_求和为零的三元组的去重解)|leetcode|白题|二分||
|003|Kruskal算法求连通图的自定义函数解|[problem1395](UVA/problem1395_Kruskal算法求连通图的自定义函数解)|uva|图论|最小生成树||
|004|线段树单点加法区间求和|[problem838](UESTC/problem838_线段树单点加法区间求和)|uestc|数据结构|线段树||
|005|线段树区间加法区间求和|[problem839](UESTC/problem839_线段树区间加法区间求和)|uestc|数据结构|线段树||
|006|全排列能否被60整除|[contest1266_problemA](Codeforces/contest1266_problemA_全排列能否被60整除)|codeforces|白题|排列||
|007|基于最大公约数的矩阵构造题|[contest1266_problemC](Codeforces/contest1266_problemC_基于最大公约数的矩阵构造题)|codeforces|白题|构造||
|008|输出串是两个输入串的母序列且规则|[contest1272_problemF](Codeforces/contest1272_problemF_输出串是两个输入串的母序列且规则)|codeforces|动态规划|||
|009|商品打折券的最大化使用|[contest1282_problemB2](Codeforces/contest1282_problemB2_商品打折券的最大化使用)|codeforces|动态规划|||
|010|合并且排序K个指针表|[problem23](Leetcode/problem23_合并且排序K个指针表)|leetcode|白题|指针||
|011|求解next_permutation|[problem31](Leetcode/problem31_求解next_permutation)|leetcode|白题|排列||
|012|**两个正序数列求中位数**|[problem4](Leetcode/problem4_两个正序数列求中位数)|leetcode|白题|二分||
|013|求1到X中数字1出现的次数|[problem233](Leetcode/problem233_求1到X中数字1出现的次数)|leetcode|动态规划|数列统计类||
|014|求一个数是否是有效数|[problem65](Leetcode/problem65_求一个数是否是有效数)|leetcode|白题|条件判断||
|015|求a到b中有多少个翻转对称数|[problem248](Leetcode/problem248_求a到b中有多少个翻转对称数)|leetcode|动态规划|数列统计类||
|016|最佳碰头地点|[problem296](Leetcode/problem296_最佳碰头地点)|leetcode|动态规划|公式推导||
|017|多条环路公交车线两点间最少上车次数|[problem815](Leetcode/problem815_多条环路公交车线两点间最少上车次数)|leetcode|搜索|广度优先搜索||
|018|计算三次最小生成树|[weekly-contest-205_problem4](Leetcode/weekly-contest-205_problem4_计算三次最小生成树)|leetcode|图论|最小生成树||
 
标签
```
数据结构
|----线段树
|----树状数组
|----二叉堆
|----主席树
|----动态树
     |----伸展树(splay)
     |----LCT(link/cut tree)
     |----动态仙人掌

字符串
|----字典树(Trie)
|----KMP
     |----拓展KMP
     |----AC自动机
|----后缀数组、后缀树
|----后缀自动机

动态规划
|----数学三角形
|----背包
|----公共子串、子序列
|----动态规划与其他结构结合

图论
|----有向无环图(DAG)
|----并查集
|----最小生成树
     |----斯坦纳树
|----最短路
     |----Dikstra
     |----Bellman-Ford、SPFA
     |----Floyd
|----tarjan
|----网络流

搜索
|----广度优先搜索（BFS）、深度优先搜索（DFS）
|----启发式搜索
     |----A*算法
     |----Alpha-Beta算法
     |----模拟退火算法
     |----遗传算法
     |----蚁群算法
     |----粒子群优化算法

数学
|----几何
     |----叉积
     |----凸包
|----数论
     |----素数、素因数分解
     |----最大公约数(GCD)、最小公倍数(LCA)
     |----费马小定理
|----数学函数
     |----概率、期望
     |----排列、组合
     |----导数、微积分
     |----快速傅里叶变换(FFT)
     |----卡特兰数、超级卡特兰数

白题
|----二分、三分
|----构造
|----排列
|----指针
|----暴力
|----条件判断
```

新建README模板
```
# 题目
### 题意
\```
\```
### 条件范围
\```
\```
### 样例输入1
\```
\```
### 样例输出1
\```
\```
### 样例解释1
\```
\```
### 关联链接
原题：
```
