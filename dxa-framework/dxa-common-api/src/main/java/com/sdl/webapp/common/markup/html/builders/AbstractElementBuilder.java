package com.sdl.webapp.common.markup.html.builders;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.HtmlTextNode;

/**
 * <p>Abstract AbstractElementBuilder class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public abstract class AbstractElementBuilder<B extends AbstractElementBuilder<B>> {

    private final String tagName;

    private final boolean closeTag;

    private final ImmutableList.Builder<HtmlAttribute> attributes = ImmutableList.builder();

    private final ImmutableList.Builder<HtmlNode> content = ImmutableList.builder();

    /**
     * <p>Constructor for AbstractElementBuilder.</p>
     *
     * @param tagName  a {@link java.lang.String} object.
     * @param closeTag a boolean.
     */
    public AbstractElementBuilder(String tagName, boolean closeTag) {
        this.tagName = tagName;
        this.closeTag = closeTag;
    }

    /**
     * <p>withAttribute.</p>
     *
     * @param attribute a {@link com.sdl.webapp.common.markup.html.HtmlAttribute} object.
     * @return a B object.
     */
    public B withAttribute(HtmlAttribute attribute) {
        this.attributes.add(attribute);
        //noinspection unchecked
        return (B) this;
    }

    /**
     * <p>withAttribute.</p>
     *
     * @param name  a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return a B object.
     */
    public B withAttribute(String name, String value) {
        return withAttribute(new HtmlAttribute(name, value));
    }

    /**
     * <p>withAttributeIfNotEmpty.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param value a {@link java.lang.String} object.
     * @return a B object.
     */
    public B withAttributeIfNotEmpty(String name, String value) {
        //noinspection unchecked
        return !Strings.isNullOrEmpty(value) ? withAttribute(name, value) : (B) this;
    }

    /**
     * <p>withId.</p>
     *
     * @param id a {@link java.lang.String} object.
     * @return a B object.
     */
    public B withId(String id) {
        return withAttribute("id", id);
    }

    /**
     * <p>withClass.</p>
     *
     * @param cssClass a {@link java.lang.String} object.
     * @return a B object.
     */
    public B withClass(String cssClass) {
        return withAttributeIfNotEmpty("class", cssClass);
    }

    /**
     * <p>withNode.</p>
     *
     * @param node a {@link com.sdl.webapp.common.markup.html.HtmlNode} object.
     * @return a B object.
     */
    public B withNode(HtmlNode node) {
        this.content.add(node);
        //noinspection unchecked
        return (B) this;
    }

    /**
     * <p>withTextualContent.</p>
     *
     * @param text a {@link java.lang.String} object.
     * @return a B object.
     */
    public B withTextualContent(String text) {
        return withNode(new HtmlTextNode(text));
    }

    /**
     * <p>withPureHtmlContent.</p>
     *
     * @param text a {@link java.lang.String} object.
     * @return a B object.
     */
    public B withPureHtmlContent(String text) {
        return withNode(new HtmlTextNode(text, false));
    }

    /**
     * <p>build.</p>
     *
     * @return a {@link com.sdl.webapp.common.markup.html.HtmlElement} object.
     */
    public HtmlElement build() {
        return new HtmlElement(tagName, closeTag, attributes.build(), content.build());
    }
}
