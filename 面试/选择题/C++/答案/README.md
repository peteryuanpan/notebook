### 题1.1

B

```
scanf 中 *3c 表示跳过3个字符
```

### 题1.2

A

```
根据纯虚函数定义，A是对的，C表述不全，D是错的
参考
- https://baike.baidu.com/item/%E7%BA%AF%E8%99%9A%E5%87%BD%E6%95%B0
- https://zhuanlan.zhihu.com/p/37331092
- https://baike.baidu.com/item/%E8%99%9A%E5%9F%BA%E7%B1%BB
```

### 题1.3

C

```
1代表的是一个单位量 
p1+5=p1+5*1=p1+5*sizeof(unsigned char)=p1+5*1=0x801000+ox5=0x801005 
p2+5=p2+5*1=p2+5*sizeof(unsigned long)=p1+5*4=0x810000+20=0x810000+0x14=0x810014 
最后要转换成16进制

另外，如果是64位机器的话，答案是多少？
```
### 题1.4（不一定向选）

B、C

### 题1.5

A

```
C语言中函数的定义都是相互平行、相互独立的，也就是说在函数定义时，函数体内不能包含另一个函数的定义，即函数不能嵌套定义，但可以 嵌套调用。嵌套函数，就是指在某些情况下，您可能需要将某函数作为另一函数的参数使用，这一函数就是嵌套函数。一个为大家所熟知的例子就是 qsort函数会将一个比较器cmp作为参数.在一个程序中， 主函数调用了sum函数，而在sum函数中又调用了mul函数。在一个函数被调用的过程中又调用另一个函数，这就是函数的 嵌套调用。如果是函数本身嵌套调用函数本身，那就是函数递归调用了。
```

### 题1.6

C

```
因while后面的表达式是默值表达式，其值为0（即为假），所以循环体内的
语句不可能执行。故正确答案是C。
```

### 题1.7

B

```
1、C++不允许仅根据函数的返回类型重载函数名称；
2、可以编写两个名称相同，参数也相同的函数，其中一个是const，另一个不是。

关于2的例子

#include <bits/stdc++.h>
using namespace std;
 
class A
{
public:
    void f()
    {
        cout << 1 << endl;
    }
    void f() const
    {
        cout << 2 << endl;
    }
};
 
int main()
{
    A a;
    const A b;
    a.f();  //1
    b.f();  //2
    return 0;
}
```

### 题1.8

D

```
预处理命令行可以出现在源程序的任何位置上，因此选项A的说法是错误的。源程序的一行上只能出现一条预处理命令，因此选项B的说法也是错误的。宏名只要是符合要求的标识符都可以，没有规定一定要大写，因此选项C的说法也是错误的。宏替换在程序编译时，就由编译程序对出现的宏名进行了相应的宏替换，因此宏替换不占用程序的运行时间。选项D的说法是正确的。
```

### 题1.9

B

```
D ： p = &x;  表示的是，对x取地址，赋值给指针p，那么p 将指向 x 的那块内存空间，但是 x  是形式参数(也有人说是方法参数，都可以)，函数调用完了之后，内存就释放了，所以再返回 *p（即取出那块内存空间的值），已经找不到了。所以错误。
A ：*p = x；  表示的是 将 x 的值赋值给 P 所指向的空间，而p之前并没有指向任何地方，这个操作将是非法的。
C：*p = new int(x)；   这个操作同 A 的结果一样。
B  ： p = new int(x);  new int(x) 新申请空间，调用完后不释放空间，所以将地址赋值给p 则p 指向了这段新申请内存空间，所以当做  *p 返回时，即取出p所执行空间的值，所以会输出5
```

### 题1.10

A

```
Filter函数可以将迭代器的数据带入函数中，返回使函数返回值为true的值
过滤掉偶数
```

### 题1.11

B

```
答案是B，简单解释一下为什么不选另外三个：
A选项中的B1(b)：基类B1已经在B1(a)中显式初始化过了，不能再次初始化
C选项中的c2(b)：成员变量c2是类B2的对象，而类B2并没有参数列表相匹配的构造函数
D选项中的B2(b)：基类B2没有参数列表相匹配的构造函数
```

### 题1.12

```
A:Constructor
B:Construct
A:f1
B:f2
B:f3
A:f4
B:f5
B:Destructor
A:Destructor

A:Constructor
B:Construct
B:f1
B:f2
B:f3
A:f4
B:f5
B:Destructor
A:Destructor

A:Constructor
B:Construct
C:Construct
B:f1
B:f2
C:f3
C:f4
C:f5
C:Destructor
B:Destructor
A:Destructor

A:Constructor
B:Construct
C:Construct
C:f1
B:f2
C:f3
C:f4
C:f5
C:Destructor
B:Destructor
A:Destructor
```

### 题1.13

D

```
a是一个二维数组，p是一个指向有5个元素的数组指针。
A：表达式是一个指针，不是对数组元素的引用。*(p+1)等于&a[1]，即*(p+1)指向a的第二行的首元素。
B：*（p+3）是一个指针，指向第4行的首元素。
C：*(p+1)指向a的第二行的首元素；*（p+1）+3则指向第二行的第3个元素。
D：* ( *p+2)是对数组元素的正确引用，它等价于a[0][2]。
所以对a数组元素的正确引用的选项是D。
```

