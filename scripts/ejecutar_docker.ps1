param(
  [ValidateSet('build','run','help')]
  [string]$Action = 'build'
)
$ErrorActionPreference = 'Stop'
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    throw 'Docker Desktop no esta instalado o no esta disponible en PATH.'
}
if ($Action -eq 'build') {
    docker build -t biciam:1.0.0 .
} elseif ($Action -eq 'run') {
    docker run --rm biciam:1.0.0 --demo
} else {
    docker run --rm biciam:1.0.0 --help
}
if ($LASTEXITCODE -ne 0) {
    throw "Docker ha terminado con codigo $LASTEXITCODE"
}
