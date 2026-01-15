# Script to refresh Java Language Server in VS Code
Write-Host "Refreshing Java Language Server..." -ForegroundColor Yellow

# Find Java workspace cache
$workspaceCache = "$env:APPDATA\Code\User\workspaceStorage"
$projectHash = (Get-FileHash -Path "D:\SocialNetwork" -Algorithm MD5).Hash.Substring(0,8)

Write-Host "Project hash: $projectHash" -ForegroundColor Cyan

# Clean workspace storage (if exists)
if (Test-Path $workspaceCache) {
    $workspaceFolders = Get-ChildItem -Path $workspaceCache -Directory -ErrorAction SilentlyContinue
    Write-Host "Found $($workspaceFolders.Count) workspace cache folders" -ForegroundColor Cyan
}

# Clean .metadata folder in project (if exists)
$metadataPath = "D:\SocialNetwork\.metadata"
if (Test-Path $metadataPath) {
    Write-Host "Removing .metadata folder..." -ForegroundColor Yellow
    Remove-Item -Path $metadataPath -Recurse -Force -ErrorAction SilentlyContinue
}

# Clean .settings folder (Eclipse-style, if exists)
$settingsPath = "D:\SocialNetwork\.settings"
if (Test-Path $settingsPath) {
    Write-Host "Removing .settings folder..." -ForegroundColor Yellow
    Remove-Item -Path $settingsPath -Recurse -Force -ErrorAction SilentlyContinue
}

Write-Host "`nDone! Now follow these steps in VS Code:" -ForegroundColor Green
Write-Host "1. Press Ctrl+Shift+P" -ForegroundColor White
Write-Host "2. Type: 'Java: Clean Java Language Server Workspace'" -ForegroundColor White
Write-Host "3. Select it and wait for rebuild" -ForegroundColor White
Write-Host "4. Or type: 'Developer: Reload Window'" -ForegroundColor White

