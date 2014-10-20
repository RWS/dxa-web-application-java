<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.tridion.referenceimpl.common.model.entity.LinkList<com.sdl.tridion.referenceimpl.common.model.entity.Link>" scope="request"/>

<c:if test="${not empty entityModel.links}" >
    <ul class="nav navbar-nav utility-nav">
        <c:forEach var="link" items="${entityModel.links}">
            <li>
                <a href="${link.url}" title="${link.alternateText}">${link.linkText}</a>
            </li>
        </c:forEach>
    </ul>
</c:if>
