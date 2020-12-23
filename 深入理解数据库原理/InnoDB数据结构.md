
# InnoDB数据结构

> 本文主要是对《MySQL技术内幕：InnoDB存储引擎》做笔记记录，以实战记录、核心理论记录为主

### 文件类型

分析构成MySQL数据库和InnoDB存储引擎表的各种类型文件，有这些
- 参数文件：
- 日志文件：
- sokcet文件：
- pid文件：
- MySQL表结构文件：
- 存储引擎文件：

### 参数文件

当MySQL实例启动时，数据库会先去读一个配置参数文件，用来寻找数据库的各种文件所在位置以及指定某些初始化参数，这些参数通常定义了某种内存结构有多大等

用户只需通过命令 mysql --help | findStr my.cnf 来寻找即可

```
mysql --help | findStr my.cnf
                      order of preference, my.cnf, $MYSQL_TCP_PORT,
C:\Windows\my.ini C:\Windows\my.cnf C:\my.ini C:\my.cnf D:\mysql-5.7.30-winx64\my.ini D:\mysql-5.7.30-winx64\my.cnf
```

查看 D:\mysql-5.7.30-winx64\my.cnf

```
[client]
port=3306
default-character-set=utf8
[mysqld] 
basedir=D:\mysql-5.7.30-winx64
datadir=D:\mysql-5.7.30-winx64\data
port=3306
character_set_server=utf8
default-storage-engine=MYISAM
sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES
```

SHOW VARIABLES 可查看所有参数，SHOW VARIABLES LIKE "" 可查看指定参数

```
mysql> SHOW VARIABLES LIKE "innodb_buffer%";
+-------------------------------------+----------------+
| Variable_name                       | Value          |
+-------------------------------------+----------------+
| innodb_buffer_pool_chunk_size       | 134217728      |
| innodb_buffer_pool_dump_at_shutdown | ON             |
| innodb_buffer_pool_dump_now         | OFF            |
| innodb_buffer_pool_dump_pct         | 25             |
| innodb_buffer_pool_filename         | ib_buffer_pool |
| innodb_buffer_pool_instances        | 1              |
| innodb_buffer_pool_load_abort       | OFF            |
| innodb_buffer_pool_load_at_startup  | ON             |
| innodb_buffer_pool_load_now         | OFF            |
| innodb_buffer_pool_size             | 134217728      |
+-------------------------------------+----------------+
10 rows in set, 1 warning (0.00 sec)
```

静态参数不可修改，动态参数可通过 SET [GLOBAL|SESSION] X=Y 修改

但是通过 SET 方式修改后，再下一次实例启动时参数还会恢复为上一次的默认值，要想一直生效需要去改配置参数文件

静态参数例子

```
mysql> SET GLOBAL datadir='db/mysql';
ERROR 1238 (HY000): Variable 'datadir' is a read only variable
```

动态参数例子
```
mysql> show variables like "long_query_time";
+-----------------+-----------+
| Variable_name   | Value     |
+-----------------+-----------+
| long_query_time | 10.000000 |
+-----------------+-----------+
1 row in set, 1 warning (0.00 sec)

mysql> set long_query_time=11;
Query OK, 0 rows affected (0.00 sec)

mysql> show variables like "long_query_time";
+-----------------+-----------+
| Variable_name   | Value     |
+-----------------+-----------+
| long_query_time | 11.000000 |
+-----------------+-----------+
1 row in set, 1 warning (0.00 sec)
```

官网所有动态参数： https://dev.mysql.com/doc/refman/8.0/en/dynamic-system-variables.html

可以看出参数有一个字段是 Variable Scope，值为 Global 表示整个实例生命周期中都会生效，值为 Seesion 表示只能在会话中生效，值为 Both 表示两者皆可

### 日志文件

日志文件有4种：错误日志（error log）、二进制日志（bin log）、慢查询日志（show query log）、查询日志（log）

#### 错误日志文件

当出现MySQL数据库不能正常启动时，第一个必须查找的文件应该就是错误日志文件，该文件记录了错误信息，能很好的指导用户发现问题

查看错误日志

```
mysql> SHOW VARIABLES LIKE "log_error";
+---------------+------------------------------------------------+
| Variable_name | Value                                          |
+---------------+------------------------------------------------+
| log_error     | D:\mysql-5.7.30-winx64\data\PS2019PQMJKMWO.err |
+---------------+------------------------------------------------+
1 row in set, 1 warning (0.00 sec)
```

