<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.LinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<div ${markup.entity(entity)}>
    <c:if test="${not empty entity.headline}">
        <div class="h4" ${markup.property(entity, "headline")}>${entity.headline}</div>
    </c:if>
    <c:if test="${not empty entity.links}">
        <ul class="list-unstyled">
            <c:forEach var="link" items="${entity.links}" varStatus="status">
                <li ${markup.property(entity, "links", status.index)}>
                    <tri:link link="${link}"/>
                </li>
            </c:forEach>
        </ul>
    </c:if>
</div>
