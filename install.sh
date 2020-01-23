#!/bin/bash
mvn clean package
if [ $? != 0 ];then
	echo "maven compile error, please check"
	exit 1
fi

install_path='/usr/local/bin/agent'
if [ -e $install_path ];
then
	echo "install path exist $install_path"
else
	echo "create install path $install_path"
	sudo mkdir -p $install_path
fi


sudo cp -f agent-core/target/agent-core-1.0-SNAPSHOT-jar-with-dependencies.jar $install_path
sudo cp -f attach-util/target/attach-util-1.0-SNAPSHOT.jar $install_path
sudo cp -f attach-util/*.sh $install_path

if [ $? = 0 ];then
	echo "install success"
else
	echo "install failed"
fi
