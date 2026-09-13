# Exercise rejection paths in an isolated copy. Never alters the active source or its receipts.
$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot 'artifact-receipts.ps1')
$fixtureRoot = Join-Path $projectRoot ('.cache/receipt-tests-' + (Get-Date -Format 'yyyyMMdd-HHmmss'))
if (Test-Path -LiteralPath $fixtureRoot) { throw 'Fixture already exists.' }
$build = Assert-BuildReceipt $projectRoot
$ui = Assert-UiReceipt $projectRoot $build
$recording = Assert-RecordingReceipt $projectRoot $build
$paths = @($build.source.files.path) + @($build.artifacts.path) + @($ui.scripts.files.path) + @($ui.results.path) +
    @($recording.scripts.files.path) + @($recording.videos.path) + @($recording.timelines.path) +
    @('.cache/verification/build.json','.cache/verification/ui.json','.cache/verification/recordings.json')
foreach ($relative in ($paths | Sort-Object -Unique)) {
    $target = Join-Path $fixtureRoot $relative
    New-Item -ItemType Directory -Force (Split-Path -Parent $target) | Out-Null
    Copy-Item -LiteralPath (Join-Path $projectRoot $relative) -Destination $target
}
$checks = [Collections.Generic.List[string]]::new()
function Reject([string]$Name, [string]$Expected, [scriptblock]$Action) {
    try { & $Action | Out-Null } catch {
        if ($_.Exception.Message -notmatch $Expected) { throw "Wrong failure for ${Name}: $($_.Exception.Message)" }
        $checks.Add($Name); return
    }
    throw "Gate unexpectedly accepted: $Name"
}
function Restore([string]$Relative) {
    Copy-Item -LiteralPath (Join-Path $projectRoot $Relative) -Destination (Join-Path $fixtureRoot $Relative)
}

$null = Assert-BuildReceipt $fixtureRoot
$null = Assert-UiReceipt $fixtureRoot $build
$null = Assert-RecordingReceipt $fixtureRoot $build
$checks.Add('matching build, UI and recordings accepted')

$source = 'shared/src/commonMain/kotlin/io/github/study0915/kuiklyfinance/chat/MockChatProvider.kt'
Add-Content -LiteralPath (Join-Path $fixtureRoot $source) -Value '// isolated source mutation'
Reject 'changed source rejected' 'Source or build inputs changed' { Assert-BuildReceipt $fixtureRoot }
Restore $source

$probe = Join-Path $fixtureRoot 'shared/src/commonMain/ReceiptProbe.kt'
Set-Content -LiteralPath $probe -Value '// untracked build input'
Reject 'untracked source included in fingerprint' 'Source or build inputs changed' { Assert-BuildReceipt $fixtureRoot }
$retained = Join-Path $fixtureRoot '.cache/rejected-source-probe.txt'
# Verify both absolute paths before moving the test-created probe; no recursive or cross-shell operations.
foreach ($path in @($probe,$retained)) {
    if (-not [IO.Path]::GetFullPath($path).StartsWith([IO.Path]::GetFullPath($fixtureRoot) + [IO.Path]::DirectorySeparatorChar,[StringComparison]::OrdinalIgnoreCase)) { throw 'Fixture path escaped.' }
}
Move-Item -LiteralPath $probe -Destination $retained

Set-Content -LiteralPath (Join-Path $fixtureRoot 'README.md') -Value 'Documentation-only edit'
$null = Assert-BuildReceipt $fixtureRoot
$checks.Add('documentation edit does not invalidate business build')

$js = 'h5App/build/kotlin-webpack/js/productionExecutable/h5App.js'
Add-Content -LiteralPath (Join-Path $fixtureRoot $js) -Value '// isolated artifact mutation'
Reject 'replaced JS rejected by build receipt' 'Stale artifact' { Assert-BuildReceipt $fixtureRoot }
$rebuilt = Read-ValidationReceipt $fixtureRoot 'build'
$rebuilt.artifacts = @(Get-FileRecords $fixtureRoot @($build.artifacts.path))
Write-ValidationReceipt $fixtureRoot 'build' $rebuilt
$null = Assert-BuildReceipt $fixtureRoot
Reject 'same-source different JS invalidates UI' 'UI receipt is stale' { Assert-UiReceipt $fixtureRoot $rebuilt }
Reject 'same-source different JS invalidates recordings' 'Recording receipt is stale' { Assert-RecordingReceipt $fixtureRoot $rebuilt }
Restore $js; Restore '.cache/verification/build.json'

$testScript = 'scripts/test-task2-refinement.js'
Add-Content -LiteralPath (Join-Path $fixtureRoot $testScript) -Value '// isolated test mutation'
Reject 'changed UI test script rejected' 'UI receipt is stale' { Assert-UiReceipt $fixtureRoot $build }
Restore $testScript

$incomplete = Read-ValidationReceipt $fixtureRoot 'ui'
$incomplete.results = @()
Write-ValidationReceipt $fixtureRoot 'ui' $incomplete
Reject 'empty UI evidence rejected' 'full required result set' { Assert-UiReceipt $fixtureRoot $build }
Restore '.cache/verification/ui.json'

$resultPath = $ui.results[0].path
Set-Content -LiteralPath (Join-Path $fixtureRoot $resultPath) -Value 'altered evidence'
Reject 'changed UI evidence rejected' 'Stale artifact' { Assert-UiReceipt $fixtureRoot $build }
Restore $resultPath

Write-ValidationReceipt $fixtureRoot 'recordings' @{schema=1;result='RUNNING'}
Reject 'unfinished recording rejected' 'not PASS' { Assert-RecordingReceipt $fixtureRoot $build }
Restore '.cache/verification/recordings.json'

$video = '.cache/task2-evidence/task2-final.webm'
[IO.File]::AppendAllText((Join-Path $fixtureRoot $video),'isolated video mutation')
Reject 'changed video rejected' 'Stale artifact' { Assert-RecordingReceipt $fixtureRoot $build }
Restore $video

$result = [ordered]@{result='RECEIPT_GATES_PASS';count=$checks.Count;checks=$checks}
$result | ConvertTo-Json -Depth 4 | Set-Content -LiteralPath (Join-Path $projectRoot '.cache/verification/gate-tests.json')
$result | ConvertTo-Json -Depth 4
