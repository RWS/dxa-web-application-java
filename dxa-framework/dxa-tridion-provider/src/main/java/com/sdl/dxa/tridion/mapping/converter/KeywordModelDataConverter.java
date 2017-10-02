package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.api.datamodel.model.KeywordModelData;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.converter.source.keyword.Converter;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class KeywordModelDataConverter implements SemanticModelConverter<KeywordModelData> {

    @Override
    public Object convert(KeywordModelData toConvert, TypeInformation targetType, SemanticField semanticField,
                          ModelBuilderPipeline pipeline, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException {

        Class<?> objectType = targetType.getObjectType();

        Object result = Converter.getConverter(objectType).convert(new KeywordModelDataWrapper(toConvert, dataProvider));
        return convertToCollectionIfNeeded(result, targetType);
    }

    @Override
    public List<Class<? extends KeywordModelData>> getTypes() {
        return Collections.singletonList(KeywordModelData.class);
    }

    private static class KeywordModelDataWrapper implements Converter.KeywordWrapper {

        private KeywordModelData toConvert;

        private DefaultSemanticFieldDataProvider dataProvider;

        KeywordModelDataWrapper(KeywordModelData toConvert, DefaultSemanticFieldDataProvider dataProvider) {
            this.toConvert = toConvert;
            this.dataProvider = dataProvider;
        }

        @Override
        public String getDescription() {
            return toConvert.getDescription();
        }

        @Override
        public String getTitle() {
            return toConvert.getTitle();
        }

        @Override
        public String getKey() {
            return toConvert.getKey();
        }

        @Override
        public String getId() {
            return toConvert.getId();
        }

        @Override
        public String getTaxonomyId() {
            return toConvert.getTaxonomyId();
        }

        @Override
        public String getSchemaId() {
            return toConvert.getSchemaId();
        }

        @Override
        public SemanticFieldDataProvider getDataProvider() {
            return DefaultSemanticFieldDataProvider.getFor(toConvert, dataProvider.getSemanticSchema());
        }
    }

}
