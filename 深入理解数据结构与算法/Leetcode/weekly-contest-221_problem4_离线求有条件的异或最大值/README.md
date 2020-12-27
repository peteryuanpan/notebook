
# 离线求有条件的异或最大值

### 题意
```
给你一个由非负整数组成的数组 nums 。另有一个查询数组 queries ，其中 queries[i] = [xi, mi] 。

第 i 个查询的答案是 xi 和任何 nums 数组中不超过 mi 的元素按位异或（XOR）得到的最大值。换句话说，答案是 max(nums[j] XOR xi) ，其中所有 j 均满足 nums[j] <= mi 。如果 nums 中的所有元素都大于 mi，最终答案就是 -1 。

返回一个整数数组 answer 作为查询的答案，其中 answer.length == queries.length 且 answer[i] 是第 i 个查询的答案。
```

### 条件范围
```
1 <= nums.length, queries.length <= 10e5
queries[i].length == 2
0 <= nums[j], xi, mi <= 10e9
```

### 样例输入
```
[0,1,2,3,4]
[[3,1],[1,3],[5,6]]
[5,2,4,6,6,3]
[[12,4],[8,1],[6,3]]
```

### 样例输出
```
[3,3,7]
[15,-1,5]
```

### 关联链接
原题：https://leetcode-cn.com/contest/weekly-contest-221/problems/maximum-xor-with-an-element-from-array/
