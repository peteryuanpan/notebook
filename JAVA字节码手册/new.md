# new

创建一个对象，并将其引用值压入栈顶

### code1

```java
package com.luban.ziya.newtest;

public class NewTest1 {

    public static void main(String[] args) {
        Test1 t = new Test1();
    }
}

class Test1 {

    static {
        System.out.println("11");
    }

    Test1() {
        System.out.println(a);
        a = "22";
        System.out.println(a);
    }

    static String a = "33";

    static {
        System.out.println(a);
        a = "44";
        System.out.println(a);
    }
}
```

输出结果
```
11
33
44
44
22
```

字节码
```
0 new #2 <com/luban/ziya/newtest/Test1>
3 dup
4 invokespecial #3 <com/luban/ziya/newtest/Test1.<init>>
7 astore_1
8 return
```

解释

Test1 t = new Test1(); 对应 new Test1，会触发对Test1类加载，输出11、a、a，第一个a是33，第二个a是44，然后调用构造函数，invokespecial Test1.init，输出a、a，第一个a是44，第二个a是22
