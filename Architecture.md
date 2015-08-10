Application Architecture
==========================

This document describes the architecture of the Tridion Reference Implementation Java web application.

The web application is a http://docs.spring.io/spring/docs/3.2.12.RELEASE/spring-framework-reference/htmlsingle/#spring-web[Spring Web MVC]
web application. It consists of the following modules:

* `webapp-main` - Main web application: Spring interceptors, controllers, views etc.
* `webapp-common-api` - Interfaces and view model classes
* `webapp-common-impl` - Implementations of common interfaces
* `webapp-dd4t` - DD4T-specific code
* `webapp-tridion` - Tridion-specific code
* `webapp-cid` - The optional Contextual Image Delivery module

## Module: webapp-main

The `webapp-main` module contains the main components of the web application. The web application is based on the Java
Servlet API version 3.1. Initialization and configuration is fully done in code, there is no `web.xml` deployment
descriptor.

The classes `com.sdl.webapp.main.WebAppInitializer` and `com.sdl.webapp.main.WebAppConfiguration` contain the code to
initialize and configure the web application. These classes register a number of components:

* Two interceptors: `LocalizationResolverInterceptor` and `StaticContentInterceptor`
* The Spring `ContextLoaderListener`
* The `AmbientDataServletFilter`, a servlet filter which is part of the Tridion Ambient Data Framework
* The Tridion Contextual Image Delivery `ImageTransformerServlet` (if it is available on the classpath)
* The Spring `DispatcherServlet`
* A Spring `ViewResolver` to find the views (JSPs) under `/WEB-INF/Views`

### Interceptors

Two interceptors handle incoming requests before the Spring `DispatcherServlet` passes them to a controller: the
`LocalizationResolverInterceptor` and the `StaticContentInterceptor`.

The `LocalizationResolverInterceptor` determines the current localization for each request and stores the localization
and other information in the `WebRequestContext`, a request-scoped Spring bean that is used by the other components of
the web application.

The `StaticContentInterceptor` checks if the request is for static content (for example a CSS or JavaScript file or an
image). It calls the current `Localization` to determine from the URL of the request if the request is for a static
content item. If it is, it uses the `StaticContentProvider` to get the item and send it to the client. The request
will not be passed on to a controller in that case.

### Controllers

If the request is not for a static content item, it will be passed to one of the controllers. Most requests will be
handled by the method `MainController.handleGetPage`. This method calls the `ContentProvider` to get the page content
as a view model object. This view model object is then passed to the appropriate view to be rendered as HTML.

The page view will include TRI-specific tags to include regions and entities on the page. To include those regions and
entities, include requests will be made to other controllers, such as `RegionController` and `EntityController`. Those
controllers will render the appropriate region and entity views which will be included in the page.

## Module: webapp-common-api

The `webapp-common-api` module contains interfaces for components of the web application and the view model classes.
The most important interfaces are:

* `ContentProvider` - Component that can provide the content for a page, including the regions and entities on the page.
* `ContentResolver` - Component that can resolve links in the content.
* `NavigationProvider` - Component that can provide information about the navigation structure of the website.
* `StaticContentProvider` - Component that can provide static content items.
* `Localization` - Component that provides information about a language-specific section on the website.
* `LocalizationFactory` - Factory that can create `Localization` instances.
* `LocalizationResolver` - Component that can determine the localization for a request.
* `SemanticMapper` - Component that helps in translating from the CMS data model to the view model.
* `MediaHelper` - Component that provides information for the URLs of images.
* `WebRequestContext` - Request-scoped component containing information relevant for the current request.

These interfaces are implemented in the other modules.

The package `com.sdl.webapp.common.api.model` contains interfaces and classes for the view model objects. The
fundamental interfaces in this package are:

* `Entity` - Contains information on a single entity on the page.
* `Region` - A region on the page which may contain a number of entities.
* `Page` - A complete page, which may consist of a number of regions.

The package `com.sdl.webapp.common.api.model.entity` contains classes for different kinds of entities that can be used
on the website.

## Module webapp-common-impl

This module contains implementations for the interfaces in `webapp-common-api` that are not specific to DD4T or Tridion.

## Module webapp-dd4t

This module contains code that is specific for DD4T. Note that this module is the only module that has direct
dependencies on the DD4T libraries.

It contains implementations of `ContentProvider`, `StaticContentProvider` and `NavigationProvider` that get content from
Tridion via DD4T. These implementations use the `SemanticMapper` to convert the DD4T-specific model objects to view
model objects that are not specific to DD4T.

## Module webapp-tridion

This module contains code that is specific to Tridion, but not DD4T. It contains an implementation of
`LocalizationResolver` that uses the Tridion CD Dynamic Content API to determine which Tridion publication should be
used to get data for a request. It also contains functionality for doing broker queries.

## Module webapp-cid

This optional module contains an implementation of interface `MediaHelper` that transforms image URLs so that they will
be handled by the `ImageTransformerServlet` that is part of Contextual Image Delivery.

