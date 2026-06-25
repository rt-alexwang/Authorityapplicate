@echo off
set MAVEN_HOME=C:\apache-maven-3.9.16
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.11
set PATH=%MAVEN_HOME%\bin;%JAVA_HOME%\bin;%PATH%

echo === 開始建置 (frontend + backend) ===
cd /d "%~dp0backend"
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo === 建置失敗 ===
    pause
    exit /b 1
)
echo === 建置完成：backend\target\permission-system-1.0.0.jar ===
pause
