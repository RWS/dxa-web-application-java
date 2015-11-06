package com.sdl.webapp.common.api.mapping.config;

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

        if (head != null ? !head.equals(fieldPath.head) : fieldPath.head != null) return false;
        if (tail != null ? !tail.equals(fieldPath.tail) : fieldPath.tail != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = head != null ? head.hashCode() : 0;
        result = 31 * result + (tail != null ? tail.hashCode() : 0);
        return result;
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
