param(
    [int]$WarnPathLength = 220,
    [int]$FailPathLength = 245,
    [switch]$FailOnWarning
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$excludedDirNames = @(
    ".git",
    ".gradle",
    ".idea",
    ".kotlin",
    "build",
    "node_modules"
)

function Test-IsExcludedPath {
    param([string]$Path)

    foreach ($name in $excludedDirNames) {
        if ($Path -match "[\\/]$([Regex]::Escape($name))([\\/]|$)") {
            return $true
        }
    }
    return $false
}

function Get-RepoFiles {
    Get-ChildItem -Path $projectRoot -Recurse -File | Where-Object {
        -not (Test-IsExcludedPath $_.FullName)
    }
}

function Check-PathBudget {
    param([System.IO.FileInfo[]]$Files)

    $warnings = New-Object System.Collections.Generic.List[string]
    $failures = New-Object System.Collections.Generic.List[string]

    foreach ($file in $Files) {
        $len = $file.FullName.Length
        if ($len -ge $FailPathLength) {
            $failures.Add("PATH_FAIL|$len|$($file.FullName)")
        } elseif ($len -ge $WarnPathLength) {
            $warnings.Add("PATH_WARN|$len|$($file.FullName)")
        }
    }

    return [pscustomobject]@{
        Warnings = $warnings
        Failures = $failures
    }
}

function Check-DuplicateKotlinSymbols {
    param([System.IO.FileInfo[]]$Files)

    $ktFiles = $Files | Where-Object { $_.Extension -eq ".kt" }
    $symbolIndex = @{}

    foreach ($file in $ktFiles) {
        $packageName = ""
        $lineNo = 0
        $braceDepth = 0

        foreach ($line in Get-Content -Path $file.FullName) {
            $lineNo++

            $depthAtLineStart = $braceDepth

            if (-not $packageName) {
                $packageMatch = [regex]::Match($line, '^\s*package\s+([A-Za-z0-9_\.]+)')
                if ($packageMatch.Success) {
                    $packageName = $packageMatch.Groups[1].Value
                }
            }

            if ($depthAtLineStart -eq 0) {
                $decl = [regex]::Match(
                    $line,
                    '^\s*(?:public\s+|internal\s+|private\s+|protected\s+)?(?:data\s+|sealed\s+|enum\s+|annotation\s+)?(class|object|interface)\s+([A-Za-z_][A-Za-z0-9_]*)'
                )
                if ($decl.Success) {
                    $symbol = $decl.Groups[2].Value
                    $qualified = if ($packageName) { "$packageName.$symbol" } else { $symbol }

                    if (-not $symbolIndex.ContainsKey($qualified)) {
                        $symbolIndex[$qualified] = New-Object System.Collections.Generic.List[string]
                    }
                    $symbolIndex[$qualified].Add("$($file.FullName):$lineNo")
                }
            }

            $openCount = ([regex]::Matches($line, '\{')).Count
            $closeCount = ([regex]::Matches($line, '\}')).Count
            $braceDepth = [Math]::Max(0, $braceDepth + $openCount - $closeCount)
        }
    }

    $duplicates = New-Object System.Collections.Generic.List[string]
    foreach ($entry in $symbolIndex.GetEnumerator()) {
        if ($entry.Value.Count -gt 1) {
            $locations = ($entry.Value | Sort-Object -Unique) -join " | "
            $duplicates.Add("DUP_KT_SYMBOL|$($entry.Key)|$locations")
        }
    }

    return $duplicates
}

function Check-ServiceRunBlocking {
    param([System.IO.FileInfo[]]$Files)

    $findings = New-Object System.Collections.Generic.List[string]
    $serviceFiles = $Files | Where-Object {
        $_.Extension -eq ".kt" -and (
            $_.FullName -match '[\\/]runtime[\\/]service[\\/]src[\\/]service[\\/].*\.kt$' -or
            $_.FullName -match '[\\/]modules[\\/]runtime[\\/]service[\\/]src[\\/]service[\\/].*\.kt$'
        )
    }

    foreach ($file in $serviceFiles) {
        $lineNo = 0
        foreach ($line in Get-Content -Path $file.FullName) {
            $lineNo++
            if ($line -match '\brunBlocking\b') {
                $findings.Add("RUNBLOCKING_SERVICE|$($file.FullName):$lineNo|$($line.Trim())")
            }
        }
    }

    return $findings
}

function Check-ServiceUnsafeAssertions {
    param([System.IO.FileInfo[]]$Files)

    $findings = New-Object System.Collections.Generic.List[string]
    $serviceFiles = $Files | Where-Object {
        $_.Extension -eq ".kt" -and (
            $_.FullName -match '[\\/]runtime[\\/]service[\\/]src[\\/]service[\\/].*\.kt$' -or
            $_.FullName -match '[\\/]modules[\\/]runtime[\\/]service[\\/]src[\\/]service[\\/].*\.kt$'
        )
    }

    foreach ($file in $serviceFiles) {
        $lineNo = 0
        foreach ($line in Get-Content -Path $file.FullName) {
            $lineNo++
            if ($line -match '!!') {
                $findings.Add("UNSAFE_ASSERT_SERVICE|$($file.FullName):$lineNo|$($line.Trim())")
            }
        }
    }

    return $findings
}

function Check-NativeBridgeSurface {
    param(
        [string]$BridgeDirPath,
        [int]$WarnThreshold = 25
    )

    if (-not (Test-Path -LiteralPath $BridgeDirPath)) {
        return $null
    }

    $bridgeFiles = Get-ChildItem -Path $BridgeDirPath -Filter "*.kt" -File -Recurse
    $nativeEntryCount = 0

    foreach ($bridgeFile in $bridgeFiles) {
        $nativeEntryCount += (Select-String -Path $bridgeFile.FullName -Pattern '\bexternal\s+fun\s+native' | Measure-Object).Count
    }

    return [pscustomobject]@{
        DirectoryPath = $BridgeDirPath
        NativeEntryCount = $nativeEntryCount
        WarnThreshold = $WarnThreshold
        Warning = if ($nativeEntryCount -gt $WarnThreshold) {
            "BRIDGE_SURFACE_WARN|$nativeEntryCount|$BridgeDirPath"
        } else {
            $null
        }
    }
}

Write-Host "[repo-health] Scanning workspace: $projectRoot"
$files = Get-RepoFiles
Write-Host "[repo-health] Files scanned: $($files.Count)"

$pathReport = Check-PathBudget -Files $files
$duplicateSymbols = Check-DuplicateKotlinSymbols -Files $files
$serviceRunBlocking = Check-ServiceRunBlocking -Files $files
$serviceUnsafeAssertions = Check-ServiceUnsafeAssertions -Files $files
$bridgeSurface = Check-NativeBridgeSurface -BridgeDirPath (Join-Path $projectRoot "modules/core/src/core/bridge")

if ($pathReport.Warnings.Count -gt 0) {
    Write-Host "[repo-health] Path warnings: $($pathReport.Warnings.Count)"
    $pathReport.Warnings | Select-Object -First 50 | ForEach-Object { Write-Host $_ }
}

if ($pathReport.Failures.Count -gt 0) {
    Write-Host "[repo-health] Path failures: $($pathReport.Failures.Count)"
    $pathReport.Failures | Select-Object -First 50 | ForEach-Object { Write-Host $_ }
}

if ($duplicateSymbols.Count -gt 0) {
    Write-Host "[repo-health] Duplicate Kotlin symbols: $($duplicateSymbols.Count)"
    $duplicateSymbols | Select-Object -First 100 | ForEach-Object { Write-Host $_ }
}

if ($serviceRunBlocking.Count -gt 0) {
    Write-Host "[repo-health] Service-layer runBlocking findings: $($serviceRunBlocking.Count)"
    $serviceRunBlocking | Select-Object -First 100 | ForEach-Object { Write-Host $_ }
}

if ($serviceUnsafeAssertions.Count -gt 0) {
    Write-Host "[repo-health] Service-layer unsafe assertion findings: $($serviceUnsafeAssertions.Count)"
    $serviceUnsafeAssertions | Select-Object -First 100 | ForEach-Object { Write-Host $_ }
}

if ($bridgeSurface -and $bridgeSurface.Warning) {
    Write-Host "[repo-health] Native bridge surface warning"
    Write-Host $bridgeSurface.Warning
}

$hasAnyWarning = ($pathReport.Warnings.Count -gt 0) -or ($bridgeSurface -and $bridgeSurface.Warning)
$hasWarningFailure = $FailOnWarning -and $hasAnyWarning
$hasFailures = ($pathReport.Failures.Count -gt 0) -or ($duplicateSymbols.Count -gt 0) -or ($serviceRunBlocking.Count -gt 0) -or ($serviceUnsafeAssertions.Count -gt 0) -or $hasWarningFailure

if ($hasFailures) {
    Write-Host "[repo-health] FAILED"
    exit 1
}

Write-Host "[repo-health] PASSED"
