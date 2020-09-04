# putstatic

为指定的类的静态域赋值

### code1

```java
package com.luban.ziya.statictest;

public class PutStaticTest1 {

    public static void main(String[] args) {
        Test4.a = "44";
        String a = Test4.a;
        System.out.println(a);
    }
}

class Test4 {

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
 2 putstatic #3 <com/luban/ziya/statictest/Test4.a>
 5 getstatic #3 <com/luban/ziya/statictest/Test4.a>
 8 astore_1
 9 getstatic #4 <java/lang/System.out>
12 aload_1
13 invokevirtual #5 <java/io/PrintStream.println>
16 return
```

解释

Test4.a = "44"; 对应 putstatic Test4.a，会触发对Test4类加载，输出11、33、a，此时a还是22

String a = Test4.a; 对应 getstatic.Test4.a，会尝试对Test4类加载，由于已经加载过了，不加载，a变为44

System.out.println(a); 输出44
