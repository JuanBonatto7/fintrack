@echo off
REM Build frontend script
cd /d "%~dp0frontend"

if not exist "package.json" (
    echo Error: package.json not found in frontend directory
    exit /b 1
)

if not exist "node_modules" (
    echo Installing frontend dependencies...
    call npm install
    if errorlevel 1 (
        echo Error: npm install failed
        exit /b 1
    )
)

echo Building frontend...
call npm run build
if errorlevel 1 (
    echo Error: npm run build failed
    exit /b 1
)

echo Frontend build completed successfully
exit /b 0
