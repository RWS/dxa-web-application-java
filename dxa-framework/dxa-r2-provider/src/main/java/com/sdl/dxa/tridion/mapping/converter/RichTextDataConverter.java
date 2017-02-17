package com.sdl.dxa.tridion.mapping.converter;

import com.google.common.base.Joiner;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.RichText;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class RichTextDataConverter implements SourceConverter<RichTextData> {

//    @Autowired
//    private RichTextProcessor richTextProcessor;

    @Autowired
    private WebRequestContext webRequestContext;

    @Override
    public List<Class<? extends RichTextData>> getTypes() {
        return Collections.singletonList(RichTextData.class);
    }

    @Override
    public Object convert(RichTextData toConvert, TypeDescriptor targetType, SemanticField semanticField, DefaultSemanticFieldDataProvider dataProvider) {

        Class<?> objectType = targetType.getObjectType();
        if (String.class == objectType) {
            return Joiner.on("").join(toConvert.getFragments());
        }

        //todo convert to RTD, need to rewrite RichTextProcessor because it uses dd4t
        return new RichText("hello!");
    }
}
