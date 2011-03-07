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
  echo "This file is required to ping PEtALS."
  exit 1
fi

. "$TARGET_DIRECTORY"/environment.sh

######################
#   ARGUMENTS
######################

cd "$PETALS_HOME/bin"

Silence=false
while getopts :hv:Sv options
do
        case ${options} in
                h)
                        echo "The ping.sh script is used to determine if Petals is started."
                        echo "usage : ping [-h] [-S]"
                        echo "     -h : prints the help message"
                        echo "     -S : active the silent mode"
                        exit 0;
                        ;;
                S)
                        Silence=true
                        ;;
                ?)
                        echo "usage : ping [-h] [-S]"
                        exit 2
                        ;;
        esac
done

pingResult=`java -Xmx128m -jar server.jar version 2>&1`

echo $pingResult | grep "Petals JBI Container - version:" >/dev/null 2>/dev/null
active=$?

cd "$CURRENT_DIRECTORY"

if [ $active -eq 0 ]
then
        if [ $Silence != true ]
        then
                echo "Petals RUNNING"
        fi
        exit 0
else
        if [ $Silence != true ]
        then
                echo "Petals STOPPED"
        fi
        exit 1
fi
