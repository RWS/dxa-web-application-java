package org.dd4t.core.util;

import java.io.Serializable;

/**
 * Created by Quirijn on 12-11-2014.
 * Used to maintain information about the current publication between requests.
 */
public class PublicationDescriptor implements Serializable {
	private String publicationUrl;
	private String imageUrl;
	private int id;
	public String getPublicationUrl() {
		return publicationUrl;
	}
	public void setPublicationUrl(String publicationUrl) {
		this.publicationUrl = publicationUrl;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
