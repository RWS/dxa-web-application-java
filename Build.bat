rem building maven package
mvn clean package
rem copying war file to tomcat
copy example-webapp\target\example-webapp.war C:\apache-tomcat-8.0.23\webapps\ROOT.war

rem restarting tomcat
C:\apache-tomcat-8.0.23\bin\shutdown.bat
C:\apache-tomcat-8.0.23\bin\debugmode.bat
