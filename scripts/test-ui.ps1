param([ValidateSet('Task1','Task2','Both')][string]$Task = 'Both', [int]$Port = 18761)
$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'use-cli-env.ps1')
. (Join-Path $PSScriptRoot 'artifact-receipts.ps1')
if ($Port -lt 1024 -or $Port -gt 65535) { throw 'Invalid localhost port.' }
Push-Location $repoRoot
try {
    Write-ValidationReceipt $repoRoot 'ui' @{schema=1;result='RUNNING'}
    $build = Assert-BuildReceipt $repoRoot
    Assert-ServedBundle $repoRoot $Port $build
    $scripts = Get-UiState $repoRoot
    $cli = Get-WorkspacePlaywrightCli $repoRoot
    $results = @()
    $uiOutput = Join-Path $repoRoot '.cache/ui-verification'
    @($uiOutput, '.cache/task1-evidence/browser', '.cache/task1-improvements-20260908', '.cache/task2-evidence') | ForEach-Object { New-Item -ItemType Directory -Force $_ | Out-Null }
    foreach ($desktop in @($false, $true)) {
        $session = if ($desktop) { 'verify-desktop' } else { 'verify-touch' }
        $config = @{ browser = @{ browserName = 'chromium'; launchOptions = @{headless=$true;channel='msedge'}; contextOptions = @{viewport=@{width=390;height=844};hasTouch=(-not $desktop);isMobile=(-not $desktop)} }; outputDir=$uiOutput }
        $configPath = Join-Path $uiOutput "$session.json"
        $config | ConvertTo-Json -Depth 6 | Set-Content $configPath
        & .cache/node/node.exe $cli "-s=$session" open "http://127.0.0.1:$Port/" --config $configPath --profile ".cache/playwright/$session-profile" | Out-File (Join-Path $uiOutput "$session-open.log")
        if ($LASTEXITCODE -ne 0) { throw 'Could not open test browser.' }
        try {
            & .cache/node/node.exe $cli "-s=$session" snapshot | Out-File (Join-Path $uiOutput "$session-snapshot.log")
            $tests = @()
            if ($Task -ne 'Task2') { $tests += if ($desktop) { 'task1-mouse' } else { @('task1-h5','task1-touch','task1-deepening') } }
            if ($Task -ne 'Task1') { $tests += @('task2-h5','task2-refinement'); if (-not $desktop) { $tests += 'task2-session' } }
            foreach ($test in $tests) {
                $result = & .cache/node/node.exe $cli --raw "-s=$session" run-code (Get-Content "scripts/test-$test.js" -Raw)
                $result | Set-Content (Join-Path $uiOutput "$session-$test.json")
                # CLI can return shell exit 0 with a tool error: require the exact PASS marker too.
                $marker = ($test -replace '-', '_').ToUpper() + '_PASS'
                if ($LASTEXITCODE -ne 0 -or ($result -join '') -notmatch [regex]::Escape($marker)) { throw "$test failed; inspect .cache/ui-verification." }
                $parsed = ($result -join "`n") | ConvertFrom-Json
                $status = if ($parsed.result) { $parsed.result } else { $parsed.status }
                if ($status -ne $marker) { throw "$test did not return a structured PASS result." }
                $results += ".cache/ui-verification/$session-$test.json"
                Write-Output "$session $marker"
            }
        } finally { & .cache/node/node.exe $cli "-s=$session" close | Out-Null }
    }
    Assert-ServedBundle $repoRoot $Port $build
    $null = Assert-BuildReceipt $repoRoot
    if ($scripts.fingerprint -ne (Get-UiState $repoRoot).fingerprint) { throw 'UI test scripts changed during validation.' }
    Write-ValidationReceipt $repoRoot 'ui' ([ordered]@{schema=1;result='PASS';createdAt=[DateTimeOffset]::Now.ToString('o');scope=$Task;
        buildFingerprint=$build.source.fingerprint;artifactFingerprint=(Get-RecordsFingerprint $build.artifacts);scripts=$scripts;results=(Get-FileRecords $repoRoot $results)})
    Write-Output 'UI_RECEIPT_PASS'
} finally { Pop-Location }
