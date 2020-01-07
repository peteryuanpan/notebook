
# 如何理解CPU的中断及软件产生的陷阱或异常

### 基本概念

现代操作系统是中断驱动的，如果没有进程需要执行，没有I/O设备需要服务，而且没有用户需要响应，那么操作系统会静静地等待某个事件的发生。事件总是由中断或陷阱引起的。

来自维基百科的定义

> 在计算机科学中，中断（英语：Interrupt）是指处理器接收到来自硬件或软件的信号，提示发生了某个事件，应该被注意，这种情况就称为中断。通常，在接收到来自外围硬件（相对于中央处理器和内存）的异步信号，或来自软件的同步信号之后，处理器将会进行相应的硬件／软件处理。发出这样的信号称为进行中断请求（interrupt request，IRQ）。

> In digital computers, an interrupt is an input signal to the processor indicating an event that needs immediate attention. An interrupt signal alerts the processor and serves as a request for the processor to interrupt the currently executing code, so that the event can be processed in a timely manner.

事件发生通常通过硬件或软件的中断来通知。硬件可以随时通过系统总线发送信号到CPU，以触发中断。软件也可通过执行特别操作即系统调用(system call)，以触发中断。

当CPU被中断时，它停止正在做的事，并立即转到固定位置再继续执行。该固定位置通常包含中断服务程序的开始地址。中断服务程序开始执行，在执行完后，CPU重新执行被中断的计算。

陷阱(trap)（或异常(exception)）是一种软件生成的中断，或源于出错（如除零，或无效存储访问），或源于用户程序的特定请求（执行操作系统的某个服务）。这种操作系统的中断特性规定了系统的通用结构。对于每种中断，操作系统由不同代码段来处理。

### 引出问题

#### Linux/Windows操作系统是中断驱动的吗？
> 中断是计算机体系结构的重要部分。虽然每个计算机设计都有自己的中断机制，但是有些功能是共同的。中断应将控制转移到合适的中断服务程序。处理这一转移的直接方法是，调用一个通用的程序以检查中断信息。接着，该程序会调用特定的中断处理程序。不过，中断处理应当快捷。由于只有少量预先定义的中断，所以可以通过中断处理程序的指针来提高速度。这样通过指针表可以间接调用中断处理程序，而无需通过其他中介程序。通常，指针表位于低地址内存（前100左右的位置）。这些位置包含各种设置的中断处理程序的地址。这种地址的数组或中断向量（interrupt vector），对于任一给定的中断请求，可通过唯一的设备号来索引，进而提供设备的中断处理程序地址。许多不同的操作系统，如Windows或UNIX，都采用这种方式处理中断。摘自《操作系统概念》第九版 P6

#### 举几个CPU被中断/软件产生异常的例子
> 鼠标点击、键盘输入
> 接收到 http request，开始处理
> 程序中有除以整数零
> 访问无效内存
> StackOverFlow、MemoryOutOfLimit

#### JAVA执行try、catch，在操作系统底层执行了什么？
> TODO

### Reference

《操作系统概念》第九版 P5-P15
