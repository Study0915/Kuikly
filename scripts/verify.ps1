param(
    [switch]$SkipAndroid
)

$ErrorActionPreference = "Stop"
. (Join-Path $PSScriptRoot "use-cli-env.ps1") -SkipAndroidRequirement:$SkipAndroid

Push-Location $repoRoot
try {
    & .\gradlew.bat :KuiklyChart:jsNodeTest :shared:compileKotlinJs :h5App:jsBrowserProductionWebpack @KuiklyGradleArgs
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    if (-not $SkipAndroid) {
        & .\gradlew.bat :shared:testDebugUnitTest :androidApp:assembleDebug @KuiklyGradleArgs
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    }
} finally {
    Pop-Location
}
