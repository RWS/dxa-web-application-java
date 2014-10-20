<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="pageModel" type="com.sdl.webapp.common.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.model.entity.ContentList<com.sdl.webapp.common.model.entity.Teaser>" scope="request"/>

<div>
    <c:if test="${not empty entityModel.headline}">
        <h3>${entityModel.headline}</h3>
    </c:if>

    <ul>
        <c:forEach var="teaser" items="${entityModel.itemListElements}">
            <a href="${teaser.link.url}" title="${teaser.link.alternateText}">
                <c:choose>
                    <c:when test="${not empty teaser.headline}">
                        ${teaser.headline}
                    </c:when>
                    <c:otherwise>
                        ${teaser.text}
                    </c:otherwise>
                </c:choose>
            </a>

            <c:if test="${not empty teaser.date}">
                [<fmt:formatDate value="${teaser.date}" pattern="d MMM yyyy" />]
            </c:if>
        </c:forEach>
    </ul>

    <c:if test="${not empty entityModel.link}">
        <p>
            <a href="${entityModel.link.url}" title="${entityModel.link.alternateText}">
                <c:choose>
                    <c:when test="${not empty entityModel.link.linkText}">
                        ${entityModel.link.linkText}
                    </c:when>
                    <c:otherwise>
                        <tri:resource key="core.readMoreLinkText" />
                    </c:otherwise>
                </c:choose>

                <i class="fa fa-chevron-right"></i>
            </a>
        </p>
    </c:if>
</div>
