@echo off
REM FinTrack - Unified Startup Script
REM Builds frontend and starts Spring Boot server

echo.
echo ╔════════════════════════════════════════╗
echo ║      FinTrack - Starting Application   ║
echo ║    Frontend + Backend Integration      ║
echo ╚════════════════════════════════════════╝
echo.

REM Check if gradlew.bat exists
if not exist "%cd%\gradlew.bat" (
    echo ERROR: gradlew.bat not found in current directory
    echo Please run this script from the fintrack root directory
    pause
    exit /b 1
)

REM Check if node/npm is available
where npm >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: npm not found in PATH
    echo Trying alternative npm path...
    if exist "C:\Users\Juan\dev-tools\node\npm.cmd" (
        echo Found npm at: C:\Users\Juan\dev-tools\node\npm.cmd
    ) else (
        echo ERROR: npm not found. Please install Node.js
        pause
        exit /b 1
    )
)

echo [1/3] Building frontend...
cd frontend
call npm run build
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Frontend build failed
    pause
    exit /b 1
)
cd ..

echo.
echo [2/3] Preparing files for Spring Boot...
REM Note: The gradle copyFrontendDist task will be called by bootRun
REM But we'll also manually copy to be safe
xcopy /E /Y /Q frontend\dist\* src\main\resources\static\ >nul 2>&1

echo.
echo [3/3] Starting Spring Boot server...
echo.
echo ╔════════════════════════════════════════╗
echo ║  FinTrack will be available at:        ║
echo ║  http://localhost:8080                 ║
echo ║                                        ║
echo ║  Press Ctrl+C to stop the server       ║
echo ╚════════════════════════════════════════╝
echo.

REM Start the application
call .\gradlew.bat bootRun

echo.
echo Application stopped.
pause
