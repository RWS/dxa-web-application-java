package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.Legacy;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.RichTextProcessor;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.common.util.StringUtils.toStrings;

@Legacy
@Component
public class XhtmlFieldConverter extends AbstractFieldConverter {

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
    public List<String> getStringValues(BaseField field) throws FieldConverterException {
        return toStrings(getFieldValues(field, null));
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
