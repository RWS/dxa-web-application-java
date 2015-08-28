<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.Teaser" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<div ${markup.entity(entity)}>
    <a class="navbar-logo" href="${entity.link.url}" title="${entity.link.linkText}">
        <c:if test="${not empty entity.media}">
            <span ${markup.property(entity, "media")}>
                <img src="${entity.media.url}" alt="${entity.media.alternateText}" height="80">
            </span>
        </c:if>
    </a>
</div>
