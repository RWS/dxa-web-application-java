package com.sdl.webapp.common.api;

import org.springframework.beans.factory.config.AbstractFactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Default Implementation
 * Is a way of doing a default implementation (singletons) in the core that can be overridden by modules.
 *
 * @author nic
 */
public abstract class DefaultImplementation<T> extends AbstractFactoryBean<T> implements InvocationHandler {

    private T implementation = (T) this;

    // TODO: Add functionality to override by prio etc

    public void override(T implementation) {
        this.implementation = implementation;
    }

    @Override
    protected T createInstance() throws Exception {
        return this.createProxy();
    }

    protected T createProxy() {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] {this.getObjectType()}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return method.invoke(this.implementation, args);
    }
}
