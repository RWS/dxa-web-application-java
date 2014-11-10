<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.LinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<c:if test="${not empty entityModel.links}" >
    <ul class="nav navbar-nav utility-nav" ${markup.entity(entityModel)}>
        <c:forEach var="link" items="${entityModel.links}">
            <li ${markup.property(entityModel, "links")}><tri:link link="${link}"/></li>
        </c:forEach>
    </ul>
</c:if>
