RWS Digital Experience Accelerator Java Spring MVC Web Application
===
Build status
------------
Java CI with Maven:
- Develop - ![Build Status](https://github.com/sdl/dxa-web-application-java/workflows/Build/badge.svg)
- 2.2 -![Build Status](https://github.com/sdl/dxa-web-application-java/workflows/Build/badge.svg?branch=release%2F2.2) 
- 1.8 -![Build Status](https://github.com/sdl/dxa-web-application-java/workflows/Build/badge.svg?branch=release%2F1.8)

About
-----
The RWS Digital Experience Accelerator (DXA) is a reference implementation of RWS Tridion Sites 10, 9.x and RWS Web 8 intended to help you create, design and publish an RWS Tridion/Web-based website quickly.

DXA is available for both .NET and Java web applications. Its modular architecture consists of a framework and example web application, which includes all core RWS Tridion/Web functionality as well as separate Modules for additional, optional functionality.

This repository contains the source code of the DXA Framework, an example Java web application, and a Maven archetype for Java.

The full DXA distribution (including Content Manager-side items and installation support) is downloadable from the [Releases in GitHub](https://github.com/sdl/dxa-web-application-java/releases).
Furthermore, the compiled DXA artifacts are available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cdxa).

To facilitate upgrades, we strongly recommend that you use official, compiled DXA artifacts from Maven Central instead of a custom build.
If you really must modify the DXA framework, we kindly request you to submit your changes as a Contribution (see the Branches and Contributions section below).

Compatibility Matrices
----------------------
<table>
    <tr>
        <th nowrap="nowrap">DXA Version</th>
        <th nowrap="nowrap">Tridion Sites 10</th>
        <th nowrap="nowrap">Tridion Sites 9.6</th>
        <th nowrap="nowrap">Tridion Sites 9.5</th>
        <th nowrap="nowrap">Tridion Sites 9.1</th>
    </tr>
    <tr>
        <td nowrap="nowrap">2.2.x</td>
        <td nowrap="nowrap" align="center">✔️</td>
        <td nowrap="nowrap" align="center">✔️</td>
        <td nowrap="nowrap" align="center">✔️</td>
        <td nowrap="nowrap" align="center">✔️</td>
    </tr>
    <tr text-align="center">
        <td nowrap="nowrap">2.3.x</td>
        <td nowrap="nowrap" align="center">✔️</td>
        <td nowrap="nowrap" align="center">✔️</td>
        <td nowrap="nowrap" align="center">✔️</td>
        <td nowrap="nowrap" align="center">✔️</td>
    </tr>
</table>

<table>
    <tr>
        <th nowrap="nowrap">DXA Version</th>
        <th nowrap="nowrap">JDK 1.8</th>
        <th nowrap="nowrap">JDK 11</th>
        <th nowrap="nowrap">JDK 17</th>
        <th nowrap="nowrap">Tomcat 9</th>
        <th nowrap="nowrap">Tomcat 10</th>
    </tr>
    <tr>
        <td nowrap="nowrap">2.2.x</td>
        <td nowrap="nowrap" align="center">✔️</td>
        <td nowrap="nowrap" align="center">✔️</td>
        <td nowrap="nowrap" align="center">❌</td>
        <td nowrap="nowrap" align="center">✔️</td>
        <td nowrap="nowrap" align="center">❌</td>
    </tr>
    <tr text-align="center">
        <td nowrap="nowrap">2.3.x</td>
        <td nowrap="nowrap" align="center">❌</td>
        <td nowrap="nowrap" align="center">❌</td>
        <td nowrap="nowrap" align="center">✔️</td>
        <td nowrap="nowrap" align="center">❌</td>
        <td nowrap="nowrap" align="center">✔️</td>
    </tr>
</table>

The latest `DXA 2.3` version includes the `Spring Framework version 6` and `Jakarta Servlet API` as dependencies.

Build
-----
You need Maven 3.2+ to build from source. Maven should be available in the system `PATH`.

`mvn install`

Support
-------
At RWS we take your investment in Digital Experience very seriously, if you encounter any issues with the Digital Experience Accelerator, please use one of the following channels:

- Report issues directly in [this repository](https://github.com/sdl/dxa-web-application-java/issues)
- Ask questions 24/7 on the RWS Tridion Community at https://tridion.stackexchange.com
- Contact RWS Professional Services for DXA release management support packages to accelerate your support requirements


Documentation
-------------
Documentation can be found online in the SDL documentation portal: https://docs.rws.com/publications/DXA

Updating the DXA Locally
------------------------
If you are updating an existing DXA web application, particularly to a minor version, the process involves updating the dependencies in Maven or Gradle, and recompiling your solution using the latest version of the DXA artifacts available from Maven Central. It should be taken into account any dependency updates to supporting libraries or frameworks such as Jackson and Spring.
RWS Professional Services can perform an upgrade scan of your web application. The outcome of this scan is a document highlighting the steps to take you to the next version. Please contact RWS for more information.

DXA Templating
-------------
Make sure to apply the latest updates to the DXA templating side in the Tridion Sites Content Manager. This is available from [this repository](https://github.com/RWS/dxa-content-management).
Instructions for updating the Content Manager can be found [here](https://docs.rws.com/784837/466718/sdl-digital-experience-accelerator-2-2/upgrading-------------dxa-on-the-------------sdl-tridion-sites-content-manager).

Repositories
-------------
You can find all the DXA related repositories [here](https://github.com/rws/?q=dxa&type=source&language=)


Branches and Contributions
--------------------------
We are using the following branching strategy:

 - `develop` - Represents the latest development version.
 - `release/x.y` - Represents the x.y Release. If hotfixes are applicable, they will be applied to the appropriate release branch so that the branch actually represents the initial release plus hotfixes.

All releases (including pre-releases and hotfix releases) are tagged.

Note that development sources (on `develop` branch) have dependencies on SNAPSHOT versions of the DXA artifacts, which are available here: https://oss.sonatype.org/content/repositories/snapshots/com/sdl/dxa/

If you wish to submit a Pull Request, it should normally be submitted on the `develop` branch so that it can be incorporated in the upcoming release.

Fixes for severe/urgent issues (that qualify as hotfixes) should be submitted as Pull Requests on the appropriate release branch.

Always submit an Issue for the problem, and indicate whether you think it qualifies as a hotfix. Pull Requests on release branches will only be accepted after agreement on the severity of the issue.
Furthermore, Pull Requests on release branches are expected to be extensively tested by the submitter.

Of course, it is also possible (and appreciated) to report an issue without associated Pull Requests.


DXA Builder
-----------
The current DXA Builder is available in Maven Central, and the latest DXA Builder is also available as a public snapshot.

If you have not configured a snapshot repository and don't want to, you may need to install the DXA Builder locally in order to run the SNAPSHOT.

To install it, run the wrapper script of the `dxa-builder` project: `gradlew(.bat) publishLocal`
On Windows, you can also just run `get-started.cmd` script at first run.


DD4T support
------------
DD4T 2.1 for Java is incorporated into the DXA codebase in the `dxa-compatible` artifact. As a result, when migrating from DD4T to DXA, you do not need separate dependencies on DD4T.


DXD version
-----------
The DXA 2.2 used to be based upon DXD 11.0.0 version. Since 2.2.12 the minimal required version for DXD is 11.0.1.
This version has no backward compatibility with 11.0.0 due to major database changes. So make sure you have completed all required task for DXD 11.0.1 installation.


Snapshots
---------
DXA publishes SNAPSHOT versions to Sonatype. To use them, configure `https://oss.sonatype.org/content/repositories/snapshots` as a repository in your Maven settings. Read [this](https://maven.apache.org/settings.html#Repositories) for instructions.


License
-------
Copyright (c) 2014-2024 RWS Group.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
