$ErrorActionPreference = 'Stop'
Write-Host 'Paso 1/3: Compilar y ejecutar todas las pruebas JUnit5'
mvn clean verify -Pquality
if ($LASTEXITCODE -ne 0) { throw 'Maven / JUnit5 / calidad han fallado.' }
Write-Host 'Paso 2/3: Comprobar cobertura del 80% por clase'
python scripts/check_sonar_coverage.py target/site/jacoco/jacoco.xml
if ($LASTEXITCODE -ne 0) { throw 'El requisito de cobertura no se ha cumplido.' }
Write-Host 'Paso 3/3: Generar informe externo de calidad'
python scripts/sonar_external_issues.py
if ($LASTEXITCODE -ne 0) { throw 'No pudo generarse el informe de calidad.' }
Write-Host 'La verificacion local ha finalizado; SonarCloud evaluara Reliability tras el push.'
