# 合并且排序K个指针表

### 题意

给K个指针表，每个指针表是排好序的

要求合并它们，且合并后指针表也是排序的

指针表结构体 及 类函数 声明如下

```
/**
 * Definition for singly-linked list.
 * struct ListNode {
 *     int val;
 *     ListNode *next;
 *     ListNode(int x) : val(x), next(NULL) {}
 * };
 */
 class Solution {
 public:    
    ListNode * mergeKLists(vector<ListNode*>& lists) {   
    }
 };
```

### 条件范围

K : 未知，可认为10^5范围

### 样例输入1
```
[[1,4,5],[1,3,4],[2,6]]
```

### 样例输出1
```
[1,1,2,3,4,4,5,6]
```

### 关联链接

原题：https://leetcode.com/problems/merge-k-sorted-lists/
