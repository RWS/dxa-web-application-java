<%@ page import="java.net.URLEncoder" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dxa" uri="http://www.sdl.com/tridion-dxa" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>

<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.TagLinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>

<% pageContext.setAttribute("pageUrl", URLEncoder.encode(request.getAttribute(com.sdl.webapp.common.controller.RequestAttributeNames.SOCIALSHARE_URL).toString(), "UTF-8")); %>
<div class="share-buttons clearfix ${entity.htmlClasses}" ${markup.entity(entity)}>
    <dxa:resource key="core.shareOnSocialCaption"/>
    <ul>
        <c:forEach var="link" items="${entity.links}" varStatus="status">
            <li ${markup.property(entity, "links", status.index)}>
                <a href="javascript:window.open('${markup.formatMessage(link.url, pageUrl)}', '_blank', 'width=400,height=500');void(0);" title="<dxa:resource key="core.shareOnSocialLinkTitle" arg1="${link.tag.displayText}"/>">
                    <i class="fa fa-${link.tag.key}"></i>
                </a>
            </li>
        </c:forEach>
    </ul>
</div>
