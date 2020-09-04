# getstatic

获取指定类的静态域，并将其值压入栈顶

### code1

```java
package com.peter.jvm.example;

public class ByteCodeGetStaticTest1 {
    
    public static void main(String[] args) {
        String a = ByteCodeGetStaticTest11.a;
        String b = ByteCodeGetStaticTest11.a;
    }
}

class ByteCodeGetStaticTest11 {

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
0 getstatic #2 <com/peter/jvm/example/ByteCodeGetStaticTest11.a>
3 astore_1
4 getstatic #2 <com/peter/jvm/example/ByteCodeGetStaticTest11.a>
7 astore_2
8 return
```

解释

String a = ByteCodeGetStaticTest11.a; 对应 getstatic ByteCodeGetStaticTest11.a，会触发ByteCodeGetStaticTest11的类加载，输出11、33

String b = ByteCodeGetStaticTest11.a; 对应 getstatic ByteCodeGetStaticTest11.a，会尝试ByteCodeGetStaticTest11的类加载，发现加载过了，就不加载了
