@echo off
setlocal

powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0preview-control.ps1" start

if /I not "%~1"=="--no-pause" pause
