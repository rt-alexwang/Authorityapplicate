@echo off
echo ==========================================
echo   Permission System Backend
echo   http://192.168.82.225:8090
echo ==========================================
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.11
set PATH=%JAVA_HOME%\bin;%PATH%
set PATCH_DIR=D:\Users\alex_wang\Documents\patch
set JAR=%~dp0backend\target\permission-system-1.0.0.jar

if not exist "%JAR%" (
    echo Building JAR (first time only)...
    cd /d "%~dp0backend"
    set PATH=C:\apache-maven-3.9.16\bin;%PATH%
    mvn package -DskipTests -q
)

echo Starting...
"%JAVA_HOME%\bin\java.exe" --patch-module java.base="%PATCH_DIR%" -Djava.net.preferIPv4Stack=true -jar "%JAR%"
pause
