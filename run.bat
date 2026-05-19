@echo off
setlocal

REM Cinema Management System — Quick Run Script
REM Requirements: Java 17+, Oracle DB running, db.properties configured

REM Check if .env or db.properties exists
if not exist ".env" (
    if not exist "src\main\resources\db.properties" (
        echo ERROR: Database configuration not found.
        echo Please create a .env file in the project root or copy db.properties.example to db.properties.
        pause
        exit /b 1
    )
)

REM Compile if needed
if not exist "target\classes\com\cinema\Main.class" (
    echo Compiling project...
    mkdir target\classes 2>nul

    REM Compile Java sources
    dir /s /b src\main\java\*.java > sources.txt
    javac -encoding UTF-8 -cp "lib\*" -d target\classes @sources.txt
    del sources.txt

    if errorlevel 1 (
        echo Compilation failed.
        pause
        exit /b 1
    )
)

REM Always copy resources (in case db.properties was added/changed)
xcopy /s /y /i "src\main\resources\*" "target\classes" >nul 2>&1

REM Run the application
echo Starting Cinema Management System...
java --enable-native-access=ALL-UNNAMED -cp "target\classes;lib\*" com.cinema.Main

if errorlevel 1 (
    echo.
    echo Application exited with an error.
    pause
)
endlocal
