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
package org.dd4t.core.serializers;

import java.io.Serializable;

/**
 * Interface for serializer which is used to deserialize the xml source from
 * tridion into the cwa content model.
 * 
 * @author bjornl
 * 
 */
public interface Serializer {
	/**
	 * Deserialize the input into the specified class
	 * 
	 * @param s
	 *            the object to deserialize
	 * @param c
	 *            the class to deserialize into
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public Object deserialize(Object s, Class c) throws Exception;

	/**
	 * Serialize an object 
	 * 
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public Serializable serialize(Object o) throws Exception;
}
