<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.LinkList" scope="request"/>
<c:if test="${not empty entityModel.links}" >
    <ul class="nav navbar-nav utility-nav">
        <c:forEach var="link" items="${entityModel.links}">
            <li>
                <tri:link link="${link}"/>
            </li>
        </c:forEach>
    </ul>
</c:if>
