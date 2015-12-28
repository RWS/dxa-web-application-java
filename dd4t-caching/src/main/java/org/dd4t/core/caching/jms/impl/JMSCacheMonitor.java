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

import org.dd4t.core.caching.CacheInvalidator;
import org.dd4t.core.services.PropertiesService;
import org.dd4t.core.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Mihai Cadariu
 */
public class JMSCacheMonitor {

    private static final Logger LOG = LoggerFactory.getLogger(JMSCacheMonitor.class);
    private int monitorServiceInterval = 30000; // milliseconds

    @Resource
    private CacheInvalidator cacheInvalidator;
    @Resource
    private PropertiesService propertiesService;

    private MQServerStatus serverStatus = MQServerStatus.UP; // assume it's down

    private final Runnable monitor = new Runnable() {
        @Override
        public void run () {
            LOG.debug("Monitor thread running");

            MQServerStatus previousServerStatus = MQServerStatus.UP;
            try {
                while (true) {
                    LOG.debug("JMS MQ server is {} ", serverStatus);

                    if (cacheInvalidator != null) {
                        if (isMQServerDown()) {
                            cacheInvalidator.flush();
                        } else if (previousServerStatus == MQServerStatus.DOWN) {
                            LOG.debug("JMS MQ server recovered. Flush local cache one more time.");
                            cacheInvalidator.flush();
                        }
                        previousServerStatus = serverStatus;
                    }

                    Thread.sleep(monitorServiceInterval);
                }
            } catch (InterruptedException e) {
                LOG.debug("Cache monitor thread interrupted");
            }
        }
    };

    private Thread thread;

    @PostConstruct
    public void init () {
        LOG.debug("Create new instance");
        monitorServiceInterval = Integer.parseInt(propertiesService.getProperty(Constants.MONITOR_SERVICE_INTERVAL,"30000"));
        LOG.debug("Using Monitor interval (or cache refresh time when JMS is down) = {} seconds", monitorServiceInterval, monitorServiceInterval / 1000);
        thread = new Thread(monitor);
        thread.setName("Dd4tWebAppJMSMonitorThread");

        LOG.debug("Start cache monitor thread");

        thread.start();
    }

    public int getMonitorServiceInterval () {
        return monitorServiceInterval;
    }

    public void setMonitorServiceInterval (int monitorServiceInterval) {
        this.monitorServiceInterval = monitorServiceInterval;
    }

    public boolean isMQServerUp () {
        return serverStatus == MQServerStatus.UP;
    }

    public boolean isMQServerDown () {
        return serverStatus == MQServerStatus.DOWN;
    }

    public MQServerStatus getMQServerStatus () {
        return serverStatus;
    }

    public void setMQServerStatus (MQServerStatus status) {
        this.serverStatus = status;
    }

    public void shutdown () throws InterruptedException {
        LOG.debug("Stopping thread monitor");

        thread.interrupt();
        thread.join();

        LOG.debug("Thread monitor stopped successfully");
    }

    public void setMQServerStatusDown () {
        if (isMQServerUp()) {
            LOG.debug("Detected MQ server connection dropped. Setting status DOWN");
            setMQServerStatus(MQServerStatus.DOWN);
        }
    }

    public void setMQServerStatusUp () {
        if (isMQServerDown()) {
            LOG.debug("Detected MQ server activity. Setting status UP");
            setMQServerStatus(MQServerStatus.UP);
        }
    }

    public void setCacheInvalidator (CacheInvalidator cacheInvalidator) {
        this.cacheInvalidator = cacheInvalidator;
    }

    public void setPropertiesService (final PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    public enum MQServerStatus {
        UP, DOWN
    }
}
