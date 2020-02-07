#!/bin/bash

function help() {
    echo "一个参数:进程id"
}

if [ "$1" = "-h" ]; then
  help
  exit 0
fi

if [ $# -lt 1 ]; then
  help
  exit 1
fi

bash agent.sh $1 "jmxStart"
