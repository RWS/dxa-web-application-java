/**
 * DXA Data Model project contains R2 model and its related classes.
 * <p>DXA data model is extensible but there are some details of implementation that are important for you if you implement custom polymorphic classes:</p>
 * <ul>
 * <li>{@link com.fasterxml.jackson.annotation.JsonTypeName} annotation will automatically add your class as a supported for DXA polymorphic mapping</li>
 * <li>{@link com.sdl.dxa.api.datamodel.json.Polymorphic} annotation tells DXA mapper that this is a polymorphic class and its type is any subtype of the current class</li>
 * <li>{@link com.sdl.dxa.api.datamodel.model.util.HandlesHierarchyTypeInformation} is required on the parent if you have a parent-children class structure</li>
 * <li>Interfaces and abstract classes cannot be polymorphic, you need concrete classes as parents.</li>
 * <li>Your Data Model classes should be in this package or its subpackages.</li>
 * </ul>
 * <p>Here there are some examples of different class structure:</p>
 * <ol>
 * <li>A single class that is supported by polymorphic mapping: <pre><code>
 * package com.sdl.dxa.api.datamodel;
 * import com.fasterxml.jackson.annotation.JsonTypeName;
 * {@literal @}JsonTypeName
 * public class SingleClass { }
 * </code></pre></li>
 * <li>A class structure for parent-children hierarchy for polymorphic mapping: <pre><code>
 * package com.sdl.dxa.api.datamodel;
 * import com.fasterxml.jackson.annotation.JsonTypeName;
 * import com.sdl.dxa.api.datamodel.json.Polymorphic;
 * import com.sdl.dxa.api.datamodel.model.util.HandlesHierarchyTypeInformation;
 * {@literal @}JsonTypeName
 * {@literal @}Polymorphic // means can by any of subclasses
 * public class ParentClass implements HandlesHierarchyTypeInformation {
 *   {@literal @}Override
 *    public String getTypeId() { return "ParentClass"; }
 * }
 * {@literal @}JsonTypeName
 * public class FirstClass extends ParentClass { }
 * {@literal @}JsonTypeName
 * public class SecondClass extends ParentClass { }
 * </code></pre></li>
 * </ol>
 *
 * @dxa.publicApi Not everything in data Model is included as Public API in data model.
 * @dxa.publicApi Only {@link com.sdl.dxa.api.datamodel.json.Polymorphic} and {@link com.sdl.dxa.api.datamodel.model.util.HandlesHierarchyTypeInformation} are.
 * @dxa.publicApi These two are the only thing that you really need, the rest you can also use but consider they are not part of officially supported public API.
 */
package com.sdl.dxa.api.datamodel;