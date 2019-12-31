# synchronized块例子1--飞刀

考虑以下例子，buy_ticket_1 及 buy_ticket_2 两种情况，输出结果有较大区别

```JAVA
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

buy_ticket_1 及 buy_ticket_2 两种情况，输出结果有较大区别。为什么？
