$ErrorActionPreference = "Stop"
. (Join-Path $PSScriptRoot "use-cli-env.ps1")

$failures = [System.Collections.Generic.List[string]]::new()
function Test-CommandResult {
    param([string]$Name, [scriptblock]$Command)
    try {
        $output = & $Command 2>&1
        if ($LASTEXITCODE -ne 0) { throw "exit $LASTEXITCODE" }
        $value = if ($Name -eq "Gradle Wrapper") {
            $output | Where-Object { [string]$_ -match '^Gradle\s+\d' } | Select-Object -First 1
        } else {
            $output | Where-Object {
                -not [string]::IsNullOrWhiteSpace([string]$_) -and
                [string]$_ -notmatch '^Picked up JAVA_TOOL_OPTIONS:'
            } | Select-Object -First 1
        }
        if ([string]::IsNullOrWhiteSpace([string]$value)) { throw "未找到版本输出" }
        Write-Host "[OK] ${Name}: $value"
    } catch {
        $failures.Add("${Name}: $($_.Exception.Message)")
        Write-Host "[FAIL] ${Name}: $($_.Exception.Message)"
    }
}

Test-CommandResult "JDK" { java -version }
Test-CommandResult "Node" { node --version }
Test-CommandResult "npm" { npm.cmd --version }
Test-CommandResult "Gradle Wrapper" { & (Join-Path $repoRoot "gradlew.bat") --version @KuiklyGradleArgs }

$sdkList = & sdkmanager.bat --sdk_root=$projectAndroidSdk --list_installed 2>&1 | Out-String
if ($LASTEXITCODE -ne 0) { $failures.Add("sdkmanager --list_installed: exit $LASTEXITCODE") }
foreach ($sdkPackage in @("build-tools;30.0.3", "platform-tools", "platforms;android-33", "platforms;android-34")) {
    if ($sdkList -match [regex]::Escape($sdkPackage)) {
        Write-Host "[OK] Android SDK: $sdkPackage"
    } else {
        $failures.Add("Android SDK: missing $sdkPackage")
        Write-Host "[FAIL] Android SDK: missing $sdkPackage"
    }
}

$drive = Get-PSDrive -Name ([System.IO.Path]::GetPathRoot($repoRoot).TrimEnd(":\"))
Write-Host ("[INFO] Workspace free space: {0:N1} GB" -f ($drive.Free / 1GB))
foreach ($name in @("JAVA_HOME", "GRADLE_USER_HOME", "npm_config_cache", "npm_config_prefix", "TEMP", "TMP", "PLAYWRIGHT_BROWSERS_PATH", "ANDROID_HOME", "ANDROID_USER_HOME")) {
    $value = [Environment]::GetEnvironmentVariable($name, "Process")
    if ([string]::IsNullOrWhiteSpace($value) -or -not [IO.Path]::GetFullPath($value).StartsWith($projectCache + [IO.Path]::DirectorySeparatorChar, [StringComparison]::OrdinalIgnoreCase)) {
        $failures.Add("Workspace isolation: $name is not under the project cache")
    }
}
Write-Host "[INFO] ADB 设备验证未执行：当前 Windows platform-tools 会访问用户配置目录；在保持 C 盘零写入约束时仅验收 Android 构建。"

if ($failures.Count -gt 0) {
    $failures | ForEach-Object { Write-Error $_ }
    exit 1
}
Write-Host "CLI_ENV_OK"
