@echo off
REM Compile the Java code
javac -cp .;libs/nanohttpd-2.3.1.jar -d bin src/NumberGuessingGameWeb.java

REM Check if compilation was successful
if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b %errorlevel%
)

REM Run the Java application
java -cp bin;libs/nanohttpd-2.3.1.jar NumberGuessingGameWeb
