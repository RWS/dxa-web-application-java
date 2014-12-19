@echo off
rem Install Tridion Content Delivery libraries and necessary third-party libraries in the local Maven repository

echo Installing Tridion Content Delivery libraries into the local Maven repository...
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_ambient              -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_ambient-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_broker               -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_broker-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_cache                -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_cache-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_common_config        -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_common_config-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_common_config_api    -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_common_config_api-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_common_config_legacy -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_common_config_legacy-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_common_util          -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_common_util-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_core                 -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_core-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_datalayer            -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_datalayer-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_dynamic              -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_dynamic-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_linking              -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_linking-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_model                -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_model-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_odata                -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_odata-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_odata_types          -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_odata_types-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_session              -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_session-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_tcdl                 -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_tcdl-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_wai                  -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_wai-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_wrapper              -Dversion=8.0.0 -Dpackaging=jar -Dfile=cd_wrapper-8.0.0.jar

call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=odata_api               -Dversion=8.0.0 -Dpackaging=jar -Dfile=odata_api-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=odata_client            -Dversion=8.0.0 -Dpackaging=jar -Dfile=odata_client-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=odata_edm               -Dversion=8.0.0 -Dpackaging=jar -Dfile=odata_edm-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=odata_parser            -Dversion=8.0.0 -Dpackaging=jar -Dfile=odata_parser-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=odata_renderer          -Dversion=8.0.0 -Dpackaging=jar -Dfile=odata_renderer-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=odata_tridion_client    -Dversion=8.0.0 -Dpackaging=jar -Dfile=odata_tridion_client-8.0.0.jar
call mvn -q install:install-file -DgroupId=com.tridion -DartifactId=odata_tridion_common    -Dversion=8.0.0 -Dpackaging=jar -Dfile=odata_tridion_common-8.0.0.jar

echo Installing third-party libraries into the local Maven repository...
call mvn -q install:install-file -DgroupId=com.vs.ezlicrun -DartifactId=easylicense -Dversion=2.5 -Dpackaging=jar -Dfile=easylicense-2.5.jar
call mvn -q install:install-file -DgroupId=com.microsoft.sqlserver -DartifactId=sqljdbc4 -Dversion=4.0 -Dpackaging=jar -Dfile=sqljdbc4-4.0.jar

echo Finished
