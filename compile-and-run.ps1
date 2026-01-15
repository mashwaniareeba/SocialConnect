# Check Java version
Write-Host "Checking Java version..." -ForegroundColor Yellow
$javaVersion = java -version 2>&1 | Select-Object -First 1
if ($javaVersion -notlike "*24*") {
    Write-Host "WARNING: Java 24 is required!" -ForegroundColor Red
    Write-Host "Found: $javaVersion" -ForegroundColor Yellow
    Write-Host "Please install Java 24 or update your PATH" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}
Write-Host "Java version: $javaVersion" -ForegroundColor Green

# Clean old class files
Write-Host "Cleaning old class files..." -ForegroundColor Yellow
Get-ChildItem -Path "src" -Recurse -Filter "*.class" -ErrorAction SilentlyContinue | Remove-Item -Force
Get-ChildItem -Path "out" -Recurse -Filter "*.class" -ErrorAction SilentlyContinue | Remove-Item -Force

# Compile with Java 24
Write-Host "Compiling with Java 24..." -ForegroundColor Yellow
cd src
javac -d ../out -source 24 -target 24 Main.java gui/*.java gui/components/*.java gui/panels/*.java models/*.java system/*.java

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilation successful!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Running application..." -ForegroundColor Yellow
    cd ../out
    java Main
} else {
    Write-Host "Compilation failed!" -ForegroundColor Red
    Read-Host "Press Enter to exit"
}

