@echo off
if not exist "%~dp0console.jar" call "%~dp0build.bat"
java -jar "%~dp0console.jar"
pause
