- [InnoDB数据结构](#InnoDB数据结构)
  - [文件类型](#文件类型)
  - [参数文件](#参数文件)
  - [日志文件](#日志文件)
    - [错误日志文件](#错误日志文件)
    - [慢查询日志文件](#慢查询日志文件)
  - [InnoDB存储引擎文件](#InnoDB存储引擎文件)
  - [分析工具]($分析工具)
    - [py_innodb_page_info](#py_innodb_page_info)
    - [hexdump](#hexdump)
  - [索引组织表](#索引组织表)
  - [InnoDB逻辑存储结构](#InnoDB逻辑存储结构)
  - [InnoDB数据页结构](#InnoDB数据页结构)
  - [InnoDB行记录格式](#InnoDB行记录格式)
    - [Compact行记录格式](#Compact行记录格式)
    - [Redundant行记录格式](#Redundant行记录格式)
    - [Compressed与Dynamic行格式记录](#Compressed与Dynamic行格式记录)

# InnoDB数据结构

> 本文主要是对《MySQL技术内幕：InnoDB存储引擎》第3章与第4章做笔记记录，以实战记录、核心理论记录为主

### 文件类型

构成MySQL数据库和InnoDB存储引擎表的各种类型文件
- 参数文件
- 日志文件
- sokcet文件
- pid文件
- MySQL表结构文件
- 存储引擎文件
- 重做日志文件

### 参数文件

当MySQL实例启动时，数据库会先去读一个配置参数文件，用来寻找数据库的各种文件所在位置以及指定某些初始化参数，这些参数通常定义了某种内存结构有多大等

用户只需通过命令 mysql --help | grep my.cnf 来寻找即可

```
mysql --help | grep my.cnf
                      order of preference, my.cnf, $MYSQL_TCP_PORT,
/etc/my.cnf /etc/mysql/my.cnf ~/.my.cnf
```

查看 /etc/mysql/my.cnf

```
cat /etc/mysql/my.cnf
#
# The MySQL database server configuration file.
#
# You can copy this to one of:
# - "/etc/mysql/my.cnf" to set global options,
# - "~/.my.cnf" to set user-specific options.
#
# One can use all long options that the program supports.
# Run program with --help to get a list of available options and with
# --print-defaults to see which it would actually understand and use.
#
# For explanations see
# http://dev.mysql.com/doc/mysql/en/server-system-variables.html

#
# * IMPORTANT: Additional settings that can override those from this file!
#   The files must end with '.cnf', otherwise they'll be ignored.
#

!includedir /etc/mysql/conf.d/
!includedir /etc/mysql/mysql.conf.d/
```

查看 /etc/mysql/mysql.conf.d/mysqld.cnf

```
cat /etc/mysql/mysql.conf.d/mysqld.cnf
#
# The MySQL database server configuration file.
#
# You can copy this to one of:
# - "/etc/mysql/my.cnf" to set global options,
# - "~/.my.cnf" to set user-specific options.
#
# One can use all long options that the program supports.
# Run program with --help to get a list of available options and with
# --print-defaults to see which it would actually understand and use.
#
# For explanations see
# http://dev.mysql.com/doc/mysql/en/server-system-variables.html

# This will be passed to all mysql clients
# It has been reported that passwords should be enclosed with ticks/quotes
# escpecially if they contain "#" chars...
# Remember to edit /etc/mysql/debian.cnf when changing the socket location.

# Here is entries for some specific programs
# The following values assume you have at least 32M ram

[mysqld_safe]
socket          = /var/run/mysqld/mysqld.sock
nice            = 0

[mysqld]
#
# * Basic Settings
#
user            = mysql
pid-file        = /var/run/mysqld/mysqld.pid
socket          = /var/run/mysqld/mysqld.sock
port            = 3306
basedir         = /usr
datadir         = /var/lib/mysql
tmpdir          = /tmp
lc-messages-dir = /usr/share/mysql
skip-external-locking
#
# Instead of skip-networking the default is now to listen only on
# localhost which is more compatible and is not less secure.
# bind-address          = 127.0.0.1
#
# * Fine Tuning
#
key_buffer_size         = 16M
max_allowed_packet      = 16M
thread_stack            = 192K
thread_cache_size       = 8
# This replaces the startup script and checks MyISAM tables if needed
# the first time they are touched
myisam-recover-options  = BACKUP
#max_connections        = 100
#table_open_cache       = 64
#thread_concurrency     = 10
#
# * Query Cache Configuration
#
query_cache_limit       = 1M
query_cache_size        = 16M
#
# * Logging and Replication
#
# Both location gets rotated by the cronjob.
# Be aware that this log type is a performance killer.
# As of 5.1 you can enable the log at runtime!
#general_log_file        = /var/log/mysql/mysql.log
#general_log             = 1
#
# Error log - should be very few entries.
#
log_error = /var/log/mysql/error.log
#
# Here you can see queries with especially long duration
#slow_query_log         = 1
#slow_query_log_file    = /var/log/mysql/mysql-slow.log
#long_query_time = 2
#log-queries-not-using-indexes
#
# The following can be used as easy to replay backup logs or for replication.
# note: if you are setting up a replication slave, see README.Debian about
#       other settings you may need to change.
#server-id              = 1
#log_bin                        = /var/log/mysql/mysql-bin.log
expire_logs_days        = 10
max_binlog_size   = 100M
#binlog_do_db           = include_database_name
#binlog_ignore_db       = include_database_name
#
# * InnoDB
#
# InnoDB is enabled by default with a 10MB datafile in /var/lib/mysql/.
# Read the manual for more InnoDB related options. There are many!
#
# * Security Features
#
# Read the manual, too, if you want chroot!
# chroot = /var/lib/mysql/
#
# For generating SSL certificates I recommend the OpenSSL GUI "tinyca".
#
# ssl-ca=/etc/mysql/cacert.pem
# ssl-cert=/etc/mysql/server-cert.pem
# ssl-key=/etc/mysql/server-key.pem
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
+---------------+--------------------------+
| Variable_name | Value                    |
+---------------+--------------------------+
| log_error     | /var/log/mysql/error.log |
+---------------+--------------------------+
1 row in set (0.00 sec)
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
+---------------------+---------------------------------------+
| Variable_name       | Value                                 |
+---------------------+---------------------------------------+
| slow_query_log_file | /var/lib/mysql/VM-8-6-ubuntu-slow.log |
+---------------------+---------------------------------------+
1 row in set (0.00 sec)

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

尝试 SET GLOBAL slow_query_log=ON; 并执行一条长SQL语句，查看 /var/lib/mysql/VM-8-6-ubuntu-slow.log

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

### InnoDB存储引擎文件

来到 /var/lib/mysql，查看有如下文件，其中mysql、performance_schema、peter、sys都是文件夹，这些文件夹名字与数据库名字是一一对应的

```
/var/lib/mysql# ls -l
total 122944
-rw-r----- 1 mysql mysql       56 Dec 23 17:52 auto.cnf
-rw------- 1 mysql mysql     1676 Dec 23 17:52 ca-key.pem
-rw-r--r-- 1 mysql mysql     1112 Dec 23 17:52 ca.pem
-rw-r--r-- 1 mysql mysql     1112 Dec 23 17:52 client-cert.pem
-rw------- 1 mysql mysql     1680 Dec 23 17:52 client-key.pem
-rw-r--r-- 1 root  root         0 Dec 23 17:52 debian-5.7.flag
-rw-r----- 1 mysql mysql      342 Dec 23 18:05 ib_buffer_pool
-rw-r----- 1 mysql mysql 12582912 Dec 23 18:12 ibdata1
-rw-r----- 1 mysql mysql 50331648 Dec 23 18:12 ib_logfile0
-rw-r----- 1 mysql mysql 50331648 Dec 23 17:52 ib_logfile1
-rw-r----- 1 mysql mysql 12582912 Dec 23 18:15 ibtmp1
drwxr-x--- 2 mysql mysql     4096 Dec 23 17:52 mysql
drwxr-x--- 2 mysql mysql     4096 Dec 23 17:52 performance_schema
drwxr-x--- 2 mysql mysql     4096 Dec 23 18:12 peter
-rw------- 1 mysql mysql     1680 Dec 23 17:52 private_key.pem
-rw-r--r-- 1 mysql mysql      452 Dec 23 17:52 public_key.pem
-rw-r--r-- 1 mysql mysql     1112 Dec 23 17:52 server-cert.pem
-rw------- 1 mysql mysql     1676 Dec 23 17:52 server-key.pem
drwxr-x--- 2 mysql mysql    12288 Dec 23 17:52 sys
```

查看所有数据库

```
mysql> show databases;
+--------------------+
| Database           |
+--------------------+
| information_schema |
| mysql              |
| performance_schema |
| peter              |
| sys                |
+--------------------+
5 rows in set (0.00 sec)
```

比如 peter 文件夹，它也是一个数据库名字（我创建的），文件夹中存储着关键数据信息

```
/var/lib/mysql/peter# ls -l
total 220
-rw-r----- 1 mysql mysql    65 Dec 23 18:08 db.opt
-rw-r----- 1 mysql mysql  8674 Dec 23 18:11 t1.frm
-rw-r----- 1 mysql mysql 98304 Dec 23 18:11 t1.ibd
-rw-r----- 1 mysql mysql  8674 Dec 23 18:12 t2.frm
-rw-r----- 1 mysql mysql 98304 Dec 23 18:12 t2.ibd
```

查看 peter 数据库的表

```
mysql> show tables in peter;
+-----------------+
| Tables_in_peter |
+-----------------+
| t1              |
| t2              |
+-----------------+
2 rows in set (0.00 sec)
```

文件 t1.xxx，t2.xxx 中 t1、t2 对应着表的名称

.frm 是表结构定义文件，它记录了该表的表结构定义

.ibd 是表空间文件，它存储每张表的数据、索引、插入缓冲Bitmap页

值得一提的是，每张表中信息并非全部存储于它的表空间，还有一些是存储在共享表空间文件中的

在/var/lib/mysql/下，还有一些重要文件

ibdata1 是共享表空间文件，它存储回滚信息、插入缓冲索引页、系统事务信息、二次写缓冲等

ib_logfile0 与 ib_logfile1 是重做日志文件，当实例或介质失败时，重做日志文件能帮助恢复数据

### 分析工具

#### py_innodb_page_info

通过 py_innodb_page_info 可以分析 .ibd 文件

参考 [py_innodb_page_info工具使用](https://blog.csdn.net/dbLjy2015/article/details/52837374)

touch 3份python文件到 /root/py_innodb_page_info 下

py_innodb_page_info.py
```python
#! /usr/bin/env python 
#encoding=utf-8
import mylib
from sys import argv
from mylib import myargv
 
if __name__ == '__main__':
	myargv = myargv(argv)
	if myargv.parse_cmdline() == 0:
		pass
	else:
		mylib.get_innodb_page_type(myargv)
```

mylib.py
```python
#encoding=utf-8
import os
import include
from include import *
 
VARIABLE_FIELD_COUNT = 1
NULL_FIELD_COUNT = 0
 
class myargv(object):
    def __init__(self, argv):
        self.argv = argv
        self.parms = {}
        self.tablespace = ''
 
    def parse_cmdline(self):
        argv = self.argv
        if len(argv) == 1:
            print 'Usage: python py_innodb_page_info.py [OPTIONS] tablespace_file'
            print 'For more options, use python py_innodb_page_info.py -h'
            return 0 
        while argv:
            if argv[0][0] == '-':
                if argv[0][1] == 'h':
                    self.parms[argv[0]] = ''
                    argv = argv[1:]
                    break
                if argv[0][1] == 'v':
                    self.parms[argv[0]] = ''
                    argv = argv[1:]
                else:
                    self.parms[argv[0]] = argv[1]
                    argv = argv[2:]
            else:
                self.tablespace = argv[0]
                argv = argv[1:]
        if self.parms.has_key('-h'):
            print 'Get InnoDB Page Info'
            print 'Usage: python py_innodb_page_info.py [OPTIONS] tablespace_file\n'
            print 'The following options may be given as the first argument:'
            print '-h   help '
            print '-o output put the result to file'
            print '-t number thread to anayle the tablespace file'
            print '-v   verbose mode'
            return 0
        return 1
 
def mach_read_from_n(page,start_offset,length):
    ret = page[start_offset:start_offset+length]
    return ret.encode('hex')
 
def get_innodb_page_type(myargv):
    f=file(myargv.tablespace,'rb')
    fsize = os.path.getsize(f.name)/INNODB_PAGE_SIZE
    ret = {}
    for i in range(fsize):
        page = f.read(INNODB_PAGE_SIZE)
        page_offset = mach_read_from_n(page,FIL_PAGE_OFFSET,4)
        page_type = mach_read_from_n(page,FIL_PAGE_TYPE,2)
        if myargv.parms.has_key('-v'):
            if page_type == '45bf':
                page_level = mach_read_from_n(page,FIL_PAGE_DATA+PAGE_LEVEL,2)
                print "page offset %s, page type <%s>, page level <%s>"%(page_offset,innodb_page_type[page_type],page_level)
            else:
                print "page offset %s, page type <%s>"%(page_offset,innodb_page_type[page_type])
        if not ret.has_key(page_type):
            ret[page_type] = 1
        else:
            ret[page_type] = ret[page_type] + 1
    print "Total number of page: %d:"%fsize
    for type in ret:
        print "%s: %s"%(innodb_page_type[type],ret[type])
```

include.py
```python
#include.py
#encoding=utf-8
INNODB_PAGE_SIZE = 1024 * 16 # InnoDB Page 16K
 
# Start of the data on the page
FIL_PAGE_DATA = 38
 
 
FIL_PAGE_OFFSET = 4 # page offset inside space
FIL_PAGE_TYPE = 24 # File page type
 
# Types of an undo log segment */
TRX_UNDO_INSERT = 1
TRX_UNDO_UPDATE = 2
 
# On a page of any file segment, data may be put starting from this offset
FSEG_PAGE_DATA = FIL_PAGE_DATA
 
# The offset of the undo log page header on pages of the undo log
TRX_UNDO_PAGE_HDR = FSEG_PAGE_DATA
 
PAGE_LEVEL = 26 #level of the node in an index tree; the leaf level is the level 0 */
 
innodb_page_type={
        '0000':u'Freshly Allocated Page',
        '0002':u'Undo Log Page',
        '0003':u'File Segment inode',
        '0004':u'Insert Buffer Free List',
        '0005':u'Insert Buffer Bitmap',
        '0006':u'System Page',
        '0007':u'Transaction system Page',
        '0008':u'File Space Header',
        '0009':u'扩展描述页',
        '000a':u'Uncompressed BLOB Page',
        '000b':u'1st compressed BLOB Page',
        '000c':u'Subsequent compressed BLOB Page',
        '45bf':u'B-tree Node'
}
innodb_page_direction={
	'0000': 'Unknown(0x0000)',
        '0001': 'Page Left',
        '0002': 'Page Right',
        '0003': 'Page Same Rec',
        '0004': 'Page Same Page',
        '0005': 'Page No Direction',
        'ffff': 'Unkown2(0xffff)'
}
```

然后 touch ibd 文件，chmod +x ibd 设置可执行权限

```
python /root/py_innodb_page_info/py_innodb_page_info.py $*
```

在 ~/.bashrc 文件末尾加上

```
export PATH=/root/py_innodb_page_info/:$PATH
```

此时就可以通过 ibd xxx.ibd -v 来分析 ibd 文件了

```
/var/lib/mysql/peter# ibd t1.ibd -v
page offset 00000000, page type <File Space Header>
page offset 00000001, page type <Insert Buffer Bitmap>
page offset 00000002, page type <File Segment inode>
page offset 00000003, page type <B-tree Node>, page level <0000>
page offset 00000000, page type <Freshly Allocated Page>
page offset 00000000, page type <Freshly Allocated Page>
Total number of page: 6:
Freshly Allocated Page: 2
Insert Buffer Bitmap: 1
File Space Header: 1
B-tree Node: 1
File Segment inode: 1
```

#### hexdump

.ibd文件是二进制的，通过hexdump可以将其内容解析成十六进制形式

命令形式：hexdump -C -v xxx.ibd

```
/var/lib/mysql/peter# hexdump -C -v t1.ibd | head
00000000  f8 49 ca f7 00 00 00 00  00 00 00 00 00 00 00 00  |.I..............|
00000010  00 00 00 00 00 29 fa 34  00 08 00 00 00 00 00 00  |.....).4........|
00000020  00 00 00 00 00 1a 00 00  00 1a 00 00 00 00 00 00  |................|
00000030  00 06 00 00 00 40 00 00  00 21 00 00 00 04 00 00  |.....@...!......|
00000040  00 00 ff ff ff ff 00 00  ff ff ff ff 00 00 00 00  |................|
00000050  00 01 00 00 00 00 00 9e  00 00 00 00 00 9e 00 00  |................|
00000060  00 00 ff ff ff ff 00 00  ff ff ff ff 00 00 00 00  |................|
00000070  00 00 00 00 00 03 00 00  00 00 ff ff ff ff 00 00  |................|
00000080  ff ff ff ff 00 00 00 00  00 01 00 00 00 02 00 26  |...............&|
00000090  00 00 00 02 00 26 00 00  00 00 00 00 00 00 ff ff  |.....&..........|
```

### 索引组织表

在InnoDB存储引擎中，表都是根据主键顺序组织存放的，这种存储方式的表称为索引组织表（index organized table）

每张表都有个主键（Primary Key），如果没有显示的定义主键，InnoDB会优先判断是否存在唯一索引（UNIQUE NOT NULL），若有，选择最先定义的唯一索引列作为主键列，若无，会自动创建一个6字节大小的指针作为主键列

创建一张表，插入数据，select中带上_rowid 即可查看主键（主键是多列时无法通过_rowid 查看）

```
mysql> create table test ( a INT NOT NULL, b INT NULL, c INT NOT NULL, d INT NOT NULL, UNIQUE KEY(b), UNIQUE KEY(d), UNIQUE KEY(c));
mysql> insert into test select 1,2,3,4;
mysql> SELECT a,b,c,d,_rowid FROM test;
+---+------+---+---+--------+
| a | b    | c | d | _rowid |
+---+------+---+---+--------+
| 1 |    2 | 3 | 4 |      4 |
+---+------+---+---+--------+
1 row in set (0.00 sec)
```

可以发现上面，定义了三个UNIQUE KEY的列，但b是NULL，不能作为主键，d比c更早定义，因此选取d作为主键列

也就是说，主键的选择是根据定义索引的顺序，而不是建表时列的顺序

### InnoDB逻辑存储结构

从InnoDB存储引擎的逻辑存储结构来看，所有数据都被逻辑地存放在一个空间中，称之为表空间（tablesapce）

表空间又由段（segment）、区（extent）、页（page）组成（页有时也被称为块），页由行（row）组成，大致逻辑关系图如下

![image](https://user-images.githubusercontent.com/10209135/103002911-c3471e80-456a-11eb-99ee-9a8f2e732ec3.png)

表空间是由各个段组成的，常见的段有数据段、索引段、回滚段等

区是由连续页组成的空间，在任何情况下每个区的大小都为1MB，为了保证区中页的连续性，InnoDB存储引擎一次从磁盘申请4-5个区，默认情况下，一个区中有64个连续的额页

InnoDB中页是磁盘管理的最小单位，默认每个页大小为16KB，可以通过innodb_page_size查询及设置大小为4K、8K、16K

InnoDB存储引擎是row-oriented的，也就是按照行进行存放的

### InnoDB数据页结构

InnoDB数据页由以下7个部分组成

| 名称 | 中文名 | 占用空间 | 简单描述 |
| -- | -- | -- | -- |
| File Header | 文件头部 | 38字节 | 页的一些通用信息 |
| Page Header | 页面头部 | 56字节 | 数据页专有的一些信息 |
| Infimum + Supremum | 最小记录和最大记录 | 26字节 | 两个虚拟的行记录 |
| User Records | 用户记录 | 不确定 | 实际存储的行记录内容 |
| Free Space | 空闲空间 | 不确定 | 页中尚未使用的空间 |
| Page Directory | 页面目录 | 不确定 | 页中的某些记录的相对位置 |
| File Trailer | 文件尾部 | 8字节 | 校验页是否完整 |

其中 File Header、Page Header、File Trailer 的大小是固定的，分别为 38、56、8 字节，这些空间用来标记该页的一些信息，如 CheckSum，数据页所在 B+树索引的层数等

User Records、Free Space、Page Directory 这些部分为实际的行记录存储空间，因此大小是动态的

### InnoDB行记录格式

InnoDB中页保存着表中一行一行的数据，一般有Compact、Redundant、Compressed及Dynamic这几种格式

可以通过 SHOW TABLE STATUS LIKE "<table_name>" 来查看当前表使用的行格式

```sql
mysql> SHOW TABLE STATUS LIKE 't1';
+------+--------+---------+------------+------+----------------+-------------+-----------------+--------------+-----------+----------------+---------------------+-------------+------------+-------------------+----------+----------------+---------+
| Name | Engine | Version | Row_format | Rows | Avg_row_length | Data_length | Max_data_length | Index_length | Data_free | Auto_increment | Create_time         | Update_time | Check_time | Collation         | Checksum | Create_options | Comment |
+------+--------+---------+------------+------+----------------+-------------+-----------------+--------------+-----------+----------------+---------------------+-------------+------------+-------------------+----------+----------------+---------+
| t1   | InnoDB |      10 | Dynamic    |    0 |              0 |       16384 |               0 |            0 |         0 |           NULL | 2020-12-24 11:36:30 | NULL        | NULL       | latin1_swedish_ci |     NULL |                |         |
+------+--------+---------+------------+------+----------------+-------------+-----------------+--------------+-----------+----------------+---------------------+-------------+------------+-------------------+----------+----------------+---------+
1 row in set (0.00 sec)
```

从上述结果中可知 Row_format = Dynamic

#### Compact行记录格式

Compact行记录是在MySQL5.0中引入的，其设计目标是高效地存储数据。简单来说，一个页中存放的行数据越多，其性能就越高

Compact行记录格式

|列名|字节|作用|
|--|--|--|
|变长字段长度列表|0-2|记录数据列中变长变量的长度|
|NULL标志位|0-1|记录数据列中长度为NULL的列|
|记录头信息|5|记录头信息|
|列1数据|不定|真实数据|
|列2数据|不定|真实数据|
|..|不定|真实数据|

Compact记录头信息

|名称|字节|作用|
|--|--|--|
|()|1|未知|
|()|1|未知|
|deleted_flag|1|该行是否已被删除|
|min_rec_flag|1|为1，如果该记录是预先被定义为最小的记录|
|n_owned|4|该记录拥有的记录数|
|heap_no|13|索引堆中该条记录的排序记录|
|record_type|3|记录类型，000表示普通，001表示B+树节点指针，010表示Infimum，011表示Supremum，1xx表示保留|
|next_record|16|页中下一条记录的相对位置|
|Total|40||

具体的例子解释见《MySQL技术内幕：InnoDB存储引擎》第二版P105

记录的真实数据，除了我们自己定义的列的数据以外，还会有三个隐藏列

|列名|是否必须|占用空间|描述|
|--|--|--|--|
|row_id|否|6字节|行ID，唯一标识一条记录|
|transaction_id|是|6字节|事务ID|
|roll_pointer|是|7字节|回滚指针|

#### Redundant行记录格式

Redundant是MySQL5.0版本之前InnoDB的行记录存储方式，MySQL5.0支持Redundant是为了兼容之前版本的页格式

Redundant行记录格式

|列名|字节|作用|
|--|--|--|
|字段长度偏移列表|0-2||
|记录头信息|5|记录头信息|
|列1数据|不定|真实数据|
|列2数据|不定|真实数据|
|..|不定|真实数据|

Redundant记录头信息

|名称|字节|作用|
|--|--|--|
|()|1|未知|
|()|1|未知|
|deleted_flag|1|该行是否已被删除|
|min_rec_flag|1|为1，如果该记录是预先被定义为最小的记录|
|n_owned|4|该记录拥有的记录数|
|heap_no|13|索引堆中该条记录的排序记录|
|n_fields|10|记录中列的数量|
|1byte_offs_flag|1|偏移列表为1字节还是2字节|
|next_record|16|页中下一条记录的相对位置|
|Total|48||

#### Compressed与Dynamic行格式记录

来看一个行溢出数据的例子

运行下面这一段sql
```sql
USE `mysql-learning`;
CREATE TABLE `test` (
	a VARCHAR(65535)
) CHARSET=ascii ROW_FORMAT=Compact;
```

得到错误结果，意思是一行的最大字节数为65535，而其不仅仅是用户记录，还有变长字段长度列表、NULL值列表等
Error Code: 1118. Row size too large. The maximum row size for the used table type, not counting BLOBs, is 65535. This includes storage overhead, check the manual. You have to change some columns to TEXT or BLOBs

Dynamic和Compressed行格式

这两种行格式类似于COMPACT行格式，只不过在处理行溢出数据时有点儿分歧，它们不会在记录的真实数据处存储一部分数据，而是把所有的数据都存储到其他页面中，只在记录的真实数据处存储其他页面的地址。另外，Compressed行格式会采用压缩算法对页面进行压缩
