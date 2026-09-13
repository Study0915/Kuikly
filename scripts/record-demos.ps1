param([int]$Port = 18761)
$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'use-cli-env.ps1')
. (Join-Path $PSScriptRoot 'artifact-receipts.ps1')
Push-Location $repoRoot
try {
    Write-ValidationReceipt $repoRoot 'recordings' @{schema=1;result='RUNNING'}
    $build = Assert-BuildReceipt $repoRoot
    $null = Assert-UiReceipt $repoRoot $build
    Assert-ServedBundle $repoRoot $Port $build
    $scripts = Get-RecordingsState $repoRoot
    $cli = Get-WorkspacePlaywrightCli $repoRoot
    $run = '.cache/recordings-' + (Get-Date -Format 'yyyyMMdd-HHmmss')
    if (Test-Path -LiteralPath $run) { throw 'Recording directory exists; retry with a new timestamp.' }
    New-Item -ItemType Directory $run | Out-Null
    foreach ($number in @(1,2)) {
        $session = "record-task$number"
        $config = @{browser=@{browserName='chromium';launchOptions=@{headless=$true;channel='msedge'};
            contextOptions=@{viewport=@{width=520;height=1020};hasTouch=$true;isMobile=$true}};outputDir=(Join-Path $repoRoot $run)}
        $configPath = Join-Path $run "$session.json"
        $config | ConvertTo-Json -Depth 6 | Set-Content $configPath
        & .cache/node/node.exe $cli "-s=$session" open "http://127.0.0.1:$Port/" --config $configPath --profile "$run/$session-profile" | Out-File "$run/$session-open.log"
        if ($LASTEXITCODE -ne 0) { throw 'Recording browser failed to start.' }
        try {
            & .cache/node/node.exe $cli "-s=$session" snapshot | Out-File "$run/$session-snapshot.log"
            & .cache/node/node.exe $cli "-s=$session" video-start "$run/task$number.webm" --size=520x1020 | Out-File "$run/$session-video-start.log"
            if ($LASTEXITCODE -ne 0) { throw 'Could not start video capture.' }
            try {
                $output = & .cache/node/node.exe $cli --raw "-s=$session" run-code (Get-Content "scripts/record-task$number-demo.js" -Raw)
                $output | Set-Content "$run/task$number-timeline.json"
                if ($LASTEXITCODE -ne 0) { throw 'Recording scenario failed.' }
                $timeline = ($output -join "`n") | ConvertFrom-Json
                if ($timeline.result -ne "TASK${number}_RECORDING_COMPLETE") { throw 'Recording scenario did not complete.' }
            } finally { & .cache/node/node.exe $cli "-s=$session" video-stop | Out-File "$run/$session-video-stop.log" }
        } finally { & .cache/node/node.exe $cli "-s=$session" close | Out-Null }
        if (-not (Test-Path "$run/task$number.webm") -or (Get-Item "$run/task$number.webm").Length -lt 10000) { throw 'Recording file is missing or empty.' }
        Write-Output "TASK${number}_VIDEO_RECORDED"
    }
    $null = Assert-BuildReceipt $repoRoot
    Assert-ServedBundle $repoRoot $Port $build
    if ($scripts.fingerprint -ne (Get-RecordingsState $repoRoot).fingerprint) { throw 'Recording scripts changed while recording.' }
    $videos=@();$timelines=@()
    foreach ($number in @(1,2)) {
        $video = ".cache/task2-evidence/task$number-final.webm"
        $timeline = ".cache/task2-evidence/task$number-video-timeline.json"
        # Earlier complete recording runs and submission candidates remain available.
        Copy-Item -LiteralPath "$run/task$number.webm" -Destination $video
        Copy-Item -LiteralPath "$run/task$number-timeline.json" -Destination $timeline
        $videos += $video; $timelines += $timeline
    }
    Write-ValidationReceipt $repoRoot 'recordings' ([ordered]@{schema=1;result='PASS';createdAt=[DateTimeOffset]::Now.ToString('o');
        buildFingerprint=$build.source.fingerprint;artifactFingerprint=(Get-RecordsFingerprint $build.artifacts);scripts=$scripts;
        videos=(Get-FileRecords $repoRoot $videos);timelines=(Get-FileRecords $repoRoot $timelines)})
    Write-Output 'RECORDING_RECEIPT_PASS'
} finally { Pop-Location }
