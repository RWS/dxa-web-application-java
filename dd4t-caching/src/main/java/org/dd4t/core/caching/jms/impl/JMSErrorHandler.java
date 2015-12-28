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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

import javax.annotation.Resource;

/**
 * Spring JMS error handler, alternative to the JMSExceptionListener which isn't always easy to hook up.
 *
 * @author rogier.oudshoorn
 */
public class JMSErrorHandler implements ErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(JMSErrorHandler.class);

    @Resource
    private JMSCacheMonitor monitor;

    @Override
    public void handleError (Throwable error) {
        LOG.error("JMS exception occurred", error);
        monitor.setMQServerStatusDown();
    }

    public void setMonitor (JMSCacheMonitor monitor) {
        this.monitor = monitor;
    }
}
