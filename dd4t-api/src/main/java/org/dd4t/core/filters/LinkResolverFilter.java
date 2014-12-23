package org.dd4t.core.filters;

public interface LinkResolverFilter extends Filter {
    public String getContextPath();

    public void setContextPath(String contextPath);
}
