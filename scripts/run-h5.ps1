$ErrorActionPreference = "Stop"
. (Join-Path $PSScriptRoot "use-cli-env.ps1") -SkipAndroidRequirement

Push-Location $repoRoot
try {
    & .\gradlew.bat :h5App:jsBrowserDevelopmentRun --continuous @KuiklyGradleArgs
    exit $LASTEXITCODE
} finally {
    Pop-Location
}
