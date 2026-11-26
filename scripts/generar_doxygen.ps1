$ErrorActionPreference = 'Stop'
if (-not (Get-Command doxygen -ErrorAction SilentlyContinue)) {
    throw 'Instala Doxygen y asegurate de que doxygen este disponible en PATH.'
}
doxygen Doxyfile
if ($LASTEXITCODE -ne 0) { throw "Doxygen fallo con codigo $LASTEXITCODE" }
$report = Join-Path (Get-Location) 'docs/doxygen/html/index.html'
if (-not (Test-Path $report)) { throw "No se genero $report" }
Write-Host "Documentacion generada: $report"
