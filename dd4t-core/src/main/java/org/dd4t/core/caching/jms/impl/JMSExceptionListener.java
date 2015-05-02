package org.dd4t.core.caching.jms.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;

/**
 * @author Mihai Cadariu
 */
public class JMSExceptionListener implements ExceptionListener {

    private static final Logger LOG = LoggerFactory.getLogger(JMSExceptionListener.class);

    @Autowired
    private JMSCacheMonitor monitor;

    @Override
    public void onException(JMSException jmse) {
        LOG.error("JMS exception occurred", jmse);
        monitor.setMQServerStatusDown();
    }

    public void setMonitor(JMSCacheMonitor monitor) {
        this.monitor = monitor;
    }
}
