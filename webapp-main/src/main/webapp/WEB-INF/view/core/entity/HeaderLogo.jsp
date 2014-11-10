<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.Teaser" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<div ${markup.entity(entityModel)}>
    <a class="navbar-logo" href="${entityModel.link.url}" title="${entityModel.link.linkText}">
        <c:if test="${not empty entityModel.media}">
            <span ${markup.property(entityModel, "media")}>
                <img src="${entityModel.media.url}" alt="${entityModel.media.alternateText}" height="80">
            </span>
        </c:if>
    </a>
</div>
