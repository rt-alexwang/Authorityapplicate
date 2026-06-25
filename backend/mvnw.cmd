@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------
@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET DP0=%~dp0
@SET MAVEN_WRAPPER_JAR="%DP0%.mvn\wrapper\maven-wrapper.jar"
@SET MAVEN_WRAPPER_PROPERTIES="%DP0%.mvn\wrapper\maven-wrapper.properties"
@SET MVNW_VERBOSE=false

@FOR /F "usebackq tokens=1,2 delims==" %%A IN (%MAVEN_WRAPPER_PROPERTIES%) DO (
    @IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

@SET JAVA_HOME_TO_USE=%JAVA_HOME%
@IF "%JAVA_HOME_TO_USE%"=="" SET JAVA_HOME_TO_USE=C:\Program Files\Java\jdk-21.0.11

@SET JAVA_CMD="%JAVA_HOME_TO_USE%\bin\java.exe"

@IF EXIST %MAVEN_WRAPPER_JAR% GOTO runWithJava

:downloadWrapper
@ECHO Downloading Maven Wrapper...
@"%JAVA_HOME_TO_USE%\bin\java.exe" -classpath "%DP0%.mvn\wrapper" MavenWrapperDownloader "%DISTRIBUTION_URL%" "%DP0%.mvn\wrapper\maven-wrapper.jar"

:runWithJava
@%JAVA_CMD% %MAVEN_OPTS% "-Dmaven.multiModuleProjectDirectory=%DP0%" -jar %MAVEN_WRAPPER_JAR% %*
