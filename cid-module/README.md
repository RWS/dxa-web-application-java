Contextual Image Delivery
=============================

This is an optional module for the Tridion Reference Implementation Java web application. It contains an implementation
of interface `MediaHelper` that uses Tridion Contextual Image Delivery (CID) to resize images on the server side.

Note that in order to use Tridion CID, you need a license. If you do not have a license for CID or if you are not sure,
then contact SDL if you want to use CID.

You can also choose to not use CID. The web application will then use a different implementation of `MediaHelper` to
resize images.

In the module `webapp-common-impl` there is a Spring factory bean `MediaHelperFactory` that automatically chooses the
implementation of `MediaHelper` to use. It does this by checking if the CID-specific implementation is available in the
classpath. If it is, it will use the CID-specific implementation, otherwise it will use the default implementation.

## Enabling or disabling the use of CID

When you activate the Maven profile `cid`, this module and its dependencies will be included in the project. This means
you can compile the web application with support for CID by compiling the project with:

    mvn clean package -P cid

If you do not want to use CID, simply leave off `-P cid`.

Note that if you do use CID, you will have to edit the configuration file `cd_ambient_conf` in
`example-webapp\src\main\resources` to enable the CID Ambient Data Framework cartridge.
