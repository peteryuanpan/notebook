# new

创建一个对象，并将其引用值压入栈顶

### code1

```java
package com.peter.jvm.example;

public class ByteCodeNewTest1 {

    public static void main(String[] args) {
        ByteCodeNewTest1 t = new ByteCodeNewTest1();
    }
}

class ByteCodeNewTest12 {

    static {
        System.out.println("11");
    }

    ByteCodeNewTest12() {
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
0 new #2 <com/peter/jvm/example/ByteCodeNewTest12>
3 dup
4 invokespecial #3 <com/peter/jvm/example/ByteCodeNewTest12.<init>>
7 astore_1
8 return
```

解释

ByteCodeNewTest12 t = new ByteCodeNewTest12(); 对应 new ByteCodeNewTest12，ByteCodeNewTest12，输出11、a、a，第一个a是33，第二个a是44，然后调用构造函数，invokespecial ByteCodeNewTest12.init，输出a、a，第一个a是44，第二个a是22
