- [JAVA线程基础](#JAVA线程基础])
  - [进程与线程](#进程与线程)
  - [并发与并行](#并发与并行)
  - [启动线程](#启动线程)
    - [JAVA启动线程](#JAVA启动线程)
      - [Thread](#Thread)
      - [Runnable](#Runnable)
      - [Callable](#Callable)
      - [Thread类源码分析](#Thread类源码分析)
      - [ExecutorService](#ExecutorService)
    - [JVM启动线程](#JVM启动线程)
  - [线程的生命周期](#线程的生命周期)
    - [Thread的6种State](#Thread的6种State)
    - [等待唤醒机制](#等待唤醒机制)
    - [线程中断机制](#线程中断机制)
  - [线程安全问题](#线程安全问题)
    - [synchonized](#synchonized)
    - [CAS与原子类](#CAS与原子类)
    - [AQS与锁体系](#AQS与锁体系)
    - [ThreadLocal](#ThreadLocal)
  - [并发基础概念](#并发基础概念)
    - [线程上下文切换](#线程上下文切换)
    - [多线程模型](#多线程模型)
    - [用户态与内核态](#用户态与内核态)
    - [轻量级线程之协程](#轻量级线程之协程)

# JAVA线程基础

> 我重新规划了这篇文章的内容，之前的标题是“JAVA线程”，现在多了“基础”二字。在看了《JAVA并发编程之美》后，个人同意将线程的基础概念作为第一章节介绍很有必要

> 并发中一个经典的问题是：进程与线程的区别是什么。这个问题虽然是老生常谈了，但一千个回答者就会有一千种答案，资历不同的人回答得深度和感受也是不一样的，比如“只是会背概念”、“实战过JAVA多线程程序，从实战中理解”、“接触过CPU底层调试，从汇编层理解”这三种层次，阅历深的面试官是可以看出来你在什么层次，这也是该问题时常作为并发面试问题首选的原因

> 围绕线程展开的基础知识有很多，比如：进程&线程的区别，JAVA线程创建和运行的多种方式，JAVA线程生命周期，线程死锁，ThreadLocal，synchronized&volatile关键字，CAS等等，其中有一些概念是值得拓展的，比如synchonized锁升级优化，volatile=>JAVA内存模型，CAS=>AQS等，有必要的，我会在其他章节进行展开

> 上面所述的基础知识就是本章的核心点

### 进程与线程

基础篇，也从这个老生常谈的问题开始：进程&线程的区别是什么？

我们不妨先来看一下定义，搜索了一下维基百科，得到如下

> In computing, a process is the instance of a computer program that is being executed by one or many threads. It contains the program code and its activity.

> In computer science, a thread of execution is the smallest sequence of programmed instructions that can be managed independently by a scheduler. In most cases a thread is a component of a process.

上面提到了几点，一是进程是代码在数据集合上的一次运行活动，二是线程是程序执行的最小单位，三是一个进程可能包含多个线程

光从定义来看，似乎还不够透彻，因此在搜索了多方资料后，我得到如下总结
- 进程，直观点说，保存在硬盘上的程序运行以后，会在内存空间里形成一个独立的内存体，这个内存体有自己独立的地址空间，有自己的堆，上级挂靠单位是操作系统。操作系统会以进程为单位，分配系统资源（CPU时间片、内存等资源），进程是代码在数据集合上的一次运行活动，进程是操作系统资源分配的最小单位
- 线程，有时被称为轻量级进程（Lightweight Process，LWP），线程是进程中的一个实体，线程本身是不会独立存在的。操作系统在分配资源时是把资源分配给进程的，但是CPU资源比较特殊，它是被分配到线程的，真正占用CPU运行的是线程，线程是CPU调度与执行的最小单位
- 程序计数器是一块线程私有的内存区域，用于记录线程执行程序地址，它被设计为私有的，原因是线程是CPU调度与执行的基本单位，而CPU一般是使用时间片轮转方式让线程轮询占用的，所以当线程CPU时间片用完后，要让出CPU，等下次轮到自己的时候再执行，所以需要一块内存地址保存上一次的执行地址

这里有几个关键点
- **（一）进程——操作系统资源分配最小单位**
- **（二）线程（轻量级进程）——CPU调度与执行的最小单位**
- **（三）CPU时间片轮询方法执行每个进程的线程 => 程序计数器保存上一次线程执行地址**

从这里我们可以画出一幅图来加强理解与记忆

![image](https://user-images.githubusercontent.com/10209135/97282506-b57e6100-1879-11eb-98f6-e9d396af54ce.png)

进程与线程的区别及联系

【区别】
- 调度：线程作为调度和分配的基本单位，进程作为拥有资源的基本单位
- 并发性：不仅进程之间可以并发执行，同一个进程的多个线程之间也可并发执行
- 拥有资源：进程是拥有资源的一个独立单位，线程不拥有系统资源，但可以访问隶属于进程的资源。进程所维护的是程序所包含的资源（静态资源）， 如：地址空间，打开的文件句柄集，文件系统状态，信号处理handler等；线程所维护的运行相关的资源（动态资源），如：运行栈，调度相关的控制信息，待处理的信号集等
- 系统开销：在创建或撤消进程时，由于系统都要为之分配和回收资源，导致系统的开销明显大于创建或撤消线程时的开销。但是进程有独立的地址空间，一个进程崩溃后，在保护模式下不会对其它进程产生影响，而线程只是一个进程中的不同执行路径。线程有自己的堆栈和局部变量，但线程之间没有单独的地址空间，一个进程死掉就等于所有的线程死掉，所以多进程的程序要比多线程的程序健壮，但在进程切换时，耗费资源较大，效率要差一些

【联系】
- 一个线程只能属于一个进程，而一个进程可以有多个线程，但至少有一个线程
- 资源分配给进程，同一进程的所有线程共享该进程的所有资源
- 处理机分给线程，即真正在处理机上运行的是线程
- 线程在执行过程中，需要协作同步，不同进程的线程间要利用消息通信的办法实现同步

> 写到这里，关于进程与线程的基本概念就说完了，对于JAVA业务程序员来说，能把上面理解其实已经足够了。然而，如果你要“打破砂锅问到底”的话，再往下深入想，可能会总感觉进程与线程的概念还是比较抽象，没有形成具象的印象，这个时候就需要去研究一下CPU的工作原理了，仔细揣摩CPU时间片到底是如何运行的。关于思考出的问题，我举一些例子。既然进程是资源分配的最小单位，那么每个进程都分配了独立的时间片，可是在并发与并行（下文会有）的概念中，单核CPU可以并发但不可以并行，只有多核CPU可以并行，既然如此的话，是否一个核中的多个进程，时间片轮序顺序也是序列化的呢（一个进程抢占了时间片，其他进程必须等待）？还有，线程与线程之间是共享资源的，利用消息通信实现同步，这里的消息通信具体是指什么方式？进程与进程之间又是通过什么方法来通信的？诸如此类的问题，只要你深想，就会出来，要去真正的理解，需要往CPU层原理上去学，这一块不是本篇文章的重点，因此不会全部包含

### 并发与并行

来看一下并发与并行的区别，同样先从定义开始，搜了下维基百科

> In computer science, concurrency is the ability of different parts or units of a program, algorithm, or problem to be executed out-of-order or in partial order, without affecting the final outcome.

> Parallel computing is a type of computation where many calculations or the execution of processes are carried out simultaneously.

在英文定义中，并行（Parallel）强调了一个单词 simultaneously，即同一时刻，而并发（Concurrency）没有强调同一时刻

通过资料总结如下
- 广义的说，并发是指同一个时间段内多个任务同时都在执行，且都没有执行结束
- 广义的说，并行是指单位时间内多个任务同时执行，同一个时间段包含多个单位时间
- 并行是并发的真子集（数学上的定义），并发的多个任务在单位时间内不一定同时在执行
- 侠义的说，并发是指在同一时刻，只有一条指令在处理器上执行，但多个指令被快速轮换执行，使得宏观上有同时执行的效果，而微观上没有
- 侠义的说，并行是指在同一时刻，有多条指令在多个处理器上同时执行，在宏观和微观都是同时执行的
- 单核CPU只能并发，多核CPU既可并发也可并行

> 上面广义的定义来自《JAVA并发编程之美》，侠义的定义来自于鲁班学院的总结，个人认为回答这个问题时，强调侠义的定义即可，尤其是回答出“并发在微观不是同时执行，在宏观是同时执行，并行在宏观与微观均是同时执行”，然后心里知晓广义的定义上，并行是并发的真子集即可

> 一般口语中所说的“JAVA并发”，是指多线程下的并发，往往忽略了并行、多核CPU的概念，因为虚拟机让程序员一般不需要关心多核可能带来的问题。线程才是重点探讨的对象，而重中之重是线程安全问题

国际惯例，为了更容易理解与记忆，画了如下图解

![image](https://user-images.githubusercontent.com/10209135/97303836-e5d3f880-1895-11eb-8344-9ed5fc38dbc7.png)

### 启动线程

#### JAVA启动线程

启动 = 创建 + 运行

平时广泛所说的，JAVA启动线程有4种方式
- Thread
- Runnable
- Callable
- ExecutorService

实际上，JAVA语法层面启动一个线程，**本质只有一种方法：implements Runnable + new Thread(Runnable a).start()**

##### Thread

```java
package part1;

public class ThreadNewThread extends Thread {

    @Override
    public void run() {
        System.out.println("hello");
    }

    public static void main(String[] args) {
        new ThreadNewThread().start();
    }
}
```

输出结果
```
hello
```

解释：继承Thread类，new一个Thread(Runnable a)对象，执行start()方法，完成创建、运行线程

本方法好处是可以再run方法内使用this对象（指向堆区中的一个Thread对象），无须使用Thread.currentThread()，坏处是主类不能再继承其他类了（JAVA不支持多继承）

##### Runnable

```java
package part1;

public class RunnableNewThread implements Runnable {

    @Override
    public void run() {
        System.out.println("hello");
    }

    public static void main(String[] args) {
        new Thread(new RunnableNewThread()).start();
    }
}
```

输出结果
```
hello
```

解释：实现Runnable，new一个Thread(Runnable a)对象，执行start()方法，完成创建、运行线程

本方法的好处与继承Thread的坏处是相对应的，即主类还能继承其他类

上面继承Thread和实现Runnable两种方法，其实有“殊途同归”的意思，本质都是一样的

在编码过程中，下面这样写是比较推荐的，既简洁又易懂，其中()->{}用的是JAVA8中的lambda表达式

```java
package part1;

public class LambaNewThread {

    public static void main(String[] args) {
        new Thread(() -> {
            System.out.println("hello");
        });
    }
}
```

输出结果
```
hello
```

##### Callable

```java
package part1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallerTaskNewThread implements Callable<String> {

    @Override
    public String call() throws Exception {
        return "hello";
    }

    public static void main(String[] args) {
        FutureTask<String> futureTask = new FutureTask<>(new CallerTaskNewThread());
        new Thread(futureTask).start();
        try {
            String result = futureTask.get();
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
```

输出结果
```
hello
```

解释：

##### Thread类源码分析

Thread
```java
package java.lang;
public
class Thread implements Runnable {
    /* Make sure registerNatives is the first thing <clinit> does. */
    private static native void registerNatives();
    static {
        registerNatives();
    }
...
    /* What will be run. */
    private Runnable target;

    /* The group of this thread */
    private ThreadGroup group;

    /* The context ClassLoader for this thread */
    private ClassLoader contextClassLoader;
...
    /**
     * Allocates a new {@code Thread} object. This constructor has the same
     * effect as {@linkplain #Thread(ThreadGroup,Runnable,String) Thread}
     * {@code (null, null, gname)}, where {@code gname} is a newly generated
     * name. Automatically generated names are of the form
     * {@code "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
     */
    public Thread() {
        init(null, null, "Thread-" + nextThreadNum(), 0);
    }

    /**
     * Allocates a new {@code Thread} object. This constructor has the same
     * effect as {@linkplain #Thread(ThreadGroup,Runnable,String) Thread}
     * {@code (null, target, gname)}, where {@code gname} is a newly generated
     * name. Automatically generated names are of the form
     * {@code "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
     *
     * @param  target
     *         the object whose {@code run} method is invoked when this thread
     *         is started. If {@code null}, this classes {@code run} method does
     *         nothing.
     */
    public Thread(Runnable target) {
        init(null, target, "Thread-" + nextThreadNum(), 0);
    }
    /**
     * Initializes a Thread.
     *
     * @param g the Thread group
     * @param target the object whose run() method gets called
     * @param name the name of the new Thread
     * @param stackSize the desired stack size for the new thread, or
     *        zero to indicate that this parameter is to be ignored.
     * @param acc the AccessControlContext to inherit, or
     *            AccessController.getContext() if null
     * @param inheritThreadLocals if {@code true}, inherit initial values for
     *            inheritable thread-locals from the constructing thread
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc,
                      boolean inheritThreadLocals) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        this.name = name;

        Thread parent = currentThread();
        SecurityManager security = System.getSecurityManager();
        if (g == null) {
            /* Determine if it's an applet or not */

            /* If there is a security manager, ask the security manager
               what to do. */
            if (security != null) {
                g = security.getThreadGroup();
            }

            /* If the security doesn't have a strong opinion of the matter
               use the parent thread group. */
            if (g == null) {
                g = parent.getThreadGroup();
            }
        }

        /* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. */
        g.checkAccess();

        /*
         * Do we have the required permissions?
         */
        if (security != null) {
            if (isCCLOverridden(getClass())) {
                security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }

        g.addUnstarted();

        this.group = g;
        this.daemon = parent.isDaemon();
        this.priority = parent.getPriority();
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;
        setPriority(priority);
        if (inheritThreadLocals && parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        /* Stash the specified stack size in case the VM cares */
        this.stackSize = stackSize;

        /* Set thread ID */
        tid = nextThreadID();
    }
...
    /**
     * Causes this thread to begin execution; the Java Virtual Machine
     * calls the <code>run</code> method of this thread.
     * <p>
     * The result is that two threads are running concurrently: the
     * current thread (which returns from the call to the
     * <code>start</code> method) and the other thread (which executes its
     * <code>run</code> method).
     * <p>
     * It is never legal to start a thread more than once.
     * In particular, a thread may not be restarted once it has completed
     * execution.
     *
     * @exception  IllegalThreadStateException  if the thread was already
     *               started.
     * @see        #run()
     * @see        #stop()
     */
    public synchronized void start() {
        /**
         * This method is not invoked for the main method thread or "system"
         * group threads created/set up by the VM. Any new functionality added
         * to this method in the future may have to also be added to the VM.
         *
         * A zero status value corresponds to state "NEW".
         */
        if (threadStatus != 0)
            throw new IllegalThreadStateException();

        /* Notify the group that this thread is about to be started
         * so that it can be added to the group's list of threads
         * and the group's unstarted count can be decremented. */
        group.add(this);

        boolean started = false;
        try {
            start0();
            started = true;
        } finally {
            try {
                if (!started) {
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {
                /* do nothing. If start0 threw a Throwable then
                  it will be passed up the call stack */
            }
        }
    }

    private native void start0();
    
     /**
     * If this thread was constructed using a separate
     * <code>Runnable</code> run object, then that
     * <code>Runnable</code> object's <code>run</code> method is called;
     * otherwise, this method does nothing and returns.
     * <p>
     * Subclasses of <code>Thread</code> should override this method.
     *
     * @see     #start()
     * @see     #stop()
     * @see     #Thread(ThreadGroup, Runnable, String)
     */
    @Override
    public void run() {
        if (target != null) {
            target.run();
        }
    }
```

Runnable
```java
package java.lang;
public interface Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see     java.lang.Thread#run()
     */
    public abstract void run();
}
```

Thread类是implements Runnable的，Runnable是一个接口，其中有一个run方法，当执行start()方法后，内部会走到一个native修饰的start0方法，调用JDK源码来启动线程

new Thread(Runnable a)对象时，会执行init方法，将Runnable a赋值给实例变量Runnable target，执行start()方法启动线程后，会执行run()方法，执行target.run()

new Thread(Runnable a)对象后，线程并没有直接运行，执行start方法后才开始运行线程，这个过程中，线程会先处于就绪状态（指该线程已经获取了除CPU资源外的其他资源），然后在获取CPU资源后，才处于真正的运行状态，一旦run方法执行完毕，线程就处于终止状态了

##### ExucutorService

#### JVM启动线程

...

### 线程安全问题

#### synchonized

#### CAS与原子类

#### AQS与锁体系

#### ThreadLocal

### 并发基础概念

#### 线程上下文切换

线程上下文的切换巧妙的利用了时间片轮转的方式（保证CPU的利用率），CPU给每个任务都服务一定的时间，然后把当前任务的状态保存下来，在加载下一任务的状态后，继续服务下一个任务。线程状态的保存及其再加载，就是线程的上下文切换

在同一个时刻，单核CPU时间片只能处理一个线程上的任务，其他进程和线程必须等待，当线程A使用完时间片后，会进行线程上下文切换，保存当前线程A的执行现场，然后让线程B抢占时间片，若线程B不是第一次进行，要恢复线程B的执行现场（先前已保存）

![image](https://user-images.githubusercontent.com/10209135/97309100-91804700-189c-11eb-821f-225c1aaaa097.png)

CPU处理的速度是非常快的（要有这个概念），相对于人来说，进程和线程的上下文切换时间都也是非常短的，而内部比较，进程的上下文切换比线程的上下文文切换开销要大得多

线程上下文切换的时机
- 当前线程的CPU时间片使用完处于就绪状态
- 当前线程被其他线程中断

#### 多线程模型

用户线程、内核线程、一对一模型、多对一模型、多对多模型

《操作系统概念》、《深入理解JAVA虚拟机》

#### 用户态与内核态

#### 轻量级线程之协程
