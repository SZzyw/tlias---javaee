@echo off
cd /d "%~dp0"

echo.
echo ====== Starting Chat Agent Service (port 8787) ======
echo.
start "Chat Agent" cmd /c "cd /d "%~dp0web-ai-project02\chat-agent" && npm run dev"

echo.
echo ====== Starting Frontend Dev Server (port 5173) ======
echo.
cd /d "%~dp0vue-tlias-management"
npm run dev

pause
