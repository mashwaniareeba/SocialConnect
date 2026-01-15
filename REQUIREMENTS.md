# Project Requirements

## Java Version
- **Required**: Java JDK 24.0.2
- **Minimum**: Java 24.0.0
- **Maximum**: Java 24.x.x

## System Requirements
- **OS**: Windows, macOS, or Linux
- **RAM**: Minimum 512MB
- **Disk Space**: ~10MB for application + data files

## Build Tools
- Java Compiler (javac) version 24
- Java Runtime (java) version 24

## Verification
To verify your Java installation:

```bash
java -version
javac -version
```

Both commands should output version 24.0.2 or higher.

## Troubleshooting

### "UnsupportedClassVersionError"
This means you're using an older Java version. Solutions:
1. Install Java 24 from [Oracle](https://www.oracle.com/java/) or [Eclipse Adoptium](https://adoptium.net/)
2. Update your PATH environment variable to point to Java 24
3. Use the full path to Java 24's `java.exe` and `javac.exe`

### "Java not found"
1. Install Java 24
2. Add Java 24's `bin` directory to your PATH
3. Restart your terminal/IDE

### Version Mismatch
If `java -version` and `javac -version` show different versions:
1. Check your PATH environment variable
2. Ensure Java 24's bin directory is first in PATH
3. Restart terminal after changing PATH

