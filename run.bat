@echo off
setlocal

REM Compile if needed
if not exist "target\classes\com\cinema\Main.class" (
    echo Compiling project...
    mkdir target\classes 2>nul
    dir /s /b src\main\java\*.java > sources.txt
    javac -encoding UTF-8 -cp "lib\*" -d target\classes @sources.txt
    del sources.txt
    if errorlevel 1 (
        echo Compilation failed.
        pause
        exit /b 1
    )
)

REM Run the application
echo Starting Cinema Management System...
java --enable-native-access=ALL-UNNAMED -cp "target\classes;lib\*" com.cinema.Main

if errorlevel 1 (
    echo.
    echo Application exited with an error.
    pause
)
endlocal
