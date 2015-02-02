package org.dd4t.core.util;

/**
 * dd4t-2
 *
 * TODO: move to somewhere else.
 * @author R. Kempees
 */
public interface PublicationDescriptor {
	int getId ();

	void setId (int id);

	String getKey ();

	void setKey (String key);

	String getTitle ();

	void setTitle (String title);

	String getMultimediaPath ();

	void setMultimediaPath (String multimediaPath);

	String getMultimediaUrl ();

	void setMultimediaUrl (String multimediaUrl);

	String getPublicationUrl ();

	void setPublicationUrl (String publicationUrl);

	String getPublicationPath ();

	void setPublicationPath (String publicationPath);
}
