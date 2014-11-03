<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.Teaser" scope="request"/>
<div>
    <a class="navbar-logo" href="${entityModel.link.url}" title="${entityModel.link.linkText}">
        <c:if test="${not empty entityModel.media}">
            <c:set var="image" value="${entityModel.media}" />
            <span>
                <img src="${image.url}" alt="${image.alternateText}" height="80">
            </span>
        </c:if>
    </a>
</div>
