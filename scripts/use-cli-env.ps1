param(
    [switch]$SkipAndroidRequirement
)

$ErrorActionPreference = "Stop"

$repoRoot = Split-Path -Parent $PSScriptRoot
$projectCache = Join-Path $repoRoot ".cache"
$projectJdk = Join-Path $projectCache "jdk17"
$projectNode = Join-Path $projectCache "node"
$projectAndroidSdk = Join-Path $projectCache "android-sdk"
$projectAndroidHome = Join-Path $projectCache "android"
$projectUserHome = Join-Path $projectCache "user-home"

$requiredFiles = @(
    (Join-Path $projectJdk "bin\java.exe"),
    (Join-Path $projectNode "node.exe")
)
if (-not $SkipAndroidRequirement) {
    $requiredFiles += Join-Path $projectAndroidSdk "cmdline-tools\latest\bin\sdkmanager.bat"
}
foreach ($requiredFile in $requiredFiles) {
    if (-not (Test-Path -LiteralPath $requiredFile)) {
        throw "缺少项目内 CLI：$requiredFile。请先执行 .\scripts\bootstrap-cli.ps1。"
    }
}

$directories = @(
    (Join-Path $projectCache "gradle"),
    $projectAndroidHome,
    (Join-Path $projectUserHome ".android"),
    (Join-Path $projectCache "npm"),
    (Join-Path $projectCache "xdg\cache"),
    (Join-Path $projectCache "xdg\config"),
    (Join-Path $projectCache "xdg\data"),
    (Join-Path $projectCache "xdg\state")
)
foreach ($directory in $directories) {
    New-Item -ItemType Directory -Force -Path $directory | Out-Null
}

$env:JAVA_HOME = $projectJdk
$env:GRADLE_USER_HOME = Join-Path $projectCache "gradle"
$env:COREPACK_HOME = Join-Path $projectCache "npm\corepack"
$env:YARN_CACHE_FOLDER = Join-Path $projectCache "npm\yarn"
$env:npm_config_cache = Join-Path $projectCache "npm"
$env:npm_config_offline = "false"
$env:XDG_CACHE_HOME = Join-Path $projectCache "xdg\cache"
$env:XDG_CONFIG_HOME = Join-Path $projectCache "xdg\config"
$env:XDG_DATA_HOME = Join-Path $projectCache "xdg\data"
$env:XDG_STATE_HOME = Join-Path $projectCache "xdg\state"
Remove-Item Env:ANDROID_SDK_HOME -ErrorAction SilentlyContinue

if (Test-Path -LiteralPath (Join-Path $projectAndroidSdk "cmdline-tools\latest\bin\sdkmanager.bat")) {
    $env:ANDROID_HOME = $projectAndroidSdk
    $env:ANDROID_SDK_ROOT = $projectAndroidSdk
    $env:ANDROID_USER_HOME = $projectAndroidHome
    $env:ANDROID_EMULATOR_HOME = $projectAndroidHome
    $env:ANDROID_AVD_HOME = Join-Path $projectAndroidHome "avd"
}

$pathEntries = @(
    (Join-Path $projectJdk "bin"),
    $projectNode
)
if (Test-Path -LiteralPath $projectAndroidSdk) {
    $pathEntries += Join-Path $projectAndroidSdk "cmdline-tools\latest\bin"
    $pathEntries += Join-Path $projectAndroidSdk "platform-tools"
}
$env:PATH = ($pathEntries + $env:PATH.Split(";")) -join ";"

$KuiklyGradleArgs = @(
    "--no-daemon",
    "--max-workers=1",
    "-Pkotlin.compiler.execution.strategy=in-process",
    "-Duser.home=$projectUserHome",
    "-Dorg.gradle.jvmargs=-Xmx512m -Xms128m -Xss256k -XX:ActiveProcessorCount=1 -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8",
    "--stacktrace"
)
