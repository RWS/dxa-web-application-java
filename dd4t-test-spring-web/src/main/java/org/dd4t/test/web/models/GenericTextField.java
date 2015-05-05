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

package org.dd4t.test.web.models;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

import java.util.List;

@ViewModel(
		viewModelNames = {"footnote","product-details"},
		rootElementNames = {"article"},
		setComponentObject = true)
public class GenericTextField extends TridionViewModelBase {

	@ViewModelProperty(entityFieldName = "author", isMetadata = true)
	private String author;

	@ViewModelProperty(entityFieldName = "heading")
	private String heading;

	@ViewModelProperty(entityFieldName = "events")
	private List<Component> events;

	// Reference another View Model
	@ViewModelProperty(entityFieldName = "reference")
	private Reference reference;

	@ViewModelProperty(entityFieldName = "heroImage", tridionFieldType = FieldType.MULTIMEDIALINK)
	private Component heroImage;

	@ViewModelProperty(entityFieldName = "multimedia")
	private List<Component> multimedia;

	@ViewModelProperty(entityFieldName = "tags")
	private List<Keyword> keywords;

	public String getAuthor () {
		return author;
	}

	public void setAuthor (final String author) {
		this.author = author;
	}

	public String getHeading () {
		return heading;
	}

	public void setHeading (final String heading) {
		this.heading = heading;
	}

	public List<Component> getEvents () {
		return events;
	}

	public void setEvents (final List<Component> events) {
		this.events = events;
	}

	public Reference getReference () {
		return reference;
	}

	public void setReference (final Reference reference) {
		this.reference = reference;
	}

	public Component getHeroImage () {
		return heroImage;
	}

	public void setHeroImage (final Component heroImage) {
		this.heroImage = heroImage;
	}

	public List<Component> getMultimedia () {
		return multimedia;
	}

	public void setMultimedia (final List<Component> multimedia) {
		this.multimedia = multimedia;
	}

	public List<Keyword> getKeywords () {
		return keywords;
	}

	public void setKeywords (final List<Keyword> keywords) {
		this.keywords = keywords;
	}
}
