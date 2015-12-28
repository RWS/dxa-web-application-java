package org.dd4t.core.caching.jms.impl;

import org.apache.activemq.transport.TransportListener;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author Mihai Cadariu
 * @since 13.09.2014
 */
public class JMSTransportListener implements TransportListener {

    @Resource
    private JMSCacheMonitor monitor;

    @Override
    public void onCommand (Object o) {
        monitor.setMQServerStatusUp();
    }

    @Override
    public void onException (IOException e) {
        monitor.setMQServerStatusDown();
    }

    @Override
    public void transportInterupted () {
        monitor.setMQServerStatusDown();
    }

    @Override
    public void transportResumed () {
        monitor.setMQServerStatusUp();
    }
}

