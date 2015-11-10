package com.sdl.webapp.common.api.mapping.semantic.config;

import java.util.Objects;

public final class FieldPath {

    private final String head;
    private final FieldPath tail;

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

    public String getHead() {
        return head;
    }

    public FieldPath getTail() {
        return tail;
    }

    public boolean hasTail() {
        return tail != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldPath fieldPath = (FieldPath) o;
        return Objects.equals(head, fieldPath.head) &&
                Objects.equals(tail, fieldPath.tail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, tail);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('/').append(head);
        if (tail != null) {
            sb.append(tail.toString());
        }
        return sb.toString();
    }
}
