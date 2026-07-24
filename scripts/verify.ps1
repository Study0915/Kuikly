param(
    [switch]$SkipAndroid
)

$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$projectJdk = Join-Path $repoRoot ".cache\jdk17"
if (-not (Test-Path -LiteralPath (Join-Path $projectJdk "bin\java.exe"))) {
    throw "未找到项目内 JDK 17：$projectJdk。请先在该目录准备完整 JDK 17。"
}
$env:JAVA_HOME = $projectJdk
$env:PATH = "$projectJdk\bin;$env:PATH"
$env:GRADLE_USER_HOME = Join-Path $repoRoot ".cache\gradle"
$env:ANDROID_USER_HOME = Join-Path $repoRoot ".cache\android"
$env:COREPACK_HOME = Join-Path $repoRoot ".cache\npm\corepack"
$env:YARN_CACHE_FOLDER = Join-Path $repoRoot ".cache\npm\yarn"
$env:npm_config_cache = Join-Path $repoRoot ".cache\npm"
$env:npm_config_offline = "false"

Push-Location $repoRoot
try {
    $gradleArgs = @(
        "--no-daemon",
        "--max-workers=1",
        "-Pkotlin.compiler.execution.strategy=in-process",
        "-Dorg.gradle.jvmargs=-Xmx512m -Xms128m -Xss256k -XX:ActiveProcessorCount=1 -XX:TieredStopAtLevel=1 -Dfile.encoding=UTF-8",
        "--stacktrace"
    )
    & .\gradlew.bat :KuiklyChart:jsNodeTest :shared:compileKotlinJs :h5App:jsBrowserProductionWebpack @gradleArgs
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    if (-not $SkipAndroid) {
        & .\gradlew.bat :androidApp:assembleDebug @gradleArgs
        if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
    }
} finally {
    Pop-Location
}
