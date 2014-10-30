package com.sdl.webapp.dd4t.fieldconverters;

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
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        final List<String> urls = new ArrayList<>();
        for (org.dd4t.contentmodel.Component component : field.getLinkedComponentValues()) {
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

        if (targetClass.isAssignableFrom(String.class)) {
            return urls;
        } else if (targetClass.isAssignableFrom(EmbeddedLink.class)) {
            return getLinkValues(urls);
        } else {
            throw new UnsupportedTargetTypeException("Unsupported target type for component link field: " +
                    targetClass.getName());
        }
    }

    private List<EmbeddedLink> getLinkValues(List<String> urls) {
        final List<EmbeddedLink> links = new ArrayList<>();
        for (String url : urls) {
            EmbeddedLink link = new EmbeddedLink();
            link.setUrl(url);
            links.add(link);
        }
        return links;
    }
}
