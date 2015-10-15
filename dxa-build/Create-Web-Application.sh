#!/bin/bash

export ANT_HOME=./lib/ant
PATH=$PATH:./lib/ant/bin

ant -d build.custom.dxa
echo "The DXA web application WAR file has been built and copied to the target directory"
