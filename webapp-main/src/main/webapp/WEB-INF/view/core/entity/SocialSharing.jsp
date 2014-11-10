<%@ page import="java.net.URLEncoder" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.TagLinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<% pageContext.setAttribute("pageUrl", URLEncoder.encode(request.getRequestURL().toString(), "UTF-8")); %>
<div class="share-buttons clearfix" ${markup.entity(entityModel)}><xpm:entity entity="${entityModel}"/>
    <tri:resource key="core.shareOnSocialCaption"/>
    <ul>
        <c:forEach var="link" items="${entityModel.links}" varStatus="status">
            <li ${markup.property(entityModel, "links")}><xpm:property entity="${entityModel}" property="links" index="${status.index}"/>
                <a href="javascript:window.open('${tri:formatMessage(link.url, pageUrl)}', '_blank', 'width=400,height=500');void(0);"
                   title="<tri:resource key="core.shareOnSocialLinkTitle" arg1="${link.tag.displayText}"/>">
                    <i class="fa fa-${link.tag.key}"></i>
                </a>
            </li>
        </c:forEach>
    </ul>
</div>
