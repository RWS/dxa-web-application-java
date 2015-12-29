dd4t-2-java
======

 - Current stable version: **2.0.2**
 - Maven Central: [org.dd4t](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22org.dd4t%22)
 - Current stable archetype version: [1.5](http://search.maven.org/#artifactdetails%7Corg.dd4t%7Cdd4t-spring-mvc-archetype%7C1.4%7Cmaven-archetype)

##Prerequisites and Java dependencies

1. Download and install Maven: https://maven.apache.org/run-maven/index.html
2. Install all Tridion dependencies (aka JAR files) in your local Maven repository as these are not available in Maven Central. The general command to do this is:

		mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_broker -Dversion=7.1.0 -Dpackaging=jar -Dfile=cd_broker-7.1.0.jar
	
To make this process a little more convenient, an install script was created to do this for all other required jars which are not in Maven Central.

* Windows: https://raw.githubusercontent.com/dd4t/dd4t-2-java/master/dd4t-providers/mvn-install.bat
* Linux, OS X: https://raw.githubusercontent.com/dd4t/dd4t-2-java/master/dd4t-providers/mvn-install.sh

Run this script in the directory where your Tridion jars are. Note that you have to rename them. For example, *cd_core.jar* has to become *cd_core-7.1.0.jar*. In addition to the Tridion jar, the script also installs the third party dependencies *easylicense-2.5.jar*,*sqljdbc4-4.0.0.jar*, *jdbcpool-1.0.jar* and the Tridion Contextual Web Delivery libraries should you need them.

In the above example, we install the Tridion 2013 SP1 Jar files, **to which we have appended the version number.** For older versions or for other version type (eg. -Dversion=2013SP1), you have to change the version property on your POM and in the script as well:
 
 		<!-- Set the correct version for your local or central setup -->
 		<properties>
		    <tridion.version>7.1.0</tridion.version>
		</properties>
		
	A fairly normal setup for a DD4T 2 web application with Tridion dependencies included is: 
	
	     <dependency>
            <groupId>com.tridion</groupId>
            <artifactId>cd_ambient</artifactId>
            <version>${tridion.version}</version>
        </dependency>
        <dependency>
            <groupId>com.tridion</groupId>
            <artifactId>cd_broker</artifactId>
            <version>${tridion.version}</version>
        </dependency>
        <dependency>
            <groupId>com.tridion</groupId>
            <artifactId>cd_cache</artifactId>
            <version>${tridion.version}</version>
        </dependency>
        <dependency>
            <groupId>com.tridion</groupId>
            <artifactId>cd_core</artifactId>
            <version>${tridion.version}</version>
        </dependency>
        <dependency>
            <groupId>com.tridion</groupId>
            <artifactId>cd_datalayer</artifactId>
            <version>${tridion.version}</version>
        </dependency>
        <dependency>
            <groupId>com.tridion</groupId>
            <artifactId>cd_dynamic</artifactId>
            <version>${tridion.version}</version>
        </dependency>
        <dependency>
            <groupId>com.tridion</groupId>
            <artifactId>cd_linking</artifactId>
            <version>${tridion.version}</version>
        </dependency>
        <dependency>
            <groupId>com.tridion</groupId>
            <artifactId>cd_model</artifactId>
            <version>${tridion.version}</version>
        </dependency>
        
Next, Tridion expects the following third party dependencies which are not in Maven Central and therefore have to be installed locally as well:
	
		<dependency>
            <groupId>com.vs.ezlicrun</groupId>
            <artifactId>easylicense</artifactId>
            <version>${easylicense-version}</version>
            <scope>runtime</scope>
        </dependency>
		<!-- SQL Server. 
		If you use Oracle, please consult the SDL Installation manual for the current proper dependency 
		-->
	     <dependency>
            <groupId>com.microsoft.sqlserver</groupId>
            <artifactId>sqljdbc4</artifactId>
            <version>${sqljdbc4-version}</version>
            <scope>runtime</scope>
        </dependency>
	
	
	These dependencies depend on the following properties in your POM:

		<properties>
			<sqljdbc4-version>4.0.0</sqljdbc4-version>
			<easylicense-version>2.5</easylicense-version>
		<properties>

Note that depending on your requirements or already present setup, version numbers may differ. Further reading on this can be done [here](http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html). 
	
##Fetching the latest version from Maven Central

dd4t-2-java is released to Maven Central, which means you do not have to download the source and embed it into your project anymore. The following core dependencies are usually always required:

       <dependency>
            <groupId>org.dd4t</groupId>
            <artifactId>dd4t-core</artifactId>
            <version>${dd4t.version}</version>
        </dependency>
        <dependency>
            <groupId>org.dd4t</groupId>
            <artifactId>dd4t-mvc-support</artifactId>
            <version>${dd4t.version}</version>
        </dependency>
        <dependency>
            <groupId>org.dd4t</groupId>
            <artifactId>dd4t-api</artifactId>
            <version>${dd4t.version}</version>
        </dependency>
        <dependency>
            <groupId>org.dd4t</groupId>
            <artifactId>dd4t-providers</artifactId>
            <version>${dd4t.version}</version>
        </dependency>
        <dependency>
            <groupId>org.dd4t</groupId>
            <artifactId>dd4t-caching</artifactId>
            <version>${dd4t.version}</version>
        </dependency>

Addionally, dd4t-2-java comes with a Maven archetype, which is also available on Maven Central. It is recommended to get started with the Archetype for quick and painless setup of your web application.

###Creating a bare dd4t-2-java Spring MVC web application

To create a complete, but minimal Spring MVC web application to develop with, create a new project based on a Maven archetype:

####Command Line:
1. Create an empty directory where you want to develop the web application in
2. Execute the following command after replacing the parameters in between the brackets ([com.example] and [mywebapp]):

		mvn archetype:generate -DgroupId=[com.example] -DartifactId=[mywebapp] -DarchetypeGroupId=org.dd4t -DarchetypeArtifactId=dd4t-spring-mvc-archetype -DarchetypeVersion=1.5  -DarchetypeCatalog=remote -DarchetypeCatalog=http://repo1.maven.org/maven2

3. Enter the requested information. Maven will ask you to specify a version (defaults to 1.0-SNAPSHOT) and will then ask you to confirm the settings.

4. The web application project is created if you see the following Maven output:

		$ mvn archetype:generate -DgroupId=com.example -DartifactId=mywebapp -DarchetypeGroupId=org.dd4t -DarchetypeArtifactId=dd4t-spring-mvc-archetype -DarchetypeVersion=1.5  -DarchetypeCatalog=remote -DarchetypeCatalog=http://repo1.maven.org/maven2
		[INFO] Scanning for projects...
		[INFO] Using the builder org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder with a thread count of 1
                                                                         
        [INFO]  ------------------------------------------------------------------------
        [INFO] Building Maven Stub Project (No POM) 1
        [INFO] ------------------------------------------------------------------------
        [INFO] 
        [INFO] >>> maven-archetype-plugin:2.3:generate (default-cli) @ standalone-pom >>>
        [INFO] 
        [INFO] <<< maven-archetype-plugin:2.3:generate (default-cli) @ standalone-pom <<<
        [INFO] 
        [INFO] --- maven-archetype-plugin:2.3:generate (default-cli) @ standalone-pom ---
        [INFO] Generating project in Interactive mode
        [INFO] Using property: groupId = com.example
        [INFO] Using property: artifactId = mywebapp
        Define value for property 'version':  1.0-SNAPSHOT: : 
        [INFO] Using property: package = com.example
        Confirm properties configuration:
        groupId: com.example
        artifactId: mywebapp
        version: 1.0-SNAPSHOT
        package: com.example
        Y: : Y
        [INFO] ----------------------------------------------------------------------------
        [INFO] Using following parameters for creating project from Archetype: dd4t-spring-mvc-archetype:1.4
        [INFO] ----------------------------------------------------------------------------
        [INFO] Parameter: groupId, Value: com.example
        [INFO] Parameter: artifactId, Value: mywebapp
        [INFO] Parameter: version, Value: 1.0-SNAPSHOT
        [INFO] Parameter: package, Value: com.example
        [INFO] Parameter: packageInPathFormat, Value: com/example
        [INFO] Parameter: version, Value: 1.0-SNAPSHOT
        [INFO] Parameter: package, Value: com.example
        [INFO] Parameter: groupId, Value: com.example
        [INFO] Parameter: artifactId, Value: mywebapp
        [INFO] project created from Archetype in dir: /your-dir/mywebapp
        [INFO] ------------------------------------------------------------------------
        [INFO] BUILD SUCCESS
        [INFO] ------------------------------------------------------------------------

5. You can now open the project using your favourite IDE.

####IDE

For setting up a webapp based on the dd4t-2-java through your IDE, you will need to enter the following information:

1. Archetype GroupId=**org.dd4t**

2. Archetype ArtifactId=**dd4t-spring-mvc-archetype**

3. ArchetypeVersion=**1.5**

 - For **Eclipse** you need the **m2eclipse** plugin. A nice guide on how to fully integrate can be found here: http://www.theserverside.com/news/1363817/Introduction-to-m2eclipse
 
 - For **IntelliJ IDEA**, follow this guide: https://www.jetbrains.com/idea/help/add-archetype-dialog.html
 

### Using the DD4T dependencies standalone

It's of course also possible to just load the DD4T dependencies in your POM. The following dependencies give you a minimum dd4t-2 setup:

		<dependencies>
			<dependency>
                <groupId>org.dd4t</groupId>
                <artifactId>dd4t-api</artifactId>
                <version>${dd4t.version}</version>
            </dependency>
            <dependency>
                <groupId>org.dd4t</groupId>
                <artifactId>dd4t-core</artifactId>
                <version>${dd4t.version}</version>
            </dependency>
            <dependency>
                <groupId>org.dd4t</groupId>
                <artifactId>dd4t-databind</artifactId>
                <version>${dd4t.version}</version>
            </dependency>
            <dependency>
                <groupId>org.dd4t</groupId>
                <artifactId>dd4t-mvc-support</artifactId>
                <version>${dd4t.version}</version>
            </dependency>
            <dependency>
            	<groupId>org.dd4t</groupId>
            	<artifactId>dd4t-providers</artifactId>
            	<version>${dd4t.version}</version>
        	</dependency>
        <dependencies>
        
#### dd4t-compatibility

For backwards compatibility with DD4T 1, add the following dependency:

		<dependencies>
            <dependency>
            	<groupId>org.dd4t</groupId>
            	<artifactId>dd4t-compatibility</artifactId>
            	<version>${dd4t.version}</version>
        	</dependency>
        <dependencies>

## Other dd4t-2 dependencies

DD4T 2 requires quite a few sub dependencies. Depending on your need, you will need to adjust them and add or change them in your POM. In general, the minimal list of dependencies can be found in the `dependencyManagement` section of dd4t-2-java's parent pom, located here: https://raw.githubusercontent.com/dd4t/dd4t-2-java/release/pom.xml

### Web Application and Tridion dependencies

In addition to dd4t-2's native dependencies, your web application and embedded Tridion stack may require extra dependencies. You can check the set dependencies in the following projects as a guide for establishing your set:

 - https://raw.githubusercontent.com/dd4t/dd4t-2-java/release/dd4t-example-site/pom.xml
 - https://raw.githubusercontent.com/dd4t/dd4t-2-java/develop/spring-mvc-archetype/src/main/resources/archetype-resources/pom.xml
 


