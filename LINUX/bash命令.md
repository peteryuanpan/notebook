## 网络排障

### 解析 ip 192.168.0.1，转成二进制查看
```
ipcalc 192.168.0.1
```

### 查看tcp端口开启情况
```
sudo lsof -i tcp:80 -s tcp:listen
sudo lsof -i -t tcp:80 -s tcp:listen
```

### 查看本地ip
```
curl -4 icanhazip.com
```

### 查看本机内网ip
```
ipconfig getifaddr en0
```

### ab测试命令
```
ab -n 100 <uri>
```

### 表单上传
```
curl -v https://upload.qiniup.com/ -F'token=<token>' -F'file=<content>' -F'key=<key>'
```

### tcpflow抓包
```
tcpflow -c port 80
```

### tcpdump抓包
```
tcpdump -i en0 -w abc.pcap host 124.163.208.161 and port 80
```

### tcpdump查看包
```
tcpdump -qns 0 -A -r abc.pcap
```

### 切割pcap包
```
editcap -c 10000 tcpdump.pcap tcpdump/
```

## 其他

### 排序，用聚合方式实现（比直接sort高效）
```
cat smalllist.txt | awk -F ' ' '{a[$4]+=$5;b[$4]+=1}END{for(i in a){print b[i],a[i],i}}' | sort -nr
```

### 输出10到100行
```
cat hotlist-times.txt | awk 'NR >= 10 && NR <= 100'
```

### 整合两个文件，用'\t'隔离每行
```
paste -d "\t" file1.txt file2.txt
```

### 替换clientip为223.104.251.15
```
cat curl.txt | sed 's/<clientip>/223.104.251.15/g'
```

### while枚举每行
```
cat clientip.txt | while read i; sleep 0.1; do cat curl.txt | sed "s/<clientip>/$i/g" | sh; done
```

### cd到brew中/include/openssl的所在目录
```
cd $(brew --prefix openssl)/include/openssl
```

### 时间戳转型输出
```
echo '154139500711111' | cut -c1-10 | awk '{print strftime("%Y:%m:%d %T", $1)}'
```

### grep子串匹配
```
grep -w '223\.104\.2\.5'
```

### 同步本地文件gebo123.txt 到远程磁盘上
```
scp -r folder root@116.62.187.159:folder
```

### 查看1.log的最后一行在2.log的哪几行
```
(tail -1 1.log | awk -F '`' '{print $2}') | while read i; do grep $i 2.log -n; done
```

### 杀掉进程pid
```
kill -9 <pid>
```

### 转f1.txt为f2.txt，并utf-8编码
```
iconv -f UTF-8 -t GBK f1.txt > f2.txt
```

### 判断file.txt是否存在
```
if test -s file.txt; then echo 'full'; else echo 'empty'; fi
```

### 查看磁盘空间情况
```
du -h
du -hs * | sort -h
du -ah | sort -h
```

### 16进制转10进制
```
echo $((16#FF))
echo "ibase=16; FF" | bc
perl -le 'print hex("FF");'
printf "%d\n" 0xFF
python -c 'print(int("FF", 16))'
```

### 按照,分隔，再按照第1列排序
```
sort -t, -nk1 file
sort -nk1
```

### 生成公钥私钥
```
ssh-keygen -t rsa
```

### 读文件x并输出x[a][b][c]、x[a][b][e]
```
cat x | jq '.a | .b | "\(.c),\(.e)"'
```

### 下载url-list.txt每行URL，并保存成文件名
```
cat url-list.txt | while read i; do
	echo $i;
	file=$(echo $i | cut -d "?" -f 1 | rev | cut -d "/" -f 1 | rev);
	curl -o $file $i
done
```

### 检测a.b.com在1.2.3.4的证书时间
```
echo | openssl s_client -servername a.b.com -connect 1.2.3.4:443 2>/dev/null | openssl x509 -noout -dates
```

### screen异步窗口命令
```
screen -RL jobname
"use ctr + A + D to quit"
screen -ls
screen -r jobname
```

### 查看linux cpu配置
```
lscpu
```

### 命令升级变色
```
curl -> http（brew install httpie）
cat -> ccat（brew install ccat）
top -> htop（brew install htop）
```

### 重定向输出到A
```
xxx | tee A
xxx | tee -a A（追加输出）
```

### 将奇偶行合为一行
```
cat 1.txt | awk 'NR%2 == 1{printf("%s\t",$0);}; NR%2 == 0{printf("%s\n",$0);}'
```
