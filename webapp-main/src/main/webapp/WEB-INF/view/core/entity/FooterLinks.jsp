<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.LinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<ul class="list-inline text-center" ${markup.entity(entityModel)}>
    <c:if test="${not empty entityModel.headline}">
        <li><small ${markup.property(entityModel, "headline")}>${entityModel.headline}</small></li>
    </c:if>
    <c:forEach var="link" items="${entityModel.links}">
        <li><small ${markup.property(entityModel, "links")}><tri:link link="${link}"/></small></li>
    </c:forEach>
</ul>
