@echo off
cd /d "%~dp0vue-tlias-management"
echo Installing frontend dependencies...
call npm install
echo.
echo Starting frontend dev server...
npm run dev
pause
