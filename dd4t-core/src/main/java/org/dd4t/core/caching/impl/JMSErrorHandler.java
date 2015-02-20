package org.dd4t.core.caching.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ErrorHandler;

/**
 * Spring JMS error handler, alternative to the JMSExceptionListener which isn't always easy to hook up.
 * 
 * @author rogier.oudshoorn
 *
 */
public class JMSErrorHandler implements ErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JMSErrorHandler.class);

    @Autowired
    private JMSCacheMonitor monitor;

    @Override
	public void handleError(Throwable error) {
        LOG.error("JMS exception occurred", error);
        monitor.setMQServerStatusDown();
    }

    public void setMonitor(JMSCacheMonitor monitor) {
        this.monitor = monitor;
    }
}
