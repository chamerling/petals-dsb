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
	goto process_arg
)

:process_arg
set VALUE=%~1
shift
if [%VALUE%]==[]	     goto stop
if [%VALUE%]==[-h]   goto help
echo The argument '%VALUE%' is not valid
goto :EOF

:help
echo The stop.bat script is used to stop Petals.
echo usage : stop.bat [-h]
echo          -h : prints the help message
goto :EOF

:stop
java -jar "%PETALS_HOME%/bin/server.jar" stop
