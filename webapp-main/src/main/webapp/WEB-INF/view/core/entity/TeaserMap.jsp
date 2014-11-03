<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.Teaser" scope="request"/>
<div>
    <h3>
        <c:choose>
            <c:when test="${not empty entityModel.link.url}">
                <a href="${entityModel.link.url}">${entityModel.headline}</a>
            </c:when>
            <c:otherwise>
                ${entityModel.headline}
            </c:otherwise>
        </c:choose>
    </h3>
    <c:if test="${not empty entityModel.location}">
        <tri:googlemap latitude="${entityModel.location.latitude}" longitude="${entityModel.location.longitude}"
                       markerName="${entityModel.headline}" mapWidth="311" mapHeight="160"/>
    </c:if>
</div>
