# Ejecutar desde la raiz del repositorio tras mvn clean verify.
$ErrorActionPreference = 'Stop'
$xmlPath = Join-Path (Get-Location) 'target/site/jacoco/jacoco.xml'
if (-not (Test-Path $xmlPath)) {
    throw "No se encuentra $xmlPath. Ejecuta primero mvn clean verify."
}
[xml]$report = Get-Content -Raw -Encoding UTF8 $xmlPath
$classes = @(
 'evolutionary_algorithms/complement/OnePointCrossover',
 'evolutionary_algorithms/complement/UniformCrossover',
 'evolutionary_algorithms/complement/TowPointsMutation',
 'factory_method/FactoryLoader',
 'local_search/candidate_type/RandomCandidate',
 'local_search/complement/StopExecute',
 'problem/definition/Problem',
 'problem/definition/State',
 'evolutionary_algorithms/complement/AIOMutation',
 'evolutionary_algorithms/complement/OnePointMutation',
 'metaheurictics/strategy/StrategyRequirements',
 'evolutionary_algorithms/complement/SteadyStateReplace',
 'evolutionary_algorithms/complement/TruncationSelection',
 'local_search/acceptation_type/AcceptBest',
 'local_search/acceptation_type/AcceptNotBad',
 'local_search/acceptation_type/AcceptNotBadT',
 'local_search/acceptation_type/AcceptNotBadU',
 'local_search/acceptation_type/Dominance',
 'local_search/candidate_type/CandidateValue',
 'local_search/candidate_type/NotDominatedCandidate'
)
$results = foreach ($className in $classes) {
    $node = $report.SelectSingleNode("/report/package/class[@name='$className']")
    if ($null -eq $node) {
        [pscustomobject]@{ Clase = ($className -split '/')[-1]; Lineas = 'NO ENCONTRADA'; Cobertura = '-'; Meta80 = 'NO' }
        continue
    }
    $counter = $node.SelectSingleNode("counter[@type='LINE']")
    if ($null -eq $counter) {
        [pscustomobject]@{ Clase = ($className -split '/')[-1]; Lineas = 'SIN LINEAS'; Cobertura = '-'; Meta80 = 'N/A' }
        continue
    }
    $covered = [int]$counter.GetAttribute('covered')
    $missed = [int]$counter.GetAttribute('missed')
    $total = $covered + $missed
    $percent = if ($total -eq 0) { 0.0 } else { 100.0 * $covered / $total }
    [pscustomobject]@{
        Clase = ($className -split '/')[-1]
        Lineas = "$covered/$total"
        Cobertura = ('{0:N1}%' -f $percent)
        Meta80 = if ($percent -ge 80) { 'SI' } else { 'NO' }
    }
}
$results | Format-Table -AutoSize
if (@($results | Where-Object { $_.Meta80 -eq 'NO' }).Count -gt 0) {
    Write-Host 'Hay clases por debajo de la meta del 80%: revisaremos su cobertura antes de cerrar el Issue.' -ForegroundColor Yellow
} else {
    Write-Host 'Las clases analizadas superan el 80% de cobertura de lineas.' -ForegroundColor Green
}
