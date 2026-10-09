# Maven Setup Script for Windows

$DownloadUrl = "https://dlcdn.apache.org/maven/maven-3/3.10.0/binaries/apache-maven-3.10.0-bin.zip"
$DownloadPath = "$env:USERPROFILE\Downloads\apache-maven-3.10.0-bin.zip"
$ExtractPath = "$env:USERPROFILE\Downloads"
$MavenHome = "$env:USERPROFILE\Downloads\apache-maven-3.10.0"
$MavenBin = "$MavenHome\bin"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Maven Setup for ForgeFit Project" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan

# Step 1: Download Maven
Write-Host "`n[1/4] Downloading Maven 3.10.0..." -ForegroundColor Yellow

if (Test-Path $DownloadPath) {
    Write-Host "Maven already downloaded at: $DownloadPath" -ForegroundColor Green
} else {
    try {
        Write-Host "Downloading from: $DownloadUrl" -ForegroundColor Gray
        $ProgressPreference = 'SilentlyContinue'
        Invoke-WebRequest -Uri $DownloadUrl -OutFile $DownloadPath
        Write-Host "Download complete!" -ForegroundColor Green
    } catch {
        Write-Host "Download failed: $_" -ForegroundColor Red
        Write-Host "Please download manually from: https://maven.apache.org/download.cgi" -ForegroundColor Yellow
        exit 1
    }
}

# Step 2: Extract Maven
Write-Host "`n[2/4] Extracting Maven..." -ForegroundColor Yellow

if (Test-Path $MavenHome) {
    Write-Host "Maven already extracted at: $MavenHome" -ForegroundColor Green
} else {
    try {
        Expand-Archive -Path $DownloadPath -DestinationPath $ExtractPath -Force
        Write-Host "Extraction complete!" -ForegroundColor Green
    } catch {
        Write-Host "Extraction failed: $_" -ForegroundColor Red
        exit 1
    }
}

# Step 3: Verify Maven
Write-Host "`n[3/4] Verifying Maven installation..." -ForegroundColor Yellow

if (-not (Test-Path "$MavenBin\mvn.cmd")) {
    Write-Host "Maven bin/mvn.cmd not found!" -ForegroundColor Red
    exit 1
}
Write-Host "Maven found at: $MavenHome" -ForegroundColor Green

# Step 4: Set Environment Variables
Write-Host "`n[4/4] Setting environment variables..." -ForegroundColor Yellow

try {
    [Environment]::SetEnvironmentVariable("MAVEN_HOME", $MavenHome, [EnvironmentVariableTarget]::User)
    Write-Host "Set MAVEN_HOME=$MavenHome" -ForegroundColor Green

    $CurrentPath = [Environment]::GetEnvironmentVariable("PATH", [EnvironmentVariableTarget]::User)
    
    if ($CurrentPath -notlike "*$MavenBin*") {
        $NewPath = "$CurrentPath;$MavenBin"
        [Environment]::SetEnvironmentVariable("PATH", $NewPath, [EnvironmentVariableTarget]::User)
        Write-Host "Added to PATH: $MavenBin" -ForegroundColor Green
    } else {
        Write-Host "Already in PATH" -ForegroundColor Green
    }
} catch {
    Write-Host "Failed to set environment variables: $_" -ForegroundColor Red
    exit 1
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "Maven Setup Complete!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan

Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "1. Close this PowerShell window and open a NEW one" -ForegroundColor White
Write-Host "2. Run: mvn -version" -ForegroundColor White
Write-Host "3. Run: mvn clean install" -ForegroundColor White
Write-Host "4. Run: mvn spring-boot:run" -ForegroundColor White
Write-Host "`nThen access: http://localhost:8080/login.html" -ForegroundColor Cyan
