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
if [%VALUE%]==[]	 goto confirm_shutdown
if [%VALUE%]==[-y]   goto shutdown
if [%VALUE%]==[-h]   goto help
echo The argument '%VALUE%' is not valid
goto :EOF

:help
echo The shutdown.bat script is used to shutdown Petals.
echo usage : shutdown.bat [-h|-y]
echo          -h : prints the help message
echo          -y : no confirmation is asked
goto :EOF

:confirm_shutdown
echo Are you sure to remove this PEtALS instance from your PEtALS network (Y/n) ?
echo     - all services assembly of this instance will be uninstalled,
echo     - all components of this instance will be uninstalled,
echo     - this PEtALS instance will be unregistered.
set /P response=
if [%response%]==[Y] goto :shutdown
echo Shutdown canceled
goto :EOF

:shutdown
java -jar "%PETALS_HOME%/bin/server.jar" shutdown
