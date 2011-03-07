#!/bin/sh

RepScript=`dirname $0`
RepCourant=`pwd`


######################
#   ENV VARIABLES
######################

echo "Setting Petals environment"

if [ ! -d "$JAVA_HOME" ]
then
        echo "The JAVA_HOME variable is not properly set. Please set it in your environment."
        exit 1
fi

if [ ! -d "$PETALS_HOME" ]
then
	PETALS_HOME="$RepCourant/$RepScript/.."
	export PETALS_HOME
fi

if [ ! -f "$PETALS_HOME/conf/server.properties" ]
then
	echo "The PETALS_HOME environment variable is not set correctly. Please set it in your environment with a correct value."
	exit 2
fi

PATH="$JAVA_HOME/bin:$PATH"
export PATH

java -version
echo ""


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

pingResult=`java -Xmx128m -jar server.jar version`

echo $pingResult | grep "Petals JBI Container - version:" >/dev/null 2>/dev/null
active=$?

cd "$RepCourant"

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
