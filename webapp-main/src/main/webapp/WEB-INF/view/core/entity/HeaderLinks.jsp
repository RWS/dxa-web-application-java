<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.EmbeddedLinkList" scope="request"/>
<c:if test="${not empty entityModel.links}" >
    <ul class="nav navbar-nav utility-nav">
        <c:forEach var="link" items="${entityModel.links}">
            <li>
                <a href="${link.url}" title="${link.alternateText}">${link.linkText}</a>
            </li>
        </c:forEach>
    </ul>
</c:if>
