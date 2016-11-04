package org.dd4t.databind;

import org.dd4t.contentmodel.Component.ComponentType;
import org.dd4t.contentmodel.FieldType;
import org.simpleframework.xml.transform.Transform;

public class ComponentTypeTransformer implements Transform<ComponentType> {
	  
	@Override
	public ComponentType read(String value) throws Exception {
		return ComponentType.findByName(value);
	}

	@Override
	public String write(ComponentType value) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
