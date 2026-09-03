@echo off
REM FinTrack - Unified Startup Script
REM Builds frontend and starts Spring Boot server

cd /d "%~dp0"

echo.
echo ╔════════════════════════════════════════╗
echo ║      FinTrack - Starting Application   ║
echo ║    Frontend + Backend Integration      ║
echo ╚════════════════════════════════════════╝
echo.

REM Check if gradlew.bat exists
if not exist "%~dp0gradlew.bat" (
    echo ERROR: gradlew.bat not found in current directory
    echo Please run this script from the fintrack root directory
    pause
    exit /b 1
)

echo Starting frontend build and Spring Boot...
echo.
echo ╔════════════════════════════════════════╗
echo ║  FinTrack will be available at:        ║
echo ║  http://localhost:8080                 ║
echo ║                                        ║
echo ║  Press Ctrl+C to stop the server       ║
echo ╚════════════════════════════════════════╝
echo.

REM Gradle builds/copies the frontend before bootRun, then starts the backend.
call .\gradlew.bat bootRun

echo.
echo Application stopped.
pause
