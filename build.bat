@echo off
set MAVEN_HOME=C:\apache-maven-3.9.16
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.11
set PATH=%MAVEN_HOME%\bin;%JAVA_HOME%\bin;%PATH%

echo === [1/3] 建置前端 ===
cd /d "%~dp0frontend"
call npm install
if %errorlevel% neq 0 (
    echo === 前端 npm install 失敗 ===
    pause
    exit /b 1
)
call npm run build
if %errorlevel% neq 0 (
    echo === 前端建置失敗 ===
    pause
    exit /b 1
)

echo === [2/3] 複製前端到 backend/src/main/resources/static ===
set STATIC_DIR=%~dp0backend\src\main\resources\static
if exist "%STATIC_DIR%" rmdir /s /q "%STATIC_DIR%"
xcopy /e /i /q "%~dp0frontend\dist" "%STATIC_DIR%"
if %errorlevel% neq 0 (
    echo === 複製前端檔案失敗 ===
    pause
    exit /b 1
)

echo === [3/3] 建置後端 JAR ===
cd /d "%~dp0backend"
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo === 後端建置失敗 ===
    pause
    exit /b 1
)

echo.
echo === 建置完成 ===
echo 執行方式: java -jar backend\target\permission-system-1.0.0.jar
echo 網站位址: http://localhost:8090/authority/
pause
