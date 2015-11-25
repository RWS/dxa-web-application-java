package com.sdl.webapp.common.markup;

import java.util.List;

public interface MarkupDecoratorRegistry {

    void registerDecorator(String decoratorId, MarkupDecorator decorator);

    List<MarkupDecorator> getDecorators(String decoratorId);
}
