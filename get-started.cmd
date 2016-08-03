@echo off

 echo This installs 'dxa-builder' locally and runs default build for DXA framework for Web 8.
 echo If you want 2013sp1 support, read Prerequisites section in README.
 cd dxa-builder
 call .\gradlew.bat
 cd ..
 call .\gradlew.bat
