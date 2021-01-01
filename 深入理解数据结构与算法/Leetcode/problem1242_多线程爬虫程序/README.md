
# 多线程爬虫程序

### 题意
```
给你一个初始地址 startUrl 和一个 HTML 解析器接口 HtmlParser，请你实现一个 多线程的网页爬虫，用于获取与 startUrl 有 相同主机名 的所有链接。
htmlParser.getUrls(startUrl) 可以返回 List<String> urls，表示爬虫结果
爬虫过程是一个BFS搜索的过程
爬虫过程中，只请求与 startUrl 同域名的链接，即若遇到非同域名的URL，则不继续爬虫
请返回以List<String>的形式，返回爬虫结果
```

### 条件范围
```
1 <= urls.length <= 1000
1 <= urls[i].length <= 300
startUrl 是 urls 中的一个
主机名的长度必须为 1 到 63 个字符（包括点 . 在内），只能包含从 “a” 到 “z” 的 ASCII 字母和 “0” 到 “9” 的数字，以及中划线 “-”。
主机名开头和结尾不能是中划线 “-”
参考资料：https://en.wikipedia.org/wiki/Hostname#Restrictions_on_valid_hostnames
你可以假设路径都是不重复的
```

### 样例输入
```
["http://news.yahoo.com","http://news.yahoo.com/news","http://news.yahoo.com/news/topics/","http://news.google.com","http://news.yahoo.com/us"]
[[2,0],[2,1],[3,2],[3,1],[0,4]]
"http://news.yahoo.com/news/topics/"
```

### 样例输出
```
["http://news.yahoo.com","http://news.yahoo.com/news","http://news.yahoo.com/news/topics/","http://news.yahoo.com/us"]
```

### 样例解释
```
```

### 关联链接
原题：https://leetcode-cn.com/problems/web-crawler-multithreaded/
