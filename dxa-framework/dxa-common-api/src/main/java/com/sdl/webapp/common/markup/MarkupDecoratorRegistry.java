package com.sdl.webapp.common.markup;

import java.util.List;

/**
 * <p>MarkupDecoratorRegistry interface.</p>
 * @deprecated todo dxa2 remove in preference of autowired bean list
 */
@Deprecated
public interface MarkupDecoratorRegistry {

    /**
     * <p>registerDecorator.</p>
     *
     * @param decoratorId a {@link java.lang.String} object.
     * @param decorator   a {@link com.sdl.webapp.common.markup.MarkupDecorator} object.
     */
    void registerDecorator(String decoratorId, MarkupDecorator decorator);

    /**
     * <p>getDecorators.</p>
     *
     * @param decoratorId a {@link java.lang.String} object.
     * @return a {@link java.util.List} object.
     * @deprecated todo dxa2 registering decorators should be done against the class and not the string-name
     */
    List<MarkupDecorator> getDecorators(String decoratorId);
}
