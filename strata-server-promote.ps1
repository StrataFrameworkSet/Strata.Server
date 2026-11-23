[CmdletBinding(DefaultParameterSetName = 'Run')]
param(
    [Parameter(Mandatory = $true, ParameterSetName = 'Run')][string]$Version,
    [switch]$DryRun,
    [switch]$Force,
    [Parameter(Mandatory = $true, ParameterSetName = 'Help')][switch]$Help
)

$ErrorActionPreference = 'Stop'

$Owner      = 'StrataFrameworkSet'
$MavenRepo  = "https://maven.pkg.github.com/$Owner/repository"
$GroupId    = 'strata.server'
$GroupPath  = $GroupId.Replace('.', '/')
$Artifacts  = @(
    'strata-server-core',
    'strata-server-spring',
    'strata-server-websocket'
)
$Snapshot    = "$Version-SNAPSHOT"
$Checksums  = @{ sha1 = 'SHA1'; md5 = 'MD5'; sha256 = 'SHA256'; sha512 = 'SHA512' }
$Utf8NoBom  = New-Object System.Text.UTF8Encoding $false

function Write-Step([string]$Message) { Write-Host "==> $Message" -ForegroundColor Cyan }
function Write-Info([string]$Message) { Write-Host "    $Message" }
function Write-Plan([string]$Message) { Write-Host "    [dry-run] $Message" -ForegroundColor Yellow }
function Stop-Promote([string]$Message) { throw "strata-server-promote: $Message" }

function Confirm-Action([string]$Message) {
    if ($Force) { return $true }
    $answer = Read-Host "    $Message [y/N]"
    return ($answer -match '^(y|yes)$')
}

function Get-HttpStatus([string]$Url, [string]$AuthConfig) {
    $arguments = @('-s', '-o', 'NUL', '-w', '%{http_code}')
    if ($AuthConfig) { $arguments += @('-K', $AuthConfig) }
    $code = & curl.exe @arguments $Url
    return [int]$code
}

function Get-Url([string]$Url, [string]$AuthConfig) {
    $output = & curl.exe -sS -f -L -K $AuthConfig $Url
    if ($LASTEXITCODE -ne 0) { Stop-Promote "GET failed: $Url" }
    return ($output -join "`n")
}

function Save-Url([string]$Url, [string]$Destination, [string]$AuthConfig) {
    & curl.exe -sS -f -L -K $AuthConfig -o $Destination $Url
    if ($LASTEXITCODE -ne 0) { Stop-Promote "download failed: $Url" }
}

function Test-Download([string]$Path, [string]$Extension) {
    $bytes = [System.IO.File]::ReadAllBytes($Path)
    $text  = [System.Text.Encoding]::UTF8.GetString($bytes)
    $ok = switch ($Extension) {
        'jar'    { $bytes.Length -ge 2 -and $bytes[0] -eq 0x50 -and $bytes[1] -eq 0x4B }
        'pom'    { $text.Contains('<project') }
        'module' { $text.TrimStart().StartsWith('{') }
        default  { $true }
    }
    if (-not $ok) {
        $start = $text.Substring(0, [Math]::Min(60, $text.Length)) -replace '[\r\n]', ''
        Stop-Promote "$(Split-Path -Leaf $Path) is not a valid .$Extension file (starts with: $start)"
    }
}

function Send-File([string]$Path, [string]$Url, [string]$AuthConfig) {
    & curl.exe -sS -f -K $AuthConfig -T $Path $Url | Out-Null
    if ($LASTEXITCODE -ne 0) { Stop-Promote "upload failed: $Url" }
}

