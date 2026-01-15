@echo off
REM Build script for Java 24
echo ========================================
echo SocialConnect - Build Script (Java 24)
echo ========================================
echo.

REM Check Java version
echo Checking Java version...
java -version
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java not found!
    pause
    exit /b 1
)

echo.
echo Cleaning old class files...
for /r src %%f in (*.class) do del /f /q "%%f" 2>nul
for /r out %%f in (*.class) do del /f /q "%%f" 2>nul

echo.
echo Compiling with Java 24...
cd src
javac -d ../out -source 24 -target 24 Main.java gui/*.java gui/components/*.java gui/panels/*.java models/*.java system/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Compilation successful!
    echo ========================================
    echo.
    echo To run the application:
    echo   cd out
    echo   java Main
    echo.
) else (
    echo.
    echo ========================================
    echo Compilation failed!
    echo ========================================
    pause
    exit /b 1
)

cd ..

