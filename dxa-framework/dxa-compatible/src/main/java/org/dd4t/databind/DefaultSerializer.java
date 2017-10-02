/**  
 *  Copyright 2011 Capgemini & SDL
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.dd4t.databind;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Component.ComponentType;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Multimedia;
import org.dd4t.contentmodel.OrganizationalItem;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.PageTemplate;
import org.dd4t.contentmodel.Publication;
import org.dd4t.contentmodel.Schema;
import org.dd4t.contentmodel.StructureGroup;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.DataBinder;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.databind.util.DataBindConstants;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.transform.Matcher;
import org.simpleframework.xml.transform.Transform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;


/**
 * Class to serialize and deserialize the xml source from tridion.
 * 
 * @author bjornl
 * 
 */
public class DefaultSerializer implements DataBinder, org.dd4t.core.serializers.DD4T1Serializer {

	private static Logger LOG = LoggerFactory.getLogger(DefaultSerializer.class);
	private Serializer serializer;

	public DefaultSerializer() {
		this.init();
	}
	
	/**
	 * Initialize the serializer. 
	 */
	protected void init(){
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		Matcher matcher = new Matcher() 
		{
		    public Transform match(Class type) throws Exception {
		        if (type.getCanonicalName().equals(FieldType.class.getCanonicalName())){
		        	return new FieldTypeTransformer();
		        }
		        
		        if (type.getCanonicalName().equals(ComponentType.class.getCanonicalName())){
		        	return new ComponentTypeTransformer();
		        }
		        
		        return null;
		    }
		 };
		 
		this.serializer = new Persister(strategy,matcher);
		
		GenericConverter converter = new GenericConverter(this);
		
		try {
			registry.bind(Page.class, converter);
			registry.bind(Component.class, converter);			
			registry.bind(Publication.class, converter);
			registry.bind(ComponentPresentation.class, converter);
			registry.bind(ComponentTemplate.class, converter);
			registry.bind(PageTemplate.class, converter);
			registry.bind(OrganizationalItem.class, converter);
			registry.bind(Schema.class, converter);
			registry.bind(Multimedia.class, converter);
			registry.bind(StructureGroup.class, converter);
			registry.bind(Field.class, converter);
			registry.bind(FieldSet.class, converter);
		} catch (Exception e) {
			LOG.error("Error registring classes to converter", e);
			throw new RuntimeException(e);
		}
	}
		


	
	@Override
	@SuppressWarnings(value = { "rawtypes", "unchecked" })
	public Object deserialize(Object object, Class c) throws Exception {
		String currentNodeName = "";
		try {
			if (object != null) {
				if(object instanceof InputNode){
					LOG.debug("about to read node " + ((InputNode) object).getName());
					currentNodeName = ((InputNode) object).getName();
					return serializer.read(c, (InputNode) object);
				}
				else {
					LOG.debug("about to read string of length " + ((String)object).length());
					return serializer.read(c, object.toString(), false);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			LOG.error("error deserializing to class " + c.getName() + " error occurred while deserializing node " + currentNodeName, e);
			throw new RuntimeException("Error deserializing node " + currentNodeName + ", target class " + c.getName(), e);
		}
	}

	@Override
	public Serializable serialize(Object o) throws Exception {
		Serializer serializer = new Persister();
		ByteArrayOutputStream sos = new ByteArrayOutputStream();
		serializer.write(o, sos);
		return sos.toString();
	}

	@Override
	public <T extends Page> T buildPage(String source, Class<T> aClass)
			throws SerializationException {
				
        try {
    		return (T) deserialize(source, aClass) ;
        } catch (Exception e) {
            LOG.error(DataBindConstants.MESSAGE_ERROR_DESERIALIZING, e);
            throw new SerializationException(e);
        }
		
	}

	@Override
	public <T extends ComponentPresentation> T buildComponentPresentation(
			String source, Class<T> componentPresentationClass)
			throws SerializationException {
		
		Component comp;
        try {
    		comp = (Component) deserialize(source, Component.class);
    		
            ComponentPresentation cp = new ComponentPresentationImpl();
            cp.setComponent(comp);
            
            return (T) cp;
        } catch (Exception e) {
            LOG.error(DataBindConstants.MESSAGE_ERROR_DESERIALIZING, e);
            throw new SerializationException(e);
        }             
	}

	@Override
	public Map<String, BaseViewModel> buildModels(Object source,
			Set<String> modelNames, String templateUri)
			throws SerializationException {
		// not supported for dd4t-1
		return null;
	}

	@Override
	public <T extends BaseViewModel> T buildModel(Object rawData,
			String modelName, String templateUri) throws SerializationException {
		// not supported for dd4t-1
		return null;
	}

	@Override
	public <T extends BaseViewModel> T buildModel(Object source,
			Class modelClass, String templateUri) throws SerializationException {
		// not supported for dd4t-1
		return null;
	}

	@Override
	public ComponentPresentation buildDynamicComponentPresentation(
			ComponentPresentation componentPresentation,
			Class<? extends Component> aClass) throws SerializationException {
		// not supported for dd4t-1
		return null;
	}

	@Override
	public <T extends Component> T buildComponent(Object source, Class<T> aClass)
			throws SerializationException {
		// not supported for dd4t-1
		return null;
	}

	@Override
	public String findComponentTemplateViewName(ComponentTemplate template)
			throws IOException {
		// not supported for dd4t-1
		return null;
	}

	@Override
	public String getRootElementName(Object componentNode) {
		// not supported for dd4t-1
		return null;
	}

	@Override
	public boolean classHasViewModelDerivatives(final String className) {
		// not supported for dd4t-1
		return false;
	}

	@Override
	public Class<? extends BaseViewModel> getConcreteModel(final String className, final String rootElementName) {
		// not supported for dd4t-1
		return null;
	}

	@Override
	public boolean renderDefaultComponentModelsOnly() {
		// not supported for dd4t-1
		return false;
	}

	@Override
	public boolean renderDefaultComponentsIfNoModelFound() {
		// not supported for dd4t-1
		return false;
	}

	/**
	 * This serializer is supposed to run everything that starts with a {@code <?xml (which is dd4t-1 proper declared xml)}
	 */
	@Override
	public boolean canDeserialize(String source) {
		return source.startsWith("<?xml");		
	}
}