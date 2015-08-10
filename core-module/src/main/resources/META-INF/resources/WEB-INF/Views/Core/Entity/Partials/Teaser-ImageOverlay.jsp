<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="region" type="com.sdl.webapp.common.api.model.Region" scope="request"/>
<jsp:useBean id="item" type="com.sdl.webapp.common.api.model.entity.Teaser" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<jsp:useBean id="screenWidth" type="com.sdl.webapp.common.api.ScreenWidth" scope="request"/>
<c:choose>
    <c:when test="${region.name == 'Hero'}">
        <c:choose>
            <c:when test="${screenWidth == 'EXTRA_SMALL'}">
                <c:set var="imageAspect" value="2.0"/>
            </c:when>
            <c:when test="${screenWidth == 'SMALL'}">
                <c:set var="imageAspect" value="2.5"/>
            </c:when>
            <c:otherwise>
                <c:set var="imageAspect" value="3.3"/>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:set var="imageAspect" value="0.0"/>
    </c:otherwise>
</c:choose>
<div ${markup.entity(item)}>
    <c:choose>
        <c:when test="${not empty item.media}" >
            <span ${markup.property(item, "media")}>
                <tri:media media="${item.media}" widthFactor="100%" aspect="${imageAspect}"/>
            </span>
        </c:when>
        <c:otherwise>
            <img src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7" alt="" width="100%">
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty item.headline or not empty item.text}">
        <div class="overlay overlay-tl ribbon">
            <c:if test="${not empty item.headline}">
                <h2 ${markup.property(item, "headline")}>${item.headline}</h2>
            </c:if>
            <c:if test="${not empty item.text}">
                <div ${markup.property(item, "text")}>${markup.replaceLineEndsWithHtmlBreaks(item.text)}</div>
            </c:if>
        </div>
    </c:if>
    <c:if test="${not empty item.link.linkText}">
        <div class="carousel-caption">
            <p>
                <a href="${item.link.url}" title="${item.link.alternateText}" class="btn btn-primary" ${markup.property(item, "link")}>
                    ${item.link.linkText}
                </a>
            </p>
        </div>
    </c:if>
</div>
