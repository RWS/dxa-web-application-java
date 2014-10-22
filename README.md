# Tridion Reference Implementation

## Prerequisites

To build the project, you need to have the following software installed:

* [Oracle JDK 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Apache Maven 3.x](https://maven.apache.org/)

After installing the JDK and unzipping Maven in a directory, set two environment variables:

* `JAVA_HOME` - make this point to your JDK installation directory
* `M2_HOME` - make this point to your Maven installation directory

Include `%JAVA_HOME%\bin` and `%M2_HOME%\bin` in your `PATH`.

To run the web application, you need the following software installed:

* [Apache Tomcat 8.x](http://tomcat.apache.org/)

## First time setup

The project needs Tridion libraries and some other third-party libraries which are not available in the Maven central
repository. You will need to install these libraries into your local Maven repository to be able to build the project.

The directory `tridion-libs` contains these libraries. See the `README.md` in that directory for instructions.

## Configuration

Some configuration needs to be done in the following configuration files. These configuration files are in the directory
`webapp-main\src\main\resources`.

* `cd_storage_conf.xml` - The CD storage configuration. Here you can configure what broker database the web application
  should use.
* `cd_dynamic_conf.xml` - The CD dynamic content configuration. Here you need to add a mapping for each publication
  that is used by the web application.

You will also need to add a `cd_licenses.xml` file with valid Tridion licenses to the directory
`webapp-main\src\main\resources`.

## Building the project

To build the project from the command line, open a command prompt window, navigate to the project directory and build
it using Maven:

`mvn clean package`

If the build is successful, you will find the WAR file `webapp-main.war` in the directory `webapp-main\target`.

## Running the project

Deploy the WAR file in Tomcat by copying it to your `%TOMCAT_HOME%\webapps` directory and starting Tomcat. After Tomcat
has started, browse to [http://localhost:8080/webapp-main/](http://localhost:8080/webapp-main/).
