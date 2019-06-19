#!/bin/bash
cd /home/baymin/work/xiaongmao-services/test/shell/
kill -9 `cat pid$1.txt`
rm "pid$1.txt"
rm "$1.log"
echo "success"