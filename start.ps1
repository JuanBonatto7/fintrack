#!/usr/bin/env pwsh

# FinTrack - Unified Startup Script (PowerShell)
# Builds frontend and starts Spring Boot server

Write-Host ""
Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║      FinTrack - Starting Application   ║" -ForegroundColor Cyan
Write-Host "║    Frontend + Backend Integration      ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Check if gradlew.bat exists
if (!(Test-Path ".\gradlew.bat")) {
    Write-Host "ERROR: gradlew.bat not found in current directory" -ForegroundColor Red
    Write-Host "Please run this script from the fintrack root directory" -ForegroundColor Red
    pause
    exit 1
}

# Check if frontend exists
if (!(Test-Path ".\frontend")) {
    Write-Host "ERROR: frontend directory not found" -ForegroundColor Red
    pause
    exit 1
}

# Try to find npm
$npmPath = (Get-Command npm -ErrorAction SilentlyContinue).Source
if (-not $npmPath) {
    # Try common location
    $npmPath = "C:\Users\Juan\dev-tools\node\npm.cmd"
    if (!(Test-Path $npmPath)) {
        Write-Host "ERROR: npm not found. Please install Node.js" -ForegroundColor Red
        pause
        exit 1
    }
    Write-Host "Found npm at: $npmPath" -ForegroundColor Yellow
}

# Step 1: Build frontend
Write-Host "[1/3] Building frontend..." -ForegroundColor Green
Push-Location .\frontend
try {
    if ($npmPath.EndsWith('.cmd')) {
        & cmd /c "$npmPath run build"
    } else {
        & npm run build
    }
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Frontend build failed" -ForegroundColor Red
        pause
        exit 1
    }
} finally {
    Pop-Location
}

Write-Host ""

# Step 2: Copy frontend files
Write-Host "[2/3] Preparing files for Spring Boot..." -ForegroundColor Green
$srcDir = "frontend\dist\*"
$destDir = "src\main\resources\static\"

if (Test-Path "frontend\dist") {
    Copy-Item -Path $srcDir -Destination $destDir -Force -Recurse -ErrorAction SilentlyContinue
    Write-Host "Frontend files copied to static resources" -ForegroundColor Green
}

Write-Host ""

# Step 3: Start Spring Boot
Write-Host "[3/3] Starting Spring Boot server..." -ForegroundColor Green
Write-Host ""
Write-Host "╔════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  FinTrack will be available at:        ║" -ForegroundColor Cyan
Write-Host "║  http://localhost:8080                 ║" -ForegroundColor Cyan
Write-Host "║                                        ║" -ForegroundColor Cyan
Write-Host "║  Press Ctrl+C to stop the server       ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Start the application
& ".\gradlew.bat" bootRun

Write-Host ""
Write-Host "Application stopped." -ForegroundColor Yellow
pause
