# getstatic

获取指定类的静态域，并将其值压入栈顶

### code1

```java
package com.luban.ziya.statictest;

public class GetStaticTest1 {

    public static void main(String[] args) {
        String a = Test3.a;
        String b = Test3.a;
    }
}

class Test3 {

    static {
        System.out.println("11");
    }

    public static String a = "22";

    static {
        System.out.println("33");
    }
}
```

输出结果
```
11
33
```

字节码
```
0 getstatic #2 <com/luban/ziya/statictest/Test3.a>
3 astore_1
4 getstatic #2 <com/luban/ziya/statictest/Test3.a>
7 astore_2
8 return
```

解释

String a = Test3.a; 对应 getstatic Test3.a，会触发Test3的类加载，输出11、33

String b = Test3.a; 对应 getstatic Test3.a，会尝试Test3的类加载，发现加载过了，就不加载了

### code2

```java
package com.luban.ziya.statictest;

public class GetStaticTest2 {

    public static void main(String[] args) {
        String a = Test5.a;
    }
}

class Test5 extends Test6 {

    static {
        System.out.println("11");
    }
}

class Test6 {

    public static String a = "22";

    static {
        System.out.println("33");
    }
}
```

输出结果
```
33
```

字节码
```
0 getstatic #2 <com/luban/ziya/statictest/Test5.a>
3 astore_1
4 return
```

解释
String a = Test5.a; 对应 getstatic Test5.a，但是Test5.a实际上是在Test6.a中的，因此触发对类Test6加载，Test5是Test6的子类，不会触发类加载

### code3

```java
package com.luban.ziya.statictest;

public class GetStaticTest2 {

    public static void main(String[] args) {
        String a = Test5.a;
    }
}

class Test5 extends Test6 {

    static {
        System.out.println("11");
    }
}

class Test6 {

    public static final String a = "22";

    static {
        System.out.println("33");
    }
}
```

输出结果

无

字节码
```
0 ldc #3 <22>
2 astore_1
3 return
```

解释

从字节码中可知道，没有getstatic，相比于code2，String a是由final修饰的，直接调用ldc处理，将常量值从常量池中推送至栈顶
