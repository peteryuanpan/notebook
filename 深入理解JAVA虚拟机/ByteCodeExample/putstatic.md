# putstatic

为指定的类的静态域赋值

### code1

```java
package com.peter.jvm.example;

public class ByteCodePutStaticTest1 {

    public static void main(String[] args) {
        ByteCodePutStaticTest11.a = "44";
        String a = ByteCodePutStaticTest11.a;
        System.out.println(a);
    }
}

class ByteCodePutStaticTest11 {

    static {
        System.out.println("11");
        //System.out.print(a); // 编译不过
    }

    public static String a = "22";

    static {
        System.out.println("33");
        System.out.println(a);
    }
}
```

输出结果
```
11
33
22
44
```

字节码
```
 0 ldc #2 <44>
 2 putstatic #3 <com/peter/jvm/example/ByteCodePutStaticTest11.a>
 5 getstatic #3 <com/peter/jvm/example/ByteCodePutStaticTest11.a>
 8 astore_1
 9 getstatic #4 <java/lang/System.out>
12 aload_1
13 invokevirtual #5 <java/io/PrintStream.println>
16 return
```

解释

ByteCodePutStaticTest1.a = "44"; 对应 putstatic ByteCodePutStaticTest1.a，会触发对ByteCodePutStaticTest1类加载，输出11、33、a，此时a还是22

String a = ByteCodePutStaticTest1.a; 对应 getstatic ByteCodePutStaticTest1.a，会尝试对ByteCodePutStaticTest1类加载，由于已经加载过了，不加载，a变为44

System.out.println(a); 输出44
