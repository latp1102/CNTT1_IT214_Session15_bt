@echo off
chcp 65001 >nul
title Microservices Order Tracking System - Session 15

echo ==========================================
echo   Starting Microservices...
echo ==========================================

set SESSION15_DIR=%~dp0

echo [1/5] Starting Order Service (port 8081)...
start "Order Service" cmd /c "cd /d "%SESSION15_DIR%order_service" && ..\gradlew.bat bootRun"

echo [2/5] Starting Inventory Service (port 8082)...
start "Inventory Service" cmd /c "cd /d "%SESSION15_DIR%inventory-service" && ..\gradlew.bat bootRun"

echo [3/5] Starting Notification Service (port 8083)...
start "Notification Service" cmd /c "cd /d "%SESSION15_DIR%notification-service" && ..\gradlew.bat bootRun"

echo [4/5] Starting Delivery Service (port 8084)...
start "Delivery Service" cmd /c "cd /d "%SESSION15_DIR%delivery-service" && ..\gradlew.bat bootRun"

echo [5/5] Starting Payment Service (port 8085)...
start "Payment Service" cmd /c "cd /d "%SESSION15_DIR%payment-service" && ..\gradlew.bat bootRun"

echo.
echo ==========================================
echo   All services started!
echo ==========================================
echo.
echo Order Service:      http://localhost:8081
echo Inventory Service:  http://localhost:8082
echo Notification Service: http://localhost:8083
echo Delivery Service:   http://localhost:8084
echo Payment Service:    http://localhost:8085
echo.
echo Press any key to exit...
pause >nul
