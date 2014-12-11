package com.sdl.webapp.main.markup.html;

public final class HtmlAttribute extends HtmlRenderable {

    private final String name;

    private final String value;

    public HtmlAttribute(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    protected String renderHtml() {
        // NOTE: This escapes double quotes, ampersands and left angle brackets in the value,
        // just like the C# method HttpUtility.HtmlAttributeEncode
        return String.format("%s=\"%s\"", name,
                value.replaceAll("&", "&amp;").replaceAll("\"", "&quot;").replaceAll("<", "&lt;"));
    }
}
