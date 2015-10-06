package com.sdl.webapp.common.impl.mapping;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.mapping.SemanticMapping;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.annotations.*;
import com.sdl.webapp.common.api.mapping.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.mapping.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.impl.localization.LocalizationFactoryImpl;
import com.sdl.webapp.common.impl.localization.semantics.JsonSchema;
import com.sdl.webapp.common.util.PackageUtils;
import com.sdl.webapp.main.WebAppConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Implementation of {@code SemanticMappingRegistry}.
 */
public class SemanticMappingImpl implements SemanticMapping {
    private static final Logger LOG = LoggerFactory.getLogger(SemanticMappingImpl.class);
    public final String DEFAULT_VOCABULARY = "http://www.sdl.com/web/schemas/core";
    public final String _mapSettingsType = "map";
    public final String _vocabSettingsType = "vocab";
    private static final String SEMANTIC_SCHEMAS_PATH = "/system/mappings/schemas.json";
    private static final String SEMANTIC_VOCABULARIES_PATH = "/system/mappings/vocabularies.json";

    private final Map<String, Map<String, SemanticSchema>> _semanticMap = new HashMap<>();
    private final Map<String, List<SemanticVocabulary>> _semanticVocabularies = new HashMap<>();

    private final ContentProvider contentProvider;
    private final ObjectMapper objectMapper;

    @Autowired
    public SemanticMappingImpl(ContentProvider provider, ObjectMapper objectMapper){
        this.contentProvider = provider;
        this.objectMapper = objectMapper;
    }

    @Override
    public String getQualifiedTypeName(String typeName, String vocab) {
        return String.format("%s:%s", vocab !=null ? vocab : DEFAULT_VOCABULARY, typeName);
    }

    @Override
    public String getQualifiedTypeName(String typeName, String prefix, Localization localization) {
        return getQualifiedTypeName(typeName, getVocabulary(prefix, localization));
    }

    @Override
    public String getVocabulary(String prefix, Localization loc) {
        String key = loc.getId();
        //TODO: TW Implement SiteConfiguration.CheckSettingsNeedReferesh from .net
        boolean localizationNeedsRefresh = true; // = SiteConfiguration.CheckSettingsNeedRefresh(_vocabSettingsType, loc);
        if(!_semanticVocabularies.containsKey(key) || localizationNeedsRefresh){
            loadVocabulariesForLocalization(loc);
        }
        if(_semanticVocabularies.containsKey(key)){
            List<SemanticVocabulary> vocabs = _semanticVocabularies.get(key);
            return getVocabulary(vocabs, prefix);
        }
        LOG.error("Localization {} does not contain a prefix {}. Check that the Publish Settings page is published and the application cache is up to date.", key, prefix);
        return null;
    }

    @Override
    public String getPrefix(String vocab, Localization loc) {
        String key = loc.getId();
        //TODO: TW Implement SiteConfiguration.CheckSettingsNeedReferesh from .net
        boolean localizationNeedsRefresh = true; // = SiteConfiguration.CheckSettingsNeedRefresh(_vocabSettingsType, loc);
        if(!_semanticVocabularies.containsKey(key) || localizationNeedsRefresh){
            loadVocabulariesForLocalization(loc);
        }
        if(_semanticVocabularies.containsKey(key)){
            List<SemanticVocabulary> vocabs = _semanticVocabularies.get(key);
            return getPrefix(vocabs, vocab);
        }
        LOG.error("Localization {} does not contain a vocabulary {}. Check that the Publish Settings page is published and the application cache is up to date.", key, vocab);
        return  null;
    }

    @Override
    public String getPrefix(List<SemanticVocabulary> vocabularies, String vocab) {
        SemanticVocabulary vocabulary = null;
        for(SemanticVocabulary v: vocabularies){
            if(v.equals(vocab)){
                return v.getPrefix();
            }
        }
        LOG.warn("Prefix not found for semantic voabulary {}", vocab);
        return null;
    }

    @Override
    public SemanticSchema getSchema(String id, Localization loc) throws DxaException {
        String key = loc.getId();
        //TODO: TW Implement SiteConfiguration.CheckSettingsNeedReferesh from .net
        boolean settingsNeedsRefresh = true; // = SiteConfiguration.CheckSettingsNeedRefresh(_mapSettingsType, loc);
        if(!_semanticMap.containsKey(key) || settingsNeedsRefresh){
            loadSemanticMapForLocalization(loc);
        }
        try{
            return _semanticMap.get(key).get(id);
        }catch(Exception ex){
            String message = String.format("Semantic schema %s not defined in localizatino [%d]. Check that the Publish Settings page is published and teh application is up to date.", id, loc);
            throw new DxaException(message);
        }
    }

    @Override
    public String getVocabulary(List<SemanticVocabulary> vocabularies, String prefix) {
        return null;
    }

    @Override
    public void loadVocabulariesForLocalization(Localization loc) {
        String id = loc.getId();
        //TODO: TW get from publication path
        //Path.Combine(loc.Path.ToCombinePath(true), StieConfiguration.SystemFolder, @"mappings\voabularies.json").Replace("\\", "/");
        String path = "/";
        try {
            //TODO: TW validate the result is as expected since the parsing might fail...
            List<SemanticVocabulary> vocabs = parseJsonFileObject(contentProvider, SEMANTIC_VOCABULARIES_PATH, id, path, new TypeReference<List<SemanticVocabulary>>() { });

            //TODO: TW Implement SiteConfiguration.ThreadSafeSettingsUpdate
            //SiteConfiguration.ThreadSafeSettingsUpdate(_vocabSettingsType, __semanticVocabularies, key, vocabs);
        } catch (LocalizationFactoryException e) {
            //TODO: Check wether we throw it or catch it
            LOG.error("Error getting scheams from json: {}", e.getMessage());
        }

    }

    @Override
    public void loadSemanticMapForLocalization(Localization loc) {
        String id = loc.getId();
        //TODO: TW get from publication path
        //Path.Combine(loc.Path.ToCombinePath(true), StieConfiguration.SystemFolder, @"mappings\voabularies.json").Replace("\\", "/");
        String path = "/";
        try {
            //TODO: TW validate the result is as expected since the parsing might fail...
            List<SemanticSchema> schemas = parseJsonFileObject(contentProvider, SEMANTIC_SCHEMAS_PATH, id, path, new TypeReference<List<SemanticSchema>>() { });
            Map<String,SemanticSchema> map = new HashMap<>();
            for(SemanticSchema schema: schemas){
                schema.setLocalization(loc);
                map.put(String.valueOf(schema.getId()), schema);
            }
            //TODO: TW Implement SiteConfiguration.ThreadSafeSettingsUpdate
            //SiteConfiguration.ThreadSafeSettingsUpdate(_mapSettingsType, _semanticMap, key, map);
        } catch (LocalizationFactoryException e) {
            //TODO: Check wether we throw it or catch it
            LOG.error("Error getting scheams from json: {}", e.getMessage());
        }
    }

    public <T> T parseJsonFileObject(ContentProvider contentProvider, String filePath, String locId,
                                     String locPath, TypeReference<T> resultType)
            throws LocalizationFactoryException {
        try {
            final StaticContentItem item = contentProvider.getStaticContent(filePath, locId, locPath);
            try (final InputStream in = item.getContent()) {
                return objectMapper.readValue(in, resultType);
            }
        } catch (ContentProviderException | IOException e) {
            throw new LocalizationFactoryException("Exception while reading configuration of localization: [" + locId +
                    "] " + locPath, e);
        }
    }
}
