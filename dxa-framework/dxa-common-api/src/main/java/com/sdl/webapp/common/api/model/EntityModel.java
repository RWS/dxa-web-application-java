package com.sdl.webapp.common.api.model;

import java.util.Map;

/**
 * EntityModel interface represents an entity.
 * <p>It is important to know about single/plural ending convention for fields mapping.
 * <a href="http://schema.org/">Schema.org</a> &amp; CM say that a single element even if we have a list of those,
 * because CM operates with the whole collection as with single entities.
 * So if we actually have multiple elements it was decided to call the field in plural in DXA.</p>
 * <pre><code>
 *     // is mapped to 'element' in singular form
 *     private List&lt;String&gt; elements;
 * </code></pre>
 *
 * @dxa.publicApi
 */
public interface EntityModel extends ViewModel {

    String getId();

    Map<String, String> getXpmPropertyMetadata();
}
