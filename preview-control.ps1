param(
    [ValidateSet('start', 'stop', 'status')]
    [string]$Action = 'start'
)

$ErrorActionPreference = 'Stop'

$root = $PSScriptRoot
$logDir = Join-Path $root 'preview-logs'
$stateFile = Join-Path $logDir 'preview-services.json'

$services = @(
    @{
        Name = 'backend'
        DisplayName = 'Backend API'
        WorkDir = Join-Path $root 'web-ai-project02\tlias-web-management'
        Command = 'mvn spring-boot:run'
        Port = 8080
        WaitSeconds = 60
        StdOut = Join-Path $logDir 'backend.log'
        StdErr = Join-Path $logDir 'backend.err.log'
    },
    @{
        Name = 'chat-agent'
        DisplayName = 'Chat Agent'
        WorkDir = Join-Path $root 'web-ai-project02\chat-agent'
        Command = 'npm run dev'
        Port = 8787
        WaitSeconds = 20
        StdOut = Join-Path $logDir 'chat-agent.log'
        StdErr = Join-Path $logDir 'chat-agent.err.log'
    },
    @{
        Name = 'frontend'
        DisplayName = 'Frontend Dev Server'
        WorkDir = Join-Path $root 'vue-tlias-management'
        Command = 'npm run dev -- --host 0.0.0.0'
        Port = 5173
        WaitSeconds = 20
        StdOut = Join-Path $logDir 'frontend.log'
        StdErr = Join-Path $logDir 'frontend.err.log'
    }
)

function Ensure-LogDir {
    if (-not (Test-Path $logDir)) {
        New-Item -ItemType Directory -Path $logDir -Force | Out-Null
    }
}

function Get-ListeningConnection([int]$Port) {
    Get-NetTCPConnection -State Listen -LocalPort $Port -ErrorAction SilentlyContinue |
        Select-Object -First 1
}

function Test-PortListening([int]$Port) {
    return $null -ne (Get-ListeningConnection -Port $Port)
}

function Wait-ForPort([int]$Port, [int]$TimeoutSeconds) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if (Test-PortListening -Port $Port) {
            return $true
        }
        Start-Sleep -Milliseconds 500
    }
    return $false
}

function Get-AliveProcess([Nullable[int]]$ProcessId) {
    if (-not $ProcessId) {
        return $null
    }
    try {
        return Get-Process -Id $ProcessId -ErrorAction Stop
    } catch {
        return $null
    }
}

function Start-ManagedService([hashtable]$Service) {
    if (Test-PortListening -Port $Service.Port) {
        Write-Host "[SKIP] $($Service.DisplayName) port $($Service.Port) is already in use."
        return [pscustomobject]@{
            name = $Service.Name
            displayName = $Service.DisplayName
            port = $Service.Port
            pid = $null
            status = 'already-listening'
            startedAt = $null
            stdout = $Service.StdOut
            stderr = $Service.StdErr
        }
    }

    $shellCommand = "Set-Location '$($Service.WorkDir)'; $($Service.Command)"
    $process = Start-Process `
        -FilePath powershell `
        -ArgumentList @('-NoProfile', '-ExecutionPolicy', 'Bypass', '-Command', $shellCommand) `
        -WindowStyle Hidden `
        -RedirectStandardOutput $Service.StdOut `
        -RedirectStandardError $Service.StdErr `
        -PassThru

    $ready = Wait-ForPort -Port $Service.Port -TimeoutSeconds $Service.WaitSeconds
    if ($ready) {
        Write-Host "[OK] $($Service.DisplayName) listening on port $($Service.Port)."
    } else {
        Write-Warning "$($Service.DisplayName) did not reach port $($Service.Port) within $($Service.WaitSeconds)s. Check $($Service.StdOut)"
    }

    return [pscustomobject]@{
        name = $Service.Name
        displayName = $Service.DisplayName
        port = $Service.Port
        pid = $process.Id
        status = if ($ready) { 'started' } else { 'starting-timeout' }
        startedAt = (Get-Date).ToString('s')
        stdout = $Service.StdOut
        stderr = $Service.StdErr
    }
}

function Save-State($Entries) {
    $state = [pscustomobject]@{
        root = $root
        updatedAt = (Get-Date).ToString('s')
        services = $Entries
    }
    $state | ConvertTo-Json -Depth 4 | Set-Content -Path $stateFile -Encoding UTF8
}

function Load-State {
    if (-not (Test-Path $stateFile)) {
        return $null
    }
    return Get-Content $stateFile -Raw | ConvertFrom-Json
}

function Stop-ProcessTree([int]$ProcessId, [string]$Label) {
    $alive = Get-AliveProcess -ProcessId $ProcessId
    if (-not $alive) {
        return $false
    }
    & taskkill /PID $ProcessId /T /F | Out-Null
    Write-Host "[STOP] $Label process tree stopped (PID $ProcessId)."
    return $true
}

function Stop-ServiceFallback([hashtable]$Service) {
    $conn = Get-ListeningConnection -Port $Service.Port
    if (-not $conn) {
        return $false
    }
    & taskkill /PID $conn.OwningProcess /T /F | Out-Null
    Write-Host "[STOP] $($Service.DisplayName) stopped by port fallback (port $($Service.Port), PID $($conn.OwningProcess))."
    return $true
}

function Show-Status {
    $state = Load-State
    if ($null -eq $state) {
        Write-Host 'No preview state file found.'
    } else {
        Write-Host "State file: $stateFile"
        foreach ($entry in $state.services) {
            $alive = if ($entry.pid) { Get-AliveProcess -ProcessId $entry.pid } else { $null }
            $portOpen = Test-PortListening -Port $entry.port
            Write-Host ("- {0}: pid={1} alive={2} port={3} listening={4}" -f $entry.displayName, $entry.pid, [bool]$alive, $entry.port, $portOpen)
        }
    }
}

Ensure-LogDir

switch ($Action) {
    'start' {
        $entries = foreach ($service in $services) {
            Start-ManagedService -Service $service
        }
        Save-State -Entries $entries
        Write-Host ''
        Write-Host 'Preview services ready:'
        Write-Host '  Frontend : http://localhost:5173/'
        Write-Host '  Backend  : http://localhost:8080/'
        Write-Host '  Chat API : http://localhost:8787/'
        Write-Host "Logs: $logDir"
    }
    'stop' {
        $state = Load-State
        if ($state -and $state.services) {
            foreach ($entry in $state.services) {
                if (-not (Stop-ProcessTree -ProcessId $entry.pid -Label $entry.displayName)) {
                    $service = $services | Where-Object { $_.Name -eq $entry.name } | Select-Object -First 1
                    if ($service) {
                        Stop-ServiceFallback -Service $service | Out-Null
                    }
                }
            }
        } else {
            Write-Host 'No preview state file found. Falling back to known preview ports.'
            foreach ($service in $services) {
                Stop-ServiceFallback -Service $service | Out-Null
            }
        }

        if (Test-Path $stateFile) {
            Remove-Item $stateFile -Force
        }
        Write-Host 'Preview services stop sequence finished.'
    }
    'status' {
        Show-Status
    }
}
