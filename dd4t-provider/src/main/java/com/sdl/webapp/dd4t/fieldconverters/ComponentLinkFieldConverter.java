package com.sdl.webapp.dd4t.fieldconverters;

import com.sdl.webapp.common.api.content.ContentResolver;
import com.sdl.webapp.common.api.model.entity.Link;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ComponentLinkFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.COMPONENTLINK };

    private final ContentResolver contentResolver;

    @Autowired
    public ComponentLinkFieldConverter(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        final List<Object> componentLinks = new ArrayList<>();

        for (org.dd4t.contentmodel.Component component : field.getLinkedComponentValues()) {
            final Object componentLink = createComponentLink(component, targetClass);
            if (componentLink != null) {
                componentLinks.add(componentLink);
            }
        }

        return componentLinks;
    }

    public Object createComponentLink(org.dd4t.contentmodel.Component component, Class<?> targetClass)
            throws FieldConverterException {
        String componentId = component.getId();
        final String url = contentResolver.resolveLink(componentId, null);

        if (targetClass.isAssignableFrom(String.class)) {
            return url;
        } else if (targetClass.isAssignableFrom(Link.class)) {
            final Link link = new Link();
            link.setUrl(url);
            return link;
        } else {
            throw new UnsupportedTargetTypeException(targetClass);
        }
    }
}
