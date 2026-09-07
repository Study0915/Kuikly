param([switch]$Production, [int]$Port = 18761)

$ErrorActionPreference = "Stop"
. (Join-Path $PSScriptRoot "use-cli-env.ps1") -SkipAndroidRequirement

Push-Location $repoRoot
try {
    if ($Production) {
        & (Join-Path $repoRoot '.cache/node/node.exe') (Join-Path $PSScriptRoot 'serve-task1-h5.cjs') $Port
        exit $LASTEXITCODE
    }
    & .\gradlew.bat :h5App:jsBrowserDevelopmentRun --continuous @KuiklyGradleArgs
    exit $LASTEXITCODE
} finally {
    Pop-Location
}
