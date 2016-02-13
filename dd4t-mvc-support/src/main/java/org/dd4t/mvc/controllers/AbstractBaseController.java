package org.dd4t.mvc.controllers;

import org.dd4t.core.resolvers.PublicationResolver;
import org.dd4t.core.services.PropertiesService;
import org.dd4t.core.util.Constants;

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
        removeContextPath = Boolean.valueOf(propertiesService.getProperty(Constants.PROPERTY_STRIP_CONTEXT_PATH,"false"));
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
