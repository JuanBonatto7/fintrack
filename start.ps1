#!/usr/bin/env pwsh

# FinTrack - Unified Startup Script (PowerShell)
# Builds frontend and starts Spring Boot server

Set-Location $PSScriptRoot

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
if (!(Test-Path ".\frontend\package.json")) {
    Write-Host "ERROR: frontend directory not found" -ForegroundColor Red
    pause
    exit 1
}

# Gradle builds/copies the frontend before bootRun, then starts the backend.
Write-Host "Starting frontend build and Spring Boot..." -ForegroundColor Green
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
