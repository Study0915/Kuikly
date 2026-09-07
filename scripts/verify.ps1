param(
    [switch]$SkipAndroid
)

$ErrorActionPreference = "Stop"
. (Join-Path $PSScriptRoot "use-cli-env.ps1") -SkipAndroidRequirement:$SkipAndroid

Push-Location $repoRoot
try {
    & .\gradlew.bat :KuiklyChart:jsNodeTest :shared:compileKotlinJs :h5App:jsBrowserProductionWebpack @KuiklyGradleArgs
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    $task1JsEntry = Join-Path $repoRoot 'shared/build/generated/ksp/js/jsMain/kotlin/KuiklyCoreEntry.kt'
    if (-not (Test-Path $task1JsEntry) -or -not (Select-String -LiteralPath $task1JsEntry -Pattern 'FinanceHomePage' -Quiet)) {
        throw 'Task 1 JS page registration is missing; do not accept a bundle that cannot open finance_home.'
    }
    if (-not $SkipAndroid) {
        & .\gradlew.bat :shared:testDebugUnitTest :androidApp:assembleDebug @KuiklyGradleArgs
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
        $task1AndroidEntry = Join-Path $repoRoot 'shared/build/generated/ksp/android/androidDebug/kotlin/KuiklyCoreEntry.kt'
        if (-not (Test-Path $task1AndroidEntry) -or -not (Select-String -LiteralPath $task1AndroidEntry -Pattern 'FinanceHomePage' -Quiet)) {
            throw 'Task 1 Android page registration is missing.'
        }
    }
} finally {
    Pop-Location
}
