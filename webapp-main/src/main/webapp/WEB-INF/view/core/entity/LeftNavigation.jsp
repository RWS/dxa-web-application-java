<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.NavigationLinks" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<nav ${markup.entity(entity)}>
    <xpm:entity entity="${entity}"/>
    <ul class="nav nav-sidebar">
        <c:forEach var="item" items="${entity.items}">
            <li> <%-- TODO: 'active' class --%>
                <tri:link link="${item}"/>
            </li>
        </c:forEach>
    </ul>
</nav>
