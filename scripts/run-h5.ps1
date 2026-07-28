$ErrorActionPreference = "Stop"
$repoRoot = Split-Path -Parent $PSScriptRoot
$projectJdk = Join-Path $repoRoot ".cache\jdk17"
if (-not (Test-Path -LiteralPath (Join-Path $projectJdk "bin\java.exe"))) {
    throw "未找到项目内 JDK 17：$projectJdk。请先在该目录准备完整 JDK 17。"
}
$env:JAVA_HOME = $projectJdk
$env:PATH = "$projectJdk\bin;$env:PATH"
$env:GRADLE_USER_HOME = Join-Path $repoRoot ".cache\gradle"

Push-Location $repoRoot
try {
    & .\gradlew.bat :h5App:jsBrowserDevelopmentRun --continuous --no-daemon --max-workers=1 -Pkotlin.compiler.execution.strategy=in-process
    exit $LASTEXITCODE
} finally {
    Pop-Location
}
