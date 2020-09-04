
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

Test1.aa(); 对应着 invokestatic Test1.aa，会先触发对Test1类加载，输出44、66，再invoke aa，输出11

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

    static {
        System.out.println("44");
    }
}

class Test22 {

    static {
        System.out.println("55");
    }

    Test22() {
        System.out.println("66");
    }

    static void aa() {
        System.out.println("77");
    }

    static {
        System.out.println("88");
    }

}
```

输出结果
```
55
88
11
44
33
```

字节码
```
0 invokestatic #2 <com/luban/ziya/statictest/Test21.aa>
3 return
```

解释

Test21.aa(); 对应着 invokestatic Test21.aa，会先触发对Test22类加载（加载子类前会先加载父类），输出55、88，再对Test21类加载，输出11、44，再invoke aa，输出33

### code3

```java
package com.luban.ziya.statictest;

public class InvokeStaticTest2 {

    public static void main(String[] args) {
        Test22.aa();
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

    static {
        System.out.println("44");
    }
}

class Test22 {

    static {
        System.out.println("55");
    }

    Test22() {
        System.out.println("66");
    }

    static void aa() {
        System.out.println("77");
    }

    static {
        System.out.println("88");
    }

}
```

输出结果
```
55
88
77
```

字节码
```
0 invokestatic #2 <com/luban/ziya/statictest/Test22.aa>
3 return
```

解释

Test22.aa();（code2 是 Test21().aa）对应着invokestatic Test22.aa，会先触发对Test22类加载（但不会加载Test21类，加载父类不会去加载子类），输出55、88，再invoke aa，输出77
