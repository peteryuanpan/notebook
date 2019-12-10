### MyBatis相比于JDBC及Hibernate的优点

```
MyBatis是一个半自动化的持久层框架

JDBC
- SQL夹在JAVA代码块里，耦合度高导致硬编码内伤
- 维护不易且实际开发需求中sql有变化，频繁修改的情况多见

Hibernate和JPA
- 长难复杂SQL，对于Hibernate而言处理也不容易
- 内部自动生成的SQL，不容易做特殊优化
- 基于全映射的全自动框架，大量字段的FOJO进行部分映射时比较困难，导致数据库性能下降

对于开发人员而言，核心sql还是需要自己优化

MyBatis，相比于JDBC及Hibernate
- sql及java代码是分开的，一个专注业务，一个专注数据，功能边界清晰
- 支持将针对sql语句做特殊优化，满足开发后期需求
- 轻量级、半自动框架
```

### Reference
- [bilibili MyBatis 视频教程全集 P1 MyBatis简介](https://www.bilibili.com/video/av59564271/?pikaqiu)
