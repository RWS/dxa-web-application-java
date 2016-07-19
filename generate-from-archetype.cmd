@echo off
rem Builds the web application, 
rem generates archetype using it, 
rem builds a new application from archetype (using archetype already installed to Maven Repo!),
rem checks if the artifacts are exactly the same analyzing the size of dxa-webapp folders.
rem Script is used during Jenkins job.
setlocal
call mvn -f dxa-webapp\pom.xml clean package

cd dxa-webapp\target
mv dxa-webapp.war dxa-webapp-original.war
For /F "tokens=1* delims==" %%A IN (maven-archiver\pom.properties) DO (
    IF "%%A"=="version" set version=%%B
)

mkdir dxa-webapp-from-archetype
cd dxa-webapp-from-archetype
call mvn archetype:generate -B -DarchetypeCatalog=local -DarchetypeGroupId=com.sdl.dxa -DarchetypeArtifactId=dxa-webapp-archetype -DarchetypeVersion=%version% -DgroupId=org.example -DartifactId=dxa-webapp -Dversion=%version% -Dpackage=org.example

endlocal

rem jenkins step!
call mvn -f dxa-webapp\target\dxa-webapp-from-archetype\dxa-webapp\pom.xml clean package

@echo off
setlocal
cd dxa-webapp\target
mv dxa-webapp-from-archetype\dxa-webapp\target\dxa-webapp.war dxa-webapp.war

set size=0
cd dxa-webapp
for /r %%x in (*) do set /a size += %%~zx

set size1=0
cd ..\dxa-webapp-from-archetype\dxa-webapp\target\dxa-webapp
for /r %%x in (*) do set /a size1 += %%~zx

if NOT %size% == %size1% (
	echo "Sizes of original (%size% Bytes) and generated (%size1% Bytes) webapps differ! Exiting."
	exit /b 1)
	
endlocal