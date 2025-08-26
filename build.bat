@echo off
echo Setting JAVA_HOME to Java 22...
set JAVA_HOME=C:\Program Files\Java\jdk-22
echo JAVA_HOME set to: %JAVA_HOME%

echo.
echo Building MinecraftSSH plugin v1.6.0...
gradlew.bat --no-daemon build

echo.
echo Build completed! Check build/libs/ for MinecraftSSH-1.6.0.jar
pause
