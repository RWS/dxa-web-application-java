package com.sdl.webapp.common.api.mapping;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.mapping.config.SemanticVocabulary;
import com.sdl.webapp.common.exceptions.DxaException;

import java.util.List;

/**
 * Created by Administrator on 10/5/2015.
 */
public interface SemanticMapping {
    String getQualifiedTypeName(String typeName, String vocab);
    String getQualifiedTypeName(String typeName, String prefix, Localization localization);
    String getVocabulary(String prefix, Localization loc);
    String getPrefix(String vocab, Localization loc);
    String getPrefix(List<SemanticVocabulary> vocabularies, String vocab);
    SemanticSchema getSchema(String id, Localization loc) throws DxaException;
    String getVocabulary(List<SemanticVocabulary> vocabularies, String prefix);
    void loadVocabulariesForLocalization(Localization loc);
    void loadSemanticMapForLocalization(Localization loc);
    //TODO: TW These two methods might not be required as we read the file using exsiting methods, so no need to implement these
    /*
    List<SemanticSchema> getSchemasFromFile(String jsonData);
    List<SemanticVocabulary> getVocabulariesFromFile(String jsonData);
    */
}
