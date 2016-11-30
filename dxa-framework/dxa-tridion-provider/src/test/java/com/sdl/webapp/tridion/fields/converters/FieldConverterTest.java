package com.sdl.webapp.tridion.fields.converters;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.tridion.SemanticFieldDataProviderImpl;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FieldConverterTest {

    private FieldConverter fieldConverter = new FieldConverter() {

        @Override
        public FieldType[] supportedFieldTypes() {
            return new FieldType[0];
        }

        @Override
        public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType, SemanticFieldDataProviderImpl semanticFieldDataProvider, ModelBuilderPipeline builder) throws FieldConverterException {
            return null;
        }
    };

    @Test
    public void shouldHaveDefaultImplementation() throws Exception {
        Date date = new Date();
        String dateStr = date.toString();
        List<String> list = fieldConverter.getStringValues(new BaseField() {
            @Override
            public List<Object> getValues() {

                return Lists.newArrayList("1", null, date);
            }
        });

        assertEquals("1" + dateStr, Joiner.on("").join(list));
        assertTrue(list.size() == 3);
    }

}