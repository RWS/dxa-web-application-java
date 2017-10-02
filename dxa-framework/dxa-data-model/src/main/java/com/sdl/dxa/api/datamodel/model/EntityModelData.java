package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.sdl.dxa.api.datamodel.model.util.CanWrapContentAndMetadata;
import com.sdl.dxa.api.datamodel.model.util.ModelDataWrapper;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@JsonTypeName
@ToString
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class EntityModelData extends ViewModelData implements CanWrapContentAndMetadata {

    private String id;

    private ComponentTemplateData componentTemplate;

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

    @Override
    public ViewModelData copyFrom(ViewModelData other) {
        super.copyFrom(other);

        EntityModelData emd = (EntityModelData) other;
        this.id = emd.id;
        this.linkUrl = emd.linkUrl;
        this.content = emd.content;
        this.binaryContent = emd.binaryContent;
        this.externalContent = emd.externalContent;
        return this;
    }
}
