package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.span;

@EqualsAndHashCode(callSuper = true)
@Data
public class ViewNotFoundEntityError extends AbstractEntityModel {

    public String renderHtml() throws DxaException {
        return this.toHtmlElement().renderHtml();
    }

    @Override
    public HtmlElement toHtmlElement() throws DxaException {
        return span().withTextualContent("No view found for " + getMvcData()).build();
    }

    @Override
    public MvcData getMvcData() {
        return MvcDataCreator.creator()
                .fromQualifiedName("Shared:Error:ViewNotFoundError")
                .defaults(DefaultsMvcData.ERROR_ENTITY)
                .create();
    }
}
