@echo off

echo %CD%>tmpFile
set /p ROOT=<tmpFile
del /Q tmpFile

set BUILD_DIR=C:\tmp\xta\win32
set BUILD_OUTPUT=%ROOT%\build.output.win32
set TARGET=xta.win32.zip
set ECLIPSE_DIR=C:\eclipse\eclipse-SDK-3.4.1-win32
set LIB_DOMAIN=infr_libs_java
set XTA_DOMAIN=tools_xta

del /Q %BUILD_DIR%
mkdir %BUILD_DIR%\build\plugins\%LIB_DOMAIN%
mkdir %BUILD_DIR%\build\plugins\%XTA_DOMAIN%
mkdir %BUILD_DIR%\build_config

xcopy /Y /E /Q %ROOT%\..\%LIB_DOMAIN%\* %BUILD_DIR%\build\plugins\%LIB_DOMAIN%
xcopy /Y /E /Q %ROOT%\..\%XTA_DOMAIN%\* %BUILD_DIR%\build\plugins\%XTA_DOMAIN%
copy %ROOT%\build.properties.win32 %BUILD_DIR%\build_config\build.properties

set CLASSPATH=

java -jar %ECLIPSE_DIR%\plugins\org.eclipse.equinox.launcher_1.0.101.R34x_v20080819.jar -application org.eclipse.ant.core.antRunner -buildfile %ECLIPSE_DIR%\plugins\org.eclipse.pde.build_3.4.1.R34x_v20080805\scripts\productBuild\productBuild.xml -Dbuilder=%BUILD_DIR%\build_config -quiet > %BUILD_OUTPUT%

tail -2 %BUILD_OUTPUT% 

mkdir %ROOT%\bin
del /Q %ROOT%\bin\%TARGET%
xcopy /i /Y %BUILD_DIR%\build\I.TestBuild\*.zip %ROOT%\bin\
move %ROOT%\bin\TestBuild*.zip %ROOT%\bin\%TARGET%
cd %ROOT%/bin
unzip -o %TARGET% >> %BUILD_OUTPUT%

