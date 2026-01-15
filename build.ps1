# Build script for Java 24
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SocialConnect - Build Script (Java 24)" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check Java version
Write-Host "Checking Java version..." -ForegroundColor Yellow
$javaVersion = java -version 2>&1 | Select-Object -First 1
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERROR: Java not found!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host $javaVersion -ForegroundColor Green

if ($javaVersion -notlike "*24*") {
    Write-Host "WARNING: Java version is not 24!" -ForegroundColor Yellow
    Write-Host "Expected Java 24, but found: $javaVersion" -ForegroundColor Yellow
    $continue = Read-Host "Continue anyway? (y/n)"
    if ($continue -ne "y") {
        exit 1
    }
}

Write-Host ""
Write-Host "Cleaning old class files..." -ForegroundColor Yellow
Get-ChildItem -Path "src" -Recurse -Filter "*.class" -ErrorAction SilentlyContinue | Remove-Item -Force
Get-ChildItem -Path "out" -Recurse -Filter "*.class" -ErrorAction SilentlyContinue | Remove-Item -Force

Write-Host ""
Write-Host "Compiling with Java 24..." -ForegroundColor Yellow
cd src
javac -d ../out -source 24 -target 24 Main.java gui/*.java gui/components/*.java gui/panels/*.java models/*.java system/*.java

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "Compilation successful!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "To run the application:" -ForegroundColor Cyan
    Write-Host "  cd out" -ForegroundColor White
    Write-Host "  java Main" -ForegroundColor White
    Write-Host ""
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "Compilation failed!" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

cd ..

