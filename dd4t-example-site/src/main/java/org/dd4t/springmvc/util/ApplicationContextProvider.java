/*
 * Copyright (c) 2015 R. Oudshoorn
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
package org.dd4t.springmvc.util;

//ApplicationContextProvider.java

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextAware;

/**
 * Wrapper to always return a reference to the Spring Application Context from
 * within non-Spring enabled beans. Unlike Spring MVC's WebApplicationContextUtils
 * we do not need a reference to the Servlet context for this. All we need is
 * for this bean to be initialized during application startup.
 *
 * @author <a href="rogier.oudshoorn@">Rogier Oudshoorn</a>
 */
public class ApplicationContextProvider implements ApplicationContextAware {

	private static org.springframework.context.ApplicationContext applicationContext;

	/**
	 * This method is called from within the ApplicationContext once it is
	 * done starting up, it will stick a reference to itself into this bean.
	 *
	 * @param context a reference to the ApplicationContext.
	 */
	@Override
	public void setApplicationContext (org.springframework.context.ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	/**
	 * This is about the same as context.getBean("beanName"), except it has its
	 * own static handle to the Spring context, so calling this method statically
	 * will give access to the beans by name in the Spring application context.
	 * As in the context.getBean("beanName") call, the caller must cast to the
	 * appropriate target class. If the bean does not exist, then a Runtime error
	 * will be thrown.
	 *
	 * @param beanName the name of the bean to get.
	 * @return an Object reference to the named bean.
	 */
	public static Object getBean (String beanName) {
		return applicationContext.getBean(beanName);
	}
}
