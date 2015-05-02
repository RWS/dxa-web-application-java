package org.dd4t.mvc.utils;

import org.dd4t.core.util.HttpUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

public final class JspBeanContext {

	private JspBeanContext() {
	}

	public static <T> T getBean(Class<T> type) {
		return getWebApplicationContext().getBean(type);
	}

	public static Object getBean(String beanName) {
		return getWebApplicationContext().getBean(beanName);
	}

	public static <T> T getBean(String beanName, Class<T> type) {
		return getWebApplicationContext().getBean(beanName, type);
	}

	private static WebApplicationContext getWebApplicationContext() {
		return RequestContextUtils.getWebApplicationContext(HttpUtils.getCurrentRequest());
	}
}
