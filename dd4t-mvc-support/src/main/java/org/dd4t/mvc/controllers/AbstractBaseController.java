package org.dd4t.mvc.controllers;

import org.dd4t.core.resolvers.PublicationResolver;
import org.dd4t.core.services.PropertiesService;
import org.dd4t.core.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public abstract class AbstractBaseController {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractBaseController.class);
    @Resource
    protected PublicationResolver publicationResolver;
    @Resource
    protected PropertiesService propertiesService;
    /**
     * Boolean indicating if context path on the page URL should be removed, defaults to true
     */
    protected boolean removeContextPath = false;

    @PostConstruct
    protected void init() {
        String stripContext = propertiesService.getProperty(Constants.PROPERTY_STRIP_CONTEXT_PATH,"false");
        if ("true".equalsIgnoreCase(stripContext)|| "false".equalsIgnoreCase(stripContext)) {
            removeContextPath = Boolean.valueOf(stripContext);
        } else {
            LOG.warn("{} not set! If a Servlet Context path is present and it should be stripped, this will not be done.",Constants.PROPERTY_STRIP_CONTEXT_PATH);
        }
    }
    /**
     * Create Date format for last-modified headers. Note that a constant
     * SimpleDateFormat is not allowed, it's access should be sync-ed.
     */
    protected static DateFormat createDateFormat () {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.HEADER_DATE_FORMAT, Locale.US);
        dateFormat.setTimeZone(Constants.TIMEZONE_GMT);
        return dateFormat;
    }

    public void setRemoveContextPath (boolean removeContextPath) {
        this.removeContextPath = removeContextPath;
    }

    public boolean removeContextPath () {
        return removeContextPath;
    }

    public PublicationResolver getPublicationResolver () {
        return publicationResolver;
    }

    public void setPublicationResolver (final PublicationResolver publicationResolver) {
        this.publicationResolver = publicationResolver;
    }
}