查看这份文件
```
2020-12-22T07:42:29.744044Z 0 [Warning] TIMESTAMP with implicit DEFAULT value is deprecated. Please use --explicit_defaults_for_timestamp server option (see documentation for more details).
2020-12-22T07:42:29.745707Z 0 [Warning] 'NO_ZERO_DATE', 'NO_ZERO_IN_DATE' and 'ERROR_FOR_DIVISION_BY_ZERO' sql modes should be used with strict mode. They will be merged with strict mode in a future release.
2020-12-22T07:42:29.745713Z 0 [Warning] 'NO_AUTO_CREATE_USER' sql mode was not set.
2020-12-22T07:42:29.746776Z 0 [ERROR] Cannot open Windows EventLog; check privileges, or start server with --log_syslog=0
2020-12-22T07:42:30.103683Z 0 [Warning] InnoDB: New log files created, LSN=45790
2020-12-22T07:42:30.147218Z 0 [Warning] InnoDB: Creating foreign key constraint system tables.
2020-12-22T07:42:30.219511Z 0 [Warning] No existing UUID has been found, so we assume that this is the first time that this server has been started. Generating a new UUID: 3ec223c9-4429-11eb-b2f4-04d9f509c207.
...
```

#### 慢查询日志文件

书中对这一块进行了大量的描述，主要都是针对DBA如何定位慢查询日志的基础知识，这里不做过多描述，只简单记录一些参数的含义

slow_query_log 默认为OFF，设置为ON后，会默认将查询时间（单位ms）大于 long_query_time 的日志打印到 slow_query_log_file 中

log_queries_not_using_indexes 默认为OFF，设置为ON后，会将没有走索引的日志打印到 slow_query_log_file 中

log_throttle_queries_not_using_indexes 默认为0，表示无限制，设置为大于0后，表示每分钟允许记录到 slow_query_log_file 中且未使用索引的日志最大次数，用于控制日志过多 

log_output 默认为FILE，若改为TABLE，慢查询日志不再输出到 slow_query_log_file 中，而是输出到 mysql.slow_log 表中

```
mysql> show variables like "slow_query_log";
+----------------+-------+
| Variable_name  | Value |
+----------------+-------+
| slow_query_log | OFF   |
+----------------+-------+
1 row in set, 1 warning (0.00 sec)

mysql> show variables like "slow_query_log_file";
+---------------------+-----------------------------------------------------+
| Variable_name       | Value                                               |
+---------------------+-----------------------------------------------------+
| slow_query_log_file | D:\mysql-5.7.30-winx64\data\PS2019PQMJKMWO-slow.log |
+---------------------+-----------------------------------------------------+
1 row in set, 1 warning (0.00 sec)

mysql> show variables like "log_output";
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| log_output    | FILE  |
+---------------+-------+
1 row in set, 1 warning (0.00 sec)

mysql> show variables like "long_query_time";
+-----------------+-----------+
| Variable_name   | Value     |
+-----------------+-----------+
| long_query_time | 10.000000 |
+-----------------+-----------+
1 row in set, 1 warning (0.00 sec)

mysql> show variables like "log_queries%";
+-------------------------------+-------+
| Variable_name                 | Value |
+-------------------------------+-------+
| log_queries_not_using_indexes | OFF   |
+-------------------------------+-------+
1 row in set, 1 warning (0.00 sec)

mysql> show variables like "log_throttle_queries%";
+----------------------------------------+-------+
| Variable_name                          | Value |
+----------------------------------------+-------+
| log_throttle_queries_not_using_indexes | 0     |
+----------------------------------------+-------+
1 row in set, 1 warning (0.00 sec)
```

尝试 SET GLOBAL slow_query_log=ON; 并执行一条长SQL语句，查看 D:\mysql-5.7.30-winx64\data\PS2019PQMJKMWO-slow.log

```
MySQL, Version: 5.7.30 (MySQL Community Server (GPL)). started with:
TCP Port: 3306, Named Pipe: MySQL
Time                 Id Command    Argument
# Time: 2020-12-23T07:55:13.425415Z
# User@Host: root[root] @ localhost [127.0.0.1]  Id:     7
# Query_time: 10.661718  Lock_time: 0.000081 Rows_sent: 10000  Rows_examined: 33831
use peter;
SET timestamp=1608710113;
SELECT t1.b,t1.c,t2.b,t2.c FROM t1 LEFT JOIN t2 ON t1.b = t2.b AND t1.c = t2.c LIMIT 10000;
```

### 表结构定义文件

### InnoDB存储引擎文件

### 重做日志文件

