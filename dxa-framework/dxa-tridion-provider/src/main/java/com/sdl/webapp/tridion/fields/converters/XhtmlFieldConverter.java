package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.RichTextProcessor;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class XhtmlFieldConverter extends AbstractFieldConverter {
    private static final Logger LOG = LoggerFactory.getLogger(XhtmlFieldConverter.class);

    private static final FieldType[] SUPPORTED_FIELD_TYPES = {FieldType.XHTML};

    private final RichTextProcessor richTextProcessor;
    private final WebRequestContext webRequestContext;

    @Autowired
    public XhtmlFieldConverter(RichTextProcessor richTextProcessor, WebRequestContext webRequestContext) {
        this.richTextProcessor = richTextProcessor;
        this.webRequestContext = webRequestContext;
    }

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass, ModelBuilderPipeline builder) throws FieldConverterException {
        final List<RichText> fieldValues = new ArrayList<>();
        for (String textValue : field.getTextValues()) {
            fieldValues.add(richTextProcessor.processRichText(textValue, this.webRequestContext.getLocalization()));
        }
        return fieldValues;
    }
}
