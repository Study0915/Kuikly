param(
    [string]$NodeSourceDirectory
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$cacheRoot = Join-Path $repoRoot ".cache"
$nodeTarget = Join-Path $cacheRoot "node"

if (-not (Test-Path -LiteralPath (Join-Path $cacheRoot "jdk17\bin\java.exe"))) {
    throw "缺少 $cacheRoot\jdk17。请把完整 JDK 17 解压到该目录；不得安装到 C 盘。"
}
if (-not (Test-Path -LiteralPath (Join-Path $cacheRoot "android-sdk\cmdline-tools\latest\bin\sdkmanager.bat"))) {
    throw "缺少 $cacheRoot\android-sdk。请把 Android command-line tools 解压到该目录；不得安装到 C 盘。"
}

if (-not (Test-Path -LiteralPath (Join-Path $nodeTarget "node.exe"))) {
    if ([string]::IsNullOrWhiteSpace($NodeSourceDirectory)) {
        $nodeCommand = Get-Command node -ErrorAction SilentlyContinue
        if ($null -eq $nodeCommand) {
            throw "未找到可复制的 Node.js。请用 -NodeSourceDirectory 指向非 C 盘的完整 Node.js 目录。"
        }
        $NodeSourceDirectory = Split-Path -Parent $nodeCommand.Source
    }
    $source = (Resolve-Path -LiteralPath $NodeSourceDirectory).Path
    New-Item -ItemType Directory -Force -Path $nodeTarget | Out-Null
    Get-ChildItem -Force -LiteralPath $source | Copy-Item -Destination $nodeTarget -Recurse -Force
}

. (Join-Path $PSScriptRoot "use-cli-env.ps1")

# The Windows Kotlin/JS build explicitly uses this workspace Yarn executable.
$workspaceYarn = Join-Path $cacheRoot 'npm\yarn-runtime\node_modules\.bin\yarn.cmd'
if (-not (Test-Path -LiteralPath $workspaceYarn)) {
    & (Join-Path $nodeTarget 'npm.cmd') install --prefix (Join-Path $cacheRoot 'npm\yarn-runtime') --no-audit --no-fund --ignore-scripts yarn@1.22.17
    if ($LASTEXITCODE -ne 0) { throw 'Workspace Yarn installation failed.' }
}

$requiredSdkPackages = @(
    "build-tools;30.0.3",
    "platform-tools",
    "platforms;android-33",
    "platforms;android-34"
)
$installed = & sdkmanager.bat --sdk_root=$projectAndroidSdk --list_installed 2>&1 | Out-String
$missingSdkPackages = [System.Collections.Generic.List[string]]::new()
foreach ($sdkPackage in $requiredSdkPackages) {
    if ($installed -notmatch [regex]::Escape($sdkPackage)) {
        $missingSdkPackages.Add($sdkPackage)
    }
}
if ($missingSdkPackages.Count -gt 0) {
    Write-Host "正在把缺失的 Android SDK 包安装到 $projectAndroidSdk"
    & sdkmanager.bat --sdk_root=$projectAndroidSdk --install @missingSdkPackages
    if ($LASTEXITCODE -ne 0) {
        throw "Android SDK 包安装失败，exit $LASTEXITCODE。"
    }
}

& (Join-Path $PSScriptRoot "doctor.ps1")
exit $LASTEXITCODE
