### 题1.1

答案：B

解析
```
== 优先级高于三目运算符，先判断 true == true，此时返回为 true,
这时表达式为 boolean b = true?false:true?false:true
此时三目运算符从右向左执行，true?false:true，返回false
这时表达式为 boolean b = true?false:false;
结果为：boolean b = false;
```