### 题1.14

A

```
*(char*)((char *)(cur)+2) 可看作 X=(char *)(cur+0),Y=*(char*)(X+2),即obj_array[0][2]='c'
*(char*)(cur+2) 可看作 X=(cur+2),Y=*(char*)(X+0),即obj_array[2][0]='g'
```

### 题1.15

C

```
int d = c * a * b++; // d = 2 * 10 * 4++，b在运算之后自增为5
```

### 题1.16

D

```
二维数组的定义可以省略第一维 但是不能省略第二维

拓展代码

/*
 * invalid
 * char a1[][10]; // error: storage size of 'a1' isn't known
 * char a2[][][10]={}; // error: declaration of 'a2' as multidimensional array must have bounds for all dimensions except the first
 */

// valid
char a1[][10]={};
char a2[][10][10]={};
char a3[][10][10][10]={};
int b[][3]={0,1,2,3};
```

### 题1.17

D

```
函数外的变量 以及 static 修饰的变量，在静态存储区存放，不属于任何对象。
有 virtual 修饰的函数，有虚函数表及虚函数表指针，虚函数表指针占一个int大小空间，继承的int型变量，总共8字节。
```

### 题1.18

D

```
C语言函数参数入栈顺序为由右向左,func(++i, i++)可以分解为
参数 i 先入栈 输出0 
然后执行i++ 此时i为1 
接着参数 i 先执行++i   i 此时为2  后入栈进行输出 2
```

### 题1.19

C

```
#include是在命令处插入，不一定要在首部插入，只是习惯用于首部
插入的是一个文本文件，.h只是一个习惯后缀，也可以#include一个.txt
```

### 题1.20

C

```
分两点说吧：
1.fork()函数会把它所在语句以后的语句复制到一个子进程里，单独执行。
2.如果printf函数最后没有"\n"，则输出缓冲区不会被立即清空，而fork函数会把输出缓冲区里的内容也都复制到子进程里。
所以，父进程和子进程各输出2个Hello，共4个。
如果第一个printf("Hello");写成printf("Hello\n");，则只会输出3个Hello，父进程2个，子进程1个。
```

### 题1.21

A

```
1 auto_ptr的意义 
 std::auto_ptr是C++标准库里面的模版类， 属于智能指针
 当系统异常退出的时候避免资源泄漏（内存）。 其他的资源还对应其他的智能指针。  
2 auto_ptr的使用 
std::auto_ptr<int> test(new int（1）); 
test将是一个auto_ptr的对象，使用一个int指针进行初始化。 
test可以象其他指针一样使用，如使用* 使用->但是＋＋不可以使用，以后也许会扩展，其实难对++做越界管理，也许可以放弃一些速度。  
当使用auto_ptr的时候，必须使用显式的类型转化来初始化，如auto_ptr<classA> a(new classA） 
而不能使用auto_ptr<classA> a = new classA;  
3 auto_ptr所有权的转移 
auto_ptr对所有权有严格的约定，一个auto_ptr只能控制一个指针，不能控制多个，当auto_ptr拥有一个指针的时候就不能在拥有其他的指针了。同时，不同的auto_ptr不能拥有同一个指针。
```
### 题1.22（不定向选）

A、B

```
A.explicit关键字强制仅有显式调用有效 ，正确，C++提供关键字explicit，用于阻止不应该允许的经过转     换构造函数进行的隐式转换的发生。声明为explicit的构造函数不能在隐式转换中使用。
B.保护成员可以在定义它的类中使用，也可以在派生类中使用，正确。保护类就是为了继承产生的
C.保护成员仅可以在定义它的类中使用。错误，见B
```

### 题1.23（不定向选）

A、B、D

```
以下是几个用到volatile关键字修饰变量的情况：
1.设备的硬件寄存器（如：状态寄存器）
2.一个中断服务子程序中会访问到的非自动变量（Non-automatic variables）
3.多线程应用中被几个任务共享的变量
```
### 题1.24（不定向选）

B、C、D

```
A选项中，operator+有两个参数，重载函数中只声明了一个参数，属于类的成员函数
B选项中，operator--前置运算符没有参数，后置运算符参数应为int型，因此它重载的是前置--友元函数
C选项中，operator&&有两个参数，属于类的友元函数
D选项中，重载的是operator++后置运算符，两个参数，为友元函数
```

### 题1.25

C

```
Derived class pointer cannot point to base class.
理论上，父类的sizeof <= 子类的sizeof，因此 子类的指针无法指向父类的对象。“多的可以给少的，少的不能给多的”。
```

### 题1.26

B 

```
const 有问题，去掉则对
error: no viable overloaded operator[] for type 'const std::unordered_map<int, int>'
        return bar[x];
```

### 题1.27

D

```
auto 可自动判断数据类型，但并非指针，而是一个变量，不会改变来源数据值
```

### 题1.28

C

```
代码中注释已标明一行输出几个结果
```
