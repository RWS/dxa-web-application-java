@ECHO OFF

REM Usage: build.cmd command
REM command is passed to mvn as it is
REM E.g. 'build.cmd clean install -Pweb8' will be 'mvn -f project-name\pom.xml clean install -Pweb8'

echo Building DXA Framework and related projects...
echo WebApp is not built!

if "%*" == "" ( set command=clean install ) else ( set command=%* )

call mvn -f dxa-bom\pom.xml %command% || exit /b %errorlevel%
call mvn -f dxa-bom-2013sp1\pom.xml %command% || exit /b %errorlevel%
call mvn -f dxa-bom-web8\pom.xml %command% || exit /b %errorlevel%
call mvn -f dxa-webapp-archetype\pom.xml %command% || exit /b %errorlevel%

call mvn -f dxa-framework\pom.xml %command% || exit /b %errorlevel%

echo We are done :)

if "%*" == "" (
	echo Press any key to continue...
	pause > nul
)

@ECHO ON
