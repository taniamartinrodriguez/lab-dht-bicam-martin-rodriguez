# Lee los porcentajes de cobertura de LINEAS para las ocho clases probadas.
# Ejecutar DESPUES de mvn clean verify -Pquality.
param([string]$Informe = 'target/site/jacoco/jacoco.xml')
if (!(Test-Path $Informe)) {
    Write-Host "No encuentro $Informe. Ejecuta mvn clean verify -Pquality." -ForegroundColor Red
    exit 1
}
[xml]$xml = Get-Content -Path $Informe -Raw -Encoding UTF8
$clases = @(
    'evolutionary_algorithms/complement/OnePointCrossover',
    'evolutionary_algorithms/complement/UniformCrossover',
    'evolutionary_algorithms/complement/TowPointsMutation',
    'factory_method/FactoryLoader',
    'local_search/candidate_type/RandomCandidate',
    'local_search/complement/StopExecute',
    'problem/definition/Problem',
    'problem/definition/State'
)
$resultados = foreach ($nombre in $clases) {
    $clase = $xml.SelectSingleNode("/report/package/class[@name='$nombre']")
    if (!$clase) {
        [pscustomobject]@{ Clase = $nombre; Lineas = '-'; Cobertura = 'sin datos'; Meta80 = 'NO' }
        continue
    }
    $contador = $clase.SelectSingleNode("counter[@type='LINE']")
    if (!$contador) {
        [pscustomobject]@{ Clase = $nombre; Lineas = '-'; Cobertura = 'sin datos'; Meta80 = 'NO' }
        continue
    }
    $cubiertas = [int]$contador.GetAttribute('covered')
    $perdidas = [int]$contador.GetAttribute('missed')
    $total = $cubiertas + $perdidas
    if ($total -eq 0) { $porcentaje = 0.0 }
    else { $porcentaje = [Math]::Round($cubiertas * 100.0 / $total, 1) }
    $meta = if ($porcentaje -ge 80.0) { 'SI' } else { 'NO' }
    [pscustomobject]@{
        Clase = ($nombre -split '/')[-1]
        Lineas = "$cubiertas/$total"
        Cobertura = "$porcentaje%"
        Meta80 = $meta
    }
}
$resultados | Format-Table -AutoSize
if (@($resultados | Where-Object { $_.Meta80 -ne 'SI' }).Count -gt 0) {
    Write-Host 'Todavia quedan clases probadas por debajo del 80%.' -ForegroundColor Yellow
} else {
    Write-Host 'Las ocho clases probadas superan el 80% de cobertura de LINEAS.' -ForegroundColor Green
}
