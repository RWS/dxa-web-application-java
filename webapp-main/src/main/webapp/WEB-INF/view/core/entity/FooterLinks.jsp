<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="pageModel" type="com.sdl.webapp.common.api.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.LinkList" scope="request"/>

<ul class="list-inline text-center">
    <c:if test="${not empty entityModel.headline}">
        <li>
            <small>${entityModel.headline}</small>
        </li>
    </c:if>

    <c:forEach var="link" items="${entityModel.links}">
        <li>
            <small>
                <a href="${link.url}" title="${link.alternateText}">${link.linkText}</a>
            </small>
        </li>
    </c:forEach>
</ul>