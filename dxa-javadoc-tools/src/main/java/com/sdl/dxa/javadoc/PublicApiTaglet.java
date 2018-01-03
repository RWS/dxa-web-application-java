package com.sdl.dxa.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Taglet that supports a custom tag {@code dxa.publicApi} and adds information to Javadoc about the DXA public API.
 * Supports multiple similar tags and all code elements.
 *
 * @see <a href="https://maven.apache.org/plugins/maven-javadoc-plugin/examples/taglet-configuration.html">How to use custom taglet with Maven</a>
 */
public class PublicApiTaglet implements Taglet {

    private static final String HEADER = "This is DXA Public API";

    @SuppressWarnings("unused")
    public static void register(Map<String, Taglet> tagletMap) {
        PublicApiTaglet taglet = new PublicApiTaglet();
        tagletMap.put(taglet.getName(), taglet);
    }

    @Override
    public boolean inField() {
        return true;
    }

    @Override
    public boolean inConstructor() {
        return true;
    }

    @Override
    public boolean inMethod() {
        return true;
    }

    @Override
    public boolean inOverview() {
        return true;
    }

    @Override
    public boolean inPackage() {
        return true;
    }

    @Override
    public boolean inType() {
        return true;
    }

    @Override
    public boolean isInlineTag() {
        return false;
    }

    @Override
    public String getName() {
        return Constants.PUBLIC_API_TAG;
    }

    /**
     * Given the <code>Tag</code> representation of this custom tag, return its string representation.
     *
     * @param tag the <code>Tag</code> representation of this custom tag.
     */
    @Override
    public String toString(Tag tag) {
        return toString(tag.text());
    }

    /**
     * Given an array of <code>Tag</code>s representing this custom
     * tag, return its string representation.
     *
     * @param tags the array of <code>Tag</code>s representing of this custom tag.
     */
    @Override
    public String toString(Tag[] tags) {
        if (tags.length == 0) {
            return null;
        }

        if (tags.length == 1) {
            return toString(tags[0]);
        }

        return toString("<ul><li>" +
                Arrays.stream(tags)
                        .map(Tag::text)
                        .collect(Collectors.joining("</li><li>")) +
                "</li></ul>");
    }

    private String toString(String body) {
        return "<dt>" +
                "<strong>" + HEADER + "</strong>" +
                "<dd>" +
                "<table cellpadding=2 cellspacing=0>" +
                "<tr>" +
                "<td>" + body + "</td>" +
                "</tr>" +
                "</table>" +
                "</dd>" +
                "</dt>";
    }
}
