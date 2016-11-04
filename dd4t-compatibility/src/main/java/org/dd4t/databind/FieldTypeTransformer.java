package org.dd4t.databind;

import org.dd4t.contentmodel.FieldType;
import org.simpleframework.xml.transform.Transform;

public class FieldTypeTransformer implements Transform<FieldType> {
	  
	@Override
	public FieldType read(String value) throws Exception {
		return FieldType.findByName(value);
	}

	@Override
	public String write(FieldType value) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
