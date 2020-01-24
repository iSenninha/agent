#!/bin/bash

base_path='/usr/local/bin/agent/'
attach_util='attach-util-1.0-SNAPSHOT.jar'
agent_lib='agent-core-1.0-SNAPSHOT-jar-with-dependencies.jar'

function help() {
    echo "主要两个参数:进程id agent指令"
    echo "agent指令如下:"
    java -jar $base_path$agent_lib
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

java -jar $base_path"$attach_util" $base_path$agent_lib "$@"
