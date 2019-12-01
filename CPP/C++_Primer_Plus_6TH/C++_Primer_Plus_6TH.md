# C++ Primer Plus 6TH

# CHAPTER 2

## 2.6 复习题, P35

### C++程序的模块叫什么？

2.5，P34
```
函数。C++程序由一个或多个被称为函数的模块组成。
```

### 下面的预处理器编译指令是做什么用的？

#include < iostream >

2.1.3，P16
```
该编译指令导致预处理器将 iostream 文件的内容添加到程序中。
```

附录J 复习题答案
```
这将导致在最终的编译之前，使用 iostream 文件的内容替换该编译指令。
```

### 下面的语句是做什么用的？

using namespace std;

2.4.5，P34
```
将 using namespace std; 放在函数定义之前，让文件中所有的函数都能够使用名称空间 std 中所有的元素。
将 using namespace std; 放在特定的函数定义中，让该函数能够使用名称空间 std 中的所有元素。
```

### 假设您编写的 main() 函数包含如下代码，而编译器指出 cout 是一个未知的标识符。导致这种问题的原因很可能是什么？指出3种修复这种问题的方法。

cout << "Please enter you Pin: ";

use of undeclared identifier 'cout'

2.4.5，P34
```
原因：未指明 cout 的名称空间。
方法一：在函数定义前 或 函数定义中，写上 using namespace std;
方法二：在函数定义前 或 函数定义中，写上 using std::cout;
方法三：改为 std::cout << "Please enter you Pin: ";
```

# CHAPTER 3

## 3.1.3, P41

### #include < climits > 中18种常量符号是什么？分别对应的值是多少？

3.1.3，P41
```
CHAR_BIT, 8
CHAR_MAX, 
CHAR_MIN,
SCHAR_MAX,
SCHAR_MIN,
UCHAR_MAX,
SHRT_MAX,
SHRT_MIN,
USHRT_MAX,
INT_MAX,
INT_MIN,
UNIT_MAX,
LONG_MAX,
LONG_MIN,
ULONG_MAX,
LLONG_MAX,
LLONG_MIN,
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
