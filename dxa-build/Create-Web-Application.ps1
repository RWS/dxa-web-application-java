# Usage examples
#   .\Create-Web-Application.ps1 Staging
#   .\Create-Web-Application.ps1 Live
#   .\Create-Web-Application.ps1 Live -Verbose

param (
    [Parameter(Mandatory=$false, HelpMessage="Type of DXA web application to deploy: 'Staging' or 'Live'")]
    [ValidateSet("Staging", "Live")]
    [string]$DeployType = "Live"
)

Write-Verbose ("Creating WAR arcive for {0} site" -f $DeployType)

Add-Type -assembly system.io.compression
Add-Type -assembly system.io.compression.filesystem

function AddFileToArchive([System.IO.Compression.ZipArchive] $archive, [String] $sourceFileName, [String] $entryName)
{
    #Write-Verbose ("Adding '{0}' to archive as {1}" -f $sourceFileName, $entryName)
    $entry = [System.IO.Compression.ZipFileExtensions]::CreateEntryFromFile($archive, $sourceFileName, $entryName)
}

function AddFolderToArchive([System.IO.Compression.ZipArchive] $archive, [String] $sourceFolderName, [String] $entryFoldername)
{
    Write-Verbose ("Adding '{0}' to {1}" -f $sourceFolderName, $entryFoldername)
    
    $sourceFiles = Get-ChildItem $sourceFolderName | where {!$_.PSIsContainer}
    foreach ($sourceFile in $sourceFiles)
    {
        AddFileToArchive $archive $sourceFile.FullName (Join-Path $entryFoldername $sourceFile.Name)
    }
}

$ErrorActionPreference = "Stop"

try
{
    $sourceWarFileName = "source-war\example-webapp.war"
    $sourceLicenseDirectory = "source-license\"
    $sourceCommonJarsDirectory = "source-jars\common"
    $sourceStagingJarsDirectory = "source-jars\staging"
    $sourceCommonConfigDirectory = "source-config\common"
    $sourceStagingConfigDirectory = "source-config\staging"
    $sourceLicenseDirectory = "source-license"
    $targetDirectory = "target\"
    $targetWarFileName = Join-Path $targetDirectory "example-webapp.war"

    #todo test if source WAR and other depedencies exist

    if(Test-Path($targetDirectory))
    {
        Write-Verbose ("Clean target directory {0} " -f $targetDirectory)
        Remove-Item $targetDirectory -Recurse
    }

    
    Write-Verbose ("Create target directory {0} " -f $targetDirectory)
    New-Item -ItemType Directory $targetDirectory | Out-Null

    Write-Verbose ("Copy source WAR from {0} to target WAR {1}" -f $sourceWarFileName, $targetWarFileName)
    Copy-Item $sourceWarFileName $targetWarFileName

    Write-Verbose ("Load target WAR from {0}" -f $targetWarFileName)
    $warFile = Get-Item($targetWarFileName)
    $war = [io.compression.zipfile]::Open($warFile.FullName, [System.IO.Compression.ZipArchiveMode]::Update)

    AddFolderToArchive $war $sourceCommonJarsDirectory "WEB-INF\lib"
    AddFolderToArchive $war $sourceCommonConfigDirectory "WEB-INF\classes"
    AddFolderToArchive $war $sourceLicenseDirectory "WEB-INF\classes"
    if($DeployType -eq "Staging")
    {
        AddFolderToArchive $war $sourceStagingJarsDirectory "WEB-INF\lib"
        AddFolderToArchive $war $sourceStagingConfigDirectory "WEB-INF\classes"
    }
    
    $war.Dispose()
    Write-Host ("Created WAR arcive for {0} site in {1}" -f $DeployType, $targetWarFileName)
} catch
{
    Write-Verbose ("Something went wrong: {0}" -f $_.Exception.Message)
    if($war -ne $null)
    {
        $war.Dispose()
    }
    throw $_
}