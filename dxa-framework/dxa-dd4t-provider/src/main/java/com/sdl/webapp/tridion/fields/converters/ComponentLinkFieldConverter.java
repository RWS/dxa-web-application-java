package com.sdl.webapp.tridion.fields.converters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingException;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.util.TcmUtils;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import com.sdl.webapp.tridion.fields.exceptions.UnsupportedTargetTypeException;
import com.sdl.webapp.tridion.mapping.ModelBuilderPipeline;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.common.util.StringUtils.toStrings;

@Component
public class ComponentLinkFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = {FieldType.COMPONENTLINK};

    private final LinkResolver linkResolver;
    private final WebRequestContext webRequestContext;

    @Autowired
    public ComponentLinkFieldConverter(LinkResolver linkResolver, WebRequestContext webRequestContext) {
        this.linkResolver = linkResolver;
        this.webRequestContext = webRequestContext;
    }

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    public List<String> getStringValues(BaseField field) throws FieldConverterException {
        return toStrings(getFieldValues(field, String.class));
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass, ModelBuilderPipeline builder) throws FieldConverterException {
        final List<Object> componentLinks = new ArrayList<>();

        for (org.dd4t.contentmodel.Component component : field.getLinkedComponentValues()) {
            Object componentLink;
            try {
                componentLink = createComponentLink(component, targetClass, builder);
                if (componentLink != null) {
                    componentLinks.add(componentLink);
                }
            } catch (SemanticMappingException e) {
                // TODO Auto-generated catch block
                throw new FieldConverterException(e);
            }
        }

        return componentLinks;
    }

    public Object createPageLink(org.dd4t.contentmodel.Page page, Class<?> targetClass)
            throws FieldConverterException {
        String pageId = page.getId();
        final String url = linkResolver.resolveLink(pageId, null);

        if (targetClass == String.class) {
            return url;
        } else if (Link.class.isAssignableFrom(targetClass)) {
            final Link link = new Link();
            link.setId(String.valueOf(TcmUtils.getItemId(pageId)));
            link.setUrl(url);
            return link;
        } else {
            throw new UnsupportedTargetTypeException(targetClass);
        }
    }

    public Object createComponentLink(org.dd4t.contentmodel.Component component, Class<?> targetClass, ModelBuilderPipeline builder)
            throws SemanticMappingException {
        String componentId = component.getId();
        final String url = linkResolver.resolveLink(componentId, null);

        if (String.class == targetClass) {
            return url;
        } else if (Link.class.isAssignableFrom(targetClass)) {
            final Link link = new Link();
            link.setId(String.valueOf(TcmUtils.getItemId(componentId)));
            link.setUrl(url);
            return link;
        } else if (EntityModel.class.isAssignableFrom(targetClass)) { //what we want should be assignable from what we want to have
            Localization localization = this.webRequestContext.getLocalization();

            try {
                Object retval = builder.createEntityModel(component, localization);
                // what we got should be assignable from what we expected
                if (retval != null && targetClass.isAssignableFrom(retval.getClass())) {
                    return retval;
                } else {
                    return null;
                }

            } catch (ContentProviderException e) {

                // Try to map using model field type
                //
                try {
                    return builder.createEntityModel(component, localization, (Class<AbstractEntityModel>) targetClass);
                } catch (ContentProviderException e2) {
                    throw new SemanticMappingException(e);
                }
            }
            //        	return semanticMapper.createEntity(targetClass.asSubclass(AbstractEntityModel.class), semanticSchema.getSemanticFields(), new DD4TSemanticFieldDataProvider(component, fieldConverterRegistry));
        } else {
            throw new UnsupportedTargetTypeException(targetClass);
        }
    }
}
