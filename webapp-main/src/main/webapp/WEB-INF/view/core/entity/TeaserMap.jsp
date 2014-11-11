<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.Teaser" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<div ${markup.entity(entityModel)}>
    <xpm:entity entity="${entityModel}"/>
    <h3 ${markup.property(entityModel, "headline")}><xpm:property entity="${entityModel}" property="headline"/>
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
