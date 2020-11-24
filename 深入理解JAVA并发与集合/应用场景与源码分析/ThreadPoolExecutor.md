- [ThreadPoolExecutor](#ThreadPoolExecutor)
  - [应用场景](#应用场景)
  - [核心思想](#核心思想)
  - [类结构分析](#类结构分析)
    - [类图](#类图)
    - [数据结构](#数据结构)
    - [核心方法](#核心方法)

# ThreadPoolExecutor

### 应用场景

线程池可解决 持续不断任务 且 多线程 场景下，减少线程的开销，让多任务可复用，减少操作系统用户态与内核态的切换次数，提高程序运行效率

场景例子1：固定N个任务，用N个线程分别处理，这种场景没有必要使用线程池

场景例子2：固定N个任务，尤其是N很大，用最多50个线程处理，一个任务处理完了紧接着下一个任务，这种场景合适线程池

场景具体例子（关键词：持续不断）：并发网络IO请求、生产者消费者模型、Tomcat

### 核心思想

### 类结构分析

#### 类图

![image](https://user-images.githubusercontent.com/10209135/100113435-45302300-2eab-11eb-9897-c327e19f9d72.png)

#### 数据结构

```java
public class ThreadPoolExecutor extends AbstractExecutorService {

    private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING    = -1 << COUNT_BITS;
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    private static final int STOP       =  1 << COUNT_BITS;
    private static final int TIDYING    =  2 << COUNT_BITS;
    private static final int TERMINATED =  3 << COUNT_BITS;

    // Packing and unpacking ctl
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    private static int workerCountOf(int c)  { return c & CAPACITY; }
    private static int ctlOf(int rs, int wc) { return rs | wc; }
    
    private final BlockingQueue<Runnable> workQueue;
    
    private final ReentrantLock mainLock = new ReentrantLock();
    
    private final HashSet<Worker> workers = new HashSet<Worker>();
    
    private final Condition termination = mainLock.newCondition();
    
    private int largestPoolSize;
    
    private long completedTaskCount;
    
    private volatile ThreadFactory threadFactory;
    
    private volatile RejectedExecutionHandler handler;
    
    private volatile long keepAliveTime;
    
    private volatile boolean allowCoreThreadTimeOut;
    
    private volatile int corePoolSize;
    
    private volatile int maximumPoolSize;
    
    private static final RejectedExecutionHandler defaultHandler = new AbortPolicy();
    
    private static final RuntimePermission shutdownPerm = new RuntimePermission("modifyThread");
    
    private final AccessControlContext acc;
    
    private final class Worker extends AbstractQueuedSynchronizer implements Runnable {
    
        private static final long serialVersionUID = 6138294804551838833L;

        final Thread thread;
        /
        Runnable firstTask;
        
        volatile long completedTasks;

        Worker(Runnable firstTask) {
            setState(-1); // inhibit interrupts until runWorker
            this.firstTask = firstTask;
            this.thread = getThreadFactory().newThread(this);
        }

        public void run() {
            runWorker(this);
        }

        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock()        { acquire(1); }
        public boolean tryLock()  { return tryAcquire(1); }
        public void unlock()      { release(1); }
        public boolean isLocked() { return isHeldExclusively(); }

        void interruptIfStarted() {
            Thread t;
            if (getState() >= 0 && (t = thread) != null && !t.isInterrupted()) {
                try {
                    t.interrupt();
                } catch (SecurityException ignore) {
                }
            }
        }
    }
    
    public static class AbortPolicy implements RejectedExecutionHandler {}
    public static class CallerRunsPolicy implements RejectedExecutionHandler {}
    public static class DiscardPolicy implements RejectedExecutionHandler {}
    public static class DiscardOldestPolicy implements RejectedExecutionHandler {}
}
```

#### 核心方法

##### execute方法

##### addWorker方法

##### runWorker方法

疑问：ThreadPoolExecutor 中的 Worker 继承了 AbstractQueuedSynchronizer，实现了 Runnable。在 addWorker 方法中每次都会 w = new Worker(firstTask); 且同时会新建一个Thread t，然后执行 t.start()，JVM启动线程后，会执行 w 中的 run() 方法，再走到 runWorker() 方法中。在 runWorker() 方法中会不断地从 同步队列中取 task 来执行，实现了多个任务复用一个线程！但，runWorker 方法中同时还会 w.lock() 和 w.unlock()，可这里就有疑问了，既然每个 w 都是一个新建的对象，岂不是 w 这个抽象队列同步器内部只会有一个线程独占，不会插入其他线程 ? 那为何还需要 w.lock() 和 w.unlock() 呢 ?

回答：这里正在执行具体任务期间加锁，是为了避免在任务运行期间，其他线程调用了shutdown后正在执行的任务被中断（shutdown只会中断当前被阻塞挂起的线程）

##### processWorkerExit方法

##### shutdown方法

##### shutdownNow方法



