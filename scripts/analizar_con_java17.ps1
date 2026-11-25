# Ejecuta la verificacion de calidad usando un JDK 17 instalado en Windows.
# No cambia JAVA_HOME global: solo se aplica durante esta ejecucion.
$ErrorActionPreference = 'Stop'
$roots = @(
    $env:JAVA_HOME,
    'C:\Program Files\Java',
    'C:\Program Files\Eclipse Adoptium',
    'C:\Program Files\Microsoft',
    'C:\Program Files\Amazon Corretto',
    (Join-Path $env:USERPROFILE '.jdks')
) | Where-Object { $_ -and (Test-Path $_) }
$candidates = @()
foreach ($root in $roots) {
    $candidates += $root
    $candidates += @(Get-ChildItem -Path $root -Directory -ErrorAction SilentlyContinue |
        Select-Object -ExpandProperty FullName)
}
$jdk17 = $null
foreach ($candidate in ($candidates | Select-Object -Unique)) {
    $javaExe = Join-Path $candidate 'bin\java.exe'
    $release = Join-Path $candidate 'release'
    if (!(Test-Path $javaExe) -or !(Test-Path $release)) { continue }
    $info = Get-Content -Path $release -Raw
    if ($info -match '(?m)^JAVA_VERSION="17(?:\.|")') {
        $jdk17 = $candidate
        break
    }
}
if (!$jdk17) {
    Write-Host 'No se ha detectado un JDK 17 en las rutas habituales.' -ForegroundColor Yellow
    Write-Host 'Instala Temurin JDK 17 desde https://adoptium.net/temurin/releases/?version=17'
    Write-Host 'Despues vuelve a ejecutar este script desde la raiz del proyecto.'
    exit 1
}
$env:JAVA_HOME = $jdk17
$env:Path = "$jdk17\bin;$env:Path"
Write-Host "JDK 17 seleccionado: $jdk17" -ForegroundColor Green
mvn -version
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
mvn clean verify -Pquality
exit $LASTEXITCODE
