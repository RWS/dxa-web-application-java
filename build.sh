#!/usr/bin/env bash

# Usage: build.sh command
# command is passed to mvn as it is
# E.g. 'build.sh clean install -Pcd-8.1.0' will be 'mvn -f project-name/pom.xml clean install -Pcd-8.1.0'

echo "Building DXA Framework and related projects..."
echo "WebApp is not built!"

command=$@
initialCommand="exists"
if [ -z "$command" ]; then
	initialCommand=""
	command="clean install"
fi

mvn -f dxa-bom/pom.xml ${command} || exit $?
mvn -f dxa-bom-2013sp1/pom.xml ${command} || exit $?
mvn -f dxa-webapp-archetype/pom.xml ${command} || exit $?

mvn -f dxa-framework/pom.xml ${command} || exit $?

echo "We are done :)"

if [ -z "${initialCommand}" ]; then
	read -n1 -r -p "Press any key to continue..." key
fi
