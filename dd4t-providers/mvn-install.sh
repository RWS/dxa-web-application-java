#!/bin/sh

# See DD4T dependency naming on https://github.com/dd4t/dd4t-2-java/blob/develop/README.md

# Set version parameter to version you want to install

TRIDION_VERSION="7.1.0"
TRIDION_CWD_VERSION="7.1.2"

echo cd_ambient-$TRIDION_VERSION.jar

echo "Install Tridion Content Delivery libraries and necessary third-party libraries in the local Maven repository"

echo "Installing Tridion Content Delivery libraries into the local Maven repository..."
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_ambient         -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_ambient-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_broker          -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_broker-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_cache           -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_cache-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_core            -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_core-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_datalayer       -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_datalayer-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_dynamic         -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_dynamic-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_linking         -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_linking-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_model           -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_model-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_session         -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_session-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_wrapper         -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_wrapper-$TRIDION_VERSION.jar

echo "Optional runtime Tridion Content Delivery JARs (for OData/Preview/HTTP Deploy) "
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_deployer            -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_deployer-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_upload              -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_upload-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_odata               -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_odata-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_odata_types         -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_odata_types-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_preview_web         -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_preview_web-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_preview_webservice  -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_preview_webservice-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_preview_ambient     -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_preview_ambient-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_tcdl                -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_tcdl-$TRIDION_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cd_wai                -Dversion=$TRIDION_VERSION -Dpackaging=jar -Dfile=cd_wai-$TRIDION_VERSION.jar

echo "Installing Tridion Contextual Web Delivery libraries into the local Maven repository..."
mvn install:install-file -DgroupId=com.tridion -DartifactId=cwd_cartridge      -Dversion=$TRIDION_CWD_VERSION -Dpackaging=jar -Dfile=cwd_cartridge-$TRIDION_CWD_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cwd_engine         -Dversion=$TRIDION_CWD_VERSION -Dpackaging=jar -Dfile=cwd_engine-$TRIDION_CWD_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cwd_image          -Dversion=$TRIDION_CWD_VERSION -Dpackaging=jar -Dfile=cwd_image-$TRIDION_CWD_VERSION.jar
mvn install:install-file -DgroupId=com.tridion -DartifactId=cwd_resource       -Dversion=$TRIDION_CWD_VERSION -Dpackaging=jar -Dfile=cwd_resource-$TRIDION_CWD_VERSION.jar

echo "Installing third-party libraries into the local Maven repository..."
mvn install:install-file -DgroupId=com.vs.ezlicrun -DartifactId=easylicense -Dversion=2.5 -Dpackaging=jar -Dfile=easylicense-2.5.jar
mvn install:install-file -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=4.0.0 -Dpackaging=jar -Dfile=sqljdbc4-4.0.0.jar

# Required for the dd4t-1.31 example site
# TODO: the proper groupid should com.bitmechanic
mvn install:install-file -DgroupId=jdbcpool -DartifactId=jdbcpool -Dversion=1.0 -Dpackaging=jar -Dfile=jdbcpool-1.0.jar
echo "Done"
