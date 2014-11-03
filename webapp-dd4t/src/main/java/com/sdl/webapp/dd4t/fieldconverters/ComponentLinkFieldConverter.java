package com.sdl.webapp.dd4t.fieldconverters;

import com.sdl.webapp.common.api.model.entity.Link;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.core.resolvers.LinkResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ComponentLinkFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.ComponentLink };

    private final LinkResolver linkResolver;

    @Autowired
    public ComponentLinkFieldConverter(LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
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
        final String url;
        try {
            url = linkResolver.resolve(component);
        } catch (ItemNotFoundException | SerializationException e) {
            throw new FieldConverterException("Exception while resolving component link for: " + component.getId(), e);
        }

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
