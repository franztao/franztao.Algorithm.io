@ECHO OFF
SETLOCAL

( type nul >> gurobi.log ) 2>nul || cd %TEMP%

if "%GUROBI_HOME%"=="" (
  echo.
  echo Gurobi installer changes have not taken effect yet.
  echo Please restart your machine before continuing.
  echo.
  set /p JUNK= [Hit ENTER to exit]
  exit
)

set PATH=%~dp0\..\bin;%PATH%
set PYTHONSTARTUP=%~dp0\..\lib\gurobi.py

"%~dp0\..\python27\bin\python" %*
