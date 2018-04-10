package com.sdl.webapp.common.markup.html.builders;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.markup.html.HtmlTextNode;

/**
 * @dxa.publicApi
 */
public abstract class AbstractElementBuilder<B extends AbstractElementBuilder<B>> {

    private final String tagName;

    private final boolean closeTag;

    private final ImmutableList.Builder<HtmlAttribute> attributes = ImmutableList.builder();

    private final ImmutableList.Builder<HtmlNode> content = ImmutableList.builder();

    public AbstractElementBuilder(String tagName, boolean closeTag) {
        this.tagName = tagName;
        this.closeTag = closeTag;
    }

    public B withAttribute(HtmlAttribute attribute) {
        this.attributes.add(attribute);
        //noinspection unchecked
        return (B) this;
    }

    public B withAttribute(String name, String value) {
        return withAttribute(new HtmlAttribute(name, value));
    }

    public B withAttributeIfNotEmpty(String name, String value) {
        //noinspection unchecked
        return !Strings.isNullOrEmpty(value) ? withAttribute(name, value) : (B) this;
    }

    public B withId(String id) {
        return withAttribute("id", id);
    }

    public B withClass(String cssClass) {
        return withAttributeIfNotEmpty("class", cssClass);
    }

    public B withNode(HtmlNode node) {
        this.content.add(node);
        //noinspection unchecked
        return (B) this;
    }

    public B withTextualContent(String text) {
        return withNode(new HtmlTextNode(text));
    }

    public B withPureHtmlContent(String text) {
        return withNode(new HtmlTextNode(text, false));
    }

    public HtmlElement build() {
        return new HtmlElement(tagName, closeTag, attributes.build(), content.build());
    }
}
