@echo off
REM This script helps you run with Java 24 explicitly
REM First, find your Java 24 installation path and update the JAVA24_PATH below

REM Common Java 24 installation paths - uncomment and update the correct one:
REM set JAVA24_PATH=C:\Program Files\Java\jdk-24
REM set JAVA24_PATH=C:\Program Files\Java\jdk-24.0.2
REM set JAVA24_PATH=C:\Program Files\Eclipse Adoptium\jdk-24.0.2-hotspot

REM If JAVA_HOME is set to Java 24, use it:
if defined JAVA_HOME (
    echo Using JAVA_HOME: %JAVA_HOME%
    "%JAVA_HOME%\bin\java.exe" -cp out Main
) else (
    echo ERROR: Please set JAVA_HOME to your Java 24 installation
    echo OR update JAVA24_PATH in this script
    echo.
    echo To find Java 24, run: .\check-java.ps1
    pause
)

