#!/bin/sh

TARGET_DIRECTORY=`dirname $0`

######################
#   ENV VARIABLES
######################

echo "Setting PEtALS environment"

if [ ! -d "$JAVA_HOME" ]
then
        echo "The JAVA_HOME variable is not properly set. Please set it in your environment."
        exit 2
fi

if [ ! -d "$PETALS_HOME" ]
then
	if [ ! -z "$PETALS_HOME" ]
	then
		echo "The PETALS_HOME variable is not properly set. Try to set it automatically."
	fi
	PETALS_HOME=`cd "$TARGET_DIRECTORY"/..;pwd`
	export PETALS_HOME
fi

if [ ! -f "$PETALS_HOME/conf/server.properties" ]
then
	echo "The PETALS_HOME environment variable is not set correctly. Please set it in your environment with a correct value."
	exit 3
fi

PATH="$JAVA_HOME/bin:$PATH"
export PATH

java -version
echo ""