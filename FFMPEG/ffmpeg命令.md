
# ffmpeg命令

以下分类灵感来源于《2-1 FFmpeg常用命令分类讲解.mp4》李超

![](https://raw.githubusercontent.com/peteryuanpan/notebook/master/FFMPEG/ffmpeg%E5%91%BD%E4%BB%A4%E5%88%86%E7%B1%BB.png)

## 1.基本查询命令

### 查询机子上有哪些设备

```
ffmpeg -f avfoundation -list_devices true -i ""
```

### 查询音视频的元信息

```
ffprobe -v quiet -show_format -print_format json -show_streams URL
```

## 2.录制命令

### 录屏（按q结束，下同）

```
ffmpeg -f avfoundation -i 1 -r 30 out.mp4
```

### 录音

```
ffmpeg -f avfoundation -i :0 out.wav
```

### 录屏 + 声音

```
ffmpeg -f avfoundation -i 1:0 -r 29.97 -c:v libx264 -crf 0 -c:a aac_at -profile:a aac_he_v2 -b:a 32k out.mp4
```

## 3.分解与复用命令

### 抽取音频流

```
ffmpeg -i 1.mp4 -acodec copy -vn out.aac

acodec：指定音频编码器
copy：指明只拷贝，不做编解码
vn：v代表视频，n 代表 no 也就是无视频的意思
```

### 抽取视频流

```
ffmpeg -i 1.mp4 -vcodec copy -an out.h264

vcodec：指定视频编码器
copy：指明只拷贝，不做编解码
ac：a表示视频，n 代表 no 也就是无音频的意思
```

## 4.处理原始数据命令

### 设置分辨率

```
ffmpeg -i 1.mp4 -vf scale=640:360 1-2.mp4 -hide_banner
```

## 5.裁剪与合并命令

### 裁剪

```
ffmpeg -i 1.mp4 -ss 00:00:00 -to 00:01:00 -c copy 1-1min.mp4
```

### 音视频合并

```
ffmpeg -i out.h264 -i out.aac -vcodec copy -acodec copy out.mp4
```

## 6.图片/视频互转命令

### 转码（mp4转flv）

```
ffmpeg -i 1.mp4 -vcodec copy -acodec copy 1.flv
```

### 转码（m3u8转mp4）

```
ffmpeg -protocol_whitelist "file,http,https,tcp,tls" -i 1.m3u8 -c copy 1.mp4
```

### 切片（mp4转m3u8）

```
ffmpeg -i 1.mp4 -c:v libx264 -hls_time 60 -hls_list_size 0 -c:a aac -strict -2 -f hls 1.m3u8
```

## 7.直播相关命令

### 推流

```
ffmpeg -re -i 'localfile' -c copy -f flv 'rtmp://xxx'
```

### ffplay带请求头播放

```
ffplay xxx -headers 'referer: xxx'
```

## 8.各种滤镜命令

TODO
