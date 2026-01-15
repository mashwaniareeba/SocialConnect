# Check which Java versions are installed and which one is being used
Write-Host "=== Java Version Check ===" -ForegroundColor Cyan
Write-Host ""

Write-Host "Java command location:" -ForegroundColor Yellow
$javaPath = (Get-Command java).Source
Write-Host $javaPath -ForegroundColor White
Write-Host ""

Write-Host "Java version:" -ForegroundColor Yellow
java -version
Write-Host ""

Write-Host "Javac command location:" -ForegroundColor Yellow
$javacPath = (Get-Command javac).Source
Write-Host $javacPath -ForegroundColor White
Write-Host ""

Write-Host "Javac version:" -ForegroundColor Yellow
javac -version
Write-Host ""

Write-Host "=== Searching for Java installations ===" -ForegroundColor Cyan
$javaPaths = @(
    "C:\Program Files\Java",
    "C:\Program Files (x86)\Java",
    "$env:JAVA_HOME"
)

foreach ($path in $javaPaths) {
    if (Test-Path $path) {
        Write-Host "Found Java in: $path" -ForegroundColor Green
        Get-ChildItem -Path $path -Directory -ErrorAction SilentlyContinue | ForEach-Object {
            $javaExe = Join-Path $_.FullName "bin\java.exe"
            if (Test-Path $javaExe) {
                Write-Host "  - $($_.Name)" -ForegroundColor White
                $version = & $javaExe -version 2>&1 | Select-Object -First 1
                Write-Host "    Version: $version" -ForegroundColor Gray
            }
        }
    }
}

