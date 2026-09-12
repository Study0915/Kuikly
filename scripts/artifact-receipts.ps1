# Shared, workspace-local validation receipts. Dot-source; no global configuration changes.
function Get-FileRecords([string]$Root, [string[]]$Paths) {
    @($Paths | Sort-Object -Unique | ForEach-Object {
        $file = Join-Path $Root $_
        if (-not (Test-Path -LiteralPath $file -PathType Leaf)) { throw "Required file missing: $_" }
        [ordered]@{path=$_;sha256=(Get-FileHash -LiteralPath $file -Algorithm SHA256).Hash}
    })
}

function Get-RecordsFingerprint($Records) {
    $canonical = ($Records | ForEach-Object { "$($_.path)`t$($_.sha256)" }) -join "`n"
    $sha = [Security.Cryptography.SHA256]::Create()
    try { [BitConverter]::ToString($sha.ComputeHash([Text.Encoding]::UTF8.GetBytes($canonical))).Replace('-','') }
    finally { $sha.Dispose() }
}

function Get-BuildState([string]$Root, [switch]$WithoutLock) {
    $paths = @('build.gradle.kts','settings.gradle.kts','gradle.properties','gradlew','gradlew.bat',
        'scripts/use-cli-env.ps1','scripts/verify.ps1','scripts/artifact-receipts.ps1')
    foreach ($module in @('shared','h5App','androidApp','KuiklyChart')) {
        $paths += "$module/build.gradle.kts"
        $src = Join-Path $Root "$module/src"
        if (Test-Path -LiteralPath $src) {
            # Includes untracked source files: git status alone is not the build input set.
            $paths += Get-ChildItem -LiteralPath $src -Recurse -File | ForEach-Object { [IO.Path]::GetRelativePath($Root,$_.FullName).Replace('\','/') }
        }
    }
    $paths += Get-ChildItem -LiteralPath (Join-Path $Root 'gradle') -Recurse -File | ForEach-Object { [IO.Path]::GetRelativePath($Root,$_.FullName).Replace('\','/') }
    if (-not $WithoutLock) { $paths += 'kotlin-js-store/yarn.lock' }
    $records = Get-FileRecords $Root $paths
    [ordered]@{fingerprint=(Get-RecordsFingerprint $records);files=$records}
}

function Get-UiState([string]$Root) {
    $paths = @('scripts/test-ui.ps1') + @(Get-ChildItem -LiteralPath (Join-Path $Root 'scripts') -Filter 'test-*.js' -File | ForEach-Object { "scripts/$($_.Name)" })
    $records = Get-FileRecords $Root $paths
    [ordered]@{fingerprint=(Get-RecordsFingerprint $records);files=$records}
}

function Get-RecordingsState([string]$Root) {
    $records = Get-FileRecords $Root @('scripts/record-demos.ps1','scripts/record-task1-demo.js','scripts/record-task2-demo.js','scripts/demo-frame.html')
    [ordered]@{fingerprint=(Get-RecordsFingerprint $records);files=$records}
}

function Write-ValidationReceipt([string]$Root, [string]$Kind, $Data) {
    $directory = Join-Path $Root '.cache/verification'
    New-Item -ItemType Directory -Force $directory | Out-Null
    $Data | ConvertTo-Json -Depth 12 | Set-Content -LiteralPath (Join-Path $directory "$Kind.json") -Encoding utf8
}

function Read-ValidationReceipt([string]$Root, [string]$Kind) {
    $path = Join-Path $Root ".cache/verification/$Kind.json"
    if (-not (Test-Path -LiteralPath $path)) { throw "Missing $Kind receipt; run the corresponding validation script." }
    $receipt = Get-Content -LiteralPath $path -Raw | ConvertFrom-Json
    if ($receipt.schema -ne 1 -or $receipt.result -ne 'PASS') { throw "$Kind receipt is not PASS." }
    $receipt
}

function Assert-RecordedFiles([string]$Root, $Files) {
    foreach ($record in $Files) {
        $current = @(Get-FileRecords $Root @($record.path))
        if ($current[0].sha256 -ne $record.sha256) { throw "Stale artifact: $($record.path)" }
    }
}

function Assert-BuildReceipt([string]$Root) {
    $receipt = Read-ValidationReceipt $Root 'build'
    if ($receipt.source.fingerprint -ne (Get-BuildState $Root).fingerprint) { throw 'Source or build inputs changed; rerun verify.ps1.' }
    $required = @('h5App/build/kotlin-webpack/js/productionExecutable/h5App.js','androidApp/build/outputs/apk/debug/androidApp-debug.apk')
    if ((($receipt.artifacts.path | Sort-Object) -join ',') -ne (($required | Sort-Object) -join ',')) { throw 'Build receipt must include H5 and APK.' }
    Assert-RecordedFiles $Root $receipt.artifacts
    $receipt
}

function Assert-UiReceipt([string]$Root, $Build) {
    $receipt = Read-ValidationReceipt $Root 'ui'
    if ($receipt.scope -ne 'Both' -or $receipt.buildFingerprint -ne $Build.source.fingerprint -or
        $receipt.artifactFingerprint -ne (Get-RecordsFingerprint $Build.artifacts) -or
        $receipt.scripts.fingerprint -ne (Get-UiState $Root).fingerprint) { throw 'UI receipt is stale or incomplete; rerun test-ui.ps1 -Task Both.' }
    $expected = @('verify-touch-task1-h5','verify-touch-task1-touch','verify-touch-task1-deepening',
        'verify-touch-task2-h5','verify-touch-task2-refinement','verify-touch-task2-session',
        'verify-desktop-task1-mouse','verify-desktop-task2-h5','verify-desktop-task2-refinement') |
        ForEach-Object { ".cache/ui-verification/$_.json" }
    if ((($receipt.results.path | Sort-Object) -join ',') -ne (($expected | Sort-Object) -join ',')) { throw 'UI receipt does not contain the full required result set.' }
    Assert-RecordedFiles $Root $receipt.results
    $receipt
}

function Assert-RecordingReceipt([string]$Root, $Build) {
    $receipt = Read-ValidationReceipt $Root 'recordings'
    if ($receipt.buildFingerprint -ne $Build.source.fingerprint -or
        $receipt.artifactFingerprint -ne (Get-RecordsFingerprint $Build.artifacts) -or
        $receipt.scripts.fingerprint -ne (Get-RecordingsState $Root).fingerprint) { throw 'Recording receipt is stale; rerun record-demos.ps1.' }
    $required = @('.cache/task2-evidence/task1-final.webm','.cache/task2-evidence/task2-final.webm')
    if ((($receipt.videos.path | Sort-Object) -join ',') -ne (($required | Sort-Object) -join ',')) { throw 'Both videos are required.' }
    Assert-RecordedFiles $Root $receipt.videos
    Assert-RecordedFiles $Root $receipt.timelines
    $receipt
}

function Assert-ServedBundle([string]$Root, [int]$Port, $Build) {
    if ($Port -lt 1024 -or $Port -gt 65535) { throw 'Invalid localhost port.' }
    $directory = Join-Path $Root '.cache/verification'
    New-Item -ItemType Directory -Force $directory | Out-Null
    foreach ($asset in @(@{url='h5App.js';path='h5App/build/kotlin-webpack/js/productionExecutable/h5App.js'},@{url='';path='h5App/src/jsMain/resources/index.html'})) {
        $download = Join-Path $directory ('served-' + $(if ($asset.url) { 'h5App.js' } else { 'index.html' }))
        Invoke-WebRequest -Uri "http://127.0.0.1:$Port/$($asset.url)" -OutFile $download -TimeoutSec 15 | Out-Null
        if ((Get-FileHash -LiteralPath $download).Hash -ne (Get-FileHash -LiteralPath (Join-Path $Root $asset.path)).Hash) {
            throw 'Localhost serves a different build. Start run-h5.ps1 -Production for the current workspace.'
        }
    }
    # Also detects local artifact replacement while the server was checked.
    Assert-RecordedFiles $Root $Build.artifacts
}

function Get-WorkspacePlaywrightCli([string]$Root) {
    $candidates = Get-ChildItem -LiteralPath (Join-Path $Root '.cache/npm/_npx') -Filter 'playwright-cli.js' -Recurse -File |
        Where-Object { $_.FullName -match '@playwright[\\/]cli[\\/]' }
    foreach ($candidate in $candidates) {
        $package = Get-Content (Join-Path $candidate.DirectoryName 'package.json') -Raw | ConvertFrom-Json
        if ($package.version -eq '0.1.18') { return $candidate.FullName }
    }
    throw 'Pinned Playwright CLI 0.1.18 is required in the workspace npm cache.'
}
