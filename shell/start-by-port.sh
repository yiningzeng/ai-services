#!/bin/bash
cd /home/baymin/work/xiaongmao-services/test/shell/
echo "starting...."
f="/home/baymin/data/darknet/scripts/http.py"
if [ ! -f $f ];then
  echo "文件不存在-1"
else
  if [ ! -f pid$1.txt ];then
    nohup python $f $1> $1.log 2>&1 & echo $! > pid$1.txt
    echo "success pid: $! 端口: $1"
  else
    echo "success 端口: $1 已存在"
  fi
fi
