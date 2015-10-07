package com.sdl.webapp.common.api.mapping.config;

import com.sdl.webapp.common.api.mapping.SemanticMapping;

/**
 * Created by TW on 10/7/2015.
 */
public class SchemaSemantics {

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    private String prefix;
    private String entity;


    public SchemaSemantics(String entity){
        this(SemanticMapping.DEFAULT_VOCABULARY,entity);
    }
    public SchemaSemantics(){}
    public SchemaSemantics(String prefix, String entity){
        this.prefix = prefix;
        this.entity = entity;
    }
}
