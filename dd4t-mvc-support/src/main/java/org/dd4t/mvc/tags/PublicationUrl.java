package org.dd4t.mvc.tags;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.core.factories.impl.PublicationResolverFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Outputs the Publication Url or else an empty string
 *
 * @author R. Kempees
 */
public class PublicationUrl {

	private static final Logger LOG = LoggerFactory.getLogger(PublicationUrl.class);

	public static String getPublicationUrl()
	{
		String publicationUrl = PublicationResolverFactoryImpl.getInstance().getPublicationResolver().getPublicationUrl();
		if (!StringUtils.isEmpty(publicationUrl))
		{
			if (publicationUrl.endsWith("/") && publicationUrl.length() > 1)
			{
				publicationUrl = publicationUrl.substring(0, publicationUrl.length()-1);
			}
			LOG.debug("Returning publication URL: {}",publicationUrl);
			return publicationUrl.toLowerCase();
		}
		return "";
	}
}
