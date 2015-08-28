package com.sdl.webapp.cid;

import com.sdl.webapp.common.api.DefaultImplementation;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.api.WebRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * ContextualMediaHelperFactory
 *
 * @author nic
 */
@Component
public class ContextualMediaHelperFactory {


    @Autowired
    @Qualifier("defaultMediaHelper")
    private DefaultImplementation<MediaHelper> defaultImplementation;

    @Autowired
    private WebRequestContext webRequestContext;

    @PostConstruct
    public void createMediaHelper() {
        MediaHelper contextualMediaHelper = new ContextualMediaHelper(this.webRequestContext);
        defaultImplementation.override(contextualMediaHelper);
    }
}
