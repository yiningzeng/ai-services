#!/bin/bash
cd /home/baymin/work/xiaongmao-services/test/shell/
echo "search...."
if [ ! -f pid$1.txt ];then
    if netstat -tlpn | grep $1
    then
      echo 1
    else
      echo 0
    fi
else
  echo 1
fi