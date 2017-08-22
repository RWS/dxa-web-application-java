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