/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.contentmodel;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

/**
 * Interface for all components
 *
 * @author bjornl
 */
public interface Component extends RepositoryLocalItem {

	/**
	 * Get the schema of the component
	 *
	 * @return the schema
	 */
	public Schema getSchema ();

	/**
	 * Set the schema of the component
	 *
	 * @param schema
	 */
	public void setSchema (Schema schema);

	/**
	 * Get the last published date
	 *
	 * @return the last published date
	 */
	public DateTime getLastPublishedDate ();

	/**
	 * Set the last published date
	 *
	 * @param date published date
	 */
	public void setLastPublishedDate (DateTime date);

	/**
	 * Get the metadata
	 *
	 * @return a map of field objects representing the metadata
	 */
	public Map<String, Field> getMetadata ();

	/**
	 * Set the metadata
	 */
	public void setMetadata (Map<String, Field> metadata);

	/**
	 * Get the content
	 *
	 * @return a map of field objects representing the content
	 */
	public Map<String, Field> getContent ();

	/**
	 * Set the content
	 */
	public void setContent (Map<String, Field> content);

	/**
	 * Get the component type
	 *
	 * @return the component type
	 */
	public ComponentType getComponentType ();

	/**
	 * Set the component type
	 *
	 * @param componentType
	 */
	public void setComponentType (ComponentType componentType);

	/**
	 * Get the multimedia object
	 *
	 * @return the multimedia object
	 */
	public Multimedia getMultimedia ();

	/**
	 * Set the multimedia object
	 */
	public void setMultimedia (Multimedia multimedia);

	public List<Category> getCategories ();

	public void setCategories (List<Category> categories);

	public int getVersion ();

	DateTime getRevisionDate ();

	void setRevisionDate (DateTime date);

	public enum ComponentType {
		Multimedia(0), Normal(1), UNKNOWN(-1);
		private final int value;

		ComponentType (int value) {
			this.value = value;
		}

		public static ComponentType findByValue (int value) {
			for (ComponentType componentType : values()) {
				if (componentType.getValue() == value) {
					return componentType;
				}
			}

			return UNKNOWN;
		}

		public static ComponentType findByName (String name) {
			try {
				return ComponentType.valueOf(name.toUpperCase());
			} catch (IllegalArgumentException iae) {
				try {
					int value = Integer.parseInt(name);
					return findByValue(value);
				} catch (NumberFormatException nfe) {
					return UNKNOWN;
				}
			}
		}

		public int getValue () {
			return value;
		}
	}
}
