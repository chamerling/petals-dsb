@Echo Off
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
if [%VALUE%]==[-c]   goto commandLine
echo The argument '%VALUE%' is not valid
goto :help


:help
echo The startup.bat script is used to start PEtALS.
echo usage : startup.sh [-h] [-C]
echo		-h : prints the help message
echo		-c : PEtALS is starting with command line
echo        -b : Start PEtALS without launching a new window
goto :EOF

:commandLine
Set cmdline=-console
goto start

:start
echo launching PEtALS with command line :
echo java %JAVA_OPTS% %DEBUG_OPTS% -jar "%PETALS_HOME%/bin/server.jar" start %cmdline%
start "OW2 PEtALS Enterprise Service Bus" %noNewWindow% java %JAVA_OPTS% %DEBUG_OPTS% -jar "%PETALS_HOME%/bin/server.jar" start %cmdline%
