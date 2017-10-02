@echo off

 echo This installs 'dxa-builder' locally and runs default build for DXA framework for Web 8.
 cd dxa-builder
 call .\gradlew.bat
 cd ..
 call .\gradlew.bat -Pcommand="clean install"
