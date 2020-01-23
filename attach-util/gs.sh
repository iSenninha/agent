#!/bin/bash

tmpFile='/tmp/agent.log'
function help() {
    echo "主要两个参数:进程id 执行脚本的路径"
    echo "执行的结果将会追加输出在:$tmpFile"
}

if [ "$1" = "-h" ]; then
  help
  exit 0
fi

if [ $# -lt 1 ]; then
  help
  exit 1
fi

if [ $# -lt 2 ]; then
  help
  exit 1
fi


# 调用脚本文件
sh agent.sh $1 "gs_""$2"
if [ -f $tmpFile ]; then
  echo "输出结果:$tmpFile"
  cat $tmpFile
else
  echo "无法找到输出文件:$tmpFile"
fi
