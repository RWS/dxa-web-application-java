package com.sdl.webapp.dd4t.fieldconv;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.EmbeddedLink;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.core.resolvers.LinkResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.TypeDescriptor;
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
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType)
            throws FieldConverterException {
        final List<String> urls = getUrls(field.getLinkedComponentValues());
        if (urls.isEmpty()) {
            return null;
        }

        final Class<?> targetClass = targetType.getType();
        if (String.class.isAssignableFrom(targetClass)) {
            return semanticField.isMultiValue() ? urls : urls.get(0);
        } else if (EmbeddedLink.class.isAssignableFrom(targetClass)) {
            if (semanticField.isMultiValue()) {
                final List<EmbeddedLink> links = new ArrayList<>();
                for (String url : urls) {
                    EmbeddedLink link = new EmbeddedLink();
                    link.setUrl(url);
                    links.add(link);
                }
                return links;
            } else {
                EmbeddedLink link = new EmbeddedLink();
                link.setUrl(urls.get(0));
                return link;
            }
        } else {
            throw new FieldConverterException("Unsupported target type for component link field: " +
                    targetClass.getName());
        }
    }


    private List<String> getUrls(List<org.dd4t.contentmodel.Component> components) throws FieldConverterException {
        final List<String> urls = new ArrayList<>();

        for (org.dd4t.contentmodel.Component component : components) {
            final String url;
            try {
                url = linkResolver.resolve(component);
            } catch (ItemNotFoundException | SerializationException e) {
                throw new FieldConverterException("Exception while resolving component link for: " + component.getId(), e);
            }

            if (!Strings.isNullOrEmpty(url)) {
                urls.add(url);
            }
        }

        return urls;
    }
}
