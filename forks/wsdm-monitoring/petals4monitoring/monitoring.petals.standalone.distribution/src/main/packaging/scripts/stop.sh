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

CLASSPATH="$PETALS_HOME/bin/server.jar:$CLASSPATH"
export CLASSPATH

java -version
echo ""


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

cd "$RepCourant"

if [ $Return != 0 ]
then
	exit 1
else
	exit 0
fi
