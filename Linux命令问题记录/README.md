
# Linux命令问题记录

### git clone很慢

问题
- git clone URL，下载很慢

解决办法
- 用https方式，并且域名由github.com改为github.com.cnpmjs.org，这样能加速git clone
- 比如 git clone https://github.com.cnpmjs.org/peteryuanpan/notebook.git
- 但是这样后，git push权限验证可能会受阻，可以用git remote set-url origin git@github.com:peteryuanpan/notebook.git，改回git方式

### root用户无法ssh登陆

问题
- ssh root@<severip>，密码正确但登陆失败

解决办法
- sudo vi /etc/ssh/sshd_config
- 找到 # Authentication，在后面添加一行 PermitRootLogin yes，wq保存退出
- sudo service ssh restart
