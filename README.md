# tomato

## 番茄笔记

|序号|日期|时间|耗时|目标事项|完成与否|备注 / 原因|
|-|-|-|-|-|-|-|
|04|2019-05-05|09:15 - 09:30|15min|跑通 rs_file_recorder_upload.c，完成上传|是|Qiniu_Zero(putPolicy)；注意声明putPolicy后加上这句话|
|03|2019-04-29|10:45 - 10:30|45min|实现python脚本发邮件到panyuan@qiniu.com|是|curl -XPOST https://morse.qiniu.io/api/notification/send/mail -H "Content-Type:application/json" -H "Client-Id:xxx" -d '{"content": "11", "to": ["panyuan@qiniu.com"], "uid": 0, "subject": ""}' -v|
|02|2019-04-26|00:00 - 00:30|30min|编译过DEMO rs_file_recorder_upload.c（https://github.com/litianqi1996/c-sdk-demo/blob/master/c-sdk-demo/up_resume_upload.cpp）|是|ld: symbol(s) not found for architecture x86_64；_strdup => strdup；记录于https://github.com/peteryuanpan/qdemo/blob/master/Kodo/recordUpload/rs_file_recorder_upload.c|
|01|2019-04-25|00:00 - 00:45|45min|在116.62.187.159上用crontab实现定时输出脚本|是|被nano命令保存给卡住了进度；已知晓nano命令如何保存；2019-04-29 10:30 已完成；crontab -l => */1 * * * * sh /root/panyuan/1.sh|
