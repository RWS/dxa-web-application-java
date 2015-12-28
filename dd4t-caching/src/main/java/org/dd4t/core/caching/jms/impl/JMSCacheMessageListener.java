/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.core.caching.jms.impl;

import com.tridion.cache.CacheEvent;
import org.dd4t.core.caching.CacheInvalidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * @author Mihai Cadariu
 */
public class JMSCacheMessageListener implements MessageListener {

    private static final Logger LOG = LoggerFactory.getLogger(JMSCacheMessageListener.class);
    @Resource
    protected CacheInvalidator cacheInvalidator;
    @Resource
    private JMSCacheMonitor monitor;

    public void setMonitor (JMSCacheMonitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public void onMessage (Message message) {
        CacheEvent event = getCacheEvent(message);
        if (event != null) {
            switch (event.getType()) {
                case CacheEvent.INVALIDATE:
                    LOG.debug("Invalidate " + event);
                    Serializable key = event.getKey();
                    cacheInvalidator.invalidate(key.toString());
                    monitor.setMQServerStatusUp();
                    break;

                case CacheEvent.FLUSH:
                    LOG.debug("Flush " + event);
                    monitor.setMQServerStatusUp();
                    break;
                default:
                    break;
            }
        }
    }

    private CacheEvent getCacheEvent (Message message) {
        CacheEvent event = null;

        try {
            if (message instanceof ObjectMessage) {
                ObjectMessage objectMessage = (ObjectMessage) message;
                Serializable serializable = objectMessage.getObject();
                if (serializable instanceof CacheEvent) {
                    event = (CacheEvent) serializable;
                } else {
                    LOG.error("JMS message is not a com.tridion.cache.CacheEvent");
                }
            } else {
                LOG.error("Unknown message type received: " + message.getClass().getName());
            }
        } catch (JMSException jmse) {
            LOG.error("Cannot read JMS message", jmse);
        }

        return event;
    }

    /**
     * Set the cache agent.
     */
    public void setCacheInvalidator (CacheInvalidator cacheAgent) {
        cacheInvalidator = cacheAgent;
    }
}
