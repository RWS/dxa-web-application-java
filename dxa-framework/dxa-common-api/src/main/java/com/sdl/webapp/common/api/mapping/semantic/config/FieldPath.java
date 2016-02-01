package com.sdl.webapp.common.api.mapping.semantic.config;

import java.util.Objects;

/**
 * <p>FieldPath class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
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

    /**
     * <p>Getter for the field <code>head</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHead() {
        return head;
    }

    /**
     * <p>Getter for the field <code>tail</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.mapping.semantic.config.FieldPath} object.
     */
    public FieldPath getTail() {
        return tail;
    }

    /**
     * <p>hasTail.</p>
     *
     * @return a boolean.
     */
    public boolean hasTail() {
        return tail != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldPath fieldPath = (FieldPath) o;
        return Objects.equals(head, fieldPath.head) &&
                Objects.equals(tail, fieldPath.tail);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(head, tail);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(32);
        sb.append('/').append(head);
        if (tail != null) {
            sb.append(tail.toString());
        }
        return sb.toString();
    }
}
