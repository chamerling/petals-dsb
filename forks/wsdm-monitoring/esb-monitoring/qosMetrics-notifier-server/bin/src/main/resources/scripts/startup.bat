@Echo Off
Rem verif variables:
:checkjavahome
if "%JAVA_HOME%" == "" (
	echo The JAVA_HOME variable is not set. Please initialise it in your environment.
	goto :EOF
) else (
	set PATH = %JAVA_HOME%\bin;%PATH%
	goto checkjavaopts
)


:checkjavaopts
if "%JAVA_OPTS%" == "" (
	set JAVA_OPTS=-Xmx256m
)
goto process_arg

:process_arg
set VALUE=%~1
shift

if [%VALUE%]==[-b] (
  Set noNewWindow=/B
  set VALUE=%~2
) else (
  Set noNewWindow=
)

if [%VALUE%]==[]     goto start
if [%VALUE%]==[-h]   goto help
echo The argument '%VALUE%' is not valid
goto :help


:help
echo The startup.bat script is used to start PEtALS.
echo usage : startup.sh [-h] [-C]
echo		-h : prints the help message
goto :EOF


:start
echo launching Firemen with command line :
echo java %JAVA_OPTS% %DEBUG_OPTS% -jar "server.jar" start
java %JAVA_OPTS% %DEBUG_OPTS% -jar "server.jar" start
