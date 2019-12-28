
# BASE

### UTF-8
```
# coding: utf-8
```
```
#!/usr/bin/env python
# -*- coding: utf-8 -*-
```

### url decode
```
import urllib
urllib.unquote('')
```

### try except
```
try:
	1
except Exception as e:
	print(e)
```

### date time
```
from datetime import datetime, timedelta
def today():
	return datetime.strftime(datetime.now(), "%Y-%m-%d")
def yesterday():
	return datetime.strftime(datetime.now() - timedelta(1), "%Y-%m-%d")
```

# OS

### 运行
```
import os
def run_bash(cmd):
        stream = os.popen(cmd)
        output = stream.read()
        return output
```

### 判断文件/目录是否存在
```
import os.path
path = ''
os.path.isfile(path)
os.path.isdir(path)
```

# IO

### 读取文件
```
def read(filename):
        with open(filename) as f:
                lines = f.readlines()
        x = [x.strip() for x in lines]
        return x
```

### 读取gzip文件
```
import gzip
def readgzip(filename):
	with gzip.open(filename, 'rb') as f:
		file_content = f.read()
	return file_content
```

### 输出到文件
```
def write(outfile, text):
        # outfile = open('', 'w')
        outfile.write(text+"\n")
```

# CLASS

### class init
```
class ClassName(object):

	__member_name = ''

	def __init__(self):
		self.__member_name = ''
```

# Qiniu

### base64 encode/decode

```
from qiniu import utils
utils.urlsafe_base64_decode('')
utils.urlsafe_base64_encode('')
```
