param(
    [string]$PackageName = "com.github.yumelira.yumebox",
    [int]$Rounds = 20,
    [int]$EventsPerRound = 300,
    [int]$ThrottleMs = 35,
    [int]$MonkeyTimeoutSec = 180,
    [int]$SeedBase = 20260405,
    [string]$OutputDir = "build/llm-runtime-fuzz",
    [switch]$InstallDebug,
    [switch]$ClearAppData,
    [switch]$StopOnFailure
)

$ErrorActionPreference = "Stop"

function Resolve-AdbPath {
    if ($env:ANDROID_SDK_ROOT) {
        $candidate = Join-Path $env:ANDROID_SDK_ROOT "platform-tools\adb.exe"
        if (Test-Path $candidate) { return $candidate }
    }

    $fallback = Join-Path $env:LOCALAPPDATA "Android\Sdk\platform-tools\adb.exe"
    if (Test-Path $fallback) { return $fallback }

    throw "adb.exe not found. Set ANDROID_SDK_ROOT or install Android platform-tools."
}

function Get-ConnectedDevices {
    param([string]$AdbPath)

    $lines = & $AdbPath devices
    return $lines |
        Where-Object { $_ -match "\tdevice$" } |
        ForEach-Object { ($_ -split "\t")[0] }
}

function New-OutputLayout {
    param([string]$Root)

    $resolved = Join-Path (Get-Location) $Root
    $logs = Join-Path $resolved "logs"
    New-Item -ItemType Directory -Path $logs -Force | Out-Null
    return [PSCustomObject]@{
        Root = $resolved
        Logs = $logs
    }
}

function Find-Keywords {
    param(
        [string]$LogPath,
        [string[]]$Patterns
    )

    $hits = @()
    foreach ($pattern in $Patterns) {
        $match = Select-String -Path $LogPath -Pattern $pattern -SimpleMatch -CaseSensitive:$false
        if ($match) {
            $hits += $pattern
        }
    }
    return $hits | Select-Object -Unique
}

function Invoke-MonkeyWithTimeout {
    param(
        [string]$AdbPath,
        [string]$Device,
        [string]$Package,
        [int]$Seed,
        [int]$Events,
        [int]$Throttle,
        [int]$TimeoutSec
    )

    $stdoutFile = [System.IO.Path]::GetTempFileName()
    $stderrFile = [System.IO.Path]::GetTempFileName()

    try {
        $args = @(
            "-s", $Device,
            "shell", "monkey",
            "-p", $Package,
            "--throttle", $Throttle,
            "--ignore-crashes",
            "--ignore-timeouts",
            "--ignore-security-exceptions",
            "-s", $Seed,
            $Events
        )

        $proc = Start-Process -FilePath $AdbPath -ArgumentList $args -NoNewWindow -PassThru -RedirectStandardOutput $stdoutFile -RedirectStandardError $stderrFile
        $finishedInTime = $true
        try {
            Wait-Process -Id $proc.Id -Timeout $TimeoutSec
        } catch {
            $finishedInTime = $false
            Write-Host "[llm-fuzz] monkey timeout after ${TimeoutSec}s, killing process and continuing"
            try { Stop-Process -Id $proc.Id -Force -ErrorAction Stop } catch {}
            & $AdbPath -s $Device shell "pkill -f monkey" | Out-Null
        }

        $stdout = if (Test-Path $stdoutFile) { Get-Content $stdoutFile } else { @() }
        $stderr = if (Test-Path $stderrFile) { Get-Content $stderrFile } else { @() }

        $exitCode = if ($finishedInTime) { $proc.ExitCode } else { 124 }
        if (-not $finishedInTime) {
            $stderr += "monkey_timeout=true"
        }

        return [PSCustomObject]@{
            ExitCode = $exitCode
            Output = @($stdout + $stderr)
            TimedOut = (-not $finishedInTime)
        }
    } finally {
        Remove-Item $stdoutFile -ErrorAction SilentlyContinue
        Remove-Item $stderrFile -ErrorAction SilentlyContinue
    }
}

$patterns = @(
    "FATAL EXCEPTION",
    "UnsatisfiedLinkError",
    "No implementation found",
    "RuntimeGatewayException",
    "RUNTIME_START_FAILED",
    "ANR in",
    "Input dispatching timed out",
    "SIGSEGV",
    "SIGABRT"
)

