param([string]$Name = ('submission-' + (Get-Date -Format 'yyyyMMdd-HHmmss')))
$ErrorActionPreference = 'Stop'
if ($Name -notmatch '^submission-[a-zA-Z0-9-]+$') { throw 'Use a simple submission-* folder name.' }
$projectRoot = Split-Path -Parent $PSScriptRoot
. (Join-Path $PSScriptRoot 'artifact-receipts.ps1')
$outputRoot = Join-Path $projectRoot ".cache/$Name"
if (Test-Path -LiteralPath $outputRoot) { throw 'Candidate already exists; use a new name. No files were replaced.' }
Push-Location $projectRoot
try {
    $buildReceipt = Assert-BuildReceipt $projectRoot
    $uiReceipt = Assert-UiReceipt $projectRoot $buildReceipt
    $recordingReceipt = Assert-RecordingReceipt $projectRoot $buildReceipt
    $tracked = @(git ls-files)
    if ($LASTEXITCODE -ne 0) { throw 'Source repository is required for the explicit tracked-file export.' }
    $roots = @('README.md','LICENSE','THIRD_PARTY_NOTICES.md','.gitignore','build.gradle.kts','settings.gradle.kts','gradle.properties','gradlew','gradlew.bat')
    $docs = @('docs/REQUIREMENTS.md','docs/ARCHITECTURE.md','docs/DECISIONS.md','docs/evaluation-pipeline.md','docs/agent-workflow.md','docs/GIT-WORKFLOW.md','docs/workboard.md','docs/task1-readiness.md','docs/task2-readiness.md','docs/TASK1-RUN.md','docs/TASK2-RUN.md')
    $docs += @('docs/research/2026-08-21-shape-brief-teacher-video-human.md','docs/research/2026-08-21-shape-brief-teacher-video-ai.md',
        'docs/REVIEWS/TASK1-PROBES.md','docs/plans/references/task1-evidence-samples-v3.json')
    $files = @($tracked | Where-Object {
        $_ -in $roots -or $_ -in $docs -or $_ -match '^(gradle|shared|androidApp|h5App|KuiklyChart|scripts)/' -or
        $_ -match '^docs/(interfaces|evidence/task1|evidence/task2)/' -or
        $_ -match '^docs/(plans|learning|handoffs|REVIEWS)/TASK[12]-(PLAN|LEARNING|CODE|TESTS)\.md$' -or
        $_ -match '^docs/decisions/ADR-01[345].*\.md$' -or $_ -match '^docs/submit/(SCORING-AUDIT|DELIVERY|TASK[12]-VIDEO)\.md$' -or
        $_ -match '^docs/demo/' -or $_ -eq 'docs/submit/course-entry/OpenSourceTalent/Study0915/README.md'
    })
    foreach ($inputFile in @($buildReceipt.source.files) + @($uiReceipt.scripts.files) + @($recordingReceipt.scripts.files)) {
        if ($inputFile.path -ne 'kotlin-js-store/yarn.lock' -and $inputFile.path -notin $files) {
            throw "Build input omitted from tracked export: $($inputFile.path). Review and commit the required file first."
        }
    }
    foreach ($relative in $files) {
        if ($relative -match '(^|/)(\.cache|build|node_modules|archive|\.env[^/]*)(/|$)' -or $relative -match '\.(jks|keystore)$') { throw "Disallowed export path: $relative" }
        $target = Join-Path $outputRoot "source/$relative"
        New-Item -ItemType Directory -Force (Split-Path -Parent $target) | Out-Null
        Copy-Item -LiteralPath $relative -Destination $target
    }
    # Kotlin/JS stores its generated lock outside tracked source; ship the validated snapshot.
    $lockTarget = Join-Path $outputRoot 'source/kotlin-js-store'
    New-Item -ItemType Directory -Force $lockTarget | Out-Null
    Copy-Item -LiteralPath 'kotlin-js-store/yarn.lock' -Destination (Join-Path $lockTarget 'yarn.lock')
    $exportRoot = Join-Path $outputRoot 'source'
    foreach ($markdown in Get-ChildItem -LiteralPath $exportRoot -Filter '*.md' -File -Recurse) {
        $text = Get-Content -LiteralPath $markdown.FullName -Raw
        foreach ($link in [regex]::Matches($text,'\[[^\]\r\n]+\]\(([^)\s]+)\)')) {
            $href = $link.Groups[1].Value
            if ($href -match '^(https?://|mailto:|#)') { continue }
            $relative = [Uri]::UnescapeDataString(($href -split '#')[0])
            $target = [IO.Path]::GetFullPath((Join-Path $markdown.DirectoryName $relative))
            if (-not $target.StartsWith($exportRoot + [IO.Path]::DirectorySeparatorChar,[StringComparison]::OrdinalIgnoreCase) -or
                -not (Test-Path -LiteralPath $target)) {
                throw "Broken candidate document link in $([IO.Path]::GetRelativePath($exportRoot,$markdown.FullName)): $href"
            }
        }
    }
    foreach ($dir in @('preview','videos','binaries','validation')) { New-Item -ItemType Directory -Force (Join-Path $outputRoot $dir) | Out-Null }
    foreach ($kind in @('build','ui','recordings')) {
        Copy-Item -LiteralPath ".cache/verification/$kind.json" -Destination (Join-Path $outputRoot "validation/$kind.json")
    }
    foreach ($record in @($uiReceipt.results) + @($recordingReceipt.timelines)) {
        Copy-Item -LiteralPath $record.path -Destination (Join-Path $outputRoot "validation/$([IO.Path]::GetFileName($record.path))")
    }
    Copy-Item h5App/build/kotlin-webpack/js/productionExecutable/h5App.js (Join-Path $outputRoot 'preview/h5App.js')
    Copy-Item h5App/src/jsMain/resources/index.html (Join-Path $outputRoot 'preview/index.html')
    Copy-Item androidApp/build/outputs/apk/debug/androidApp-debug.apk (Join-Path $outputRoot 'binaries/android-debug-build-only.apk')
    foreach ($taskNumber in @(1,2)) {
        $publicVideo = "docs/demo/task$taskNumber.webm"
        if (-not (Test-Path -LiteralPath $publicVideo) -or
            (Get-FileHash -LiteralPath $publicVideo).Hash -ne (Get-FileHash -LiteralPath ".cache/task2-evidence/task$taskNumber-final.webm").Hash) {
            throw "Public Task $taskNumber video differs from the validated recording."
        }
        Copy-Item ".cache/task2-evidence/task$taskNumber-final.webm" (Join-Path $outputRoot "videos/task$taskNumber.webm")
        Copy-Item "docs/submit/TASK$taskNumber-VIDEO.md" (Join-Path $outputRoot "videos/task$taskNumber-transcript.md")
    }
    @'
const http = require('node:http');
const fs = require('node:fs');
const path = require('node:path');
const port = Number(process.argv[2] || 18770);
if (!Number.isInteger(port) || port < 1024 || port > 65535) throw new Error('Invalid port');
http.createServer((req,res) => {
  const route = new URL(req.url,'http://localhost').pathname;
  const file = route === '/' ? 'index.html' : route === '/h5App.js' ? 'h5App.js' : null;
  if (!file) { res.writeHead(route === '/favicon.ico' ? 204 : 404); res.end(); return; }
  res.setHeader('Content-Type', file.endsWith('.js') ? 'text/javascript; charset=utf-8' : 'text/html; charset=utf-8');
  res.setHeader('Cache-Control','no-store'); fs.createReadStream(path.join(__dirname,file)).pipe(res);
}).listen(port,'127.0.0.1',()=>console.log(`Preview: http://127.0.0.1:${port}/`));
'@ | Set-Content (Join-Path $outputRoot 'preview/serve.cjs')
    @'
# Kuikly 两题本地候选

先看 videos/task1.webm 与 videos/task2.webm：真实浏览器操作，应用画面外附说明字幕，无音频。
评分、实现与测试见 source/README.md 和 source/docs/submit/SCORING-AUDIT.md。

快速离线预览：已有 Node.js 环境中执行 `node preview/serve.cjs`，打开 http://127.0.0.1:18770/。
从源码构建：进入 source，按 README 与 docs/TASK1-RUN.md 准备工作区内 JDK17/Node/Android command-line tools，再运行 scripts/doctor.ps1 和 scripts/verify.ps1。
本包不含 SDK、依赖缓存、账号、密钥、Git历史或旧归档；首次源码构建需要下载固定版本依赖。

validation/ 内的构建、浏览器、录像收据绑定同一源码输入与运行产物；MANIFEST 对本包逐文件记录 SHA256。收据记录的是原工作区验证，包内不含原缓存目录。

两个Demo均为确定性历史Mock，不调用真实行情或AI模型。Android APK仅构建验证，设备/iOS/鸿蒙未运行验证。
这是本地候选，未发布或对外提交；个人讲述能力与老师实际评分未知。
'@ | Set-Content (Join-Path $outputRoot 'START-HERE.md')
    $manifest = @(Get-ChildItem -LiteralPath $outputRoot -File -Recurse | ForEach-Object {
        [pscustomobject]@{ path=[IO.Path]::GetRelativePath($outputRoot,$_.FullName).Replace('\','/');bytes=$_.Length;sha256=(Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash }
    })
    # Recheck after copying: a concurrent edit must never receive a verified manifest.
    $null = Assert-BuildReceipt $projectRoot
    $null = Assert-UiReceipt $projectRoot $buildReceipt
    $null = Assert-RecordingReceipt $projectRoot $buildReceipt
    $exportedState = Get-BuildState (Join-Path $outputRoot 'source')
    if ($exportedState.fingerprint -ne $buildReceipt.source.fingerprint) { throw 'Exported source differs from validated build inputs.' }
    if ((Get-FileHash (Join-Path $outputRoot 'preview/h5App.js')).Hash -ne ($buildReceipt.artifacts | Where-Object path -like '*.js').sha256 -or
        (Get-FileHash (Join-Path $outputRoot 'binaries/android-debug-build-only.apk')).Hash -ne ($buildReceipt.artifacts | Where-Object path -like '*.apk').sha256) { throw 'Copied build artifacts differ from receipt.' }
    [ordered]@{createdAt=[DateTimeOffset]::Now.ToString('o');repositoryHead=(git rev-parse HEAD);buildFingerprint=$buildReceipt.source.fingerprint;
        artifactFingerprint=(Get-RecordsFingerprint $buildReceipt.artifacts);snapshot='Selected tracked working-tree files; exact hashes below include retained documentation edits';files=$manifest} |
        ConvertTo-Json -Depth 5 | Set-Content (Join-Path $outputRoot 'MANIFEST.json')
    Add-Type -AssemblyName System.IO.Compression.FileSystem
    $zipPath = "$outputRoot.zip"
    [IO.Compression.ZipFile]::CreateFromDirectory($outputRoot,$zipPath)
    $zip = [IO.Compression.ZipFile]::OpenRead($zipPath)
    try {
        foreach ($file in $manifest) {
            $entry = $zip.GetEntry($file.path)
            if ($null -eq $entry -or $entry.Length -ne $file.bytes) { throw "Archive entry mismatch: $($file.path)" }
            $stream = $entry.Open()
            try { $sha=[Security.Cryptography.SHA256]::Create(); $actual=[BitConverter]::ToString($sha.ComputeHash($stream)).Replace('-',''); $sha.Dispose() }
            finally { $stream.Dispose() }
            if ($actual -ne $file.sha256) { throw "Archive hash mismatch: $($file.path)" }
        }
    } finally { $zip.Dispose() }
    $zipHash = (Get-FileHash -LiteralPath $zipPath -Algorithm SHA256).Hash
    "$zipHash  $Name.zip" | Set-Content "$zipPath.sha256"
    [pscustomobject]@{result='CANDIDATE_ARCHIVE_VERIFIED';folder=$outputRoot;zip=$zipPath;fileCount=$manifest.Count;bytes=(Get-Item $zipPath).Length;sha256=$zipHash} | ConvertTo-Json
} finally { Pop-Location }
