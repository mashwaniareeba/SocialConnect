# 🚀 Setup Guide - Running SocialNetwork on New Laptop (D Drive)

## Main Entry Point
**File:** `src/Main.java`  
This is the main file that starts the application.

---

## Step-by-Step Setup Instructions

### Prerequisites
Before running the application, you need:

1. **Java JDK 8 or higher** installed on your laptop
   - Download from: https://www.oracle.com/java/technologies/downloads/
   - Or use OpenJDK: https://adoptium.net/
   - Make sure Java is added to your PATH

### Verify Java Installation

Open Command Prompt or PowerShell and type:
```bash
java -version
javac -version
```

Both commands should show a version number (like "java version 1.8.0" or higher).

---

## Method 1: Run Using Command Line (Easiest)

### Step 1: Navigate to Project Folder
```bash
cd D:\SocialNetwork
```

### Step 2: Compile the Project
```bash
javac -d out -encoding UTF-8 src/*.java src/gui/*.java src/gui/components/*.java src/gui/panels/*.java src/models/*.java src/system/*.java
```

**OR** if you're in Windows PowerShell, use:
```powershell
javac -d out -encoding UTF-8 src\*.java src\gui\*.java src\gui\components\*.java src\gui\panels\*.java src\models\*.java src\system\*.java
```

### Step 3: Run the Application
```bash
java -cp out Main
```

**OR** if the `out` folder already has compiled classes, you can skip Step 2 and directly run:
```bash
java -cp out Main
```

---

## Method 2: Run Using IDE (Recommended)

### Option A: IntelliJ IDEA
1. Open IntelliJ IDEA
2. Click **File** → **Open**
3. Navigate to `D:\SocialNetwork` folder
4. Click **OK**
5. Wait for IDE to index files
6. Find `src/Main.java` in the project tree
7. Right-click on `Main.java` → **Run 'Main.main()'**

### Option B: Eclipse
1. Open Eclipse
2. Click **File** → **Import**
3. Select **General** → **Existing Projects into Workspace**
4. Browse to `D:\SocialNetwork`
5. Click **Finish**
6. Find `src/Main.java` in Project Explorer
7. Right-click → **Run As** → **Java Application**

### Option C: VS Code
1. Install **Extension Pack for Java** in VS Code
2. Open VS Code
3. Click **File** → **Open Folder**
4. Navigate to `D:\SocialNetwork`
5. Open `src/Main.java`
6. Click the **Run** button (▶️) above the main method
   - Or press `Ctrl+F5`

---

## Quick Start Script (Windows)

You can also create a batch file to run it easily:

### Create `run.bat` in the SocialNetwork folder:
```batch
@echo off
cd /d "%~dp0"
echo Compiling Java files...
javac -d out -encoding UTF-8 src\*.java src\gui\*.java src\gui\components\*.java src\gui\panels\*.java src\models\*.java src\system\*.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo.
echo Starting SocialConnect...
echo.
java -cp out Main
pause
```

**To use it:**
- Double-click `run.bat` file
- It will compile and run automatically

---

## Important Notes

1. **Data Folder**: The application creates a `data/` folder to save user data, posts, and comments. This folder will be auto-created when you run the app for the first time.

2. **Demo Accounts**: When the app starts, you'll see demo credentials:
   - Username: `john_doe`
   - Password: `password123`

3. **If You Get Errors**:
   - **"javac is not recognized"**: Java is not installed or not in PATH
   - **"ClassNotFoundException"**: The `out` folder is missing or compilation failed
   - **Encoding errors**: The compilation command includes `-encoding UTF-8` to fix this

---

## Folder Structure After Setup

```
D:\SocialNetwork\
├── src\
│   ├── Main.java          ← MAIN ENTRY POINT
│   ├── gui\
│   ├── models\
│   └── system\
├── out\                   ← Compiled classes (auto-created)
├── data\                  ← User data (auto-created when app runs)
├── README.md
└── SETUP_GUIDE.md        ← This file
```

---

## Troubleshooting

### Problem: Java not found
**Solution**: Install Java JDK and add it to PATH

### Problem: Can't compile
**Solution**: Make sure you're in the SocialNetwork folder when running commands

### Problem: Class files in wrong location
**Solution**: Delete the `out` folder and recompile

---

## Need Help?

- Check the main `README.md` file for more details
- Make sure Java JDK is properly installed
- Ensure all source files are present in the `src/` folder

---

**🎉 Once running, you'll see the SocialConnect login window!**


