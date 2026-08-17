@echo off
setlocal
set ROOT=%~dp0

if exist "%ROOT%out" rmdir /s /q "%ROOT%out"
mkdir "%ROOT%out\engine"
mkdir "%ROOT%out\console"

for /f "delims=" %%f in ('dir /s /b "%ROOT%engine\src\*.java"') do echo %%f>>"%ROOT%out\engine-sources.txt"
javac -d "%ROOT%out\engine" -encoding UTF-8 @"%ROOT%out\engine-sources.txt"
if errorlevel 1 exit /b 1

for /f "delims=" %%f in ('dir /s /b "%ROOT%console\src\*.java"') do echo %%f>>"%ROOT%out\console-sources.txt"
javac -d "%ROOT%out\console" -encoding UTF-8 -cp "%ROOT%out\engine" @"%ROOT%out\console-sources.txt"
if errorlevel 1 exit /b 1

jar --create --file "%ROOT%engine.jar" -C "%ROOT%out\engine" .
(
  echo Manifest-Version: 1.0
  echo Main-Class: ui.Main
  echo Class-Path: engine.jar
) > "%ROOT%out\manifest.txt"
jar --create --file "%ROOT%console.jar" --manifest "%ROOT%out\manifest.txt" -C "%ROOT%out\console" .

echo Build done: engine.jar and console.jar are ready in "%ROOT%".
