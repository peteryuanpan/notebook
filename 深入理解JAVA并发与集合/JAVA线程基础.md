- [JAVA线程基础](#JAVA线程基础])
  - [进程与线程](#进程与线程)
  - [并发与并行](#并发与并行)
  - [启动线程](#启动线程)
    - [JAVA启动线程](#JAVA启动线程)
      - [Thread](#Thread)
      - [Runnable](#Runnable)
      - [Callable](#Callable)
      - [ExecutorService](#ExecutorService)
    - [JVM启动线程](#JVM启动线程)
  - [线程的生命周期](#线程的生命周期)
  - [等待唤醒机制](#等待唤醒机制)
    - [虚假唤醒](#虚假唤醒)
    - [Object等待唤醒](#Object等待唤醒)
    - [Thread等待唤醒](#Thread等待唤醒)
    - [LockSupport等待唤醒](#LockSupport等待唤醒)
  - [线程中断机制](#线程中断机制)
  - [并发基础概念](#并发基础概念)
    - [线程上下文切换](#线程上下文切换)
    - [多线程模型](#多线程模型)
    - [用户态与内核态](#用户态与内核态)
    - [轻量级线程之协程](#轻量级线程之协程)

# JAVA线程基础

> 我重新规划了这篇文章的内容，之前的标题是“JAVA线程”，现在多了“基础”二字。在看了《JAVA并发编程之美》后，个人同意将线程的基础概念作为第一章节介绍很有必要

> 并发中一个经典的问题是：进程与线程的区别是什么。这个问题虽然是老生常谈了，但一千个回答者就会有一千种答案，资历不同的人回答得深度和感受也是不一样的，比如“只是会背概念”、“实战过JAVA多线程程序，从实战中理解”、“接触过CPU底层调试，从汇编层理解”这三种层次，阅历深的面试官是可以看出来你在什么层次，这也是该问题时常作为并发面试问题首选的原因

> 围绕线程展开的基础知识有很多，比如：进程&线程的区别，并发&并行的区别，启动线程，线程的生命周期，线程安全问题，synchronized&volatile关键字，CAS与AQS等等，其中有一些概念是值得拓展的，比如synchonized锁升级优化，volatile=>JAVA内存模型，AQS=>锁体系等，有必要的，我会在其他章节进行展开

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

关于Thread类的源码分析，可见[Thread](源码分析/Thread.md)

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

解释：new Thread(futureTask).start();这句话表示本质上与前面2种方法一样，也是实现Runnable，new一个Thread(Runnable a)对象，执行start()方法，来完成创建、运行线程

而不同的点是，FutureTask是实现了Runnable与Future接口，它是一个异步处理框架，通过get方法来异步获取线程执行结果，关于Future源码分析，可见[FutureTask](源码分析/FutureTask.md)

相比于前2种方法，本方法好处是可以获得一个任务的返回值

##### ExecutorService

```java
package part1;

import java.util.concurrent.*;

public class ExecutorServiceNewThread {

    public static void main(String[] args) {
        ExecutorService pool = Executors.newSingleThreadExecutor();
        Future<String> future = pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "hello";
            }
        });
        pool.shutdown();
        try {
            System.out.println(future.get());
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

解释：使用了线程池的方法，来提交并执行任务，但底层本质上与前面几种方法一样

来看一下pool.submit后会发生什么
- AbstractExecutorService.submit，新建并执行任务ftask
- execute(ftask)到下一步
```java
    /**
     * @throws RejectedExecutionException {@inheritDoc}
     * @throws NullPointerException       {@inheritDoc}
     */
    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<T> ftask = newTaskFor(task);
        execute(ftask);
        return ftask;
    }
```
- ThreadPoolExecutor.execute，尝试添加任务到工作队列
- addWorker(command, true)到下一步
```java
    public void execute(Runnable command) {
        if (command == null)
            throw new NullPointerException();
            
        int c = ctl.get();
        if (workerCountOf(c) < corePoolSize) {
            if (addWorker(command, true))
                return;
            c = ctl.get();
        }
        if (isRunning(c) && workQueue.offer(command)) {
            int recheck = ctl.get();
            if (! isRunning(recheck) && remove(command))
                reject(command);
            else if (workerCountOf(recheck) == 0)
                addWorker(null, false);
        }
        else if (!addWorker(command, false))
            reject(command);
    }
```
- ThreadPoolExecutor.addWorker，新建一个任务Worker，加入到HashSet<Worker> workers
- w = new Worker(firstTask); 到下一步
```java
    private boolean addWorker(Runnable firstTask, boolean core) {
        retry:
        for (;;) {
            int c = ctl.get();
            int rs = runStateOf(c);

            // Check if queue empty only if necessary.
            if (rs >= SHUTDOWN &&
                ! (rs == SHUTDOWN &&
                   firstTask == null &&
                   ! workQueue.isEmpty()))
                return false;

            for (;;) {
                int wc = workerCountOf(c);
                if (wc >= CAPACITY ||
                    wc >= (core ? corePoolSize : maximumPoolSize))
                    return false;
                if (compareAndIncrementWorkerCount(c))
                    break retry;
                c = ctl.get();  // Re-read ctl
                if (runStateOf(c) != rs)
                    continue retry;
                // else CAS failed due to workerCount change; retry inner loop
            }
        }

        boolean workerStarted = false;
        boolean workerAdded = false;
        Worker w = null;
        try {
            w = new Worker(firstTask);
            final Thread t = w.thread;
            if (t != null) {
                final ReentrantLock mainLock = this.mainLock;
                mainLock.lock();
                try {
                    // Recheck while holding lock.
                    // Back out on ThreadFactory failure or if
                    // shut down before lock acquired.
                    int rs = runStateOf(ctl.get());

                    if (rs < SHUTDOWN ||
                        (rs == SHUTDOWN && firstTask == null)) {
                        if (t.isAlive()) // precheck that t is startable
                            throw new IllegalThreadStateException();
                        workers.add(w);
                        int s = workers.size();
                        if (s > largestPoolSize)
                            largestPoolSize = s;
                        workerAdded = true;
                    }
                } finally {
                    mainLock.unlock();
                }
                if (workerAdded) {
                    t.start();
                    workerStarted = true;
                }
            }
        } finally {
            if (! workerStarted)
                addWorkerFailed(w);
        }
        return workerStarted;
    }
```
- ThreadPoolExecutor.Worker，实例化一个任务
- this.thread = getThreadFactory().newThread(this); 到下一步
```java
    private final class Worker
        extends AbstractQueuedSynchronizer
        implements Runnable
    {
...
        Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }
```
- DefaultThreadFactory.newThread新建一个线程，从这里可以看出来，本质上还是通过new Thread(Runnable a)来创建线程
```java
    /**
     * The default thread factory
     */
    static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                                  Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" +
                          poolNumber.getAndIncrement() +
                         "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement(),
                                  0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
 ```
 
 上面通过分析源码看出来ExecutorService本质上还是使用new Thread(Runnable a)来创建线程的，而运行线程的逻辑是如何的，这一块不妨放到线程池原理中展开

#### JVM启动线程

上面介绍了JAVA语法层面启动线程的4种方法，而它们本质上都是同一种方式，即 implements Runnable + new Thread(Runnable a).start()

现在来看一下，在JVM底层中，new Thread(Runnable a).start() 内部是如何实现的

JVM底层启动线程详细图解（建议下载到本地打开查看更清晰）
![image](https://user-images.githubusercontent.com/10209135/95410102-af215700-0955-11eb-8a91-9b25e3f18416.png)

这里有几个关键点
- JAVA线程实际属于内核线程，线程的生命周期（创建、运行、销毁）是由内核管理的，JVM不具备CPU调度的权限，操作系统才具备CPU调度的权限
- new Thread(Runnable a).start() 最终会走到Thread中的 start0() 方法，在JVM内部会创建一个对象OSThread，JVM底层会调用操作系统的内核库os::create_thread方法来创建线程（比如linux的pthread库中的pthread_create方法）
- start0方法建立了一种映射关系，即（1）new Thread(Runnable a).start() =>（2）JVM的OSThread对象 =>（3）内核库os::create_thread方法。其中（1）到（2）是用户态，（2）到（3）是内核态，这里就涉及到了用户态与内核态的切换
- 使用操作系统内核库os::create_thread方法创建线程后，线程状态为 NEW（新建）状态，内部会调用sync->wait方法进入等待（个人认为不要理解为WAITING状态为好，只是JVM内部的等待），只有当执行了的Thread#start0方法后，才会唤醒线程，进入 READY（就绪）状态，就绪状态下还未获取到CPU资源，当线程被分配到CPU时间片后，线程就进入了真正的 RUNNING（运行）了，然后会调用Thread#run方法运行任务

上面的关键点可以引出一个**面试题：为什么不能直接执行 run() 方法，而需要执行 start() 方法来启动线程？**

**回答**：一个线程创建之后它将处于 NEW（新建）状态，在调用 start() 方法后才开始运行，线程这时候处于 READY（就绪）状态，就绪状态的线程获得了 CPU 时间片后就处于 RUNNING（运行）状态，随后会调用 run() 方法执行任务，而直接调用 run() 方法是不会在JVM内部启动线程的

### 线程的生命周期

在Thread类中，有一个枚举类enum State，它定义了6种线程状态，分别是NEW、RUNNABLE、BLOCKED、WAITING、TIMED_WAITING、TERMINATED

```java
    /**
     * A thread state.  A thread can be in one of the following states:
     * <ul>
     * <li>{@link #NEW}<br>
     *     A thread that has not yet started is in this state.
     *     </li>
     * <li>{@link #RUNNABLE}<br>
     *     A thread executing in the Java virtual machine is in this state.
     *     </li>
     * <li>{@link #BLOCKED}<br>
     *     A thread that is blocked waiting for a monitor lock
     *     is in this state.
     *     </li>
     * <li>{@link #WAITING}<br>
     *     A thread that is waiting indefinitely for another thread to
     *     perform a particular action is in this state.
     *     </li>
     * <li>{@link #TIMED_WAITING}<br>
     *     A thread that is waiting for another thread to perform an action
     *     for up to a specified waiting time is in this state.
     *     </li>
     * <li>{@link #TERMINATED}<br>
     *     A thread that has exited is in this state.
     *     </li>
     * </ul>
     *
     * <p>
     * A thread can be in only one state at a given point in time.
     * These states are virtual machine states which do not reflect
     * any operating system thread states.
     *
     * @since   1.5
     * @see #getState
     */
    public enum State {
        /**
         * Thread state for a thread which has not yet started.
         */
        NEW,

        /**
         * Thread state for a runnable thread.  A thread in the runnable
         * state is executing in the Java virtual machine but it may
         * be waiting for other resources from the operating system
         * such as processor.
         */
        RUNNABLE,

        /**
         * Thread state for a thread blocked waiting for a monitor lock.
         * A thread in the blocked state is waiting for a monitor lock
         * to enter a synchronized block/method or
         * reenter a synchronized block/method after calling
         * {@link Object#wait() Object.wait}.
         */
        BLOCKED,

        /**
         * Thread state for a waiting thread.
         * A thread is in the waiting state due to calling one of the
         * following methods:
         * <ul>
         *   <li>{@link Object#wait() Object.wait} with no timeout</li>
         *   <li>{@link #join() Thread.join} with no timeout</li>
         *   <li>{@link LockSupport#park() LockSupport.park}</li>
         * </ul>
         *
         * <p>A thread in the waiting state is waiting for another thread to
         * perform a particular action.
         *
         * For example, a thread that has called <tt>Object.wait()</tt>
         * on an object is waiting for another thread to call
         * <tt>Object.notify()</tt> or <tt>Object.notifyAll()</tt> on
         * that object. A thread that has called <tt>Thread.join()</tt>
         * is waiting for a specified thread to terminate.
         */
        WAITING,

        /**
         * Thread state for a waiting thread with a specified waiting time.
         * A thread is in the timed waiting state due to calling one of
         * the following methods with a specified positive waiting time:
         * <ul>
         *   <li>{@link #sleep Thread.sleep}</li>
         *   <li>{@link Object#wait(long) Object.wait} with timeout</li>
         *   <li>{@link #join(long) Thread.join} with timeout</li>
         *   <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
         *   <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
         * </ul>
         */
        TIMED_WAITING,

        /**
         * Thread state for a terminated thread.
         * The thread has completed execution.
         */
        TERMINATED;
    }
```

6种状态的转换关系图解

![image](https://user-images.githubusercontent.com/10209135/97520335-eecbe380-19d5-11eb-8f81-5a6ce563b594.png)

6种状态解析
- NEW，初始状态，表示线程被初始化（new Thread()），但未执行start方法
- RUNNABLE，运行状态，Java线程将操作系统中的就绪和运行两种状态笼统地称作“运行状态”
  - READY，就绪状态，属于JVM底层的状态，不属于JAVA层面，表示线程正在运行但还未分配到CPU时间片
  - RUNNING，运行状态，属于JVM底层的状态，不属于JAVA层面，表示线程获取到CPU时间片，正在运行
- BLOCKED，阻塞状态，表示线程正在阻塞于monitor锁，等待进入synchronized方法或块
- WAITING，等待状态，表示线程由于一些特定动作（Object#wait()、LockSupport.park()、Thread#join()）进入等待状态，当其他线程执行对应特定动作（Object#notify、LockSupport.unpark(Thread)、其他线程执行完毕）后会唤醒该线程，然后该线程进入运行状态
- TIMED_WAITING，超时等待状态，它与等待状态很类似，但可以设置指定超时时间，超时后线程会自动被唤醒，进入运行状态
- TERMINATED，终止状态，表示线程已经执行完毕

其中，有两个点需要注意
- 理解RUNNABLE在JVM底层包含READY和RUNNING两种状态
- 理解BLOCKED与WAITING状态的区别

值得说明的一点是，平时我们口语所说的“阻塞”，往往包含了BLOCKED与WAITING两种状态，把Object#wait()、LockSupport.park()等动作进入的等待状态也称为了“阻塞”，这样说无妨，只要正确的理解JAVA线程生命周期中BLOCKED与WAITING状态的区别即可

### 等待唤醒机制

一个线程由于一些特定动作进入等待状态，当其他线程执行特定动作后会唤醒该线程，然后该线程进入运行状态，这就是等待唤醒机制，也对应着生命周期状态中 WAITING <=> RUNNABLE 与 TIMED_WAITING <=> RUNNABLE 的转换

可以将等待唤醒机制分为三类
- 基于Object类的等待唤醒
- 基于Thread类的等待唤醒
- 基于LockSupport类的等待唤醒

#### 虚假唤醒

在谈具体的等待唤醒方法时，先来看一个操作系统层面比较玄乎的问题，即 虚假唤醒

> A spurious wakeup happens when a thread wakes up from waiting on a condition variable that's been signaled, only to discover that the condition it was waiting for isn't satisfied. It's called spurious because the thread has seemingly been awakened for no reason. But spurious wakeups don't happen for no reason, they usually happen because in between the time when the condition variable was signaled and when the waiting thread finally ran, another thread ran and changed the condition.<br>
> On many systems, especially multiprocessor systems, the problem of spurious wakeups is exacerbated because if there are several threads waiting on the condition variable when it's signaled, the system may decide to wake them all up, treating every signal() to wake one thread as a broadcast() to wake all of them, thus breaking any possibly expected 1:1 relationship between signals and wakeups. If there are ten threads waiting, only one will win and the other nine will experience spurious wakeups.<br>
> From https://en.wikipedia.org/wiki/Spurious_wakeup



#### Object等待唤醒

#### Thread等待唤醒

#### LockSupport等待唤醒

### 线程中断机制

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
