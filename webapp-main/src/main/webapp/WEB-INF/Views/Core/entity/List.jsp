<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.ContentList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<div ${markup.entity(entity)}>
    <xpm:entity entity="${entity}"/>
    <c:if test="${not empty entity.headline}">
        <h3 ${markup.property(entity, "headline")}><xpm:property entity="${entity}" property="headline"/>${entity.headline}</h3>
    </c:if>
    <ul>
        <c:forEach var="item" items="${entity.itemListElements}">
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
                    <time class="meta small">[${markup.formatDateTime(item.date, "d MMM yyyy")}]</time>
                </c:if>
            </li>
        </c:forEach>
    </ul>
    <c:if test="${not empty entity.link.url}">
        <p ${markup.property(entity.link, "link")}>
            <xpm:property entity="${entity.link}" property="linkText"/>
            <a href="${entity.link.url}" title="${entity.link.alternateText}">
                <c:choose>
                    <c:when test="${not empty entity.link.linkText}">${entity.link.linkText}</c:when>
                    <c:otherwise><tri:resource key="core.readMoreLinkText"/></c:otherwise>
                </c:choose>
                <i class="fa fa-chevron-right"></i>
            </a>
        </p>
    </c:if>
</div>
