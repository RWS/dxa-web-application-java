package com.sdl.webapp.common.markup.html.builders;

public final class HtmlBuilders {

    private HtmlBuilders() {
    }

    public static SimpleElementBuilder element(String tagName, boolean closeTag) {
        return new SimpleElementBuilder(tagName, closeTag);
    }

    public static SimpleElementBuilder element(String tagName) {
        return element(tagName, true);
    }

    public static SimpleElementBuilder div() {
        return element("div");
    }

    public static SimpleElementBuilder span() { return element("span"); }

    public static SimpleElementBuilder i() {
        return element("i");
    }

    public static SimpleElementBuilder iframe() {
        return element("iframe");
    }

    public static AnchorElementBuilder a() {
        return new AnchorElementBuilder();
    }

    public static AnchorElementBuilder a(String href) {
        return a().withHref(href);
    }

    public static ButtonElementBuilder button() {
        return new ButtonElementBuilder();
    }

    public static ButtonElementBuilder button(String type) {
        return button().ofType(type);
    }

    public static ImgElementBuilder img() {
        return new ImgElementBuilder();
    }

    public static ImgElementBuilder img(String src) {
        return img().withSrc(src);
    }
}
