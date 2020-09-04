### anewarray

### code1

```java
package com.luban.ziya.newtest;

public class ANewArrayTest1 {

    public static void main(String[] args) {
        Test2[] a = new Test2[3];
    }

}

class Test2 {

    static {
        System.out.println("11");
    }

    public static String a = "22";

    static {
        System.out.println(a);
        System.out.println("33");
    }

}
```

输出结果

无

字节码
```
0 iconst_3
1 anewarray #2 <com/luban/ziya/newtest/Test2>
4 astore_1
5 return
```

解释

Test2[] a = new Test2[3]; 对应 iconst_3 好 anewarray Test2，这一步是创建一个引用类型数组变量，并不会去进行类加载，因此无输出

### code2

```java
package com.luban.ziya.newtest;

public class ANewArrayTest1 {

    public static void main(String[] args) {
        Test2[] a = new Test2[3];
        System.out.println("00");
        for (int i = 0; i < 3; i ++) {
            a[i].a = "44";
        }
    }

}

class Test2 {

    static {
        System.out.println("11");
    }

    public static String a = "22";

    static {
        System.out.println(a);
        System.out.println("33");
    }

}
```

输出结果
```
00
11
22
33
```

字节码
```
 0 iconst_3
 1 anewarray #2 <com/luban/ziya/newtest/Test2>
 4 astore_1
 5 getstatic #3 <java/lang/System.out>
 8 ldc #4 <00>
10 invokevirtual #5 <java/io/PrintStream.println>
13 iconst_0
14 istore_2
15 iload_2
16 iconst_3
17 if_icmpge 35 (+18)
20 aload_1
21 iload_2
22 aaload
23 pop
24 ldc #6 <44>
26 putstatic #7 <com/luban/ziya/newtest/Test2.a>
29 iinc 2 by 1
32 goto 15 (-17)
35 return
```

解释

code2在code1基础上多了
```
        System.out.println("00");
        for (int i = 0; i < 3; i ++) {
            a[i].a = "44";
        }
```

Test2[] a = new Test2[3]; 并不会类加载，因此不会输出

System.out.println("00"); 输出00

for循环内 a[0].a = "44"; 会触发Test2类加载，因此输出11、a、33，此时a是22

继续，a[1].a = "44"; ... a[2].a = "44"; 会尝试类加载，但由于Test2类加载过了，不会再加载，也就没输出了
