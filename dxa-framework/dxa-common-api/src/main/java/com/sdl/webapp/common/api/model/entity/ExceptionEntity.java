package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.mvcdata.DefaultsMvcData;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

/**
 * Exception entity that will be shows on a page as an exception block without braking the whole page.
 *
 * @dxa.publicApi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionEntity extends AbstractEntityModel {

    private Exception exception;

    @Override
    public HtmlElement toHtmlElement() throws DxaException {
        return HtmlBuilders.empty().withTextualContent(exception.getLocalizedMessage()).build();
    }

    @Nullable
    @Override
    public MvcData getDefaultMvcData() {
        return MvcDataCreator.creator()
                .fromQualifiedName("Shared:Entity:ExceptionEntity")
                .defaults(DefaultsMvcData.ENTITY).create();
    }
}
