package com.sdl.webapp.common.impl.mapping;

/**
 * To be thrown when semantic annotations are specified incorrectly on an entity class or a field of an entity class.
 * <p>This is a {@link RuntimeException} because when this happens, it most likely means there is a bug in the code.</p>
 */
public class SemanticAnnotationException extends RuntimeException {

    public SemanticAnnotationException(String message) {
        super(message);
    }
}
