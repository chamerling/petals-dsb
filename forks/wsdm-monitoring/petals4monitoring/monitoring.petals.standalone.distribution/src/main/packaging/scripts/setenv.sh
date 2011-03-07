#
#                        PEtALS environment
#
#############################################################################
NAME="petals-esb"
TARGET_DIRECTORY=`dirname $0`
if [ ! -d "$JAVA_HOME" ]
then
        JAVA_HOME="/usr/lib/jvm/java-6-sun"
        echo "The JAVA_HOME variable is wrong. We are going to try a default path: $JAVA_HOME"
        if [ ! -d "$JAVA_HOME" ]
		then
			echo "The JAVA_HOME variable is wrong. Please set it in your environment with a correct value."
        	exit 2        	
        fi
fi

if [ ! -d "$PETALS_HOME" ]
then	
	PETALS_HOME=`cd "$TARGET_DIRECTORY"/..;pwd`
	echo "The PETALS_HOME variable is wrong. We are going to try a default path: $PETALS_HOME"
	if [ ! -d "$PETALS_HOME" ]
	then
		echo "The JAVA_HOME variable is wrong. Please set it in your environment with a correct value."
		exit 3			
	fi	
fi

PETALS_OPTS="-Xms100m -Xmx1g -XX:MaxPermSize=256m"
DEBUG_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE  -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y"
PETALS_EXEC="${JAVA_HOME}/bin/java ${PETALS_OPTS} -jar ${PETALS_HOME}/bin/server.jar -server"
PETALS_DEBUG_EXEC="${JAVA_HOME}/bin/java ${PETALS_OPTS} ${DEBUG_OPTS} -jar ${PETALS_HOME}/bin/server.jar -server"
PATH="$PATH:$PETALS_HOME/bin"
export JAVA_HOME PETALS_HOME PETALS_OPTS PETALS_EXEC PATH