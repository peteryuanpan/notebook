
```
➜  ~ cat a.cpp
#include <iostream>

int main() {
	int a = 10;
	int * b = &a;
	std::cout << a << std::endl;
	std::cout << &a << std::endl;
	std::cout << b << std::endl;
	std::cout << *b << std::endl;
	std::cout << &b << std::endl;
	return 0;
}
➜  ~ g++ a.cpp -o exec_a; ./exec_a
10
0x7ffeeed1c688
0x7ffeeed1c688
10
0x7ffeeed1c680
➜  ~ cat b.cpp
#include <iostream>

int main() {
	char a = 'x';
	char * b = &a;
	std::cout << a << std::endl;
	std::cout << &a << std::endl;
	std::cout << b << std::endl;
	std::cout << *b << std::endl;
	std::cout << &b << std::endl;
	return 0;
}
➜  ~ g++ b.cpp -o exec_b; ./exec_b
x
x
x
x
0x7ffee2079680
➜  ~ cat b2.cpp
#include <iostream>

int main() {
	char a = 'x';
	char * b = &a;
	printf("%c\n", a);
	printf("%p\n", &a);
	printf("%p\n", b);
	printf("%d\n", *b);
	printf("%p\n", &b);
	return 0;
}
➜  ~ g++ b2.cpp -o exec_b2; ./exec_b2
x
0x7ffee487c6db
0x7ffee487c6db
120
0x7ffee487c6d0
```

```
➜  ~ cat a.cpp
#include <cstdio>

int main() {
	int a1 = 1;
	int * a2 = &a1;
	printf("%lu\n", sizeof(a1));
	printf("%lu\n", sizeof(a2));
	printf("%lu\n", sizeof(*a2));

	char b1 = '1';
	char * b2 = &b1;
	printf("%lu\n", sizeof(b1));
        printf("%lu\n", sizeof(b2));
        printf("%lu\n", sizeof(*b2));

	unsigned long long c1 = 111111;
	unsigned long long * c2 = &c1;
        printf("%lu\n", sizeof(c1));
        printf("%lu\n", sizeof(c2));
        printf("%lu\n", sizeof(*c2));
	return 0;
}
➜  ~ g++ a.cpp -o exec_a; ./exec_a
4
8
4
1
8
1
8
8
8
```
