@echo off
REM ForgeFit Login Feature - Start Server Script
REM This script starts the Spring Boot application

echo.
echo ========================================
echo ForgeFit Application Startup
echo ========================================
echo.

REM Check if mvn is available
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Maven is not installed or not in PATH
    echo.
    echo Please run setup-maven.ps1 first:
    echo   1. Right-click setup-maven.ps1
    echo   2. Select "Run with PowerShell"
    echo   3. Choose "Run once"
    echo   4. Then run this script again
    echo.
    pause
    exit /b 1
)

REM Verify Java is available
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Java is not installed or not in PATH
    echo.
    echo Please install Java 17+ from:
    echo   https://www.oracle.com/java/technologies/downloads/
    echo.
    pause
    exit /b 1
)

REM Navigate to project directory
cd /d "C:\Users\ANKIT\Downloads\java project"

if not exist "pom.xml" (
    echo Error: pom.xml not found in current directory
    echo Current directory: %CD%
    pause
    exit /b 1
)

echo.
echo Versions:
mvn -version
echo.

echo Starting Spring Boot application...
echo Access at: http://localhost:8080/login.html
echo.
echo Press Ctrl+C to stop the server
echo.

mvn spring-boot:run

pause
