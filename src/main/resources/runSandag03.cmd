@echo off

rem ############  PARAMETERS  ############
set DRIVE=%1
set PROJECT_DIRECTORY=%2

rem ############  JPPF DRIVER  ############
set JPPF_LIB=%PROJECT_DIRECTORY%\application\*
set CLASSPATH=%PROJECT_DIRECTORY%\conf;%JPPF_LIB%

%DRIVE%
cd %PROJECT_DIRECTORY%
start %JAVA_64_PATH%\bin\java -server -Xms16m -Xmx16m -cp "%CLASSPATH%" -Dlog4j.configuration=log4j-sandag03.properties -Djppf.config=jppf-sandag03.properties org.jppf.node.NodeLauncherr
