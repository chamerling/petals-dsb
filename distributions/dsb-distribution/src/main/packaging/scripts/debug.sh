#!/bin/sh

TARGET_DIRECTORY=`dirname $0`
CURRENT_DIRECTORY=`pwd`
TARGET_DIRECTORY=`cd $TARGET_DIRECTORY;pwd`

ProfilerId=netbeans
EclipseProfilerToLoad=CGProf

usage()
{
	echo "The debug.sh script is used to start Petals in debug mode."
	echo "usage : debug.sh [-h|-?]  [-P [<profilerId>]] [-M [<time|memory|thread>]]"
	echo "     -h|? : prints the help message"
	echo "     -P [<profilerId>] : Petals is starting in profiling mode instead of debug mode."
	echo "                       The Java Profiling Agent is defined by <profilerId>."
	echo "                             Possible values are:"
	echo "                                    'eclipse'  : to use the eclipse agent controler."
	echo "                                    'netbeans' : to use the netbeans agent (default value)."
	echo "     -M [<time|memory|thread>] : In case of the Eclispe profiling, use this parameter"
	echo "                                 to define the profiler library to load (ie. Agent Controller)."
	echo "                                 Possible values are:"
	echo "                                    'CGProf'     : execution time analysis (default value)."
	echo "                                    'HeapProf'   : object allocation/heap analysis."
	echo "                                    'ThreadProf' : thread analysis."

	exit 0;
}


######################################
#   ENV VARIABLES
######################################
if [ ! -x "$TARGET_DIRECTORY"/environment.sh ] 
then
  echo "Cannot find $TARGET_DIRECTORY/environment.sh script."
  echo "This file is required to debug PEtALS."
  exit 1
fi

. "$TARGET_DIRECTORY"/environment.sh

if [ -z "$JAVA_OPTS" ]; then
    JAVA_OPTS="-Xmx1024m -XX:MaxPermSize=256m"
fi

######################
#   ARGUMENTS
######################

cd "$PETALS_HOME/bin"

DebugMode=true
ProfilingAgent=false
CommandLine=false
while getopts :hv:P:M:dv: options
do
        case ${options} in
                h)
						usage
                        ;;
                P)
                        ProfilingAgent=true
                        DebugMode=false
                        ProfilerId=$OPTARG
                        ;;
                M)
                        EclipseProfilerToLoad=$OPTARG
                        ;;
        esac
done

if [ $DebugMode != false ]
then
	echo "Debug mode activated."
	DEBUG_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE  -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y"
fi

if [ $ProfilingAgent != false ]
then
	if [ "$ProfilerId" = "eclipse" ]
	then
		echo "Eclipse Java Profiling Agent activated."
		if [ -z $TPTP_AC_HOME ]
	        then
			echo "The TPTP_AC_HOME Agent Controler Home variable is not set. Please initialize it in your environment."
			exit 1
		fi

		# Add a check ti verify that the agentcontroler is started

		JAVA_PROFILER_HOME="$TPTP_AC_HOME/plugins/org.eclipse.tptp.javaprofiler"
		LD_LIBRARY_PATH="$JAVA_PROFILER_HOME:$TPTP_AC_HOME/lib:$LD_LIBRARY_PATH"
		PATH="$JAVA_PROFILER_HOME:$PATH:$TPTP_AC_HOME/bin"
		PROFILE_OPTS="-agentlib:JPIBootLoader=JPIAgent:server=enabled"
		if [ "$EclipseProfilerToLoad" = "time" ]
		then
			PROFILE_OPTS="$PROFILE_OPTS;CGProf:execdetails=true"
		elif [ "$EclipseProfilerToLoad" = "memory" ]
		then
			PROFILE_OPTS="$PROFILE_OPTS;HeapProf:allocsites=true"
		elif [ "$EclipseProfilerToLoad" = "thread" ]
		then
			PROFILE_OPTS="$PROFILE_OPTS;ThreadProf"
		else
			echo "Unknown profiler library to load: $EclipseProfilerToLoad."
			exit 1
		fi

	elif [ "$ProfilerId" = "netbeans" ]
	then
		echo "NetBeans Java Profiling Agent activated."
		if [ -d "$NETBEANS_PROFILER_HOME" ]
	        then
			echo "The NETBEANS_PROFILER_HOME variable is not set. Please initialize it on your environment."
			exit 1
		fi
		PROFILE_OPTS="-agentpath:$NETBEANS_PROFILER_HOME/lib/deployed/jdk15/linux/libprofilerinterface.so=$NETBEANS_PROFILER_HOME/lib,5140"
	fi
fi

export PATH
export LD_LIBRARY_PATH

java $JAVA_OPTS $DEBUG_OPTS $PROFILE_OPTS -cp $CLASSPATH -server -jar server.jar start $CLOption 
Return=$?
        
cd "$CURRENT_DIRECTORY"

if [ $Return != 0 ]
then 
	exit 1
else
	exit 0
fi
