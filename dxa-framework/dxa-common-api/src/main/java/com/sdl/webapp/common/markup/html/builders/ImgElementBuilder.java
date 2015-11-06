package com.sdl.webapp.common.markup.html.builders;

public final class ImgElementBuilder extends AbstractElementBuilder<ImgElementBuilder> {

    public ImgElementBuilder() {
        super("img", false);
    }

    public ImgElementBuilder withSrc(String src) {
        return withAttribute("src", src);
    }

    public ImgElementBuilder withAlt(String alt) {
        return withAttributeIfNotEmpty("alt", alt);
    }

    public ImgElementBuilder withWidth(String width) {
        return withAttributeIfNotEmpty("width", width);
    }

    public ImgElementBuilder withWidth(int width) {
        return withWidth(Integer.toString(width));
    }

    public ImgElementBuilder withHeight(String height) {
        return withAttributeIfNotEmpty("height", height);
    }

    public ImgElementBuilder withHeight(int height) {
        return withHeight(Integer.toString(height));
    }
}
