
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

3.1 参数文件

当MySQL实例启动时，数据库会先去读一个配置参数文件，用来寻找数据库的各种文件所在位置以及指定某些初始化参数，这些参数通常定义了某种内存结构有多大等

用户只需通过命令 mysql --help | findStr my.cnf 来寻找即可

```
mysql --help | findStr my.cnf
                      order of preference, my.cnf, $MYSQL_TCP_PORT,
C:\Windows\my.ini C:\Windows\my.cnf C:\my.ini C:\my.cnf D:\mysql-5.7.30-winx64\my.ini D:\mysql-5.7.30-winx64\my.cnf
```

查看D:\mysql-5.7.30-winx64\my.cnf

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

3.1.1 什么是参数

查看innodb_buffer_pool_size参数（键值对）

```
mysql> SHOW VARIABLES LIKE 'innodb_buffer%';
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

3.1.2 参数类型

动态参数可通过SET X=Y修改，静态参数不可修改

### 日志文件

3.2 日志文件

日志文件有4种：错误日志（error log）、二进制日志（bin log）、慢查询日志（show query log）、查询日志（log）

#### 错误日志文件

当出现MySQL数据库不能正常启动时，第一个必须查找的文件应该就是错误日志文件，该文件记录了错误信息，能很好的指导用户发现问题

查看错误日志

```
mysql> SHOW VARIABLES LIKE 'log_error';
+---------------+------------------------------------------------+
| Variable_name | Value                                          |
+---------------+------------------------------------------------+
| log_error     | D:\mysql-5.7.30-winx64\data\PS2019PQMJKMWO.err |
+---------------+------------------------------------------------+
1 row in set, 1 warning (0.00 sec)
```

