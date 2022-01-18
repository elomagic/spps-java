@echo off

@setlocal

:OkJHome
set "JAVACMD=%JAVA_HOME%\bin\java.exe"

:checkJCmd
if exist "%JAVACMD%" goto chkHome

echo The JAVA_HOME environment variable is not defined correctly >&2
echo This environment variable is needed to run this program >&2
echo NB: JAVA_HOME should point to a JDK not a JRE >&2
goto error

:chkHome
set "J2C_HOME=%~dp0"
if not "%J2C_HOME%"=="" goto valHome
goto error

:valHome

:init
set CMD_LINE_ARGS=%*

:endInit

set CLASS_LAUNCHER=de.elomagic.spps.shared.SimpleCrypt

"%JAVACMD%" ^
    -cp "%J2C_HOME%\libs\*" ^
    -Dlog4j.configurationFile="%J2C_HOME%\conf\log4j2.xml" ^
    %CLASS_LAUNCHER% %CMD_LINE_ARGS%

goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%