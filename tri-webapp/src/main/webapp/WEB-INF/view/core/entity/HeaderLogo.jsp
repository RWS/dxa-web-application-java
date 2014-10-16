<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.tridion.referenceimpl.common.model.entity.Teaser" scope="request"/>

<div>
    <a class="navbar-logo" href="${entityModel.link.url}" title="${entityModel.link.linkText}">

        <!-- TODO Media item has no altText? -->
        <c:if test="${not empty entityModel.media}">
            <span>
                <img src="${entityModel.media.url}" alt="${entityModel.media.fileName}" height="80">
            </span>
        </c:if>
    </a>
</div>