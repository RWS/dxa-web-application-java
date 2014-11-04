<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sdl.webapp.common.api.model.entity.TagLink" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.text.MessageFormat" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@ page import="com.sdl.webapp.common.api.WebRequestContext" %>
<%@ page import="com.sdl.webapp.common.api.localization.Localization" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.TagLinkList" scope="request"/>
<div class="share-buttons clearfix">
    <tri:resource key="core.shareOnSocialCaption"/>
    <ul>
        <%
            final String pageUrl = URLEncoder.encode(request.getRequestURL().toString(), "UTF-8");

            final Localization localization = WebApplicationContextUtils.getRequiredWebApplicationContext(
                    pageContext.getServletContext()).getBean(WebRequestContext.class).getLocalization();

            final String shareOnSocialLinkTitle = localization.getResource("core.shareOnSocialLinkTitle");

            if (entityModel.getLinks() != null) {
                for (TagLink link : entityModel.getLinks()) {
                    %><li>
                        <a href="javascript:window.open('<%= MessageFormat.format(link.getUrl(), pageUrl) %>', '_blank', 'width=400,height=500');void(0);" title="<%= MessageFormat.format(shareOnSocialLinkTitle, link.getTag().getDisplayText()) %>">
                            <i class="fa fa-<%= link.getTag().getKey() %>"></i>
                        </a>
                    </li><%
                }
            }
        %>
    </ul>
</div>
