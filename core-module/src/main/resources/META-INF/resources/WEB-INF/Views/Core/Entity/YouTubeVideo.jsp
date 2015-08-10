<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.YouTubeVideo" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<div class="video" ${markup.entity(entity)}>
    <c:if test="${not empty entity.headline}">
        <h3 ${markup.property(entity, "headline")}>${entity.headline}</h3>
    </c:if>
    <tri:media media="${entity}" widthFactor="100%"/>
</div>
