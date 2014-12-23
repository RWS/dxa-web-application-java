DD4T 2 ChangeLog

+ Moved /dd4t-api/src/main/java/org/dd4t/contentmodel/exceptions to /dd4t-api/src/main/java/org/dd4t/core/exceptions
- Removed BrokerCacheProvider
+ Providers have 2 classes - one for the base operation, one for the caching
  and possible compression if used in a service kind of way

## View Model Loading

+ Models are loaded by putting a @ViewModel("name") annotation on a model class. This
  name must match the viewName Metadata set on the Component Templates. The deserializer then
  attempts to load the class(es) annotated with this viewName and deserialize into the found
  class(es)
+ View Models are by default put in the componentPresentations node of the PageImpl object AND
  will be put on the HttpRequest in the post-processing phase of the PageFactory actions, if
  there is a HttpRequest available. This way, both the <jsp:useBean /> as well as custom tags
  for processing can be used.
+ If no viewName is set on the Component Template, the data is deserialized into a normal
  ComponentImpl
+ Use the @SemanticEntity and @SemanticProperty annotations..



## XML processing
+ Removed from this version, but should be added back with a 
+ cleaned up Simple Framework or otherwise. TBD

Principles:

+ No Tridion dependencies anywhere apart from the providers (Under discussion, what to do with ADF?)
+ Minimum version: Java 7 (which means Tridion 2013)
+ Load dd4t-api, dd4t-core. dd4t-providers externally in your website project. Use Nexus, or
  even better: Maven Central
