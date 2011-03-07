@Echo Off

setlocal

Rem verif variables:
goto checkpetalshome

:checkpetalshome
if "%PETALS_HOME%" == "" (
	if exist ..\conf\server.properties (
		set PETALS_HOME=%~dp0..
	) else (
		echo The PETALS_HOME variable is not set. Please initialise it in your environment.
		goto :EOF
	)
)
if exist "%PETALS_HOME%\conf\server.properties" (
	goto checkjavahome
) else ( 
	echo The PETALS_HOME variable is not correctly set. Please set a correct path.
	goto :EOF
)

:checkjavahome
if "%JAVA_HOME%" == "" (
	echo The JAVA_HOME variable is not set. Please initialise it in your environment.
	goto :EOF
) else (
	set PATH=%JAVA_HOME%\bin;%PATH%
	goto checkclasspath
)

:checkclasspath
if "%CLASSPATH%" == "" (
	set CLASSPATH = "%PETALS_HOME%\schema\jbi"
	goto process_arg
) else (
	set CLASSPATH = %CLASSPATH%;"%PETALS_HOME%\schema\jbi"
	goto process_arg
)

:process_arg
if [%1]==[]     goto debugging
if [%1]==[-h]   goto usage
if [%1]==[-?]   goto usage
if [%1]==[-P]   goto profiling
goto usage

:usage
echo The debug.sh script is used to start Petals in debug mode.
echo usage : debug.bat [-h^|-?]  [-P [^<profilerId^>]] [-M [^<time^|memory^|thread^>]]
echo      -h^|? : prints the help message
echo      -P [^<profilerId^>] : Petals is starting in profiling mode instead of debug mode.
echo                            The Java Profiling Agent is defined by ^<profilerId^>.
echo                                  Possible values are:
echo                                     'eclipse'  : to use the eclipse agent controler (default value).
echo                                     'netbeans' : to use the netbeans agent.
echo      -M [^<time^|memory^|thread^>] : In case of the Eclispe profiling, use this parameter
echo                                  to define the profiler library to load (ie. Agent Controller).
echo                                  Possible values are:
echo                                     'CGProf'     : execution time analysis (default value).
echo                                     'HeapProf'   : object allocation/heap analysis.
echo                                     'ThreadProf' : thread analysis.
goto :EOF

:debugging
echo Debug mode activated.
set DEBUG_OPTS=-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y
goto start


:profiling
shift
if [%1]==[eclipse] goto profiling_eclipse
if [%1]==[netbeans] goto profiling_netbeans
if [%1]==[-M] goto profiling_eclipse
if [%1]==[] goto profiling_eclipse

echo Unsupported profiler: %1
goto :EOF

:profiling_eclipse
echo Eclipse Java Profiling Agent activated.

if [%TPTP_AC_HOME%]==[] (
	echo The TPTP_AC_HOME Agent Controler Home variable is not set. Please initialize it in your environment.
	goto :EOF
)

REM Add a check to verify that the agentcontroler is started

set JAVA_PROFILER_HOME=%TPTP_AC_HOME%\plugins\org.eclipse.tptp.javaprofiler
set PATH=%JAVA_PROFILER_HOME%;%JAVA_HOME%\bin;%PATH%;%TPTP_AC_HOME%/bin;%JAVA_HOME%\bin
set PROFILE_OPTS="-agentlib:JPIBootLoader=JPIAgent:server=enabled"
set ECLIPSE_PROFILER_TO_LOAD=time

shift
if [%1]==[-M] (
	shift
	goto profiling_eclipse_args
)
if [%1]==[] goto profiling_eclipse_configure
:profiling_eclipse_args
if [%1]==[time] (
	set ECLIPSE_PROFILER_TO_LOAD=%1
	goto profiling_eclipse_configure
)
if [%1]==[memory] (
	set ECLIPSE_PROFILER_TO_LOAD=%1
	goto profiling_eclipse_configure
)
if [%1]==[thread] (
	set ECLIPSE_PROFILER_TO_LOAD=%1
	goto profiling_eclipse_configure
)
echo Unsupported Eclipse profiler: %1
goto :EOF

:profiling_eclipse_configure
if "%ECLIPSE_PROFILER_TO_LOAD%" == "time" (
	echo Time analysis activated.
	set PROFILE_OPTS="%PROFILE_OPTS%;CGProf:execdetails=true"
	goto start
)
if "%ECLIPSE_PROFILER_TO_LOAD%" == "memory" (
	echo Object allocation/heap analysis activated.
	set PROFILE_OPTS="%PROFILE_OPTS%;HeapProf:allocsites=true"
	goto start
)
if "%ECLIPSE_PROFILER_TO_LOAD%" == "thread" (
	echo Thread analysis activated.
	set PROFILE_OPTS="%PROFILE_OPTS%;ThreadProf"
	goto start
)
echo "Unknown profiler library to load: %ECLIPSE_PROFILER_TO_LOAD%."
goto :EOF


:profiling_netbeans
echo NetBeans Java Profiling Agent activated.
if not defined NETBEANS_PROFILER_HOME (
	echo The NETBEANS_PROFILER_HOME variable is not set. Please initialise it in your environment.
	goto :EOF
)
set PROFILE_OPTS=-agentpath:"%NETBEANS_PROFILER_HOME%\lib\deployed\jdk15\windows\profilerinterface.dll=\"%NETBEANS_PROFILER_HOME%\lib\"",5140
goto start

:start
java -Xmx1024m %DEBUG_OPTS% %PROFILE_OPTS% -server -jar server.jar start
