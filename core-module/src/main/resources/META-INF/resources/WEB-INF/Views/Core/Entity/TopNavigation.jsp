<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.springframework.web.util.UrlPathHelper" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.NavigationLinks" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<% request.setAttribute("requestPath", new UrlPathHelper().getOriginatingRequestUri(request)); %>
<ul class="nav navbar-nav main-nav" ${markup.entity(entity)}>
    <c:forEach var="item" items="${entity.items}">
        <c:set var="cssClass" value="${requestPath.startsWith(item.url) ? 'active' : ''}"/>
        <li class="${cssClass}">
            <a href="${item.url}" title="${item.alternateText}">${item.linkText}</a>
        </li>
    </c:forEach>
</ul>
