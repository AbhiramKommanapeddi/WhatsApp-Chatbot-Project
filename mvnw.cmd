@echo off
REM Maven Wrapper Script for Windows
REM This script downloads and runs Maven if it's not already available

set MAVEN_VERSION=3.9.4
set MAVEN_HOME=%USERPROFILE%\.m2\wrapper\maven-%MAVEN_VERSION%
set MAVEN_URL=https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip

REM Check if Maven is already installed in wrapper directory
if not exist "%MAVEN_HOME%" (
    echo Downloading Maven %MAVEN_VERSION%...
    if not exist "%USERPROFILE%\.m2\wrapper" mkdir "%USERPROFILE%\.m2\wrapper"
    
    REM Download Maven using PowerShell
    powershell -Command "Invoke-WebRequest -Uri '%MAVEN_URL%' -OutFile '%USERPROFILE%\.m2\wrapper\apache-maven-%MAVEN_VERSION%-bin.zip'"
    
    REM Extract Maven using PowerShell
    powershell -Command "Expand-Archive -Path '%USERPROFILE%\.m2\wrapper\apache-maven-%MAVEN_VERSION%-bin.zip' -DestinationPath '%USERPROFILE%\.m2\wrapper'"
    
    REM Rename directory
    move "%USERPROFILE%\.m2\wrapper\apache-maven-%MAVEN_VERSION%" "%MAVEN_HOME%"
    
    REM Clean up
    del "%USERPROFILE%\.m2\wrapper\apache-maven-%MAVEN_VERSION%-bin.zip"
    
    echo Maven %MAVEN_VERSION% installed successfully!
)

REM Run Maven with all arguments
"%MAVEN_HOME%\bin\mvn.cmd" %*
