
# JAVA字节码手册

### 本文内容
- 记录每个字节码的含义说明
- 写一个简单的代码例子给每个字节码，用于理解

### 字节码表

如果助记符带了链接（蓝色的），点进去可以看到代码例子

|指令码|助记符|说明|
|--|--|--|
|0x12|ldc|将int型常量值从常量池中推送至栈顶|
|0x3a|astore|将栈顶引用型数值存入指定本地变量|
|0xb2|[getstatic](getstatic.md)|获取指定类的静态域，并将其值压入栈顶|
|0xb3|[putstatic](putstatic.md)|为指定的类的静态域赋值|
|0xb6|invokevirtual|调用实例方法|
|0xb7|invokespecial|调用超类构造方法，实例初始化方法，私有方法|
|0xb8|[invokestatic](invokestatic.md)|调用静态方法|
|0xb9|invokeinterface|调用接口方法|
|0xbb|[new](new.md)|创建一个对象，并将其引用值压入栈顶|
|0xbc|newarray|创建一个指定原始类型（如int, float, char…）的数组，并将其引用值压入栈顶|
|0xbd|[anewarray](anewarray.md)|创建一个引用型（如类，接口，数组）的数组，并将其引用值压入栈顶|

### 参考
- [JVM字节码指令手册](https://www.cnblogs.com/xpwi/p/11360692.html)
- [维基百科JAVA字节码介绍列表](https://en.wikipedia.org/wiki/Java_bytecode_instruction_listings)
