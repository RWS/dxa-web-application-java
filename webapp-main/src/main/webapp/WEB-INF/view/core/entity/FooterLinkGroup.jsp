<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.LinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<div ${markup.entity(entityModel)}><xpm:entity entity="${entityModel}"/>
    <c:if test="${not empty entityModel.headline}">
        <div class="h4" ${markup.property(entityModel, "headline")}><xpm:property entity="${entityModel}" property="headline"/>${entityModel.headline}</div>
    </c:if>
    <c:if test="${not empty entityModel.links}">
        <ul class="list-unstyled">
            <c:forEach var="link" items="${entityModel.links}" varStatus="status">
                <li ${markup.property(entityModel, "links")}><xpm:property entity="${entityModel}" property="links" index="${status.index}"/><tri:link link="${link}"/></li>
            </c:forEach>
        </ul>
    </c:if>
</div>
