
# invokestatic

调用静态方法

### code1

```java
package com.luban.ziya.statictest;

public class InvokeStaticTest1 {

    public static void main(String[] args) {
        Test1.aa();
    }
}

class Test1 {

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
0 invokestatic #2 <com/luban/ziya/statictest/Test1.aa>
3 return
```

解释

Test1.aa(); 对应着 invokestatic Test1.aa，会先触发对Test1的类加载，输出44和66，再invoke aa，输出11

### code2

```java
package com.luban.ziya.statictest;

public class InvokeStaticTest2 {

    public static void main(String[] args) {
        Test21.aa();
    }
}

class Test21 extends Test22 {

    static {
        System.out.println("11");
    }

    Test21() {
        System.out.println("22");
    }

    static void aa() {
        System.out.println("33");
    }
}

class Test22 {

    static {
        System.out.println("44");
    }

    Test22() {
        System.out.println("55");
    }

    static void aa() {
        System.out.println("66");
    }

}
```
