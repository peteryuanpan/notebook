
# Java字节码例子解读

### 列表

|字段|原文|理解|例子|
|--|--|--|--|
|astore<br>dstore<br>fstore<br>istore<br>lstore|store a reference into a local variable #index<br>store a double value into a local variable #index<br>store a float value into a local variable #index<br>store int value into variable #index<br>store a long value in a local variable #index|将value存储进一个局部变量<br>TODO：int value包括那些类型？|TODO：link|
|ldc|push a constant #index from a constant pool (String, int, float, Class, java.lang.invoke.MethodType, java.lang.invoke.MethodHandle, or a dynamically-computed constant) onto the stack|从常量池中取出一个常量放入栈中|TODO：link|
|invokespecial|invoke instance method on object objectref and puts the result on the stack (might be void); the method is identified by method reference index in constant pool|调用实例方法，将结果放于栈上|TODO：link|

### 参考
- https://en.wikipedia.org/wiki/Java_bytecode_instruction_listings
