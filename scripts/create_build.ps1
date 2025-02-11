#!/usr/bin/env pwsh

param(
    [string]$mcVersion,
    [string]$token,
    [string]$channel = "default"
)

$SCRIPT_CONFIG = @{
    ApiBaseUrl = "https://mars.tranic.one"
    BuildLibsPath = "sewlia-server/build/libs"
    UserAgent = "Mars-Utils/v1"
}

function Assert-GitRepository {
    $currentDirectory = Get-Location
    if (-not (Test-Path "$currentDirectory\.git")) {
        Exit-WithError "Current directory is not a Git repository."
    }
}

function Assert-ValidToken {
    param([string]$token)

    if (-not $token) {
        $token = $env:MARS_TOKEN
        if (-not $token) {
            Exit-WithError 'Mars Token cannot be empty, you need to set $MARS_TOKEN="YOUR_MARS_API_TOKEN" in the environment variable'
        }
    }
    return $token
}

function Exit-WithError {
    param([string]$message)
    Write-Error $message
    exit 1
}

function Get-RepositoryInfo {
    $repoNameClassic = git rev-parse --show-toplevel | Split-Path -Leaf
    return @{
        ClassicName = $repoNameClassic
        NormalizedName = $repoNameClassic.ToLower()
    }
}

function Get-GitInfo {
    param([string]$repoName)

    $commit = git log -1 --pretty=format:"%H"
    return @{
        Commit = $commit
        Title = git log -1 --pretty=format:"%s"
        Message = (git log -1 --pretty=format:"%B") -replace "`r`n", "\n" -replace "`n", "\n"
        RepoName = $repoName
    }
}

function Get-BuildInfo {
    param(
        [string]$repoName,
        [string]$repoNameClassic,
        [string]$mcVersion
    )

    $files = Get-ChildItem -Path "$($SCRIPT_CONFIG.BuildLibsPath)/*.jar"
    if ($files.Count -eq 0) {
        Exit-WithError "No jar files found in $($SCRIPT_CONFIG.BuildLibsPath)"
    }

    $fileInfo = @{}
    foreach ($file in $files) {
        if ($file.Name -match "$repoName-paperclip-(\d+\.\d+\.\d+)(-[^\-]+)*\.jar") {
            $commitHash = (git log -1 --pretty=format:"%H")[0..6] -join ""
            $fileHash = Get-FileHash $file.FullName SHA256

            $fileInfo["paperclip"] = @{
                name = $file.Name
                sha256 = $fileHash.Hash
                url = "https://github.com/404Setup/$($repoNameClassic)/releases/download/$mcVersion-$commitHash/$($file.Name)"
            }
        }
    }

    if ($fileInfo.Count -eq 0) {
        Exit-WithError "No valid jar files found in $($SCRIPT_CONFIG.BuildLibsPath)"
    }

    return @{
        Files = $fileInfo
        Version = $mcVersion
        Family = $mcVersion -replace "\.\d+$"
    }
}

function Invoke-MarsApi {
    param (
        [string]$endpoint,
        [string]$token,
        [hashtable]$data
    )

    $headers = @{
        "Cookie" = "mars_token=$token"
        "User-Agent" = $SCRIPT_CONFIG.UserAgent
        "Content-Type" = "application/json"
    }

    try {
        $url = "$($SCRIPT_CONFIG.ApiBaseUrl)$endpoint"
        $response = Invoke-RestMethod -Uri $url -Method Post -Body ($data | ConvertTo-Json -Depth 10) -Headers $headers

        if ($response -is [string]) {
            Exit-WithError "API response is not a valid JSON object."
        }
        return $response
    }
    catch {
        Exit-WithError "Failed to send request to $url. Error: $_"
    }
}

Assert-GitRepository
$token = Assert-ValidToken $token
$repoInfo = Get-RepositoryInfo
$gitInfo = Get-GitInfo -repoName $repoInfo.NormalizedName
$buildInfo = Get-BuildInfo -repoName $repoInfo.NormalizedName -repoNameClassic $repoInfo.ClassicName -mcVersion $mcVersion

$buildData = @{
    project = $repoInfo.NormalizedName
    version = $buildInfo.Version
    family = $buildInfo.Family
    channel = $channel
    changes = @(
        @{
            commit = $gitInfo.Commit
            summary = $gitInfo.Title
            message = $gitInfo.Message
        }
    )
}

$buildResponse = Invoke-MarsApi -endpoint "/v2/new/build" -token $token -data $buildData
if ($buildResponse.build_id -eq 0) {
    Exit-WithError "Failed to get valid build_id from API response"
}

$uploadData = @{
    project = $repoInfo.NormalizedName
    version = $buildInfo.Version
    build = $buildResponse.build_id
    file = $buildInfo.Files
}

$uploadResponse = Invoke-MarsApi -endpoint "/v2/new/external_download" -token $token -data $uploadData
if (-not $uploadResponse.result) {
    Exit-WithError $uploadResponse
}

Write-Host "Server: $($uploadResponse.result)"