- [ThreadPoolExecutor](#ThreadPoolExecutor)
  - [应用场景](#应用场景)
  - [核心思想](#核心思想)
  - [类结构分析](#类结构分析)
    - [类图](#类图)
    - [数据结构](#数据结构)
    - [核心方法](#核心方法)

# ThreadPoolExecutor

### 应用场景

### 核心思想

### 类结构分析

#### 类图

#### 数据结构

#### 核心方法

##### runWorker方法

疑问：ThreadPoolExecutor 中的 Worker 继承了 AbstractQueuedSynchronizer，实现了 Runnable。在 addWorker 方法中每次都会 w = new Worker(firstTask); 且同时会新建一个Thread t，然后执行 t.start()，JVM启动线程后，会执行 w 中的 run() 方法，再走到 runWorker() 方法中。在 runWorker() 方法中会不断地从 同步队列中取 task 来执行，实现了多个任务复用一个线程！但，runWorker 方法中同时还会 w.lock() 和 w.unlock()，可这里就有疑问了，既然每个 w 都是一个新建的对象，岂不是 w 这个抽象队列同步器内部只会有一个线程独占，不会插入其他线程 ? 那为何还需要 w.lock() 和 w.unlock() 呢 ?

回答：这里正在执行具体任务期间加锁，是为了避免在任务运行期间，其他线程调用了shutdown后正在执行的任务被中断（shutdown只会中断当前被阻塞挂起的线程）

