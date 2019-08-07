# C++ Primer Plus 6TH

# CHAPTER 2

## 2.6, P35

### C++程序的模块叫什么？

2.5，P34
```
函数
```

### 下面的预处理器编译指令是做什么用的？#include < iostream >

2.1.3，P16
```
该编译指令导致预处理器将 iostream 文件的内容添加到程序中。
```

附录J 复习题答案
```
这将导致在最终的编译之前，使用 iostream 文件的内容替换该编译指令。
```

### 下面的语句是做什么用的？using namespace std;

2.4.5，P34
```
将 using namespace std; 放在函数定义之前，让文件中所有的函数都能够使用名称空间 std 中所有的元素。
将 using namespace std; 放在特定的函数定义中，让该函数能够使用名称空间 std 中的所有元素。
```

### 什么语句可以用来打印短语"Hello, world"，然后开始新的一行？

easy
```
printf("Hello, world\n");
```

easy
```
std::cout << "Hello, world" << endl;
```

# CHAPTER 3

## 3.1.3, P41

### climits中18种常量符号是什么？分别对应的值是多少？

3.1.3，P41
```
CHAR_BIT, 8
...
ULLONG_MAX, (1 << 64) - 1
```

# CHAPTER 4

## 4.6.1, P97

### 枚举量的值可以是非整形吗？

4.6.1，P97
```
不可以
```

## 4.6.1, P97

### enum A{a, b = 100, c}，a、b、c的值分别是多少？

4.6.1，P97
```
0, 100, 101
```