function New-AuthConfigs([string]$WorkDir) {
    if (-not $env:REPOSITORY_USER -or -not $env:REPOSITORY_PASSWORD) {
        Stop-Promote 'REPOSITORY_USER and REPOSITORY_PASSWORD must be set'
    }
    $maven = Join-Path $WorkDir 'maven.curlrc'
    $api   = Join-Path $WorkDir 'api.curlrc'
    [System.IO.File]::WriteAllText($maven, "user = `"$($env:REPOSITORY_USER):$($env:REPOSITORY_PASSWORD)`"`n", $Utf8NoBom)
    [System.IO.File]::WriteAllText($api, (@(
        "header = `"Authorization: Bearer $($env:REPOSITORY_PASSWORD)`"",
        'header = "Accept: application/vnd.github+json"',
        'header = "X-GitHub-Api-Version: 2022-11-28"'
    ) -join "`n") + "`n", $Utf8NoBom)
    return @{ Maven = $maven; Api = $api }
}

function Get-PackageVersion([string]$ArtifactId, [string]$ApiAuth) {
    $name = "$GroupId.$ArtifactId"
    foreach ($scope in @("orgs/$Owner", "users/$Owner")) {
        $page = 1
        while ($true) {
            $url  = "https://api.github.com/$scope/packages/maven/$name/versions?per_page=100&page=$page"
            $json = & curl.exe -sS -f -K $ApiAuth $url 2>$null
            if ($LASTEXITCODE -ne 0) { break }
            $parsed   = ($json -join "`n") | ConvertFrom-Json
            $versions = @($parsed | ForEach-Object { $_ })
            if ($versions.Count -eq 0) { break }
            $match = @($versions | Where-Object { $_.name -eq $Version })
            if ($match.Count -gt 1) { Stop-Promote "multiple GitHub package versions named $Version for $name" }
            $match = $match | Select-Object -First 1
            if ($match) { return @{ Url = "https://api.github.com/$scope/packages/maven/$name/versions/$($match.id)"; Id = $match.id } }
            $page++
        }
    }
    return $null
}

function Write-Checksums([string]$Path) {
    $files = @()
    foreach ($ext in $Checksums.Keys) {
        $hash = (Get-FileHash -Path $Path -Algorithm $Checksums[$ext]).Hash.ToLowerInvariant()
        $out  = "$Path.$ext"
        [System.IO.File]::WriteAllText($out, $hash, $Utf8NoBom)
        $files += $out
    }
    return $files
}

function Invoke-MavenPromote([string]$ArtifactId, [hashtable]$Auth, [string]$WorkDir) {
    Write-Step "Maven $GroupId`:$ArtifactId $Snapshot -> $Version"
    $base = "$MavenRepo/$GroupPath/$ArtifactId"
    $dir  = Join-Path $WorkDir $ArtifactId
    New-Item -ItemType Directory -Path $dir | Out-Null

    [xml]$meta = Get-Url "$base/$Snapshot/maven-metadata.xml" $Auth.Maven
    $build = $meta.metadata.versioning.snapshot
    if (-not $build -or -not $build.timestamp -or -not $build.buildNumber) {
        Stop-Promote "no current snapshot build listed for $ArtifactId $Snapshot"
    }
    $current = "$Version-$($build.timestamp)-$($build.buildNumber)"
    Write-Info "snapshot build: $current"
    $entries = @($meta.metadata.versioning.snapshotVersions.snapshotVersion |
        Where-Object { $_.value -eq $current -and $_.extension -notmatch '\.(sha1|md5|sha256|sha512)$' })
    if ($entries.Count -eq 0) { Stop-Promote "no files listed for snapshot build $current of $ArtifactId" }

    $files = @()
    foreach ($entry in $entries) {
        $classifier = if ($entry.classifier) { "-$($entry.classifier)" } else { '' }
        $source = "$ArtifactId-$($entry.value)$classifier.$($entry.extension)"
        $target = "$ArtifactId-$Version$classifier.$($entry.extension)"
        $path   = Join-Path $dir $target
        Save-Url "$base/$Snapshot/$source" $path $Auth.Maven
        Test-Download $path $entry.extension
        if ($entry.extension -in @('pom', 'module')) {
            $text = [System.IO.File]::ReadAllText($path)
            $text = $text.Replace($entry.value, $Version).Replace($Snapshot, $Version)
            [System.IO.File]::WriteAllText($path, $text, $Utf8NoBom)
            if ($text.Contains($Snapshot) -or $text.Contains($entry.value)) {
                Stop-Promote "$target still references $Snapshot after rewrite"
            }
            Write-Info "rewrote $target (no $Snapshot references remain)"
        }
        $files += $path
        $files += Write-Checksums $path
        Write-Info "staged $target  <=  $source"
    }

    $releasePom = "$base/$Version/$ArtifactId-$Version.pom"
    $existing   = Get-PackageVersion $ArtifactId $Auth.Api
    $pomStatus  = Get-HttpStatus $releasePom $Auth.Maven
    if ($existing -or $pomStatus -in @(200, 301, 302, 303, 307, 308)) {
        if (-not $existing) { Stop-Promote "$ArtifactId $Version exists but its GitHub package version id could not be found" }
        if ($DryRun) {
            Write-Plan "would DELETE existing $ArtifactId $Version (package version id $($existing.Id))"
        } elseif (Confirm-Action "$ArtifactId $Version already exists. Delete it and republish?") {
            & curl.exe -sS -f -K $Auth.Api -X DELETE $existing.Url | Out-Null
            if ($LASTEXITCODE -ne 0) { Stop-Promote "delete failed for $ArtifactId $Version (token needs delete:packages)" }
            Write-Info "deleted existing $ArtifactId $Version"
        } else {
            Write-Info "skipped $ArtifactId (existing $Version kept)"
            return $false
        }
    } else {
        Write-Info "$ArtifactId $Version does not exist yet"
    }

    foreach ($file in $files) {
        $url = "$base/$Version/$(Split-Path -Leaf $file)"
        if ($DryRun) { Write-Plan "would PUT $url" } else { Send-File $file $url $Auth.Maven }
    }
    if (-not $DryRun) { Write-Info "published $ArtifactId $Version ($($files.Count) files)" }
    return $true
}

function Show-Help {
    $name = Split-Path -Leaf $PSCommandPath
    Write-Host @"
Usage:
  $name -Version <x.y.z> [-DryRun] [-Force]
  $name -Help

Promotes published <version>-SNAPSHOT builds of Strata Server to release <version>.

  Maven (GitHub Packages, $MavenRepo):
    Downloads $($Artifacts -join ', ')
    at <version>-SNAPSHOT, rewrites the pom and module to <version>, and deploys them as <version>.
    An existing <version> is deleted (after confirmation) and republished.

Parameters:
  -Version <x.y.z>     Release version to promote (required), e.g. 1.0.0
  -DryRun              Download and stage everything; print deletes and uploads without
                       changing the registry
  -Force               Do not prompt before deleting an existing Maven release
  -Help                Show this help

Environment:
  REPOSITORY_USER, REPOSITORY_PASSWORD   GitHub Packages credentials
                                         (token needs read, write and delete:packages)

Examples:
  $name -Version 1.0.0 -DryRun
  $name -Version 1.0.0 -Force
"@
}

if ($Help) { Show-Help; return }

if ($Version -match '(?i)snapshot') { Stop-Promote "pass the release version (e.g. 1.0.0), not a snapshot" }

$work = Join-Path ([System.IO.Path]::GetTempPath()) ("strata-server-promote-" + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $work | Out-Null
$skipped = @()
try {
    if ($DryRun) { Write-Step 'DRY RUN: the registry will not be changed' }
    $auth = New-AuthConfigs $work
    foreach ($artifactId in $Artifacts) {
        if (-not (Invoke-MavenPromote $artifactId $auth $work)) { $skipped += "maven:$artifactId" }
    }
    if ($skipped.Count -gt 0) { Write-Step "Done; skipped: $($skipped -join ', ')" } else { Write-Step 'Done' }
} finally {
    Remove-Item -Recurse -Force $work -ErrorAction SilentlyContinue
}
