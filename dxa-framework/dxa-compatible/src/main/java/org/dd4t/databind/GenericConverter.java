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

import org.dd4t.contentmodel.impl.CategoryImpl;
import org.dd4t.contentmodel.impl.ComponentImpl;
import org.dd4t.contentmodel.impl.ComponentLinkField;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.contentmodel.impl.ComponentTemplateImpl;
import org.dd4t.contentmodel.impl.DateField;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.KeywordImpl;
import org.dd4t.contentmodel.impl.MultimediaImpl;
import org.dd4t.contentmodel.impl.NumericField;
import org.dd4t.contentmodel.impl.OrganizationalItemImpl;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.contentmodel.impl.PageTemplateImpl;
import org.dd4t.contentmodel.impl.PublicationImpl;
import org.dd4t.contentmodel.impl.SchemaImpl;
import org.dd4t.contentmodel.impl.StructureGroupImpl;
import org.dd4t.contentmodel.impl.TextField;
import org.dd4t.contentmodel.impl.XhtmlField;
import org.dd4t.core.serializers.DD4T1Serializer;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;


/**
 * A class which is used by the deserializer to map the xml elements to the classes. 
 * 
 * @author Bjorn Lindstrom
 *
 */
public class GenericConverter implements Converter<Object> {

	private DD4T1Serializer serializer;

	public GenericConverter(DD4T1Serializer serializer){
		this.serializer = serializer;
	}
	
	/**
	 * TODO: make the element to class mapping configurable.
	 */
	@Override
	public Object read(InputNode node) throws Exception {
		
		String name = node.getName();
		Object object = null;
		if("publication".equals(name)){
			object = (PublicationImpl) serializer.deserialize(node, PublicationImpl.class);
		}
		else if("owningPublication".equals(name)){
			object = (PublicationImpl) serializer.deserialize(node, PublicationImpl.class);
		}
		else if("component".equals(name)){
			object = (ComponentImpl) serializer.deserialize(node, ComponentImpl.class);			
		}
		else if("pageTemplate".equals(name)){
			object = (PageTemplateImpl) serializer.deserialize(node, PageTemplateImpl.class);			
		}
		else if("folder".equals(name)){
			object = (OrganizationalItemImpl) serializer.deserialize(node, OrganizationalItemImpl.class);			
		}
		else if("schema".equals(name)){
			object = (SchemaImpl) serializer.deserialize(node, SchemaImpl.class);			
		}
		else if("structureGroup".equals(name)){
			object = (StructureGroupImpl) serializer.deserialize(node, StructureGroupImpl.class);			
		}
		else if("multimedia".equals(name)){
			object = (MultimediaImpl) serializer.deserialize(node, MultimediaImpl.class);			
		}
		else if("componentPresentation".equals(name)){
			object = (ComponentPresentationImpl) serializer.deserialize(node, ComponentPresentationImpl.class);			
		}
		else if("componentTemplate".equals(name)){
			object = (ComponentTemplateImpl) serializer.deserialize(node, ComponentTemplateImpl.class);			
		}
		else if("category".equals(name)){
			object = (CategoryImpl) serializer.deserialize(node, CategoryImpl.class);			
		}
		else if("keyword".equals(name)){
			object = (KeywordImpl) serializer.deserialize(node, KeywordImpl.class);			
		}
		else if("page".equals(name)){
			object = (PageImpl) serializer.deserialize(node, PageImpl.class);			
		}
		else if("field".equals(name)){
			InputNode fieldTypeNode = node.getAttribute("fieldType");
			if(fieldTypeNode != null){
				
				String type = fieldTypeNode.getValue();
				
				if("Number".equals(type)){
					object = (NumericField) serializer.deserialize(node, NumericField.class);
				}
				else if("Date".equals(type)){
					object = (DateField) serializer.deserialize(node, DateField.class);
				}
				else if("ComponentLink".equals(type) || "MultiMediaLink".equals(type)){
					object = (ComponentLinkField) serializer.deserialize(node, ComponentLinkField.class);
				}
				else if("Xhtml".equals(type)){
					object = (XhtmlField) serializer.deserialize(node, XhtmlField.class);
				}
				else if("Embedded".equals(type)){
					object = (EmbeddedField) serializer.deserialize(node, EmbeddedField.class);
				}
				else {
					object = (TextField) serializer.deserialize(node, TextField.class);					
				}
			}
		}
		
		return object;
	}
	
	@Override
	public void write(OutputNode node, Object object) throws Exception {
		// TODO: implement to support serialization.

	}
}
