package com.sdl.dxa.tridion.mapping.converter.source.keyword;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMapper;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.model.KeywordModel;
import com.sdl.webapp.common.api.model.entity.Tag;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.lang.Boolean.parseBoolean;

@Slf4j
public abstract class Converter<T> {

    private static String getKeywordDisplayText(KeywordWrapper keyword) {
        return isNullOrEmpty(keyword.getDescription()) ? keyword.getTitle() : keyword.getDescription();
    }

    public static Converter getConverter(Class<?> targetClass) throws UnsupportedTargetTypeException {
        Converter<?> converter;
        if (Tag.class == targetClass) {
            converter = new Converter.TagConverter();
        } else if (Boolean.class == targetClass) {
            converter = new Converter.BooleanConverter();
        } else if (String.class == targetClass) {
            converter = new Converter.StringConverter();
        } else if (KeywordModel.class.isAssignableFrom(targetClass)) {
            //typecast is safe which is guaranteed by if condition
            //noinspection unchecked
            converter = new KeywordConverter((Class<? extends KeywordModel>) targetClass);
        } else {
            throw new UnsupportedTargetTypeException(targetClass);
        }
        return converter;
    }

    public abstract T convert(KeywordWrapper keyword) throws FieldConverterException;

    public interface KeywordWrapper {

        String getDescription();

        String getTitle();

        String getKey();

        String getId();

        String getTaxonomyId();

        String getSchemaId();

        SemanticFieldDataProvider getDataProvider();
    }

    private static class TagConverter extends Converter<Tag> {

        private String getKeywordKey(KeywordWrapper keyword) {
            return isNullOrEmpty(keyword.getKey()) ? keyword.getId() : keyword.getKey();
        }

        @Override
        public Tag convert(KeywordWrapper keyword) {
            final Tag tag = new Tag();
            tag.setDisplayText(getKeywordDisplayText(keyword));
            tag.setKey(getKeywordKey(keyword));
            tag.setTagCategory(keyword.getTaxonomyId());
            return tag;
        }
    }

    private static class StringConverter extends Converter<String> {

        @Override
        public String convert(KeywordWrapper keyword) {
            return getKeywordDisplayText(keyword);
        }
    }

    private static class BooleanConverter extends Converter<Boolean> {

        @Override
        public Boolean convert(KeywordWrapper keyword) {
            final String key = keyword.getKey();
            final String title = keyword.getTitle();

            return parseBoolean(isNullOrEmpty(key) ? title : key);
        }
    }

    private static class KeywordConverter extends Converter<KeywordModel> {

        private final SemanticMapper semanticMapper;

        private final Class<? extends KeywordModel> targetClass;

        private Localization localization;

        KeywordConverter(Class<? extends KeywordModel> targetClass) {
            this.semanticMapper = ApplicationContextHolder.getContext().getBean(SemanticMapper.class);
            this.localization = ApplicationContextHolder.getContext().getBean(WebRequestContext.class).getLocalization();
            this.targetClass = targetClass;
        }

        private String getSchemaId(KeywordWrapper keyword) {
            return keyword.getSchemaId();
        }

        @Override
        public KeywordModel convert(KeywordWrapper keyword) throws FieldConverterException {
            KeywordModel keywordModel;
            String schemaId = getSchemaId(keyword);
            if (isNullOrEmpty(schemaId)) {
                keywordModel = new KeywordModel();
            } else {
                try {
                    SemanticSchema semanticSchema = localization.getSemanticSchemas().get(Long.parseLong(schemaId));
                    if (semanticSchema == null) {
                        log.warn("Semantic schema with schemaId {} is not found in localization, skipping semantic mapping", schemaId, localization);
                        throw new SemanticMappingException("Semantic schema not found");
                    }
                    Map<FieldSemantics, SemanticField> semanticFields = semanticSchema.getSemanticFields();
                    keywordModel = semanticMapper.createEntity(this.targetClass, semanticFields, keyword.getDataProvider());
                } catch (SemanticMappingException e) {
                    log.error("Failed to do a semantic mapping for keyword {}", keyword, e);
                    throw new FieldConverterException("Failed to do a semantic mapping for keyword", e);
                }
            }

            keywordModel.setId(keyword.getId());
            keywordModel.setTitle(keyword.getTitle());
            keywordModel.setDescription(keyword.getDescription());
            keywordModel.setKey(keyword.getKey());
            keywordModel.setTaxonomyId(keyword.getTaxonomyId());

            return keywordModel;
        }
    }

}
