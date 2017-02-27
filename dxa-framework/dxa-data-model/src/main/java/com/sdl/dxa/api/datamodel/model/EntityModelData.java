package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.model.util.CanWrapData;
import com.sdl.dxa.api.datamodel.model.util.ModelDataWrapper;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@JsonTypeName
@ToString
@NoArgsConstructor
@Getter
public class EntityModelData extends ViewModelData implements CanWrapData {

    private String id;

    private String linkUrl;

    private ContentModelData content;

    private BinaryContentData binaryContent;

    private ExternalContentData externalContent;

    @Builder
    public EntityModelData(String schemaId, String htmlClasses, Map<String, Object> xpmMetadata, ContentModelData metadata, Map<String, Object> extensionData, MvcModelData mvcData, String id, String linkUrl, ContentModelData content, BinaryContentData binaryContent, ExternalContentData externalContent) {//NOSONAR
        super(schemaId, htmlClasses, xpmMetadata, metadata, extensionData, mvcData);
        this.id = id;
        this.linkUrl = linkUrl;
        this.content = content;
        this.binaryContent = binaryContent;
        this.externalContent = externalContent;
    }

    @Builder
    public EntityModelData(String id, String linkUrl, ContentModelData content, BinaryContentData binaryContent, ExternalContentData externalContent) {
        this.id = id;
        this.linkUrl = linkUrl;
        this.content = content;
        this.binaryContent = binaryContent;
        this.externalContent = externalContent;
    }

    @Override
    public ModelDataWrapper getDataWrapper() {
        return new ModelDataWrapper() {
            @Override
            public ContentModelData getContent() {
                return EntityModelData.this.getContent();
            }

            @Override
            public ContentModelData getMetadata() {
                return EntityModelData.this.getMetadata();
            }

            @Override
            public Object getWrappedModel() {
                return EntityModelData.this;
            }
        };
    }
}
