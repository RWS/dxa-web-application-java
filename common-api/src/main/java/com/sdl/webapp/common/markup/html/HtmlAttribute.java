package com.sdl.webapp.common.markup.html;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HtmlAttribute that = (HtmlAttribute) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}
