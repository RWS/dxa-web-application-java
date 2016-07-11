package com.sdl.webapp.common.api.mapping.semantic.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
public final class FieldPath {

    private final String head;
    private final FieldPath tail;

    /**
     * <p>Constructor for FieldPath.</p>
     *
     * @param path a {@link java.lang.String} object.
     */
    public FieldPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        final int i = path.indexOf('/');
        if (i < 0) {
            this.head = path;
            this.tail = null;
        } else {
            this.head = path.substring(0, i);
            this.tail = new FieldPath(path.substring(i));
        }
    }

    public boolean hasTail() {
        return tail != null;
    }
}
