#!/usr/bin/env pwsh
# Powered by Transoft, distributed in the GPL-3 protocol.
# https://github.com/LevelTranic

$script:Config = @{
    LibsDirPath = "sewlia-server", "build", "libs"
    JarExtension = "*.jar"
    GitDir = ".git"
    LogFile = "jar-cleanup.log"
}

function Write-Log {
    param(
        [string]$Message,
        [string]$Type = "INFO"
    )

    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    $logMessage = "[$timestamp] ${Type}: $Message"
    Write-Host $logMessage
    Add-Content -Path $Config.LogFile -Value $logMessage
}

function Test-GitCommand {
    try {
        $null = git --version
        return $true
    }
    catch {
        Write-Log "Git command is not available" "ERROR"
        return $false
    }
}

function Get-GitRepoName {
    try {
        return (git rev-parse --show-toplevel | Split-Path -Leaf).ToLower()
    }
    catch {
        Write-Log "Failed to get Git repository name: $_" "ERROR"
        return $null
    }
}

function Test-GitRepository {
    param (
        [string]$Path
    )

    try {
        return Test-Path (Join-Path -Path $Path -ChildPath $Config.GitDir)
    }
    catch {
        Write-Log "Failed to check Git repository: $_" "ERROR"
        return $false
    }
}

function Remove-JarFiles {
    param (
        [Parameter(Mandatory = $true)]
        [string]$JarDirectory,
        [Parameter(Mandatory = $true)]
        [string]$GitRepoName,
        [Parameter(Mandatory = $true)]
        [string[]]$Patterns
    )

    try {
        $deletedCount = 0
        $errorCount = 0

        foreach ($pattern in $Patterns) {
            Get-ChildItem -Path $JarDirectory -Filter $Config.JarExtension -ErrorAction Stop |
                Where-Object { $_.Name -match $pattern } |
                ForEach-Object {
                    try {
                        $filePath = $_.FullName
                        Write-Log "Attempting to delete: $filePath"
                        Remove-Item -Path $filePath -Force -ErrorAction Stop
                        $deletedCount++
                        Write-Log "Successfully deleted: $filePath"
                    }
                    catch {
                        $errorCount++
                        Write-Log "Failed to delete $filePath : $_" "ERROR"
                    }
                }
        }

        Write-Log "Operation completed - Files deleted: $deletedCount, Errors: $errorCount"
        return $deletedCount
    }
    catch {
        Write-Log "Error in Remove-JarFiles: $_" "ERROR"
        return -1
    }
}

function Start-Cleanup {
    if (-not (Test-GitCommand)) {
        exit 1
    }

    $currentDirectory = Get-Location
    if (-not (Test-GitRepository $currentDirectory)) {
        Write-Log "Current directory is not a Git repository." "ERROR"
        exit 1
    }

    $gitRepoName = Get-GitRepoName
    if (-not $gitRepoName) {
        exit 1
    }

    $jarDirectory = Join-Path -Path $currentDirectory -ChildPath ($Config.LibsDirPath -join [IO.Path]::DirectorySeparatorChar)
    if (-not (Test-Path $jarDirectory)) {
        Write-Log "Directory '$jarDirectory' does not exist." "ERROR"
        exit 1
    }

    $patterns = @(
        "$gitRepoName-bundler-(\d+\.\d+\.\d+)(-[^\-]+)*\.jar",
        "$gitRepoName-server-(\d+\.\d+\.\d+)(-[^\-]+)*\.jar"
    )

    $result = Remove-JarFiles -JarDirectory $jarDirectory -GitRepoName $gitRepoName -Patterns $patterns
    if ($result -eq -1) {
        exit 1
    }
}

Start-Cleanup