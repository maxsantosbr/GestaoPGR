@echo off
title Gestao PGR - Sistema de Gerenciamento de Riscos
echo ============================================
echo   Gestao PGR - Iniciando...
echo ============================================
echo.

REM Verificar se Java esta instalado (usar JRE local se disponivel)
if exist "jre\bin\java.exe" (
    set JAVA_EXE=jre\bin\java.exe
) else (
    java -version >nul 2>&1
    if %errorlevel% neq 0 (
        echo ERRO: Java nao encontrado!
        echo.
        echo Instale o Java 21 ou superior:
        echo https://adoptium.net/
        echo.
        pause
        exit /b 1
    )
    set JAVA_EXE=java
)

REM O JAR sempre esta em target\gestao-pgr.jar
if exist "target\gestao-pgr.jar" (
    set JAR=target\gestao-pgr.jar
) else (
    echo ERRO: Arquivo JAR nao encontrado!
    echo.
    echo Execute primeiro: mvn clean package -DskipTests
    echo.
    pause
    exit /b 1
)

echo Iniciando aplicacao...
echo.
%JAVA_EXE% -jar %JAR%

if %errorlevel% neq 0 (
    echo.
    echo A aplicacao encerrou com erro.
    pause
)
