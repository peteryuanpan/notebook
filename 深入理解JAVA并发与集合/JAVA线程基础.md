- [JAVA线程基础](#JAVA线程基础)
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
    - [LockSupport等待唤醒](#LockSupport等待唤醒)
    - [Thread等待唤醒](#Thread等待唤醒)
  - [线程中断机制](#线程中断机制)
  - [并发基础概念](#并发基础概念)
    - [守护线程与用户线程](#守护线程与用户线程)
    - [线程上下文切换](#线程上下文切换)
    - [用户态与内核态](#用户态与内核态)
    - [多线程模型](#多线程模型)
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

而不同的点是，FutureTask是实现了Runnable与Future接口，它是一个异步处理框架，通过get方法来异步获取线程执行结果

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
 
 上面通过分析源码看出来ExecutorService本质上还是使用new Thread(Runnable a)来创建线程的，而运行线程的逻辑是如何的，这一块不妨放到线程池源码分析中展开

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

这里有几个点需要注意
- RUNNABLE在JVM底层包含READY和RUNNING两种状态，它们的区别点是线程是否分配到了CPU时间片，在线程运行start方法或者被唤醒后，往往是先处于READY状态的，在获取了时间片后才处于RUNNING状态
- Thread.yield()方法可以告诉线程调度器“主动放弃CPU时间片，不想用了”，线程调度器会从就绪队列中后去一个优先级最高的线程（也可能是刚才让出CPU时间片的线程）
- 平时我们口语所说的“阻塞”，往往包含了BLOCKED与WAITING两种状态，把Object#wait()、LockSupport.park()等动作进入的等待状态也称为了“阻塞”，口语上这样说无妨，只要正确的理解JAVA线程生命周期中BLOCKED与WAITING状态的区别，在需要区分的时候不要弄混淆即可

### 等待唤醒机制

一个线程由于一些特定动作进入等待状态，当其他线程执行特定动作后会唤醒该线程，然后该线程进入运行状态，这就是等待唤醒机制，也对应着生命周期状态中 WAITING <=> RUNNABLE 与 TIMED_WAITING <=> RUNNABLE 的转换

可以将等待唤醒机制分为三类
- 基于Object类的等待唤醒
- 基于LockSupport类的等待唤醒
- 基于Thread类的等待唤醒

#### 虚假唤醒

在谈具体的等待唤醒方法时，先来看一个操作系统层面比较玄乎的问题，即 虚假唤醒

> A spurious wakeup happens when a thread wakes up from waiting on a condition variable that's been signaled, only to discover that the condition it was waiting for isn't satisfied. It's called spurious because the thread has seemingly been awakened for no reason. But spurious wakeups don't happen for no reason, they usually happen because in between the time when the condition variable was signaled and when the waiting thread finally ran, another thread ran and changed the condition.<br>
> On many systems, especially multiprocessor systems, the problem of spurious wakeups is exacerbated because if there are several threads waiting on the condition variable when it's signaled, the system may decide to wake them all up, treating every signal() to wake one thread as a broadcast() to wake all of them, thus breaking any possibly expected 1:1 relationship between signals and wakeups. If there are ten threads waiting, only one will win and the other nine will experience spurious wakeups.<br>
> From https://en.wikipedia.org/wiki/Spurious_wakeup

虚假唤醒是操作系统与CPU底层工作时可能出现一种现象，即线程莫名其妙地、在没有被其他线程主动唤醒或者中断的情况下，也被唤醒了

具体虚假唤醒的原因没有必要去刨根问底，重点是：在让线程进入等待状态前（主要是无限期等待，Object#wait()和LockSupport.park()），若存在条件判断，不要用 if 条件判断，而应该使用 while 循环判断，这是良好的编程习惯！虚假唤醒不仅仅是JAVA语言，在任何语言都有可能出现

我不想去复现虚假唤醒的例子，因为它很难复现，但不妨看一下源码和注释

java.util.concurrent.locks.Condition中await()方法的注释

![image](https://user-images.githubusercontent.com/10209135/97564520-ac2ef900-1a1f-11eb-923e-0c303d9d3ad1.png)

LockSupport中park方法的注释

![image](https://user-images.githubusercontent.com/10209135/97564626-d08ad580-1a1f-11eb-8e18-d7745d624817.png)

它们都说明了一点，线程在等待状态的过程中，除了会被其他线程唤醒或中断以外，还可能存在虚假唤醒的情况！不过很遗憾，Object中wait方法没有类似的注释

AbstractQueuedSynchronizer 中的内部类 ConditionObject 的实现，可以看到 LockSupport.park(this); 是放在 while 循环中进行的，这是推荐的方式，如果采用了 if 判断，一旦出现虚假唤醒，程序会不符合预期的往下执行了！

```java
        /**
         * Implements interruptible condition wait.
         * <ol>
         * <li> If current thread is interrupted, throw InterruptedException.
         * <li> Save lock state returned by {@link #getState}.
         * <li> Invoke {@link #release} with saved state as argument,
         *      throwing IllegalMonitorStateException if it fails.
         * <li> Block until signalled or interrupted.
         * <li> Reacquire by invoking specialized version of
         *      {@link #acquire} with saved state as argument.
         * <li> If interrupted while blocked in step 4, throw InterruptedException.
         * </ol>
         */
        public final void await() throws InterruptedException {
            if (Thread.interrupted())
                throw new InterruptedException();
            Node node = addConditionWaiter();
            int savedState = fullyRelease(node);
            int interruptMode = 0;
            while (!isOnSyncQueue(node)) {
                LockSupport.park(this);
                if ((interruptMode = checkInterruptWhileWaiting(node)) != 0)
                    break;
            }
            if (acquireQueued(node, savedState) && interruptMode != THROW_IE)
                interruptMode = REINTERRUPT;
            if (node.nextWaiter != null) // clean up if cancelled
                unlinkCancelledWaiters();
            if (interruptMode != 0)
                reportInterruptAfterWait(interruptMode);
        }
```

#### Object等待唤醒

基于Object的等待唤醒与synchonized是分不开的，synchonized中有一个概念叫monitor机制，简单来说即每一个JAVA Object在虚拟机底层都可视为一个Monitor Object，Monitor翻译过来是监视器的意思，而synchonzied锁也时常被称为监视器锁，关于monitor机制的细节，我计划放在线程安全原理中展开，目前只需要了解这个概念即可

在Object类中，定义了几个实例方法：wait()、wait(long)、wait(long,int)、notify()、notifyAll()，它们是用于等待与唤醒线程的，但作用的是实例对象，

总结下来，有这么几点
- 1、wait方法和notify方法都需要在synchonized方法或块中被执行，更准确的说，是执行前必须先获得监视器锁
- 2、wait方法执行后，会让出CPU时间片，同时释放对应实例的监视器锁，但不会释放其他实例的监视器锁，对应线程进入等待状态（WAITING）
- 3、notify方法执行后，实例的线程被唤醒，但不会马上执行wait方法的下一行指令，而需要先与其他线程竞争到监视器锁后才会执行
- 4、notify方法执行后，如果该实例存在多个线程在等待，唤醒哪个线程是随机的
- 5、相比于notify方法，notifyAll方法会唤醒所有该实例正在等待的线程
- 6、相比于wait()方法，wait(long)、wait(long,int)方法是将线程进入TIMED_WATING状态，即超时后会自动被唤醒
- 7、wait方法执行后，其他线程中断了该线程，则该线程会抛出InterrupteException异常并返回

关于1、2两点，来看一个例子

```java
package part1;

public class WaitNotifyTest1 {

    static Object obj = new Object();

    public static void main(String[] args) {
        Thread a = new Thread(() -> {
            synchronized (obj) {
                try {
                    System.out.println(Thread.currentThread().getName() + " willing to wait");
                    obj.wait();
                    System.out.println(Thread.currentThread().getName() + " end waiting");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "threadA");

        a.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (obj) {
            obj.notify();
        }
        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("mainThread end");
    }
}
```

输出结果
```
threadA willing to wait
threadA end waiting
mainThread end
```

解释：mainThread睡1s保证threadA正常执行到obj.wait，threadA进入等待状态，让出时间片、释放锁，mainThread中执行obj.notify唤醒threadA，threadA获取锁和时间片，进入运行状态

将例子改一下
```java
        Thread a = new Thread(() -> {
            //synchronized (obj) {
                try {
            //}
...
        //synchronized (obj) {
            obj.notify();
        //}
```

输出结果
```
threadA willing to wait
Exception in thread "threadA" java.lang.IllegalMonitorStateException
	at java.lang.Object.wait(Native Method)
	at java.lang.Object.wait(Object.java:502)
	at part1.WaitNotifyTest1.lambda$main$0(WaitNotifyTest1.java:12)
	at java.lang.Thread.run(Thread.java:748)
Exception in thread "main" java.lang.IllegalMonitorStateException
	at java.lang.Object.notify(Native Method)
	at part1.WaitNotifyTest1.main(WaitNotifyTest1.java:28)
```

解释：wait方法和notify方法都需要在synchonized方法或块中被执行，更准确的说，是执行前必须先获得监视器锁

关于第3点，来看一个例子

```java
package part1;

public class WaitNotifyTest2 {

    static Object obj = new Object();

    public static void main(String[] args) {
        Thread a = new Thread(() -> {
            synchronized (obj) {
                try {
                    System.out.println(Thread.currentThread().getName() + " willing to wait");
                    obj.wait();
                    System.out.println(Thread.currentThread().getName() + " end waiting");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "threadA");

        a.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (obj) {
            System.out.println(Thread.currentThread().getName() + " willing to notify");
            obj.notify();
            System.out.println(Thread.currentThread().getName() + " end notifing");
            //while (true) {}
        }
        synchronized (obj) {
            while (true) {}
        }
    }
}
```

输出结果1
```
threadA willing to wait
main willing to notify
main end notifing
threadA end waiting
```

输出结果2
```
threadA willing to wait
main willing to notify
main end notifing
```

解释：obj.notify();执行后，出synchronized (obj)代码块，释放obj的监视器锁，此时两个线程在竞争obj的监视器锁，一个是刚被唤醒的threadA，一个是主线程即将执行while死循环，输出结果1表示threadA优先竞争到了锁，输出结果2表示主线程优先竞争到了锁

关于4、5两点，来看一个例子

```java
package part1;

public class WaitNotifyTest3 {

    static Object obj = new Object();

    public static void test() {
        Thread a = new Thread(() -> {
            synchronized (obj) {
                try {
                    System.out.println(Thread.currentThread().getName() + " willing to wait");
                    obj.wait();
                    System.out.println(Thread.currentThread().getName() + " end waiting");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "threadA");

        Thread b = new Thread(a);
        b.setName("threadB");

        a.start();
        b.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        synchronized (obj) {
            obj.notify();
        }
    }

    public static void main(String[] args) {
        while (true) {
            test();
        }
    }
}
```

输出结果
```
threadA willing to wait
threadB willing to wait
threadA end waiting
threadA willing to wait
threadB willing to wait
threadB end waiting
threadB willing to wait
threadA willing to wait
threadA end waiting
threadA willing to wait
threadB willing to wait
threadB end waiting
threadA willing to wait
threadB willing to wait
threadB end waiting
threadA willing to wait
threadB willing to wait
threadA end waiting
...
```

解释：threadA获取监视器锁，执行obj.wait释放锁，进入等待集合，threadB获取监视器锁，执行obj.wait释放锁，进入等待集合，主线程睡3s使得threadA和threadB都顺利进入等待集合，然后执行obj.notify唤醒等待集合中的线程（随机地），可能唤醒A，可能唤醒B

将例子改一下
```java
        synchronized (obj) {
            obj.notifyAll();
        }
```

输出结果
```
threadA willing to wait
threadB willing to wait
threadB end waiting
threadA end waiting
threadA willing to wait
threadB willing to wait
threadB end waiting
threadA end waiting
...
```

解释：notifyAll会唤醒该实例的当前等待集合中的所有线程

#### LockSupport等待唤醒

LockSupport中也定义了许多方法，可以用于等待唤醒机制，都是类方法，分别有：park()、park(Object)、parkNanos(long)、parkNanos(Object, long)、parkUntil(long)、parkUntil(Object, long)、unpark(Thread)

总结下来，有这么几点
- 1、Locksupport与每个使用它线程关联一个许可证，LockSupport类是使用Unsafe类实现的
- 2、park()方法执行后，线程会进入等待状态，让出CPU时间片，不会释放监视器锁
- 3、unpark(Thread)方法执行后，会给Thread a一个许可证，唤醒线程，线程会从之前的等待状态进入运行状态，获取CPU时间片
- 4、unpark(Thread)方法执行后，会给Thread a一个许可证，这个许可证是有延迟效应的，即在执行park()方法不会让线程进入等待状态
- 5、park(Object)方法执行后，除了执行与park()方法一样的效果外，还会执行setBlocker将object记录到线程内部，使用线程堆栈信息可以查看，因此是推荐的方法！
- 6、相比于park方法，parkNanos、parkUntil会将线程进入TIMED_WATING状态，parkNanos是超时x秒后自动唤醒线程，parkUntil是uix时间戳到达deadline后自动唤醒线程
- 7、park方法执行后，其他线程中断了该线程，该线程会返回，但不会抛InterruptedException异常！
- 8、AQS实现的锁的lock、unlock方法，以及条件变量Condition的await、singal方法，都是基于LockSupport类的park、unpark方法来实现的

关于第8点，不妨放到AbstractQueuedSynchronizer、ReentrantLock源码分析中展开

关于2、3两点，来看一个例子

```java
package part1;

import java.util.concurrent.locks.LockSupport;

public class LockSupportTest1 {

    static int b = 0;

    public static void main(String[] args) {
        Thread a = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " begin");
            for (int i = 0; i < 10; i ++) {
                b ++;
            }
            System.out.println(Thread.currentThread().getName() + " b = " + b);
            while (b == 10) {
                LockSupport.park();
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }, "threadA");

        a.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 10; i ++) {
            b ++;
        }
        System.out.println(Thread.currentThread().getName() + " b = " + b);
        LockSupport.unpark(a);
        System.out.println(Thread.currentThread().getName() + " end");
    }
}
```

输出结果
```
threadA begin
threadA b = 10
main b = 20
main end
threadA end
```

改一下例子
```java
            while (b == 10) {
                synchronized (obj) {
                    LockSupport.park();
                }
            }
...
        a.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (obj) {
            System.out.println("111");
        }
```

输出结果
```
threadA begin
threadA b = 10
（死锁）
```

解释：无论执行多少次，输出结果都一样，LockSupport.park()会让线程进入等待状态，让出时间片，不会释放监视器锁，也不需要先获取监视器锁才能执行，LockSupport.unpark(a)会提供Thread a一个许可证，会唤醒线程，线程重新获取时间片

关于第4点，来看一个例子

```java
package part1;

import java.util.concurrent.locks.LockSupport;

public class LockSupportTest2 {

    public static void main(String[] args) {
        Thread t = Thread.currentThread();
        LockSupport.unpark(t);
        LockSupport.unpark(t);
        LockSupport.park();
        System.out.println("end park");
        LockSupport.park();
        System.out.println("end park");
    }
}
```

输出结果
```
end park
（等待状态）
```

解释：LockSupport.unpark(t); 给线程t（当前线程）了一个许可证，虽然执行了两次，但许可证只有一次有效，LockSupport.park();后本来应该让线程进入等待状态的，但由于有许可证了，就没有进入，输出end park，许可证只有一次有效，下一次再LockSupport.park();时会进入等待状态

关于第5点，来看一个例子

```java
package part1;

import java.util.concurrent.locks.LockSupport;

public class LockSupportTest3 {

    public void test() {
        LockSupport.park();
    }
    
    public static void main(String[] args) {
        new LockSupportTest3().test();
    }
}
```

使用jps查看到pid，然后执行jstack pid，得到结果之一
```
"main" #1 prio=5 os_prio=0 tid=0x0000000000f4e800 nid=0x310c waiting on condition [0x0000000002daf000]
   java.lang.Thread.State: WAITING (parking)
        at sun.misc.Unsafe.park(Native Method)
        at java.util.concurrent.locks.LockSupport.park(LockSupport.java:304)
        at part1.LockSupportTest3.main(LockSupportTest3.java:8)
```

改一下例子
```java
package part1;

import java.util.concurrent.locks.LockSupport;

public class LockSupportTest3 {

    public void test() {
        LockSupport.park(this);
    }

    public static void main(String[] args) {
        new LockSupportTest3().test();
    }
}
```

使用jps查看到pid，然后执行jstack pid，得到结果之一
```
"main" #1 prio=5 os_prio=0 tid=0x000000000352e800 nid=0x1d70 waiting on condition [0x000000000348e000]
   java.lang.Thread.State: WAITING (parking)
        at sun.misc.Unsafe.park(Native Method)
        - parking to wait for  <0x000000076b2a0630> (a part1.LockSupportTest3)
        at java.util.concurrent.locks.LockSupport.park(LockSupport.java:175)
        at part1.LockSupportTest3.test(LockSupportTest3.java:8)
        at part1.LockSupportTest3.main(LockSupportTest3.java:12)
```

解释：LockSupport.park(this) 将this指针对象传入了Thread内部，使用jstack打印线程堆栈信息时，会显示对应对象（parking to wait for x000000076b2a0630 (a part1.LockSupportTest3)）

来看一下LockSupport源码
```java
public class LockSupport {
    private LockSupport() {} // Cannot be instantiated.

    private static void setBlocker(Thread t, Object arg) {
        // Even though volatile, hotspot doesn't need a write barrier here.
        UNSAFE.putObject(t, parkBlockerOffset, arg);
    }
...
    public static void park(Object blocker) {
        Thread t = Thread.currentThread();
        setBlocker(t, blocker);
        UNSAFE.park(false, 0L);
        setBlocker(t, null);
    }
...
    // Hotspot implementation via intrinsics API
    private static final sun.misc.Unsafe UNSAFE;
    private static final long parkBlockerOffset;
    private static final long SEED;
    private static final long PROBE;
    private static final long SECONDARY;
    static {
        try {
            UNSAFE = sun.misc.Unsafe.getUnsafe();
            Class<?> tk = Thread.class;
            parkBlockerOffset = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("parkBlocker"));
            SEED = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomSeed"));
            PROBE = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomProbe"));
            SECONDARY = UNSAFE.objectFieldOffset
                (tk.getDeclaredField("threadLocalRandomSecondarySeed"));
        } catch (Exception ex) { throw new Error(ex); }
    }
```

来看一下Thread类源码
public
class Thread implements Runnable {
...
```java
    /**
     * The argument supplied to the current call to
     * java.util.concurrent.locks.LockSupport.park.
     * Set by (private) java.util.concurrent.locks.LockSupport.setBlocker
     * Accessed using java.util.concurrent.locks.LockSupport.getBlocker
     */
    volatile Object parkBlocker;
```

可以看出，park(Object)方法比park()方法多出了setBlocker一步，而其内部实现是给Thread t的parkBlocker变量赋值（CAS方式实现），以便在打印堆栈时可以输出

#### Thread等待唤醒

在Thread类中，也定义了一些方法，可以用于等待唤醒机制，实例方法有：join()、join(long)、join(long, int)，类方法有：sleep()

总结一下，有那么几点
- 1、join方法是实例方法，在threadA.join方法执行后，当前线程会进入等待状态（WAITING），等待threadA执行完毕，让出CPU时间片，不会释放监视器锁
- 2、sleep方法是类方法，在sleep方法执行后，当前线程会进入有期限的等待状态（TIMED_WAITING），让出CPU时间片，不会释放监视器锁
- 3、相比于join()方法，join(long)、join(long, int)是将线程进入TIMED_WATING状态，即超时后会自动被唤醒
- 4、join或者sleep方法执行后，其他线程中断了该线程，则该线程会抛出InterrupteException异常并返回
- 5、Thread的等待唤醒机制，与Object、LockSupport有所不同，它并非让其他线程来唤醒，而是达到一定条件（比如休眠时间到、线程执行完毕）后自动唤醒

关于1、2两点，来看一个例子

```java
package part1;

public class ThreadJoinTest1 {

    static Object obj = new Object();

    public static void main(String[] args) {
        Thread a = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " begin");
            int b = 0;
            for (int i = 0; i < 100000; i ++) {
                b ++;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < 100000; i ++) {
                b ++;
            }
            System.out.println("b = " + b);
        });

        a.start();
        try {
            a.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(Thread.currentThread().getName() + " end");
    }
}
```

输出结果
```
Thread-0 begin
b = 200000
main end
```

代码改一下
```java
            try {
                synchronized (obj) {
                    Thread.sleep(1000);
                }
...
        a.start();
        try {
            Thread.sleep(1000);
            synchronized (obj) {
                a.join();
            }
            synchronized (obj) {
                System.out.println(Thread.currentThread().getName() + " print");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
```

输出结果
```
Thread-0 begin
b = 200000
main print
main end
```

解释：无论执行多少次，输出结果都一样，Thread.sleep会让出时间片，Thread#join会等待对应线程执行完毕，但二者都不会释放监视器锁，也不需要先获取监视器锁才能执行

### 线程中断机制

线程中断是一种线程间优雅的协作模式，通过设置线程的中断标志并不能直接终止该线程的执行，而是被中断的线程根据中断状态自行处理

Thread中有三个方法，两个是实例方法：public void interrupt()，public boolean isInterrupted()，一个是类方法：public static boolean interrupted()

总结下来，有这么几点
- 1、interrupt方法执行后，会给线程设置中断标志，但仅仅是设置标志，不会中断线程，线程中需要使用isInterrupted或interrupted方法来检测中断标志
- 2、interrupt方法执行后，如果线程已经因Object#wait、Thread#join、Thread.sleep方法而进入等待状态，线程会抛出InterruptedException异常，然后返回
- 3、interrupt方法执行后，如果线程已经因LockSupport.park方法而进入等待状态，线程会返回，但不会抛出InterruptedException异常！
- 4、isInterrupted方法执行后，会返回对应线程（不是当前线程）是否设置了中断标志，但不会清除中断标志！
- 5、interrupted方法执行后，会返回currentThread（注意是当前线程）是否设置了中断标志，并清除中断标志

关于第1点，来看一个例子

```java
package part1;

public class ThreadInterruptTest1 {

    public static void main(String[] args) {
        Thread a = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " begin");
            for (;;) {
                if (0 == 1) {
                    break;
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        });
        a.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        a.interrupt();
        System.out.println("interrupt end");
    }
}
```

输出结果
```
Thread-0 begin
interrupt end
```

解释：线程a进入for死循环一直执行，a.interrupt();只是给线程a设置了一个中断标志，但不会中断线程，线程中应使用使用isInterrupted或interrupted方法来检测中断标志

关于2、3两点，来看一个例子

```java
package part1;

import java.util.concurrent.locks.LockSupport;

public class ThreadInterruptTest2 {

    public static void main(String[] args) {
        Thread a = new Thread(new Test() {
            @Override
            void gotowait() throws Exception {
                this.wait();
            }
        }, "ThreadA");
        Thread b = new Thread(new Test() {
            @Override
            void gotowait() throws Exception {
                Thread.currentThread().join();
            }
        }, "ThreadB");
        Thread c = new Thread(new Test() {
            @Override
            void gotowait() throws Exception {
                Thread.sleep(100000000);
            }
        }, "ThreadC");
        Thread d = new Thread(new Test() {
            @Override
            void gotowait() throws Exception {
                LockSupport.park(this);
            }
        }, "ThreadD");
        a.start();
        b.start();
        c.start();
        d.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        a.interrupt();
        b.interrupt();
        c.interrupt();
        d.interrupt();
    }

    abstract static class Test implements Runnable {

        abstract void gotowait() throws Exception;

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " begin");
            try {
                gotowait();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }
    }
}
```

输出结果
```
ThreadA begin
ThreadD begin
ThreadC begin
ThreadB begin
ThreadA end
java.lang.IllegalMonitorStateException
	at java.lang.Object.wait(Native Method)
	at java.lang.Object.wait(Object.java:502)
	at part1.ThreadInterruptTest2$1.gotowait(ThreadInterruptTest2.java:12)
	at part1.ThreadInterruptTest2$Test.run(ThreadInterruptTest2.java:56)
	at java.lang.Thread.run(Thread.java:748)
java.lang.InterruptedException
	at java.lang.Object.wait(Native Method)
	at java.lang.Thread.join(Thread.java:1252)
	at java.lang.Thread.join(Thread.java:1326)
	at part1.ThreadInterruptTest2$2.gotowait(ThreadInterruptTest2.java:18)
	at part1.ThreadInterruptTest2$Test.run(ThreadInterruptTest2.java:56)
	at java.lang.Thread.run(Thread.java:748)
java.lang.InterruptedException: sleep interrupted
	at java.lang.Thread.sleep(Native Method)
	at part1.ThreadInterruptTest2$3.gotowait(ThreadInterruptTest2.java:24)
	at part1.ThreadInterruptTest2$Test.run(ThreadInterruptTest2.java:56)
	at java.lang.Thread.run(Thread.java:748)
ThreadB end
ThreadC end
ThreadD end
```

解释：Object#wait、Thread#join、Thread.sleep 和 LockSupport.park方法都会将线程进入等待状态，但在被其他线程调用 interrupt 方法而中断时，前三者会抛InterruptedException异常而返回，LockSupport.park也会返回，但不会抛异常

关于4、5两点，来看一个例子

```java
package part1;

public class ThreadInterruptTest3 {

    public static void main(String[] args) {
        Thread a = new Thread(() -> {
            for (;;) {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + " " + Thread.currentThread().isInterrupted());
                    break;
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }, "threadA");
        Thread b = new Thread(() -> {
            for (;;) {
                if (Thread.interrupted()) {
                    System.out.println(Thread.currentThread().getName() + " " + Thread.currentThread().isInterrupted());
                    break;
                }
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }, "threadB");
        a.start();
        b.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        a.interrupt();
        b.interrupt();
    }
}
```

输出结果
```
threadA true
threadB false
threadB end
threadA end
```

解释：线程被其他线程调用interrupt方法后，会设置中断标志，通过isInterrupted和interrupted方法都可以检测中断标志，但前者不会清除中断标志，后者会清除中断标志

关于第5点，再来看一个例子

```java
package part1;

public class ThreadInterruptTest4 {

    public static void main(String[] args) {
        Thread a = new Thread(() -> {
           for (;;) {

           }
        });
        a.start();
        a.interrupt();
        System.out.println(a.isInterrupted());
        System.out.println(a.interrupted());
        System.out.println(Thread.interrupted());
        System.out.println(a.isInterrupted());
    }
}
```

输出结果
```
true
false
false
true
```

解释：这个例子很容易让人以为输出结果是 true、true、false、false... 关键在于a.interrupted()这句话，它内部实际上不是检测Thread a的中断标志，而是检测当前线程（也就是主线程）的中断标志

来看一下Thread类源码
```java
public
class Thread implements Runnable {
...
    public static boolean interrupted() {
        return currentThread().isInterrupted(true);
    }
    
    public boolean isInterrupted() {
        return isInterrupted(false);
    }
    
    /**
     * Tests if some Thread has been interrupted.  The interrupted state
     * is reset or not based on the value of ClearInterrupted that is
     * passed.
     */
    private native boolean isInterrupted(boolean ClearInterrupted);
```

可以看出来，interrupted是返回当前线程的中断标志，且会清除中断标志（set ClearInterrupted = true），而isInterrupted是返回调用线程（不是当前线程）的中断标志，且不会清除中断标志（set ClearInterrupted = false）

这里比较有意思的是，IDEA中输入a.interr，给函数提示时候，没有给出interrupted方法（实际上是可以调用的），实际写代码的时候也不建议用类似 a.interrupted() 的方式，会造成很大的误解，而建议使用 Thread.interrupted()

![image](https://user-images.githubusercontent.com/10209135/97849320-57e08d80-1d2d-11eb-9f61-8a423f337a47.png)

### 并发基础概念

#### 守护线程与用户线程

JAVA中的线程分为两类，分别为 daemon线程（守护线程）与 user线程（用户线程）

守护线程与用户线程的区别是，只有当最后一个用户线程结束时，JVM才会正常退出，而不管守护线程是否结束了，守护线程是否结束不影响JVM的退出

main线程运行结束后，JVM会自动启动一个叫做DestroyJAVAVM的线程，该线程会等待所有用户线程结束后，终止JVM

来看下源码：https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/blob/master/jdk/src/share/bin/java.c#L354

```cpp
int JNICALL
JavaMain(void * _args)
{
...
    /*
     * Get the application's main class.
     *
     * See bugid 5030265.  The Main-Class name has already been parsed
     * from the manifest, but not parsed properly for UTF-8 support.
     * Hence the code here ignores the value previously extracted and
     * uses the pre-existing code to reextract the value.  This is
     * possibly an end of release cycle expedient.  However, it has
     * also been discovered that passing some character sets through
     * the environment has "strange" behavior on some variants of
     * Windows.  Hence, maybe the manifest parsing code local to the
     * launcher should never be enhanced.
     *
     * Hence, future work should either:
     *     1)   Correct the local parsing code and verify that the
     *          Main-Class attribute gets properly passed through
     *          all environments,
     *     2)   Remove the vestages of maintaining main_class through
     *          the environment (and remove these comments).
     *
     * This method also correctly handles launching existing JavaFX
     * applications that may or may not have a Main-Class manifest entry.
     */
    mainClass = LoadMainClass(env, mode, what);
    CHECK_EXCEPTION_NULL_LEAVE(mainClass);
    /*
     * In some cases when launching an application that needs a helper, e.g., a
     * JavaFX application with no main method, the mainClass will not be the
     * applications own main class but rather a helper class. To keep things
     * consistent in the UI we need to track and report the application main class.
     */
    appClass = GetApplicationClass(env);
    NULL_CHECK_RETURN_VALUE(appClass, -1);
    /*
     * PostJVMInit uses the class name as the application name for GUI purposes,
     * for example, on OSX this sets the application name in the menu bar for
     * both SWT and JavaFX. So we'll pass the actual application class here
     * instead of mainClass as that may be a launcher or helper class instead
     * of the application class.
     */
    PostJVMInit(env, appClass, vm);
    /*
     * The LoadMainClass not only loads the main class, it will also ensure
     * that the main method's signature is correct, therefore further checking
     * is not required. The main method is invoked here so that extraneous java
     * stacks are not in the application stack trace.
     */
    mainID = (*env)->GetStaticMethodID(env, mainClass, "main",
                                       "([Ljava/lang/String;)V");
    CHECK_EXCEPTION_NULL_LEAVE(mainID);

    /* Build platform specific argument array */
    mainArgs = CreateApplicationArgs(env, argv, argc);
    CHECK_EXCEPTION_NULL_LEAVE(mainArgs);

    /* Invoke main method. */
    (*env)->CallStaticVoidMethod(env, mainClass, mainID, mainArgs);

    /*
     * The launcher's exit code (in the absence of calls to
     * System.exit) will be non-zero if main threw an exception.
     */
    ret = (*env)->ExceptionOccurred(env) == NULL ? 0 : 1;
    LEAVE();
}
```

```cpp
#define LEAVE() \
    do { \
        if ((*vm)->DetachCurrentThread(vm) != JNI_OK) { \
            JLI_ReportErrorMessage(JVM_ERROR2); \
            ret = 1; \
        } \
        if (JNI_TRUE) { \
            (*vm)->DestroyJavaVM(vm); \
            return ret; \
        } \
    } while (JNI_FALSE)
```

在JAVA中实现时，只需要给thread设置setDaemon(true)即可将线程设置为守护线程了

```java
package part1;

import java.util.concurrent.locks.LockSupport;

public class DaemonThread {

    public static void main(String[] args) {
        Thread a = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " begin");
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + " end");
        });
        a.setDaemon(true);
        a.start();
        System.out.println(Thread.currentThread().getName() + " end");
    }
}
```

输出结果
```
main end
Thread-0 begin
（程序结束）
```

改一下例子
```java
a.setDaemon(false);
```

输出结果
```
main end
Thread-0 begin
（程序未结束）
```

守护线程的应用场景
- 给其他线程提供服务
- JVM进程结束时，不要求等待该线程结束

守护线程应用例子
- JVM中垃圾回收线程
- Tomcat的NIO中接受及处理用户请求线程

Tomcat的NIO实现NioEndPoint中会开启一组接受线程来接受用户的连接请求，以及一组处理线程负责具体处理用户请求，这些线程都是守护线程

来看下NioEndPoint类源码
```java
public class NioEndpoint extends AbstractJsseEndpoint<NioChannel,SocketChannel> 
...
    /**
     * Start the NIO endpoint, creating acceptor, poller threads.
     */
    @Override
    public void startInternal() throws Exception {

        if (!running) {
            running = true;
            paused = false;

            processorCache = new SynchronizedStack<>(SynchronizedStack.DEFAULT_SIZE,
                    socketProperties.getProcessorCache());
            eventCache = new SynchronizedStack<>(SynchronizedStack.DEFAULT_SIZE,
                            socketProperties.getEventCache());
            nioChannels = new SynchronizedStack<>(SynchronizedStack.DEFAULT_SIZE,
                    socketProperties.getBufferPool());

            // Create worker collection
            if ( getExecutor() == null ) {
                createExecutor();
            }

            initializeConnectionLatch();

            // Start poller threads
            pollers = new Poller[getPollerThreadCount()];
            for (int i=0; i<pollers.length; i++) {
                pollers[i] = new Poller();
                Thread pollerThread = new Thread(pollers[i], getName() + "-ClientPoller-"+i);
                pollerThread.setPriority(threadPriority);
                pollerThread.setDaemon(true);
                pollerThread.start();
            }

            startAcceptorThreads();
        }
    }
...
    protected void startAcceptorThreads() {
        int count = getAcceptorThreadCount();
        acceptors = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            Acceptor<U> acceptor = new Acceptor<>(this);
            String threadName = getName() + "-Acceptor-" + i;
            acceptor.setThreadName(threadName);
            acceptors.add(acceptor);
            Thread t = new Thread(acceptor, threadName);
            t.setPriority(getAcceptorThreadPriority());
            t.setDaemon(getDaemon());
            t.start();
        }
    }
...
    /**
     * The default is true - the created threads will be
     *  in daemon mode. If set to false, the control thread
     *  will not be daemon - and will keep the process alive.
     */
    private boolean daemon = true;
    public void setDaemon(boolean b) { daemon = b; }
    public boolean getDaemon() { return daemon; }
```

#### 线程上下文切换

线程上下文的切换巧妙的利用了时间片轮转的方式（保证CPU的利用率），CPU给每个任务都服务一定的时间，然后把当前任务的状态保存下来，在加载下一任务的状态后，继续服务下一个任务。线程状态的保存及其再加载，就是线程的上下文切换

在同一个时刻，单核CPU时间片只能处理一个线程上的任务，其他进程和线程必须等待，当线程A使用完时间片后，会进行线程上下文切换，保存当前线程A的执行现场，然后让线程B抢占时间片，若线程B不是第一次进行，要恢复线程B的执行现场（先前已保存）

![image](https://user-images.githubusercontent.com/10209135/97309100-91804700-189c-11eb-821f-225c1aaaa097.png)

CPU处理的速度是非常快的（要有这个概念），相对于人来说，进程和线程的上下文切换时间都也是非常短的，而内部比较，进程的上下文切换比线程的上下文文切换开销要大得多

线程上下文切换的时机
- 当前线程的CPU时间片使用完处于就绪状态
- 当前线程被其他线程中断

#### 用户态与内核态

Linux的架构中，很重要的一个能力就是操纵系统资源的能力。但是，系统资源是有限的，如果不加限制的允许任何程序以任何方式去操纵系统资源，必然会造成资源的浪费，发生资源不足等情况。为了减少这种情况的发生，Linux制定了一个等级制定，即特权。Linux将特权分成两个层次，以0和3标识。0的特权级要高于3。换句话说，0特权级在操纵系统资源上是没有任何限制的，可以执行任何操作，而3，则会受到极大的限制。我们把特权级0称之为内核态，特权级3称之为用户态

Intel x86架构使用了4个级别来标明不同的特权级权限。R0实际就是内核态，拥有最高权限。而一般应用程序处于R3状态--用户态。在Linux中，还存在R1和R2两个级别，一般归属驱动程序的级别。在Windows平台没有R1和R2两个级别，只用R0内核态和R3用户态。在权限约束上，使用的是高特权等级状态可以阅读低等级状态的数据，例如进程上下文、代码、数据等等，但是反之则不可。R0最高可以读取R0-3所有的内容，R1可以读R1-3的，R2以此类推，R3只能读自己的数据

![image](https://user-images.githubusercontent.com/10209135/103340755-d6ed0a80-4abf-11eb-98bb-d84088f1d6f3.png)

应用程序一般会在以下几种情况下切换到内核态
- 系统调用
- 异常事件
- 设备中断

关于异常事件，当发生某些预先不可知的异常时，就会切换到内核态，以执行相关的异常事件

关于设备中断，在使用外围设备时，如外围设备完成了用户请求，就会向CPU发送一个中断信号，此时，CPU就会暂停执行原本的下一条指令，转去处理中断事件。此时，如果原来在用户态，则自然就会切换到内核态

大量的进行用户态与内核态切换是非常消耗CPU资源的

#### 多线程模型

摘自《深入理解JAVA虚拟机》第二版12.4节

多线程模型有：一对一模型（1:1），一对多模型（1:N），多对多模型（N:M）

线程是比进程更轻量级的调度执行单位，线程的引入，可以把一个进程的资源分配和执行调度分开，各个线程既可以共享进程资源（内存地址、文件I/O等），又可以独立调度（线程是CPU调度的基本单位）

主流的操作系统，都提供了线程实现，Java语言则提供了在不同硬件和操作系统平台下对线程操作的统一处理，每个已经执行 start() 且还未结束的 java.lang.Thread 类的实例就代表了一个线程

对于SunJDK来说，它的Windows版与Linux版都是使用的一对一模型实现的，一条Java线程就映射到一条轻量级进程中，因为Windows与Linux操作系统提供的线程模型就是一对一的（Windows下有纤程包Fiber Package，Linux下也有NGPT来实现N:M模型，但没有成为主流），而Solaris平台中，由于操作系统的线程特性可以同时支持一对一及多对多的线程模型，因此在Solaris版的JDK中也对应提供了两个平台专有的虚拟机参数：-XX:+UseLWPSynchronization（默认，多对多模型）和 -XX:+UseBoundThreas（一对一模型）来明确指定虚拟机使用哪种模型

#### 轻量级线程之协程

谈到协程需要先谈一谈线程，CPU的调度执行是以线程为单位的，CPU核的时间片在同一时刻只能分配于一个线程上，那么当多线程在一个CPU核上执行时，就会涉及到了线程间的上下文切换，过于频繁的线程上下文切换是会消耗CPU资源的

而协程不一样，多个协程作用于一个线程时，是不存在CPU给多个协程分配时间片的说法的，协程间切换调度执行是用户态层面的，不涉及到内核态层面

协程是一个个过程，是更轻量级的线程，下面以一个伪代码与真实代码例子来进一步说明

伪代码例子

```ruby
call_sombody = Fiber.new do
  take_phone
  receive_call
  Fiber.yield
  speaking
  Fiber.yield
  shutdown
end

eat_cake = Fiber.new do
  take_cake
  open_box
  Fiber.yield
  taste
  Fiber.yield
  drop_box
end

call_somebody.resume // take_phone and receive_call then pause
eat_cake.resume // take_cake and open_box then pause
call_somebody.resume // speaking then pause
eat_cake.resume // taste then pause
call_somebody.resume // shutdown then pause
eat_cake.resume // dropbox then pause
```

执行起来大概是这样的：拿起电话、接听、拿蛋糕、开蛋糕盒、说话、吃蛋糕、挂机、扔蛋糕盒。多个协程协作好比就是你一个人其实同时只能做一件事，但是你把几个任务拆成几截来交叉执行

协程就是一串比函数粒度还要小的可手动控制的过程，从 并发、并行、协程 的角度来看，协程的发明主要是为了解决并发的问题，而线程的发明主要解决的并行的问题

Golang协程例子

```golang
package main


import (
	"fmt"
	"runtime"
	"strconv"
	"time"

	"golang.org/x/sys/windows"
)

func name(s string) {
	time.Sleep(1 * time.Millisecond)
	str := fmt.Sprint(windows.GetCurrentThreadId())
	var t = "iqoo" + s + " belong thread " + str
	fmt.Println(t)
}

func main() {
	fmt.Println("逻辑cpu数量 " + strconv.Itoa(runtime.NumCPU()))
	str := fmt.Sprint(windows.GetCurrentThreadId())
	fmt.Println("主协程所属线程id " + str)
	for i := 1; i <= 1000; i++ {
		go name(strconv.Itoa(i))
	}
	time.Sleep(2 * time.Second)
}
```

输出结果（部分）
```
逻辑cpu数量 6
主协程所属线程id 1108
iqoo116 belong thread 7136
iqoo86 belong thread 11448
iqoo132 belong thread 7136
iqoo91 belong thread 11448
iqoo10 belong thread 7136
...
iqoo1000 belong thread 12156
iqoo947 belong thread 1108
iqoo965 belong thread 12156
iqoo960 belong thread 12392
iqoo949 belong thread 15024
```

解析结果
```
cat result_windows.txt | grep "belong" | awk -F 'belong thread ' '{a[$2]+=1}END{for(i in a){print i, a[i]}}'
1108 96
7136 240
11448 232
12156 76
12348 141
12392 90
15024 125
```

上面是我的windows10电脑上的执行结果，CPU有6个核，开启了1000个协程任务，CPU底层默认只用6个线程处理

下面来看Linux版本

```golang
package main


import (
        "fmt"
        "runtime"
        "strconv"
        "time"

        "golang.org/x/sys/unix"
)

func name(s string) {
        time.Sleep(1 * time.Millisecond)
        str := fmt.Sprint(unix.Gettid())
        var t = "iqoo" + s + " belong thread " + str
        fmt.Println(t)
}

func main() {
        fmt.Println("逻辑cpu数量 " + strconv.Itoa(runtime.NumCPU()))
        str := fmt.Sprint(unix.Gettid())
        fmt.Println("主协程所属线程id " + str)
        for i := 1; i <= 1000; i++ {
                go name(strconv.Itoa(i))
        }
        time.Sleep(2 * time.Second)
}
```

输出结果（部分）
```
逻辑cpu数量 2
主协程所属线程id 1111
iqoo9 belong thread 1115
iqoo26 belong thread 1111
iqoo10 belong thread 1111
iqoo11 belong thread 1111
iqoo17 belong thread 1111
...
iqoo891 belong thread 1114
iqoo871 belong thread 1113
iqoo870 belong thread 1113
iqoo892 belong thread 1114
iqoo872 belong thread 1114
```

解析结果

```
cat result_linux.txt | grep "belong" | awk -F 'belong thread ' '{a[$2]+=1}END{for(i in a){print i, a[i]}}'
1111 629
1113 124
1114 237
1115 10
```

上面的Ubuntu是2个CPU核，开启了1000个协程任务，CPU底层默认只用4个线程处理（有超核技术，一个CPU核可以处理2个线程）

在Golang中，并发编程默认是走N协程:M线程模式的，M一般是CPU的核数，而Java中，JVM目前依然是以一个JAVA线程对应一个操作系统内核线程模式为主，社区一直在尝试协程的实现，比如协程库quasar

项目的名字叫 loom，由Ron pressler主导的，在他加入Oracle之前，已经做了一个名为 quasar 的协程库。quasar的实现原理是做字节码注入，在字节码层面对当前被调用函数中的所有局部变量进行保存，这种做法一是对性能影响很大，对JIT编译器的影响也非常大。另外，还有一点很致命，就是它必须要用户手动标注每一个函数是否会在协程上下文被调用。关于quasar更多的信息，这里就不拓展了，后续了解了再补充。据说quasar非常容易使用，接口特别友好，这是它的优点

需要指出的是，Java仅仅是没有解决”协程在Java中的定义”，以及“写得优雅“这个问题。可以发现，许多需要“协程”解决的场景问题，都可以用各种各样线程的方式来解决（比如控制线程总数，减少线程stack的大小，用线程池配置max和min idle等），更多的讨论与说明见 https://www.zhihu.com/question/332042250/answer/734115120
