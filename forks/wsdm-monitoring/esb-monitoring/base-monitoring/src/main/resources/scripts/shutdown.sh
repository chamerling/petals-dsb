#!/bin/sh

TARGET_DIRECTORY=`dirname $0`
CURRENT_DIRECTORY=`pwd`
TARGET_DIRECTORY=`cd $TARGET_DIRECTORY;pwd`

usage()
{
	echo "The shutdown.sh script is used to shutdown ESB Base Monitoring."
    echo "usage : shutdown.sh [-h|-y]"
    echo "     -h : prints the help message"
    echo "     -y : no confirmation is asked"
    exit 0;
}

######################
#   ENV VARIABLES
######################
if [ ! -x "$TARGET_DIRECTORY"/environment.sh ] 
then
  echo "Cannot find $TARGET_DIRECTORY/environment.sh script."
  echo "This file is required to shutdown ESB Base Monitoring."
  exit 1
fi

. "$TARGET_DIRECTORY"/environment.sh


######################
#   ARGUMENTS
######################

cd "$PETALS_HOME/bin"

Confirmed=false
while getopts :hv:yv options
do
        case ${options} in
                h)
                        usage
                        ;;
                y)
                        Confirmed=true
                        ;;
        esac
done

if [ "$Confirmed" = "false" ]
then
	echo "Are you sure to remove this ESB Base Monitoring from your Monitoring network (Y/n) ?"
	echo "    - all services assembly of this container will be undeployed,"
	echo "    - all components of this container will be uninstalled,"
	echo "    - this ESB Base Monitoring container will be removed from the topology."
	read ANSWER
	if [ "$ANSWER" = "Y" ]
	then
		Confirmed=true
	fi
fi

Return=0
if [ "$Confirmed" = "true" ]
then
	java -jar server.jar shutdown
	Return=$?
else
	echo "Shutdown canceled."
fi

cd "$CURRENT_DIRECTORY"

if [ $Return != 0 ]
then
	exit 1
else
	exit 0
fi
