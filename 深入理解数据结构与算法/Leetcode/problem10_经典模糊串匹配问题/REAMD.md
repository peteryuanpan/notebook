
# 经典模糊串匹配问题

### 题意
```
给你一个字符串 s 和一个字符规律 p，请你来实现一个支持 '.' 和 '*' 的正则表达式匹配

'.' 匹配任意单个字符
'*' 匹配零个或多个前面的那一个元素
所谓匹配，是要涵盖 整个 字符串 s的，而不是部分字符串
```

### 条件范围
```
0 <= s.length <= 20
0 <= p.length <= 30
s 可能为空，且只包含从 a-z 的小写字母。
p 可能为空，且只包含从 a-z 的小写字母，以及字符 . 和 *
保证每次出现字符 * 时，前面都匹配到有效的字符
```

### 样例输入
```
"aa"
"a"
"aa"
"a*"
"ab"
".*"
"aab"
"c*a*b"
"mississippi"
"mis*is*p*."
"aaaa"
"**"
"aaa"
".*"
"aaa"
"a*"
"a"
"b*a"
```

### 样例输出
```
false
true
true
true
false
false
true
true
true
```

### 关联链接
原题：https://leetcode-cn.com/problems/regular-expression-matching/
