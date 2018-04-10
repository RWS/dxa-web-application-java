# DXA Configuration manual

The framework and web application are configurable, and this readme briefly describes how you configure your application.

Application is configured differently in different parts of the system:
- SDL Web CIL configuration
- DXA Framework configuration
- Common systems (like logback) configuration

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
This configuration is _mostly_ based on *.properties files. You can find details of how it is done at the DXA documentation on the [Docs portal](http://docs.sdl.com).

### Property resolution overview
DXA loads all the `dxa.**.properties` files that it can find in `classpath` including those inside JARs.
Depending on the name, it sets the priority and tries to iterate over the files to find a value. When the property is found, it stops looking for it.
The priority is the following (checks first > checks last):

`dxa.addons.*.propeties` > `dxa.properties` > `dxa.modules.*.properties` > `dxa.defaults.propeties`.

If the property is found, it is replaced either with the default value or no value.
The `addons` mechanism is useful if you need to replace a single property and not the whole file (like to switch between staging and live).

### Where to find properties
All the properties (_or most of them_) are just next to this file. Each property is commented out, 
and includes an explanation of what the property does.

## Common systems
- `logback.xml`, refer to Logback documentation for details.
- `ehcache.xml`, refer to EhCache 3 documentation for details. Out of the box, DXA comes with the minimal configuration.
If DXA needs cache and the cache is not preconfigured in this file, then depending on what CIL is using, 
DXA will create a cache using values either from `cd_client_conf.xml` or `ehcache.xml`. You
will also find some cache-related properties in the `dxa.properties` file. Refer to the DXA Cache documentation for more details.