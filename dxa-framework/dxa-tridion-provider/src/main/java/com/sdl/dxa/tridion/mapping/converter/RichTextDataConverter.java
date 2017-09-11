package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.RichTextFragment;
import com.sdl.webapp.common.api.model.RichTextFragmentImpl;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class RichTextDataConverter implements SemanticModelConverter<RichTextData> {

    @Override
    public Object convert(RichTextData toConvert, TypeInformation targetType, SemanticField semanticField,
                          ModelBuilderPipeline pipeline, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException {
        Class<?> objectType = targetType.getObjectType();

        List<RichTextFragment> fragments = new ArrayList<>();
        for (Object fragment : toConvert.getValues()) {
            if (fragment instanceof String) {
                fragments.add(new RichTextFragmentImpl((String) fragment));
            } else {
                log.debug("Fragment {} is a not a string but perhaps EntityModelData, skipping link resolving");
                MediaItem mediaItem;
                EntityModelData entityModelData = (EntityModelData) fragment;
                try {
                    mediaItem = pipeline.createEntityModel(entityModelData, MediaItem.class);
                } catch (DxaException e) {
                    throw new FieldConverterException("Cannot create an instance of Media Item in RichText, model id " + entityModelData.getId(), e);
                }
                mediaItem.setEmbedded(true);
                fragments.add(mediaItem);
            }
        }

        RichText richText = new RichText(fragments);

        return convertToCollectionIfNeeded(objectType == String.class ? richText.toString() : richText, targetType);
    }

    @Override
    public List<Class<? extends RichTextData>> getTypes() {
        return Collections.singletonList(RichTextData.class);
    }
}
