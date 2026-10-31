# mmcUtils 一键发布脚本
# 用法: powershell -ExecutionPolicy Bypass -File release.ps1 -Version 1.5
# 可选: -SkipPush 只发布到 Maven Central，不推送 GitHub
# 换机后：脚本会自动检查并引导恢复 settings.xml 与 GPG 私钥（备份在 pushDoc 目录）
param(
    [Parameter(Mandatory=$true)][string]$Version,
    [switch]$SkipPush
)

$ErrorActionPreference = "Stop"
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$ProjectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ProjectDir

$GpgExe = "C:\Program Files (x86)\GnuPG\bin\gpg.exe"
$KeyFpr = "76DAC59E934821F7400C6C545BA49797266A5CDA"
$SettingsPath = Join-Path $env:USERPROFILE ".m2\settings.xml"
$BackupSettings = Join-Path $ProjectDir "pushDoc\mmcUtils_settings_backup.xml"
$BackupKey = Join-Path $ProjectDir "pushDoc\mmcUtils_gpg_private.asc"

Write-Host "===== mmcUtils 一键发布 v$Version =====" -ForegroundColor Cyan

# [1/5] 环境自检（换机后自动引导恢复）
Write-Host "[1/5] 环境自检..." -ForegroundColor Cyan

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Write-Error "未找到 mvn，请先安装 Maven 并配置 PATH"
}
if (-not (Test-Path $GpgExe)) {
    Write-Error "未找到 GPG 程序: $GpgExe，请安装 GnuPG"
}
if (-not (Test-Path $SettingsPath)) {
    Write-Warning "缺少 $SettingsPath"
    if (Test-Path $BackupSettings) {
        $ans = Read-Host "检测到备份，是否从 pushDoc 自动恢复?(y/n)"
        if ($ans -eq "y") {
            New-Item -ItemType Directory -Force -Path (Split-Path $SettingsPath) | Out-Null
            Copy-Item $BackupSettings $SettingsPath
            Write-Host "settings.xml 已恢复" -ForegroundColor Green
        } else { exit 1 }
    } else {
        Write-Error "找不到备份 settings，请手动配置"; exit 1
    }
}
& $GpgExe --batch --list-secret-keys $KeyFpr 2>$null | Out-Null
if ($LASTEXITCODE -ne 0) {
    Write-Warning "GPG 私钥缺失(指纹 $KeyFpr)"
    if (Test-Path $BackupKey) {
        $ans = Read-Host "检测到私钥备份，是否导入?(y/n)"
        if ($ans -eq "y") {
            & $GpgExe --batch --yes --import $BackupKey 2>$null | Out-Null
            if ($LASTEXITCODE -ne 0) { Write-Error "私钥导入失败，请手动导入"; exit 1 }
            Write-Host "GPG 私钥已导入" -ForegroundColor Green
        } else { exit 1 }
    } else {
        Write-Error "找不到私钥备份，无法继续"; exit 1
    }
}
Write-Host "环境检查通过" -ForegroundColor Green

# [2/5] 修改版本号
Write-Host "[2/5] 修改 pom.xml 版本 -> $Version ..." -ForegroundColor Cyan
$pomPath = Join-Path $ProjectDir "pom.xml"
$pom = [System.IO.File]::ReadAllText($pomPath)
$pattern = '(<groupId>io\.github\.foyuexiaoerbuyu</groupId>\s*<artifactId>mmcUtils</artifactId>\s*<version>)[^<]+(</version>)'
if ($pom -notmatch $pattern) {
    Write-Error "pom.xml 未找到项目版本号，请检查 groupId/artifactId"; exit 1
}
$newPom = [regex]::Replace($pom, $pattern, "`${1}$Version`${2}")
$utf8NoBom = New-Object System.Text.UTF8Encoding($false)
[System.IO.File]::WriteAllText($pomPath, $newPom, $utf8NoBom)
Write-Host "版本号已更新" -ForegroundColor Green

# [3/5] 构建并发布到 Maven Central
Write-Host "[3/5] mvn clean deploy ..." -ForegroundColor Cyan
mvn clean deploy
if ($LASTEXITCODE -ne 0) {
    Write-Error "mvn deploy 失败"; exit 1
}
Write-Host "Central 发布成功" -ForegroundColor Green

# [4/5] git 提交与打 tag
Write-Host "[4/5] git 提交与打 tag ..." -ForegroundColor Cyan
$changed = git status --porcelain
if ($changed) {
    git add -A
    git commit -m "v$Version"
    if ($LASTEXITCODE -ne 0) { Write-Error "git commit 失败"; exit 1 }
} else {
    Write-Warning "工作区无改动，跳过 commit"
}
if (git rev-parse -q --verify "refs/tags/v$Version") {
    Write-Warning "本地已存在 tag v$Version，跳过创建"
} else {
    git tag "v$Version"
}

# [5/5] 推送 GitHub
if ($SkipPush) {
    Write-Host "已跳过 GitHub 推送" -ForegroundColor Yellow
} else {
    Write-Host "[5/5] git push ..." -ForegroundColor Cyan
    git push origin HEAD --tags
    if ($LASTEXITCODE -ne 0) {
        Write-Error "git push 失败"; exit 1
    }
}
Write-Host "===== 发布完成 v$Version =====" -ForegroundColor Green
