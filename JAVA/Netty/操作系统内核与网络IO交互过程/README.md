
# 操作系统内核与网络IO交互过程

步骤
- 网卡收到经过网线传来的网络数据，并将网络数据写到内存中。
- 当网卡把数据写入到内存后，网卡向CPU发出一个中断信号，操作系统便能得知有新数据到来，再通过网卡中断程序去处理数据。
- 将内存中的网络数据写入到对应socket的接收缓冲区中。
- 当接收缓冲区的数据写好后，应用程序开始进行数据处理。

java的socket代码简单示例
```Java
public class SocketServer {
  public static void main(String[] args) throws Exception {
    // 监听指定的端口
    int port = 8080;
    ServerSocket server = new ServerSocket(port);
    // server将一直等待连接的到来
    Socket socket = server.accept();
    // 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
    InputStream inputStream = socket.getInputStream();
    byte[] bytes = new byte[1024];
    int len;
    while ((len = inputStream.read(bytes)) != -1) {
      //获取数据进行处理
      String message = new String(bytes, 0, len,"UTF-8");
    }
    // socket、server，流关闭操作，省略不表
  }
}
```

可以看到这个过程和底层内核的网络IO很类似，主要体现在accept()等待从网络中的请求到来然后bytes[]数组作为缓冲区等待数据填满后进行处理。

而***BIO、NIO、AIO之间的区别就在于这些操作是同步还是异步，阻塞还是非阻塞。***

### Reference

[如何理解BIO、NIO、AIO的区别？](https://juejin.im/post/5dbba5df6fb9a0204a08ae55)
