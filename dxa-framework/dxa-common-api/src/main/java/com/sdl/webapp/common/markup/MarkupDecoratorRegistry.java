package com.sdl.webapp.common.markup;

import java.util.List;

/**
 * MarkupDecoratorRegistry
 *
 * @author nic
 */
public interface MarkupDecoratorRegistry {

    public void registerDecorator(String decoratorId, MarkupDecorator decorator);

    public List<MarkupDecorator> getDecorators(String decoratorId);
}
