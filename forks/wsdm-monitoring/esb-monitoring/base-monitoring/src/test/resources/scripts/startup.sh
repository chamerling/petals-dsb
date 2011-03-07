#!/bin/sh

TARGET_DIRECTORY=`dirname $0`
CURRENT_DIRECTORY=`pwd`
TARGET_DIRECTORY=`cd $TARGET_DIRECTORY;pwd`

#usage()
#{
#	echo "The startup.sh script is used to start Petals."
#	echo "usage : startup.sh [-h|-?] [-D|C] [-E] [-P <profilerId>]"
#	echo "     -h|? : prints the help message"
#	echo "     -D : Petals is starting in daemon mode"
#	echo "     -C : Petals is starting with command line"
#	exit 0;
#}


######################
#   ENV VARIABLES
######################

#if [ ! -x "$TARGET_DIRECTORY"/environment.sh ] 
#then
#  echo "Cannot find $TARGET_DIRECTORY/environment.sh script."
#  echo "This file is required to start PEtALS."
#  exit 1
#fi

#. "$TARGET_DIRECTORY"/environment.sh

#if [ ! -d "$PETALS_HOME/logs" ]
#then
#        mkdir "$PETALS_HOME/logs"
#fi

#LOG_FILE="$PETALS_HOME/logs/petals-out.log"

if [ -z "$JAVA_OPTS" ]; then
    JAVA_OPTS="-Xmx256m"
fi

######################
#   ARGUMENTS
######################

#cd "$PETALS_HOME/bin"

DaemonMode=false
CommandLine=false
while getopts :hv:Dv:Cv:Ev options
do
        case ${options} in
                h)
                        usage
                        ;;
                C)
                        CommandLine=true
                        CLOption="-console"
                        ;;
                D)
                        DaemonMode=true
                        ;;
                ?)
			usage
                        ;;
        esac
done


if [ $CommandLine != false -a $DaemonMode != false ]
then
        echo "ESB Base Monitoring can be started in daemon mode or with command line, but not the two at the same time."
        echo "Use -D or -C!"
        exit 1
fi



if [ $DaemonMode != false ]
then
        echo "ESB Base Monitoring starting in daemon mode..."
        nohup java $JAVA_OPTS -server -jar server.jar start $CLOption 1>$LOG_FILE 2>>$LOG_FILE &
        Return=$?
else
        java $JAVA_OPTS -server -jar server.jar start $CLOption
        Return=$?
fi

cd "$CURRENT_DIRECTORY"

if [ $Return != 0 ]
then
	exit 1
else
	exit 0
fi
