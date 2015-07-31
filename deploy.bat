rem copying war file to tomcat
copy example-webapp\target\example-webapp.war C:\apache-tomcat-8.0.23\webapps\ROOT.war

rem restarting tomcat
cd C:\apache-tomcat-8.0.23\bin\

debugmode.bat
