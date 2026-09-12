param([string]$Name = ('submission-' + (Get-Date -Format 'yyyyMMdd-HHmmss')))
$ErrorActionPreference = 'Stop'
if ($Name -notmatch '^submission-[a-zA-Z0-9-]+$') { throw 'Use a simple submission-* folder name.' }
$projectRoot = Split-Path -Parent $PSScriptRoot
$outputRoot = Join-Path $projectRoot ".cache/$Name"
if (Test-Path -LiteralPath $outputRoot) { throw 'Candidate already exists; use a new name. No files were replaced.' }
Push-Location $projectRoot
try {
    $tracked = @(git ls-files)
    if ($LASTEXITCODE -ne 0) { throw 'Source repository is required for the explicit tracked-file export.' }
    $roots = @('README.md','LICENSE','THIRD_PARTY_NOTICES.md','.gitignore','build.gradle.kts','settings.gradle.kts','gradle.properties','gradlew','gradlew.bat')
    $docs = @('docs/REQUIREMENTS.md','docs/ARCHITECTURE.md','docs/DECISIONS.md','docs/evaluation-pipeline.md','docs/agent-workflow.md','docs/GIT-WORKFLOW.md','docs/workboard.md','docs/task1-readiness.md','docs/task2-readiness.md','docs/TASK1-RUN.md','docs/TASK2-RUN.md')
    $files = @($tracked | Where-Object {
        $_ -in $roots -or $_ -in $docs -or $_ -match '^(gradle|shared|androidApp|h5App|KuiklyChart|scripts)/' -or
        $_ -match '^docs/(interfaces|evidence/task1|evidence/task2)/' -or
        $_ -match '^docs/(plans|learning|handoffs|REVIEWS)/TASK[12]-(PLAN|LEARNING|CODE|TESTS)\.md$' -or
        $_ -match '^docs/decisions/ADR-01[34].*\.md$' -or $_ -match '^docs/submit/(SCORING-AUDIT|DELIVERY|TASK[12]-VIDEO)\.md$'
    })
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
    foreach ($dir in @('preview','videos','binaries')) { New-Item -ItemType Directory -Force (Join-Path $outputRoot $dir) | Out-Null }
    Copy-Item h5App/build/kotlin-webpack/js/productionExecutable/h5App.js (Join-Path $outputRoot 'preview/h5App.js')
    Copy-Item h5App/src/jsMain/resources/index.html (Join-Path $outputRoot 'preview/index.html')
    Copy-Item androidApp/build/outputs/apk/debug/androidApp-debug.apk (Join-Path $outputRoot 'binaries/android-debug-build-only.apk')
    foreach ($taskNumber in @(1,2)) {
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

两个Demo均为确定性历史Mock，不调用真实行情或AI模型。Android APK仅构建验证，设备/iOS/鸿蒙未运行验证。
这是本地候选，未发布或对外提交；个人讲述能力与老师实际评分未知。
'@ | Set-Content (Join-Path $outputRoot 'START-HERE.md')
    $manifest = @(Get-ChildItem -LiteralPath $outputRoot -File -Recurse | ForEach-Object {
        [pscustomobject]@{ path=[IO.Path]::GetRelativePath($outputRoot,$_.FullName).Replace('\','/');bytes=$_.Length;sha256=(Get-FileHash -LiteralPath $_.FullName -Algorithm SHA256).Hash }
    })
    [ordered]@{createdAt=[DateTimeOffset]::Now.ToString('o');repositoryHead=(git rev-parse HEAD);codeCommit='66c9ab0';snapshot='Selected tracked working-tree files; exact hashes below include retained documentation edits';files=$manifest} |
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
