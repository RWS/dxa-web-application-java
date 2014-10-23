package com.sdl.webapp.common.impl.mapping;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.model.Entity;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.common.impl.mapping.SemanticInfoRegistry.DEFAULT_PREFIX;
import static com.sdl.webapp.common.impl.mapping.SemanticInfoRegistry.DEFAULT_VOCABULARY;

class SemanticEntityInfo {

    private final String entityName;
    private final String vocabulary;
    private final String prefix;
    private final boolean public_;

    private final List<SemanticPropertyInfo> propertyInfo = new ArrayList<>();

    public SemanticEntityInfo(String entityName, String vocabulary, String prefix, boolean public_) {
        this.entityName = entityName;
        this.vocabulary = vocabulary;
        this.prefix = prefix;
        this.public_ = public_;
    }

    public SemanticEntityInfo(SemanticEntity annotation, Class<? extends Entity> entityClass) {
        String s = annotation.entityName();
        if (Strings.isNullOrEmpty(s)) {
            s = Strings.nullToEmpty(annotation.value());
        }
        if (Strings.isNullOrEmpty(s)) {
            s = entityClass.getSimpleName();
        }

        String v = annotation.vocabulary();
        if (Strings.isNullOrEmpty(v)) {
            v = DEFAULT_VOCABULARY;
        }

        String p = annotation.prefix();
        if (Strings.isNullOrEmpty(p)) {
            p = DEFAULT_PREFIX;
        }

        this.entityName = s;
        this.vocabulary = v;
        this.prefix = p;
        this.public_ = annotation.public_();
    }

    public String getEntityName() {
        return entityName;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isPublic() {
        return public_;
    }

    public List<SemanticPropertyInfo> getPropertyInfo() {
        return propertyInfo;
    }

    public void addPropertyInfo(SemanticPropertyInfo info) {
        this.propertyInfo.add(info);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SemanticEntityInfo that = (SemanticEntityInfo) o;

        if (entityName != null ? !entityName.equals(that.entityName) : that.entityName != null) return false;
        if (prefix != null ? !prefix.equals(that.prefix) : that.prefix != null) return false;
        if (vocabulary != null ? !vocabulary.equals(that.vocabulary) : that.vocabulary != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = entityName != null ? entityName.hashCode() : 0;
        result = 31 * result + (vocabulary != null ? vocabulary.hashCode() : 0);
        result = 31 * result + (prefix != null ? prefix.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SemanticEntityInfo{" +
                "entityName='" + entityName + '\'' +
                ", vocabulary='" + vocabulary + '\'' +
                ", prefix='" + prefix + '\'' +
                ", public_=" + public_ +
                ", propertyInfo=" + propertyInfo +
                '}';
    }
}
