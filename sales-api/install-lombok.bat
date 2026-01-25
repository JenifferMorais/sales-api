@echo off
echo ========================================
echo Instalando Lombok no Spring Tools Suite
echo ========================================
echo.

set LOMBOK_JAR=C:\Users\jenim\.m2\repository\org\projectlombok\lombok\1.18.34\lombok-1.18.34.jar
set STS_INI=C:\spring-tools-for-eclipse-4.32\sts-4.32.2.RELEASE\SpringToolSuite4.ini

echo Verificando se o Lombok ja existe...
if not exist "%LOMBOK_JAR%" (
    echo ERRO: Lombok nao encontrado em %LOMBOK_JAR%
    echo Execute: mvnw dependency:resolve
    pause
    exit /b 1
)

echo.
echo FECHE o Spring Tools Suite se estiver aberto!
echo.
pause

echo.
echo Iniciando instalador do Lombok...
echo.
start "" javaw -jar "%LOMBOK_JAR%"

echo.
echo ========================================
echo INSTRUCOES:
echo ========================================
echo 1. Na janela do Lombok que abriu:
echo    - Clique em "Specify location..."
echo    - Navegue ate: C:\spring-tools-for-eclipse-4.32\sts-4.32.2.RELEASE
echo    - Selecione: SpringToolSuite4.exe
echo    - Clique em "Select"
echo.
echo 2. Clique em "Install / Update"
echo.
echo 3. Aguarde a mensagem de sucesso
echo.
echo 4. Reinicie o Spring Tools Suite
echo.
echo ========================================
pause
