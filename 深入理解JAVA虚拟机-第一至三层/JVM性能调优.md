- [JVM性能调优](#JVM性能调优)
  - [基础故障处理工具](#基础故障处理工具)
    - [jps-虚拟机进程状况工具](#jps-虚拟机进程状况工具)
    - [jstat-虚拟机统计信息监视工具](#jstat-虚拟机统计信息监视工具)
    - [jinfo-Java配置信息工具](#jinfo-Java配置信息工具)
    - [jmap-Java内存映像工具](#jmap-Java内存映像工具)
    - [jhat-虚拟机堆转储快照分析工具](#jhat-虚拟机堆转储快照分析工具)
    - [jstack-Java堆栈跟踪工具](#jstack-Java堆栈跟踪工具)
  - [可视化故障处理工具](#可视化故障处理工具)
    - [JHSDB-基于服务性代理的调试工具](#JHSDB-基于服务性代理的调试工具)
    - [JConsole-Java监视与管理控制台](#JConsole-Java监视与管理控制台)
  - [生产环境中的故障处理工具](#生产环境中的故障处理工具)
    - [VisualVM-多合一故障处理工具](#VisualVM-多合一故障处理工具)
    - [Arthas-Alibaba开源的Java诊断工具](#Arthas-Alibaba开源的Java诊断工具)
  - [性能调优记录](#性能调优记录)
    - [程序死循环问题排查](#程序死循环问题排查)
    - [程序死锁问题排查](#程序死锁问题排查)
    - [CPU占用过高问题排查](#CPU占用过高问题排查)
    - [JAVA反射是否真的很慢](#JAVA反射是否真的很慢)

# JVM性能调优

> 文章标题中包含了“性能调优”，但是，我个人觉得，调优案例这一部分，目前手上有的书本资料远远不够，《深入理解JAVA虚拟机》只是描述了一下案例简介，完全没有深入分析（当然也理解，篇幅有限，不可能做深入分析）。这一部分，需要结合实际项目来分析，成本比较高，我手上一时半会没有好的项目例子。因此呢，我将文章核心放小一些：至少对JVM各种运维工具进行一次介绍，介绍它们的基本功能，能实战演练的就尝试一次，会涉及到的工具有：jps、jstat、jinfo、jmap、jhat、jstack、JHSDB、JConsole、VisualVM、Arthas。其中重点介绍 VisualVM 和 Arthas 两款工具

> 在不断学习的过程中，我接触到了许多实战例子，我将它们逐一记录了下来，同时，我想到是否可以多维度的模拟性能调优案例来自行测试，比如搭建一套Spring Web应用，不断模拟用户端请求，创建大量的Bean，通过VisualVM或者Arthas工具查看内存调用情况。至此我的思路就展开了

### 基础故障处理工具

安装了JAVA后，在 %JAVA_HOME%/bin/ 下存在许多工具，它们是JDK内部自带的工具，用于问题排查和分析

这些工具的源码都在 https://github.com/peteryuanpan/openjdk-8u40-source-code-mirror/tree/master/jdk/src/share/classes/sun/tools ，都是开源的，且是JAVA代码

![image](https://user-images.githubusercontent.com/10209135/100352674-505c8d80-3028-11eb-809e-90fce04409af.png)

#### jps-虚拟机进程状况工具

jps，JVM Process Status Tool，显示指定系统内所有的 Hotspot 虚拟机进程

命令指南
```cmd
jps -help
usage: jps [-help]
       jps [-q] [-mlvV] [<hostid>]

Definitions:
    <hostid>:      <hostname>[:<port>]
```

通过如下方法可以打印本地电脑上 jps 命令输出的本地 hsperfdata 文件所在文件夹

```java
package jtools;

import sun.misc.VMSupport;

public class JPSTempFile {

    public static void main(String[] args) {
        System.out.println(VMSupport.getVMTemporaryDirectory());
    }
}
```

输出结果
```
C:\Users\Admin\AppData\Local\Temp\
```

最终路径 = 输出结果 + "hsperfdata_<Username>"（我这里是Admin）

![image](https://user-images.githubusercontent.com/10209135/100350483-ef7f8600-3024-11eb-96ee-d0e93d7191cc.png)

再查询jps命令，可以看出来是一一对应的

```cmd
jps -l
15976 sun.tools.jps.Jps
10876
5692 org.jetbrains.jps.cmdline.Launcher
```

#### jstat-虚拟机统计信息监视工具

jstat，JVM Statistics Monitoring Tool，是用于监视虚拟机各种运行状态信息的命令行工具。它可以显示本地或者远程虚拟机进程中 类加载、内存、垃圾收集、JIT编译 等运行数据。jstat 可以理解为 jconsole、jvisualvm 图形界面工具的命令行版本

命令指南

```cmd
jstat
invalid argument count
Usage: jstat -help|-options
       jstat -<option> [-t] [-h<lines>] <vmid> [<interval> [<count>]]

Definitions:
  <option>      An option reported by the -options option
  <vmid>        Virtual Machine Identifier. A vmid takes the following form:
                     <lvmid>[@<hostname>[:<port>]]
                Where <lvmid> is the local vm identifier for the target
                Java virtual machine, typically a process id; <hostname> is
                the name of the host running the target Java virtual machine;
                and <port> is the port number for the rmiregistry on the
                target host. See the jvmstat documentation for a more complete
                description of the Virtual Machine Identifier.
  <lines>       Number of samples between header lines.
  <interval>    Sampling interval. The following forms are allowed:
                    <n>["ms"|"s"]
                Where <n> is an integer and the suffix specifies the units as
                milliseconds("ms") or seconds("s"). The default units are "ms".
  <count>       Number of samples to take before terminating.
  -J<flag>      Pass <flag> directly to the runtime system.
```

|命令|解释|
|--|--|
|-class pid|显示加载class的数量，及所占空间等信息|
|-compiler pid|显示VM实时编译的数量等信息|
|-gc pid|可以显示gc的信息，查看gc的次数，及时间|
|-gccapacity pid|可以显示VM内存中三代（young,old,perm）对象的使用和占用大小|
|-gcnew pid|new对象的信息|
|-gcnewcapacity pid|new对象的信息及其占用量|
|-gcold pid|old对象的信息|
|-gcoldcapacity pid|old对象的信息及其占用量|
|-gcpermcapacity pid| perm对象的信息及其占用量|
|-util pid|统计gc信息统计|
|-printcompilation pid|当前VM执行的信息|

以 -gc 为例子，先通过 jps 获取 pid，然后执行 jstat -gc < pid >，得到如下结果

```cmd
jstat -gc 16952
 S0C    S1C    S0U    S1U      EC       EU        OC         OU       MC     MU    CCSC   CCSU   YGC     YGCT    FGC    FGCT     GCT
2048.0 2048.0  0.0    0.0   12800.0   5329.2   34304.0      0.0     4480.0 776.7  384.0   76.6       0    0.000   0      0.000    0.000
```

#### jinfo-Java配置信息工具

jinfo，Configuration Info for Java，作用是实时查看和调整虚拟机各项参数

命令指南

```cmd
jinfo
Usage:
    jinfo [option] <pid>
        (to connect to running process)
    jinfo [option] <executable <core>
        (to connect to a core file)
    jinfo [option] [server_id@]<remote server IP or hostname>
        (to connect to remote debug server)

where <option> is one of:
    -flag <name>         to print the value of the named VM flag
    -flag [+|-]<name>    to enable or disable the named VM flag
    -flag <name>=<value> to set the named VM flag to the given value
    -flags               to print VM flags
    -sysprops            to print Java system properties
    <no option>          to print both of the above
    -h | -help           to print this help message
```

先通过 jps 获取 pid，然后执行 jinfo < pid >，得到如下结果

```cmd
jinfo 16952
Attaching to process ID 16952, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.231-b11
Java System Properties:

java.runtime.name = Java(TM) SE Runtime Environment
java.vm.version = 25.231-b11
sun.boot.library.path = C:\Program Files\Java\jdk1.8.0_231\jre\bin
java.vendor.url = http://java.oracle.com/
java.vm.vendor = Oracle Corporation
path.separator = ;
file.encoding.pkg = sun.io
java.vm.name = Java HotSpot(TM) 64-Bit Server VM
sun.os.patch.level =
sun.java.launcher = SUN_STANDARD
user.script =
user.country = CN
user.dir = D:\workspace\luban-jvm-research
java.vm.specification.name = Java Virtual Machine Specification
java.runtime.version = 1.8.0_231-b11
java.awt.graphicsenv = sun.awt.Win32GraphicsEnvironment
os.arch = amd64
java.endorsed.dirs = C:\Program Files\Java\jdk1.8.0_231\jre\lib\endorsed
line.separator =

java.io.tmpdir = C:\Users\Admin\AppData\Local\Temp\
java.vm.specification.vendor = Oracle Corporation
user.variant =
os.name = Windows 10
sun.jnu.encoding = GBK
java.library.path = C:\Program Files\Java\jdk1.8.0_231\bin;C:\Windows\Sun\Java\bin;C:\Windows\system32;C:\Windows;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Windows\System32\OpenSSH\;D:\download\qshell-windows-x64-v2.4.1.exe;C:\Program Files\NVIDIA Corporation\NVIDIA NvDLISR;D:\qiniu\tools;D:\download\arthas-packaging-3.4.4-bin\;D:\ffmpeg\bin;D:\Git\cmd;C:\MinGW\bin;C:\Program Files\Java\jdk1.8.0_231\bin;D:\GnuWin32\bin;C:\Users\Admin\Desktop\plan\2020\gradle-5.5.1-bin\gradle-5.5.1\bin;C:\Program Files\MySQL\MySQL Server 8.0\bin;D:\download\v2ray-windows-64;D:\erl-23.1\bin;D:\RabbitMQ Server\rabbitmq_server-3.8.9\sbin;D:\download\Redis-x64-5.0.9;D:\nodejs\;D:\python2.7.18;C:\ProgramData\chocolatey\bin;C:\Program Files\dotnet\;C:\Users\Admin\AppData\Local\Microsoft\WindowsApps;;D:\Microsoft VS Code\bin;C:\Users\Admin\AppData\Roaming\npm;C:\Users\Admin\.dotnet\tools;.
java.specification.name = Java Platform API Specification
java.class.version = 52.0
sun.management.compiler = HotSpot 64-Bit Tiered Compilers
os.version = 10.0
user.home = C:\Users\Admin
user.timezone = Asia/Shanghai
java.awt.printerjob = sun.awt.windows.WPrinterJob
file.encoding = UTF-8
java.specification.version = 1.8
user.name = Admin
java.class.path = C:\Program Files\Java\jdk1.8.0_231\jre\lib\charsets.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\deploy.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\access-bridge-64.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\cldrdata.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\dnsns.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\jaccess.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\jfxrt.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\localedata.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\nashorn.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\sunec.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\sunjce_provider.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\sunmscapi.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\sunpkcs11.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext\zipfs.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\javaws.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\jce.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\jfr.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\jfxswt.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\jsse.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\management-agent.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\plugin.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\resources.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\rt.jar;D:\workspace\luban-jvm-research\target\classes;C:\Users\Admin\.m2\repository\org\openjdk\jol\jol-core\0.10\jol-core-0.10.jar;C:\Users\Admin\.m2\repository\mysql\mysql-connector-java\8.0.20\mysql-connector-java-8.0.20.jar;C:\Users\Admin\.m2\repository\com\google\protobuf\protobuf-java\3.6.1\protobuf-java-3.6.1.jar;C:\Users\Admin\.m2\repository\cglib\cglib\2.2.2\cglib-2.2.2.jar;C:\Users\Admin\.m2\repository\asm\asm\3.3.1\asm-3.3.1.jar;D:\IntelliJ IDEA 2020.2.3\lib\idea_rt.jar
java.vm.specification.version = 1.8
sun.arch.data.model = 64
sun.java.command = com.peter.jvm.example2.oom.HeapOOM
java.home = C:\Program Files\Java\jdk1.8.0_231\jre
user.language = zh
java.specification.vendor = Oracle Corporation
awt.toolkit = sun.awt.windows.WToolkit
java.vm.info = mixed mode
java.version = 1.8.0_231
java.ext.dirs = C:\Program Files\Java\jdk1.8.0_231\jre\lib\ext;C:\Windows\Sun\Java\lib\ext
sun.boot.class.path = C:\Program Files\Java\jdk1.8.0_231\jre\lib\resources.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\rt.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\sunrsasign.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\jsse.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\jce.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\charsets.jar;C:\Program Files\Java\jdk1.8.0_231\jre\lib\jfr.jar;C:\Program Files\Java\jdk1.8.0_231\jre\classes
java.vendor = Oracle Corporation
file.separator = \
java.vendor.url.bug = http://bugreport.sun.com/bugreport/
sun.io.unicode.encoding = UnicodeLittle
sun.cpu.endian = little
sun.desktop = windows
sun.cpu.isalist = amd64

VM Flags:
Non-default VM flags: -XX:CICompilerCount=3 -XX:+HeapDumpOnOutOfMemoryError -XX:InitialHeapSize=52428800 -XX:MaxHeapSize=52428800 -XX:MaxNewSize=17301504 -XX:MinHeapDeltaBytes=524288 -XX:NewSize=17301504 -XX:OldSize=35127296 -XX:+PrintGC -XX:+PrintGCDetails -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:+UseFastUnorderedTimeStamps -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC
Command line:  -Xms50M -Xmx50M -XX:+HeapDumpOnOutOfMemoryError -verbose:gc -XX:+PrintGCDetails -javaagent:D:\IntelliJ IDEA 2020.2.3\lib\idea_rt.jar=56037:D:\IntelliJ IDEA 2020.2.3\bin -Dfile.encoding=UTF-8
```

可以通过 jinfo -flag +-name | name=value pid 来修改参数值

可以通过 jinfo -flag name pid 来查看参数值，得到如下结果

```
jinfo -flag MaxNewSize 16952
-XX:MaxNewSize=17301504
```

同样的，还有其他办法也能达到 jinfo 的效果，比如 jps -v，得到如下结果

```cmd
jps -v
10536 Launcher -Xmx700m -Djava.awt.headless=true -Djava.endorsed.dirs="" -Djdt.compiler.useSingleThread=true -Dpreload.project.path=D:/workspace/luban-jvm-research -Dpreload.config.path=C:/Users/Admin/AppData/Roaming/JetBrains/IntelliJIdea2020.2/options -Dcompile.parallel=false -Drebuild.on.dependency.change=true -Dio.netty.initialSeedUniquifier=-4213499304903621881 -Dfile.encoding=GBK -Duser.language=zh -Duser.country=CN -Didea.paths.selector=IntelliJIdea2020.2 -Didea.home.path=D:\IntelliJ IDEA 2020.2.3 -Didea.config.path=C:\Users\Admin\AppData\Roaming\JetBrains\IntelliJIdea2020.2 -Didea.plugins.path=C:\Users\Admin\AppData\Roaming\JetBrains\IntelliJIdea2020.2\plugins -Djps.log.dir=C:/Users/Admin/AppData/Local/JetBrains/IntelliJIdea2020.2/log/build-log -Djps.fallback.jdk.home=D:/IntelliJ IDEA 2020.2.3/jbr -Djps.fallback.jdk.version=11.0.8 -Dio.netty.noUnsafe=true -Djava.io.tmpdir=C:/Users/Admin/AppData/Local/JetBrains/IntelliJIdea2020.2/compile-server/luban-jvm-research_3f84df24/_temp_ -Djps.backward.ref.index.builder=true
16952 HeapOOM -Xms50M -Xmx50M -XX:+HeapDumpOnOutOfMemoryError -verbose:gc -XX:+PrintGCDetails -javaagent:D:\IntelliJ IDEA 2020.2.3\lib\idea_rt.jar=56037:D:\IntelliJ IDEA 2020.2.3\bin -Dfile.encoding=UTF-8
2008 Jps -Dapplication.home=C:\Program Files\Java\jdk1.8.0_231 -Xms8m
10876  exit -Xms128m -Xmx2039m -XX:ReservedCodeCacheSize=240m -XX:+UseConcMarkSweepGC -XX:SoftRefLRUPolicyMSPerMB=50 -ea -XX:CICompilerCount=2 -Dsun.io.useCanonPrefixCache=false -Djdk.http.auth.tunneling.disabledSchemes="" -XX:+HeapDumpOnOutOfMemoryError -XX:-OmitStackTraceInFastThrow -Djdk.attach.allowAttachSelf=true -Dkotlinx.coroutines.debug=off -Djdk.module.illegalAccess.silent=true -javaagent:C:\Users\Public\.jetbrains\jetbrains-agent-v3.2.0.0f1f.69e=6e68f9eb,LFq51qqupnaiTNn39w6zATiOTxZI2JYuRJEBlzmUDv4zeeNlXhMgJZVb0q5QkLr+CIUrSuNB7ucifrGXawLB4qswPOXYG7+ItDNUR/9UkLTUWlnHLX07hnR1USOrWIjTmbytcIKEdaI6x0RskyotuItj84xxoSBP/iRBW2EHpOc -Djb.vmOptionsFile=C:\Users\Admin\AppData\Roaming\JetBrains\IntelliJIdea2020.2\idea64.exe.vmoptions -Djava.library.path=D:\IntelliJ IDEA 2020.2.3\jbr\\bin;D:\IntelliJ IDEA 2020.2.3\jbr\\bin\server -Didea.jre.check=true -Dide.native.launcher=true -Didea.vendor.name=JetBrains -Didea.paths.selector=IntelliJIdea2020.2 -XX:ErrorFile=C:\Users\Admin\java_error_in_idea_%p.log -XX:HeapDumpPath=C:
```

java -XX:+PrintCommandLineFlags -version，查看JVM常用参数，得到如下结果

```cmd
-XX:InitialHeapSize=267373952 -XX:MaxHeapSize=4277983232 -XX:+PrintCommandLineFlags -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseParallelGC
java version "1.8.0_231"
Java(TM) SE Runtime Environment (build 1.8.0_231-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.231-b11, mixed mode)
```

java -XX:+PrintFlagsFinal -version，查看JVM主要参数，得到如下结果

```cmd
    uintx MarkStackSize                             = 4194304                             {product}
    uintx MarkStackSizeMax                          = 536870912                           {product}
    uintx MarkSweepAlwaysCompactCount               = 4                                   {product}
    uintx MarkSweepDeadRatio                        = 1                                   {product}
     intx MaxBCEAEstimateLevel                      = 5                                   {product}
     intx MaxBCEAEstimateSize                       = 150                                 {product}
    uintx MaxDirectMemorySize                       = 0                                   {product}
     bool MaxFDLimit                                = true                                {product}
    uintx MaxGCMinorPauseMillis                     = 4294967295                          {product}
    uintx MaxGCPauseMillis                          = 4294967295                          {product}
    uintx MaxHeapFreeRatio                          = 100                                 {manageable}
    uintx MaxHeapSize                              := 4278190080                          {product}
     intx MaxInlineLevel                            = 9                                   {product}
     intx MaxInlineSize                             = 35                                  {product}
     intx MaxJNILocalCapacity                       = 65536                               {product}
     intx MaxJavaStackTraceDepth                    = 1024                                {product}
     intx MaxJumpTableSize                          = 65000                               {C2 product}
     intx MaxJumpTableSparseness                    = 5                                   {C2 product}
     intx MaxLabelRootDepth                         = 1100                                {C2 product}
     intx MaxLoopPad                                = 11                                  {C2 product}
    uintx MaxMetaspaceExpansion                     = 5451776                             {product}
    uintx MaxMetaspaceFreeRatio                     = 70                                  {product}
    uintx MaxMetaspaceSize                          = 4294901760                          {product}
    uintx MaxNewSize                               := 1426063360                          {product}
     intx MaxNodeLimit                              = 75000                               {C2 product}

...

java version "1.8.0_231"
Java(TM) SE Runtime Environment (build 1.8.0_231-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.231-b11, mixed mode)
```

#### jmap-Java内存映像工具

#### jhat-虚拟机堆转储快照分析工具

#### jstack-Java堆栈跟踪工具

### 可视化故障处理工具

#### JHSDB-基于服务性代理的调试工具

#### JConsole-Java监视与管理控制台

### 生产环境中的故障处理工具

#### VisualVM-多合一故障处理工具

VisualVM，All-in-One Java Troubleshooting Tool，是功能最强大的运行监视和故障处理程序之一，曾经在很长一段时间内是Oracle官方主力发展的虚拟机故障处理工具

Oracle曾在VisualVM的软件说明中写上了“All-in-One”的字样，预示着它除了常规的运行监视、故障处理外，还将提供其他方面的能力，譬如性能分析（Profiling）。VisualVM的性能分析功能比起JProfiler、YourKit等专业且收费的Profiling工具都不遑多让。而且相比这些第三方工具，VisualVM还有一个很大的优点：不需要被监视的程序基于特殊Agent去运行，因此它的通用性很强，对应用程序实际性能的影响也较小，使得它可以直接应用在生产环境中。这个优点是JProfiler、YourKit等工具无法与之媲美的

命令行执行 jvisualvm，可以得到如下结果

（需要安装插件VisualGC，左上角点击工具 - 插件 - 可用插件 - VisualGC，点击安装，完成后就可以点击VisualGC界面了，该界面对于分析非常有用）

![image](https://user-images.githubusercontent.com/10209135/100355624-0cb85280-302d-11eb-975e-f8ea194fad8d.png)

jvisualvm 还可以用来分析日志

（左上角点击 文件，装入，导入日志，然后分析）

![image](https://user-images.githubusercontent.com/10209135/100319607-e1b50b00-2ffa-11eb-9873-400084f6223f.png)

这里的日志是通过VM参数 -XX:+HeapDumpOnOutOfMemoryError 打印的，会打印到 project 所在目录下。另外，通过寒泉子 https://memory.console.perfma.com/ ，也可以上传日志后进行分析

#### Arthas-Alibaba开源的Java诊断工具

Alibaba 开源的工具，Java应用诊断利器，中文文档：https://arthas.aliyun.com/zh-cn/ ，github：https://github.com/alibaba/arthas

TODO

### 性能调优案例

#### 程序死循环问题排查

#### 程序死锁问题排查

#### CPU占用过高问题排查

#### JAVA反射是否真的很慢

