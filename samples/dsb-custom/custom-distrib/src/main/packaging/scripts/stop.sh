#!/bin/sh

TARGET_DIRECTORY=`dirname $0`
CURRENT_DIRECTORY=`pwd`
TARGET_DIRECTORY=`cd $TARGET_DIRECTORY;pwd`

######################
#   ENV VARIABLES
######################
if [ ! -x "$TARGET_DIRECTORY"/environment.sh ] 
then
  echo "Cannot find $TARGET_DIRECTORY/environment.sh script."
  echo "This file is required to stop PEtALS."
  exit 1
fi

. "$TARGET_DIRECTORY"/environment.sh

######################
#   ARGUMENTS
######################

cd "$PETALS_HOME/bin"

while getopts :hv:Dv:Cv options
do
        case ${options} in
                h)
                        echo "The stop.sh script is used to stop Petals."
                        echo "usage : stop.sh [-h]"
                        echo "     -h : prints the help message"
                        exit 0;
                        ;;
                ?)
                        echo "usage : stop.sh [-h] "
                        exit 2
                        ;;
        esac
done

java -jar server.jar stop
Return=$?

cd "$CURRENT_DIRECTORY"

if [ $Return != 0 ]
then
	exit 1
else
	exit 0
fi
