package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MultimediaFieldConverter extends ComponentLinkFieldConverter {

    public MultimediaFieldConverter(LinkResolver linkResolver, WebRequestContext webRequestContext) {
        super(linkResolver, webRequestContext);
    }

    @Override
    public FieldType[] supportedFieldTypes() {
        return new FieldType[]{FieldType.MULTIMEDIALINK};
    }

    @Override
    public List<String> getStringValues(BaseField field) throws FieldConverterException {
        final List<String> urls = new ArrayList<>();

        for (org.dd4t.contentmodel.Component component : field.getLinkedComponentValues()) {
            urls.add(component.getMultimedia().getUrl());
        }

        return urls;
    }
}
