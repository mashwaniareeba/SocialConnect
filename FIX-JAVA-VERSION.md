# Fix Java Version Mismatch Issue

## Problem
You're getting: `UnsupportedClassVersionError: class file version 69.0` 
This means files were compiled with Java 21, but your `java` command is using Java 20 or earlier.

## Solution Steps

### Step 1: Check which Java is being used
Run this on your other PC:
```powershell
.\check-java.ps1
```

This will show you:
- Which Java version `java` command is using
- Which Java version `javac` command is using  
- Where Java 24 is installed

### Step 2: Fix the PATH issue

**Option A: Use full path to Java 24**
```powershell
# Find Java 24 path (usually one of these):
# C:\Program Files\Java\jdk-24\bin\java.exe
# C:\Program Files\Eclipse Adoptium\jdk-24.0.2-hotspot\bin\java.exe

# Then run with full path:
"C:\Program Files\Java\jdk-24\bin\java.exe" -cp out Main
```

**Option B: Update PATH environment variable**
1. Search "Environment Variables" in Windows
2. Edit "Path" variable
3. Add Java 24's `bin` folder at the TOP of the list
4. Example: `C:\Program Files\Java\jdk-24\bin`
5. Restart PowerShell/terminal

**Option C: Set JAVA_HOME**
1. Create new environment variable: `JAVA_HOME`
2. Set value to: `C:\Program Files\Java\jdk-24` (your actual path)
3. Add `%JAVA_HOME%\bin` to PATH
4. Restart terminal

### Step 3: Recompile with matching Java version

After fixing PATH, recompile:
```powershell
# Clean old files
Get-ChildItem -Path "src" -Recurse -Filter "*.class" | Remove-Item -Force
Get-ChildItem -Path "out" -Recurse -Filter "*.class" | Remove-Item -Force

# Compile
cd src
javac -d ../out Main.java gui/*.java gui/components/*.java gui/panels/*.java models/*.java system/*.java

# Run
cd ../out
java Main
```

### Quick Test
After fixing, verify both use the same version:
```powershell
java -version
javac -version
```

Both should show Java 24.0.2

