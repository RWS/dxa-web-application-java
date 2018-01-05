# DXA Configuration manual

The framework and web application is configurable, and this short manual briefly desribes how you configure your application.

Application is configured differently in different parts of the system:
- SDL Web CIL configuration;
- DXA Framework configuration;
- Common systems (like logback) configuration.

You can read about all these in a separate topic below.

## SDL Web CIL configuration
Some of the most important properties are listed here. 
For detailed explanation, please refer to the official SDL Web documentation at the [Docs portal](http://docs.sdl.com).

- `cd_client.xml` configures communication with CIS services. 
    - `/ServiceConfig/@CacheEnabled` enables or disables caching for CIL. If CIL is using `GeneralCacheProvider`, 
    then DXA cache also uses this setting.
    - `/ServiceConfig/DiscoveryService/@ServiceUri` sets the Discovery Service URL.
- `cd_ambient_conf.xml` configures ADF or Context Service functionality.

## DXA Framework configuration
This configuration is _mostly_ based on *.properties files. You can find details of how at the [Docs portal](http://docs.sdl.com).

### Property resolving, in short
DXA loads all the `dxa.**.properties` files that it can find in `classpath` including those inside JARs.
Depending on the name it sets the priority and tries to iterate over the files to find a value. WHen the property is found, it stops looking for it.
The priority is the following (checks first > checks last)

`dxa.addons.*.propeties` > `dxa.properties` > `dxa.modules.*.properties` > `dxa.defaults.propeties`.

If the property is now found, it's either replaced with default or no value.
`addons` mechanism is useful if you need to replace a single property and not the whole file (like to switch between staging and live)

### Where to find properties
All the (_or most of them_) properties are just next to this file. Each properties is commented out, 
and there is an explanation next to each property what it does.

## Common systems
- `logback.xml`, refer to Logback documentation for details.
- `ehcache.xml`, refer to EhCache 3 documentation for details. Out of the box, DXA comes with the minimal configuration.
If DXA needs cache, and the cache is not pre-configured in this file, then depending on what CIL is using, 
DXA will create a cache using either values from `cd_client_conf.xml` or `ehcache.xml`. Some cache-related properties you
will also find in `dxa.properties` file. Refer to the DXA Cache documentation for more details.