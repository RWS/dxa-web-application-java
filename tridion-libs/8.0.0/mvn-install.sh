#!/bin/bash
# Install Tridion Content Delivery libraries and necessary third-party libraries in the local Maven repository

echo Installing Tridion Content Delivery libraries into the local Maven repository...
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_ambient               -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_ambient-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_broker                -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_broker-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_cache                 -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_cache-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_common_config         -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_common_config-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_common_config_api     -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_common_config_api-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_common_config_legacy  -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_common_config_legacy-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_common_util           -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_common_util-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_core                  -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_core-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_datalayer             -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_datalayer-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_dynamic               -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_dynamic-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_linking               -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_linking-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_model                 -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_model-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_session               -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_session-8.0.0.jar
mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_wrapper               -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_wrapper-8.0.0.jar

echo Installing third-party libraries into the local Maven repository...
mvn -q install:install-file -DgroupId=com.vs.ezlicrun -DartifactId=easylicense -Dversion=2.5 -Dpackaging=jar -Dfile=easylicense-2.5.jar
mvn -q install:install-file -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=4.0 -Dpackaging=jar -Dfile=sqljdbc4-4.0.jar

echo Finished
