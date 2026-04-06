param(
    [Parameter(Position = 0)]
    [ValidateSet("alpha", "Alpha", "meta", "Meta", "smart", "Smart")]
    [string]$Choice
)

$ErrorActionPreference = "Stop"

if (-not $Choice) {
    Write-Host "Usage: sync-kernel.ps1 <alpha|meta|smart>"
    exit 1
}

$projectRoot = Split-Path -Parent $PSScriptRoot
$kernelProperties = Join-Path $projectRoot "config/kernel.properties"
$golangRoot = Join-Path $projectRoot "lib\mihomo"
$golangMain = Join-Path $projectRoot "lib\native\go"
$mihomoDir = Join-Path $golangRoot "mihomo"

switch ($Choice.ToLowerInvariant()) {
    "alpha" {
        $repoUrl = "https://github.com/MetaCubeX/mihomo.git"
        $releaseTag = "Prerelease-Alpha"
        $releaseApiUrl = $null
        $versionSuffix = ""
    }
    "meta" {
        $repoUrl = "https://github.com/MetaCubeX/mihomo.git"
        $releaseTag = $null
        $releaseApiUrl = "https://api.github.com/repos/MetaCubeX/mihomo/releases/latest"
        $versionSuffix = ""
    }
    "smart" {
        $repoUrl = "https://github.com/vernesong/mihomo.git"
        $releaseTag = "Prerelease-Alpha"
        $releaseApiUrl = $null
        $versionSuffix = "-Smart"
    }
}

function Require-Command {
    param([string]$Name)

    if (-not (Get-Command $Name -ErrorAction SilentlyContinue)) {
        throw "Missing required command: $Name"
    }
}

function Resolve-ReleaseTag {
    if ($releaseTag) {
        return $releaseTag
    }

    if (-not $releaseApiUrl) {
        throw "Failed to resolve release tag: no release API configured."
    }

    try {
        $headers = @{ Accept = "application/vnd.github+json" }
        $response = Invoke-RestMethod -Uri $releaseApiUrl -Headers $headers -ErrorAction Stop
        if ($response.tag_name) {
            return [string]$response.tag_name
        }
    } catch {
        $curl = Get-Command curl.exe -ErrorAction SilentlyContinue
        if (-not $curl) {
            throw "Failed to resolve release tag from $releaseApiUrl and curl.exe is unavailable."
        }

        $json = & $curl.Source -fsSL -H "Accept: application/vnd.github+json" $releaseApiUrl
        if ($LASTEXITCODE -ne 0) {
            throw "Failed to resolve release tag from $releaseApiUrl"
        }

        if ($json -match '"tag_name"\s*:\s*"([^"]+)"') {
            return $Matches[1]
        }
    }

    throw "Failed to parse release tag from $releaseApiUrl"
}

function Resolve-ReleaseRevision {
    param([string]$Tag)

    $peeledLine = & git ls-remote $repoUrl "refs/tags/$Tag^{}" | Select-Object -First 1
    $directLine = & git ls-remote $repoUrl "refs/tags/$Tag" | Select-Object -First 1
    $peeledRevision = if ($peeledLine) { ($peeledLine -split "`t")[0] } else { "" }
    $directRevision = if ($directLine) { ($directLine -split "`t")[0] } else { "" }
    $revision = if ($peeledRevision) { $peeledRevision } else { $directRevision }

    if (-not $revision) {
        throw "Failed to resolve revision for tag $Tag from $repoUrl"
    }

    return $revision
}

function Update-KernelProperties {
    $lines = Get-Content -Path $kernelProperties -ErrorAction Stop
    $seenRepo = $false
    $seenBranch = $false
    $seenSuffix = $false
    $updated = New-Object System.Collections.Generic.List[string]

    foreach ($line in $lines) {
        if ($line -like "external.mihomo.repo=*") {
            $updated.Add("external.mihomo.repo=$repoUrl")
            $seenRepo = $true
            continue
        }
        if ($line -like "external.mihomo.branch=*") {
            $updated.Add("external.mihomo.branch=$releaseTag")
            $seenBranch = $true
            continue
        }
        if ($line -like "external.mihomo.suffix=*") {
            $updated.Add("external.mihomo.suffix=$versionSuffix")
            $seenSuffix = $true
            continue
        }
        $updated.Add($line)
    }

    if (-not $seenRepo) {
        $updated.Add("external.mihomo.repo=$repoUrl")
    }
    if (-not $seenBranch) {
        $updated.Add("external.mihomo.branch=$releaseTag")
    }
    if (-not $seenSuffix) {
        $updated.Add("external.mihomo.suffix=$versionSuffix")
    }

    Set-Content -Path $kernelProperties -Value $updated -Encoding UTF8
    Write-Host "Updated kernel.properties -> repo=$repoUrl tag=$releaseTag revision=$releaseRevision suffix=$versionSuffix"
}

function Sync-Repo {
    if (Test-Path -LiteralPath $mihomoDir) {
        Write-Host "Removing existing directory $mihomoDir"
        Remove-Item -LiteralPath $mihomoDir -Recurse -Force
    }

    Write-Host "Cloning $repoUrl -> $mihomoDir"
    & git clone --no-checkout $repoUrl $mihomoDir
    if ($LASTEXITCODE -ne 0) {
        throw "git clone failed"
    }

    & git -C $mihomoDir checkout --force $releaseRevision
    if ($LASTEXITCODE -ne 0) {
        throw "git checkout failed"
    }
}

function Run-GoModTidy {
    param([string]$TargetDir)

    $goMod = Join-Path $TargetDir "go.mod"
    if (-not (Test-Path -LiteralPath $goMod)) {
        Write-Host "Skipping tidy for $TargetDir (no go.mod found)"
        return
    }

    Write-Host "Running go mod tidy in $TargetDir"
    Push-Location $TargetDir
    try {
        & go mod tidy
        if ($LASTEXITCODE -ne 0) {
            throw "go mod tidy failed in $TargetDir"
        }
    } finally {
        Pop-Location
    }
}

Require-Command git
Require-Command go
$releaseTag = Resolve-ReleaseTag
$releaseRevision = Resolve-ReleaseRevision -Tag $releaseTag
Update-KernelProperties
Sync-Repo
Run-GoModTidy -TargetDir $golangRoot
Run-GoModTidy -TargetDir $golangMain

Write-Host "Done: selected $Choice (tag=$releaseTag revision=$releaseRevision)"
