package org.dd4t.core.caching.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 * @author Mihai Cadariu
 * @since 24.07.2014
 */
public class JMSExceptionListener implements ExceptionListener {

    private static final Logger LOG = LoggerFactory.getLogger(JMSExceptionListener.class);

    @Autowired
    private AnchorageCacheMonitor monitor;

    @Override
    public void onException(JMSException jmse) {
        LOG.error("JMS exception occurred", jmse);
        monitor.setMQServerStatusDown();
    }

    public void setMonitor(AnchorageCacheMonitor monitor) {
        this.monitor = monitor;
    }
}
