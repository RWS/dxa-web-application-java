package com.sdl.webapp.dd4t.fieldconverters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.SemanticMapper;
import com.sdl.webapp.common.api.mapping.SemanticMappingException;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.entity.AbstractEntityModel;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.dd4t.DD4TSemanticFieldDataProvider;
import com.sdl.webapp.dd4t.EntityBuilder;

import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ComponentLinkFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.COMPONENTLINK, FieldType.MULTIMEDIALINK };

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
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass, EntityBuilder builder) throws FieldConverterException {
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

  /*  public Object createComponentLink(org.dd4t.contentmodel.Component component, Class<?> targetClass)
            throws FieldConverterException {
        String componentId = component.getId();
        final String url = linkResolver.resolveLink(componentId, null);

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
    */
    public Object createComponentLink(org.dd4t.contentmodel.Component component, Class<?> targetClass, EntityBuilder builder)
            throws SemanticMappingException {
        String componentId = component.getId();
        final String url = linkResolver.resolveLink(componentId, null);

        if (targetClass.isAssignableFrom(String.class)) {
            return url;
        } else if (targetClass.isAssignableFrom(Link.class)) {
            final Link link = new Link();
            link.setUrl(url);
            return link;
        } else if (AbstractEntityModel.class.isAssignableFrom(targetClass)){
        	Localization localization = this.webRequestContext.getLocalization(); 
        	
        	
			try {
				Object retval = builder.createEntity(component, localization);
				if(targetClass.isAssignableFrom(retval.getClass()))
				{
					return retval;
				}
				else
				{
					return null;
				}
				
			} catch (ContentProviderException e) {
				// TODO Auto-generated catch block
				throw new SemanticMappingException(e);
			}
  			//        	return semanticMapper.createEntity(targetClass.asSubclass(AbstractEntityModel.class), semanticSchema.getSemanticFields(), new DD4TSemanticFieldDataProvider(component, fieldConverterRegistry));
        }
        else
        {
        	throw new UnsupportedTargetTypeException(targetClass);
        }
    }
}