# 编译原理

gcc编译的几个过程
```
参考：https://blog.csdn.net/xiaohouye/article/details/52084770
关键词："gcc编译过程"
```
```
gcc -E 1.c -o 1.i（生成预编译文件）
gcc -S 1.i -o 1.s（生成汇编代码）
gcc -c 1.s -o 1.o（生成目标文件）
gcc 1.o -o 1（链接成可执行文件）
```

# C++ Primer Plus 6TH

## CHAPTER 3

climits中18种常量符号是什么？分别对应的值是多少？
```
CHAR_BIT, 8
...
ULLONG_MAX, (1 << 64) - 1
```

## CHAPTER 4

枚举量的值可以是非整形吗？
```
不可以
```

enum A{a, b = 100, c}，a、b、c的值分别是多少？
```
0, 100, 101
```
