# synchronized锁住的是栈中的对象还是堆中的内存

### 考虑以下例子，buy_ticket_1 及 buy_ticket_2 两种情况，输出结果有较大区别

```Java
public class Web12306Example1 implements Runnable {
    
    private Integer ticketNumber;
    
    public Web12306Example1() {
        this.ticketNumber = 10;
    }
    
    void buy_ticket_1() {
        synchronized (ticketNumber) {
            if (ticketNumber > 0) {
                ticketNumber = ticketNumber - 1;
                System.out.println(Thread.currentThread().getName() + "---->" + ticketNumber);
            }
        }
    }
    
    void buy_ticket_2() {
        if (ticketNumber > 0) {
            synchronized (ticketNumber) {
                ticketNumber = ticketNumber - 1;
                System.out.println(Thread.currentThread().getName() + "---->" + ticketNumber);
            }
        }
    }
    
    @Override
    public void run() {
        for (int times = 1; times <= 10; times ++) {
            // 请求，网络延迟500ms
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (ticketNumber <= 0) break;
            // 抢票
            buy_ticket_1();
            //buy_ticket_2();
        }
        System.out.println("final---->" + Thread.currentThread().getName() + "---->" + ticketNumber);
    }
    
    public static void main(String[] args) {
        Web12306Example1 ex1 = new Web12306Example1();
        new Thread(ex1, "码农").start();
        new Thread(ex1, "码畜").start();
        new Thread(ex1, "码蝗").start();
    }
    
}
```

buy_ticket_1 输出结果，稳定如下
```
码蝗---->9
码农---->8
码畜---->7
码蝗---->6
码农---->5
码畜---->4
码蝗---->3
码农---->2
码畜---->1
码蝗---->0
final---->码畜---->0
final---->码农---->0
final---->码蝗---->0
```

buy_ticket_2

输出结果 第一次
```
码蝗---->9
码农---->8
码畜---->7
码畜---->5
码蝗---->5
码农---->4
码畜---->3
码蝗---->2
码农---->1
码畜---->0
final---->码蝗---->0
final---->码农---->0
final---->码畜---->0
```

输出结果 第二次
```
码农---->8
码蝗---->8
码畜---->7
码蝗---->5
码畜---->4
码农---->5
码蝗---->3
码农---->1
码畜---->2
码蝗---->0
final---->码农---->0
final---->码畜---->0
final---->码蝗---->0
```

### buy_ticket_1 及 buy_ticket_2 两种情况，输出结果有较大区别。为什么？

主要是因为 Integer，是一个 final 类，在 ticketNumber = ticketNumber - 1; 中，栈中的 ticketNumber 指向了堆中不同的地址，这样 synchronized 锁住的就不是同一个内存地址了

如果程序改为以下

```Java
public class Web12306Example1 implements Runnable {
    
    private class IntegerClass {
        Integer ticketNumber;
    }
    IntegerClass integerClass = new IntegerClass();
    
    public Web12306Example1() {
        this.integerClass.ticketNumber = 10;
    }
    
    void buy_ticket_1() {
        synchronized (integerClass) {
            if (integerClass.ticketNumber > 0) {
                integerClass.ticketNumber = integerClass.ticketNumber - 1;
                System.out.println(Thread.currentThread().getName() + "---->" + integerClass.ticketNumber);
            }
        }
    }
    
    void buy_ticket_2() {
        if (integerClass.ticketNumber > 0) {
            synchronized (integerClass) {
                integerClass.ticketNumber = integerClass.ticketNumber - 1;
                System.out.println(Thread.currentThread().getName() + "---->" + integerClass.ticketNumber);
            }
        }
    }
    
    @Override
    public void run() {
        for (int times = 1; times <= 10; times ++) {
            // 请求，网络延迟500ms
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (integerClass.ticketNumber <= 0) break;
            // 抢票
            buy_ticket_1();
            //buy_ticket_2();
        }
        System.out.println("final---->" + Thread.currentThread().getName() + "---->" + integerClass.ticketNumber);
    }
    
    public static void main(String[] args) {
        Web12306Example1 ex1 = new Web12306Example1();
        new Thread(ex1, "码农").start();
        new Thread(ex1, "码畜").start();
        new Thread(ex1, "码蝗").start();
    }
    
}
```

则 buy_ticket_1 输出结果如下
```
码畜---->9
码蝗---->8
码农---->7
码农---->6
码蝗---->5
码畜---->4
码农---->3
码蝗---->2
码畜---->1
码农---->0
final---->码农---->0
final---->码蝗---->0
final---->码畜---->0
```

buy_ticket_2
```
码蝗---->9
码畜---->8
码农---->7
码畜---->6
码蝗---->5
码农---->4
码畜---->3
码蝗---->2
码农---->1
码畜---->0
final---->码农---->0
码蝗---->-1
final---->码畜---->-1
final---->码蝗---->-1
```

可以看出，码蝗多输出了一个-1，这是符合预期的，主要是 buy_ticket_1 及 buy_ticket_2 的结果，都是按顺序递减了

### synchronized 锁住的是栈中的对象还是堆中的内存？

在上面的例子中，是堆中的地址变了的，现在来做个测试，即让栈中多个对象指向同一个堆中的内存，在不同线程中synchronized分别锁住这些对象，会有什么结果
