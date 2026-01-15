@echo off
echo Checking Java version...
java -version
echo.

echo Cleaning old class files...
for /r src %%f in (*.class) do del /f /q "%%f" 2>nul
for /r out %%f in (*.class) do del /f /q "%%f" 2>nul

echo Compiling with Java 24...
cd src
javac -d ../out -source 24 -target 24 Main.java gui/*.java gui/components/*.java gui/panels/*.java models/*.java system/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
    echo.
    echo Running application...
    cd ../out
    java Main
) else (
    echo Compilation failed!
    pause
)

