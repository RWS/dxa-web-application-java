dxa-web-application-java
===
SDL Digital Experience Accelerator Java Spring MVC web application

About
-----
The SDL Digital Experience Accelerator (DXA) is a reference implementation of SDL Web 8 and SDL Tridion 2013 SP1 intended to help you create, design and publish an SDL Web/Tridion-based website quickly.

It is available for .NET and Java Web Applications and has a modular architecture consisting of a Framework and example web application providing core functionality and separate Modules for additional, optional functionality.

This repository contains the source code of the DXA Framework, example web application and Maven archetype for Java. 
The full DXA distribution (including CM-side items and installation support) is downloadable from the [SDL Community site](https://community.sdl.com/developers/tridion_developer/m/mediagallery/1241) (latest version)
or the [Releases in GitHub](https://github.com/sdl/dxa-web-application-java/releases) (all versions)

Furthermore, the compiled DXA artifacts are available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cdxa). 
To facilitate upgrades, it is highly recommended to use official, compiled DXA artifacts from Maven Central instead of a custom build.
If you really have to modify the DXA Framework, we kindly request you to submit your changes as a Contribution; see below. 



Support
---------------
At SDL we take your investment in Digital Experience very seriously, and will do our best to support you throughout this journey. 
If you encounter any issues with the Digital Experience Accelerator, please reach out to us via one of the following channels:

- Report issues directly in [this repository](https://github.com/sdl/dxa-web-application-java/issues)
- Ask questions 24/7 on the SDL Web Community at https://tridion.stackexchange.com
- Contact Technical Support through the Customer Support Web Portal at https://www.sdl.com/support


Documentation
-------------
Documentation can be found online in the SDL documentation portal: http://docs.sdl.com/sdldxa


Repositories
------------
The following repositories with source code are available:

 - https://github.com/sdl/dxa-content-management - Core Template Building Blocks
 - https://github.com/sdl/dxa-html-design - Whitelabel HTML Design
 - https://github.com/sdl/dxa-modules - Modules (.NET and Java)
 - https://github.com/sdl/dxa-web-application-dotnet - ASP.NET MVC web application (incl. framework)
 - https://github.com/sdl/dxa-web-application-java - Java Spring MVC web application (incl. framework)


Branches and Contributions
--------------------------
We are using the following branching strategy:

 - `master` - Represents the latest stable version. This may be a pre-release version (tagged as `DXA x.y Sprint z`). Updated each development Sprint (approx. bi-weekly).
 - `develop` - Represents the latest development version. Updated very frequently (typically nightly).
 - `release/x.y` - Represents the x.y Release. If hotfixes are applicable, they will be applied to the appropriate release branch, so that the release branch actually represent the initial release plus hotfixes.

All releases (including pre-releases and hotfix releases) are tagged. 

Note that development sources (on `develop` branch) have dependencies on SNAPSHOT versions of the DXA artifacts, which are available here: https://oss.sonatype.org/content/repositories/snapshots/com/sdl/dxa/

If you wish to submit a Pull Request, it should normally be submitted on the `develop` branch, so it can be incorporated in the upcoming release.

Fixes for really severe/urgent issues (which qualify as hotfixes) should be submitted as Pull Request on the appropriate release branch.

Please always submit an Issue for the problem and indicate whether you think it qualifies as a hotfix; Pull Requests on release branches will only be accepted after agreement on the severity of the issue.
Furthermore, Pull Requests on release branches are expected to be extensively tested by the submitter.

Of course, it's also possible (and appreciated) to report an Issue without associated Pull Requests.


Prerequisites for building
--------------------------
In order to build the DXA sources against SDL Tridion 2013 SP1, you need these artifacts (`groudId : artifactId : version`) to be installed into your Maven repository:
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
 - `com.vs.ezlicrun : easylicense : 2.5`

To use SmartTarget module you also need
 - `com.tridion.smarttarget : session_cartridge : 2014sp1`
 - `com.tridion.smarttarget : smarttarget_cartridge : 2014sp1`
 - `com.tridion.smarttarget : smarttarget_core : 2014sp1`
 - `com.tridion.smarttarget : smarttarget_entitymodel : 2014sp1`
 - `com.tridion.smarttarget : smarttarget_google-analytics : 2014sp1`

To ease the installation you can use the latest [`/install-libs/`](https://github.com/sdl/dxa-web-application-java/tree/develop/install-libs). For help, run `install`.

**'dxa-framework' project has a Maven profile '2013sp1' which should be enabled to build tridion-provider for that version: `mvn install -P 2013sp1`**

License
-------
Copyright (c) 2014-2016 SDL Group.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
