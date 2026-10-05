@echo off
if exist "%~dp0.tools\apache-maven-3.9.6\bin\mvn.cmd" (
    "%~dp0.tools\apache-maven-3.9.6\bin\mvn.cmd" %*
) else (
    mvn %*
)