$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$adb = Resolve-AdbPath
$layout = New-OutputLayout -Root $OutputDir
$summaryPath = Join-Path $layout.Root "summary.json"
$reportPath = Join-Path $layout.Root "report.txt"

if ($InstallDebug) {
    Write-Host "[llm-fuzz] Installing debug apk..."
    & .\gradlew.bat --no-daemon installDebug
}

$devices = Get-ConnectedDevices -AdbPath $adb
if (-not $devices -or $devices.Count -eq 0) {
    throw "No connected Android device/emulator found."
}

$device = $devices[0]
Write-Host "[llm-fuzz] Using device: $device"

if ($ClearAppData) {
    Write-Host "[llm-fuzz] Clearing app data for $PackageName"
    & $adb -s $device shell pm clear $PackageName | Out-Null
}

$results = New-Object System.Collections.Generic.List[object]
$startAt = Get-Date

for ($round = 1; $round -le $Rounds; $round++) {
    $seed = $SeedBase + $round
    $logFile = Join-Path $layout.Logs ("round-{0:D3}.log" -f $round)
    $expectedSec = [Math]::Round(($EventsPerRound * $ThrottleMs) / 1000.0, 1)

    Write-Host "[llm-fuzz] Round $round/$Rounds seed=$seed events=$EventsPerRound expected~${expectedSec}s timeout=${MonkeyTimeoutSec}s"

    & $adb -s $device logcat -c | Out-Null

    $monkeyResult = Invoke-MonkeyWithTimeout -AdbPath $adb -Device $device -Package $PackageName -Seed $seed -Events $EventsPerRound -Throttle $ThrottleMs -TimeoutSec $MonkeyTimeoutSec
    $monkeyOutput = $monkeyResult.Output
    $monkeyExitCode = $monkeyResult.ExitCode

    & $adb -s $device logcat -d > $logFile

    $hits = Find-Keywords -LogPath $logFile -Patterns $patterns
    $hasFailure = ($monkeyExitCode -ne 0) -or ($hits.Count -gt 0)

    $results.Add([PSCustomObject]@{
        round = $round
        seed = $seed
        events = $EventsPerRound
        monkeyExitCode = $monkeyExitCode
        monkeyTimedOut = $monkeyResult.TimedOut
        hasFailureSignals = $hasFailure
        matchedPatterns = @($hits)
        monkeyOutputTail = ($monkeyOutput | Select-Object -Last 8)
        logFile = $logFile
    })

    if ($hasFailure) {
        Write-Host "[llm-fuzz] Failure signals detected in round $round"
        if ($StopOnFailure) {
            break
        }
    }
}

$failedRounds = $results | Where-Object { $_.hasFailureSignals }
$endAt = Get-Date

$summary = [PSCustomObject]@{
    packageName = $PackageName
    device = $device
    roundsRequested = $Rounds
    roundsExecuted = $results.Count
    failedRoundCount = @($failedRounds).Count
    startedAt = $startAt.ToString("o")
    finishedAt = $endAt.ToString("o")
    durationSeconds = [Math]::Round(($endAt - $startAt).TotalSeconds, 2)
    outputDir = $layout.Root
    logDir = $layout.Logs
    results = $results
}

$summary | ConvertTo-Json -Depth 8 | Set-Content -Path $summaryPath -Encoding UTF8

$lines = @()
$lines += "LLM Runtime Fuzz Report"
$lines += "package: $PackageName"
$lines += "device: $device"
$lines += "rounds requested: $Rounds"
$lines += "rounds executed: $($results.Count)"
$lines += "failed rounds: $(@($failedRounds).Count)"
$lines += "duration sec: $([Math]::Round(($endAt - $startAt).TotalSeconds, 2))"
$lines += "summary json: $summaryPath"
$lines += ""

foreach ($item in $results) {
    $lines += ("round {0:D3} seed={1} monkeyExit={2} fail={3}" -f $item.round, $item.seed, $item.monkeyExitCode, $item.hasFailureSignals)
    if ($item.matchedPatterns.Count -gt 0) {
        $lines += ("  matched: " + ($item.matchedPatterns -join ", "))
    }
    $lines += ("  log: " + $item.logFile)
}

$lines | Set-Content -Path $reportPath -Encoding UTF8

Write-Host "[llm-fuzz] Done. Summary: $summaryPath"
Write-Host "[llm-fuzz] Report:  $reportPath"
if (@($failedRounds).Count -gt 0) {
    exit 2
}
exit 0
