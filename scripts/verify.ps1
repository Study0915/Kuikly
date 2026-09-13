param(
    [switch]$SkipAndroid
)

$ErrorActionPreference = "Stop"
. (Join-Path $PSScriptRoot "use-cli-env.ps1") -SkipAndroidRequirement:$SkipAndroid
. (Join-Path $PSScriptRoot 'artifact-receipts.ps1')

Push-Location $repoRoot
try {
    $before = Get-BuildState $repoRoot -WithoutLock
    Write-ValidationReceipt $repoRoot 'build' @{schema=1;result='RUNNING'}
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
        if ($before.fingerprint -ne (Get-BuildState $repoRoot -WithoutLock).fingerprint) { throw 'Build inputs changed while verification ran; rerun.' }
        $jvm = @(Get-ChildItem shared/build/test-results/testDebugUnitTest -Filter 'TEST-*.xml' | ForEach-Object { ([xml](Get-Content $_.FullName -Raw)).testsuite })
        $tests = ($jvm | Measure-Object -Property tests -Sum).Sum
        $failures = ($jvm | Measure-Object -Property failures -Sum).Sum
        $errors = ($jvm | Measure-Object -Property errors -Sum).Sum
        if ($tests -lt 1 -or $failures -gt 0 -or $errors -gt 0) { throw 'JVM result files are empty or failed.' }
        Write-ValidationReceipt $repoRoot 'build' ([ordered]@{schema=1;result='PASS';createdAt=[DateTimeOffset]::Now.ToString('o');
            source=(Get-BuildState $repoRoot);jvm=@{tests=$tests;failures=$failures;errors=$errors};
            artifacts=(Get-FileRecords $repoRoot @('h5App/build/kotlin-webpack/js/productionExecutable/h5App.js','androidApp/build/outputs/apk/debug/androidApp-debug.apk'))})
        Write-Output 'BUILD_RECEIPT_PASS'
    }
} finally {
    Pop-Location
}
