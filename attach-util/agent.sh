#!/bin/bash

base_path='/usr/local/bin/agent/'
attach_util='attach-util-1.0-SNAPSHOT.jar'
agent_lib='agent-core-1.0-SNAPSHOT-jar-with-dependencies.jar'

java -jar $base_path"$attach_util" $base_path$agent_lib "$@"
