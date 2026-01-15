# Run script for Java 24
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SocialConnect - Run Script (Java 24)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if compiled
if (-not (Test-Path "out\Main.class")) {
    Write-Host "ERROR: Application not compiled!" -ForegroundColor Red
    Write-Host "Please run build.ps1 first" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "Checking Java version..." -ForegroundColor Yellow
java -version
Write-Host ""

Write-Host "Running application..." -ForegroundColor Yellow
cd out
java Main

cd ..

