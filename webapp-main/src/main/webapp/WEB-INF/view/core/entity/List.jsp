<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.ContentList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<div ${markup.entity(entityModel)}>
    <c:if test="${not empty entityModel.headline}">
        <h3 ${markup.property(entityModel, "headline")}>${entityModel.headline}</h3>
    </c:if>
    <ul>
        <c:forEach var="item" items="${entityModel.itemListElements}">
            <li>
                <c:choose>
                    <c:when test="${not empty item.link.url}">
                        <a href="${item.link.url}" title="${item.link.alternateText}">
                            <c:choose>
                                <c:when test="${not empty item.headline}">${item.headline}</c:when>
                                <c:otherwise>${item.link.url}</c:otherwise>
                            </c:choose>
                        </a>
                    </c:when>
                    <c:otherwise>
                        ${item.headline}
                    </c:otherwise>
                </c:choose>
                <c:if test="${not empty item.date}">
                    <time class="meta small">[${tri:formatDateTime(item.date, "d MMM yyyy")}]</time>
                </c:if>
            </li>
        </c:forEach>
    </ul>
    <c:if test="${not empty entityModel.link.url}">
        <p ${markup.property(entityModel, "link.linkText")}> <%-- TODO: This does not work! --%>
            <a href="${entityModel.link.url}" title="${entityModel.link.alternateText}">
                <c:choose>
                    <c:when test="${not empty entityModel.link.linkText}">${entityModel.link.linkText}</c:when>
                    <c:otherwise><tri:resource key="core.readMoreLinkText"/></c:otherwise>
                </c:choose>
                <i class="fa fa-chevron-right"></i>
            </a>
        </p>
    </c:if>
</div>
