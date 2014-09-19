package com.sdl.tridion.referenceimpl.common.model;

public class EntityImpl implements Entity {

    public static abstract class Builder<B extends Builder<?, ? extends T>, T extends EntityImpl> {
        private String id;
        private String viewName;

        protected Builder() {
        }

        @SuppressWarnings("unchecked")
        public B setId(String id) {
            this.id = id;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B setViewName(String viewName) {
            this.viewName = viewName;
            return (B) this;
        }

        public abstract T build();
    }

    private final String id;
    private final String viewName;

    protected EntityImpl(Builder<? extends Builder, ? extends EntityImpl> builder) {
        this.id = builder.id;
        this.viewName = builder.viewName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getViewName() {
        return viewName;
    }

    @Override
    public String toString() {
        return String.format("%s { id=%s, viewName=%s }", this.getClass().getSimpleName(), id, viewName);
    }
}
