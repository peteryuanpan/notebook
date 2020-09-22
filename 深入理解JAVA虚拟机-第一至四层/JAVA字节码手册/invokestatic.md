
# invokestatic

调用静态方法

### code1

```java
package com.peter.jvm.example;

public class ByteCodeInvokeStaticTest1 {

    public static void main(String[] args) {
        ByteCodeInvokeStaticTest11.aa();
    }
}

class ByteCodeInvokeStaticTest11 {

    {
        System.out.println("33");
    }

    static {
        System.out.println("44");
    }

    static void aa() {
        System.out.println(a);
    }

    static {
        System.out.println("66");
    }

    public static String a = new String("11");

    public String b = new String("22");

    void bb() {
        System.out.println("55");
    }
}
```

输出结果
```
44
66
11
```

字节码
```
0 invokestatic #2 <com/peter/jvm/example/ByteCodeInvokeStaticTest11.aa>
3 return
```

解释

ByteCodeInvokeStaticTest11.aa(); 对应着 invokestatic ByteCodeInvokeStaticTest11.aa，会先触发对ByteCodeInvokeStaticTest11类加载，输出44、66，再invoke aa，输出11
