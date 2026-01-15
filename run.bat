@echo off
REM Run script for Java 24
echo ========================================
echo SocialConnect - Run Script (Java 24)
echo ========================================
echo.

REM Check if out directory exists and has classes
if not exist "out\Main.class" (
    echo ERROR: Application not compiled!
    echo Please run build.bat first
    pause
    exit /b 1
)

echo Checking Java version...
java -version
echo.

echo Running application...
cd out
java Main

cd ..
