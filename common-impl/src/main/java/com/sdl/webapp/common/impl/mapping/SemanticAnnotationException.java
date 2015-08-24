package com.sdl.webapp.common.impl.mapping;

/**
 * Thrown by {@code SemanticMappingRegistry} when semantic annotations are specified incorrectly on an entity class
 * or a field of an entity class.
 *
 * This is a {@code RuntimeException} because when this happens, it most likely means there is a bug in the code.
 */
public class SemanticAnnotationException extends RuntimeException {

    public SemanticAnnotationException(String message) {
        super(message);
    }
}
