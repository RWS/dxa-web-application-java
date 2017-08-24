/**
 * Copyright 2016 Nordea
 * <p></p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p></p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p></p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
