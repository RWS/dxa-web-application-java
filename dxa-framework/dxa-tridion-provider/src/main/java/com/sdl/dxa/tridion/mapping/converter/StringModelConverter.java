package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.dxa.tridion.mapping.impl.DefaultSemanticFieldDataProvider;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class StringModelConverter implements SemanticModelConverter<String> {

    @Override
    public Object convert(String toConvert, TypeInformation targetType, SemanticField semanticField,
                          ModelBuilderPipeline pipeline, DefaultSemanticFieldDataProvider dataProvider) throws FieldConverterException {
        Class<?> objectType = targetType.getObjectType();
        Object result;
        if (Date.class.isAssignableFrom(objectType)) {
            try {
                result = DateFormat.getInstance().parse(toConvert);
            } catch (ParseException e) {
                throw new FieldConverterException("Cannot parse a date string " + toConvert + " to a date of class Date");
            }
        } else if (DateTime.class == objectType) {
            result = DateTime.parse(toConvert);
        } else if (Number.class.isAssignableFrom(objectType)) {
            result = toSpecificNumber(new BigDecimal(toConvert), objectType);
        } else if (Boolean.class == objectType) {
            result = Boolean.parseBoolean(toConvert);
        } else if (String.class == objectType) {
            result = toConvert;
        } else if (Link.class.isAssignableFrom(objectType)) {
            Link link = new Link();
            link.setUrl(toConvert);
            result = link;
        } else if (RichText.class.isAssignableFrom(objectType)) {
            result = new RichText(toConvert);
        } else {
            throw new FieldConverterException("The type " + objectType + " is not supported by String Converter");
        }

        return convertToCollectionIfNeeded(result, targetType);
    }

    @Override
    public List<Class<? extends String>> getTypes() {
        return Collections.singletonList(String.class);
    }
}
