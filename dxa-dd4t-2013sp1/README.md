DD4T support for 2013SP1
===

This artifact provides legacy support for 2013SP1 for DD4T-based projects. Thus you need it only if you your project is fully or partially based on DD4T for SDL Tridion 2013 SP1.

Publishing
---
This project is not published as a part of DXA, so you need to build it manually.

Prerequisites for building
---
In order to build this, you need these artifacts (SDL Tridion 2013 SP1) (`groudId : artifactId : version`) to be installed into your Maven repository:
- `com.tridion : cd_ambient : 7.1.0`
 - `com.tridion : cd_broker : 7.1.0`
 - `com.tridion : cd_cache : 7.1.0`
 - `com.tridion : cd_core : 7.1.0`
 - `com.tridion : cd_datalayer : 7.1.0`
 - `com.tridion : cd_deployer : 7.1.0`
 - `com.tridion : cd_dynamic : 7.1.0`
 - `com.tridion : cd_linking : 7.1.0`
 - `com.tridion : cd_model : 7.1.0`
 - `com.tridion : cd_odata : 7.1.0`
 - `com.tridion : cd_odata_types : 7.1.0`
 - `com.tridion : cd_preview_ambient : 7.1.0`
 - `com.tridion : cd_preview_web : 7.1.0`
 - `com.tridion : cd_preview_webservice : 7.1.0`
 - `com.tridion : cd_session : 7.1.0`
 - `com.tridion : cd_tcdl : 7.1.0`
 - `com.tridion : cd_upload : 7.1.0`
 - `com.tridion : cd_wrapper : 7.1.0`
 - `com.tridion : cwd_cartridge : 7.1.2`
 - `com.tridion : cwd_engine : 7.1.2`
 - `com.tridion : cwd_image : 7.1.2`
 - `com.tridion : cwd_resource : 7.1.2`
 - `com.vs.ezlicrun : easylicense : 2.5`

To use SmartTarget 2013SP1 module you also need
 - `com.tridion.smarttarget : session_cartridge : 2014sp1`
 - `com.tridion.smarttarget : smarttarget_cartridge : 2014sp1`
 - `com.tridion.smarttarget : smarttarget_core : 2014sp1`
 - `com.tridion.smarttarget : smarttarget_entitymodel : 2014sp1`
 - `com.tridion.smarttarget : smarttarget_google-analytics : 2014sp1`
 
To use AudienceManager 2013SP1 module you also need
 - `com.tridion.marketingsolution : profile : 7.1.0-SNAPSHOT`
 - `com.tridion.marketingsolution : profilesync : 7.1.0-SNAPSHOT`
 - `com.tridion.marketingsolution : utils : 7.1.0-SNAPSHOT`
 - `com.tridion.marketingsolution : tracking : 7.1.0-SNAPSHOT`
 - `com.tridion.marketingsolution : trackingsync : 7.1.0-SNAPSHOT`

To ease the installation you can use the [`/install-libs/`](https://github.com/sdl/dxa-web-application-java/tree/release/1.6/install-libs) tool. For help, run `install`.

Usage of `install-libs` tool
---
1. Download and unpack [`/install-libs/`](https://github.com/sdl/dxa-web-application-java/tree/release/1.6/install-libs).
2. Run `install.bat help` (or `install.sh help` for *nix) for detailed usage help.

Usage of older scripts
---
In the current repository you have two scripts `mvn-install.bat` and `mvn-install.sh` for Windows and *nix systems accordignly. It's strongly recommended to use `install-libs` tool, but in case you do want to follor the older way to install missing libs, you can use these scripts:

To install all Tridion dependencies (aka JAR files) in your local Maven repository as these are not available in Maven Central. The general command to do this is:

		mvn -q install:install-file -DgroupId=com.tridion -DartifactId=cd_broker -Dversion=7.1.0 -Dpackaging=jar -Dfile=cd_broker-7.1.0.jar
	
The script runs this command for all needed artifacts at once. You need to make sure that you run it in the directory where your Tridion jars are. Note that you have to rename them. For example, *cd_core.jar* has to become *cd_core-7.1.0.jar*. In addition to the Tridion jar, the script also installs the third party dependencies *easylicense-2.5.jar*,*sqljdbc4-4.0.0.jar*, *jdbcpool-1.0.jar* and the Tridion Contextual Web Delivery libraries should you need them.
