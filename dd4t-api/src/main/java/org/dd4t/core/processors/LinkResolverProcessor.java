package org.dd4t.core.processors;

public interface LinkResolverProcessor extends Processor {
    public String getContextPath();

    public void setContextPath(String contextPath);
}
