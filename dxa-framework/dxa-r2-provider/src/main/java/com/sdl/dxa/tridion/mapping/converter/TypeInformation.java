package com.sdl.dxa.tridion.mapping.converter;

import lombok.Builder;
import lombok.Value;

import java.util.Collection;

@Value
@Builder
public class TypeInformation {

    private Class<?> objectType;

    private Class<? extends Collection> collectionType;

    public boolean isCollection() {
        return collectionType != null;
    }
}
