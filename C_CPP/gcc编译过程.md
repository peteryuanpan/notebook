# gcc编译过程
```
gcc -E 1.c -o 1.i（生成预编译文件）
gcc -S 1.i -o 1.s（生成汇编代码）
gcc -c 1.s -o 1.o（生成目标文件）
gcc 1.o -o 1（链接成可执行文件）

参考：https://blog.csdn.net/xiaohouye/article/details/52084770
```
