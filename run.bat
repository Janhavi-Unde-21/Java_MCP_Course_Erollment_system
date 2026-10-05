@echo off
title Online Course Enrollment System - Launching
echo ================================================================
echo   Online Course Enrollment System - College Mini-Project
echo   Java Swing + MongoDB + FlatLaf Modern UI
echo ================================================================
echo.

REM Check if target JAR exists
if exist "target\online-course-enrollment-1.0.0.jar" (
    echo [OK] Running prebuilt application JAR...
    java -jar target\online-course-enrollment-1.0.0.jar
    goto end
)

REM If not built yet, build and run using bundled Maven
if exist ".tools\apache-maven-3.9.6\bin\mvn.cmd" (
    echo [INFO] Building and packaging application...
    call ".tools\apache-maven-3.9.6\bin\mvn.cmd" clean package -DskipTests
    echo [INFO] Launching application...
    java -jar target\online-course-enrollment-1.0.0.jar
    goto end
)

REM Fallback to system maven
echo [INFO] Building and running with system Maven...
call mvn clean compile exec:java

:end
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application exited with error code %ERRORLEVEL%.
    pause
)
