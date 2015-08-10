<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.Teaser" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<jsp:useBean id="formatOptions" type="java.util.HashMap" scope="request"/>
<jsp:useBean id="screenWidth" type="com.sdl.webapp.common.api.ScreenWidth" scope="request"/>
<c:set var="linkStyle" value="${formatOptions['linkStyle']}"/>
<c:if test="${empty linkStyle}"><c:set var="linkStyle" value="teaser-link"/></c:if>
<div class="teaser ${formatOptions['style']}" ${markup.entity(entity)}>
    <c:if test="${not empty entity.media}">
        <div ${markup.property(entity, "media")}>
            <tri:media media="${entity.media}" aspect="1.62" widthFactor="${screenWidth == 'EXTRA_SMALL' ? '160px' : '100%'}" cssClass="teaser-img loader-img"/>
        </div>
    </c:if>
    <h3 class="teaser-heading ${formatOptions['headingStyle']}" ${markup.property(entity, "headline")}>
        <c:choose>
            <c:when test="${not empty entity.link.url}">
                <a href="${entity.link.url}">${entity.headline}</a>
            </c:when>
            <c:otherwise>
                ${entity.headline}
            </c:otherwise>
        </c:choose>
    </h3>
    <p class="teaser-description ${formatOptions['descriptionStyle']}" ${markup.property(entity, "text")}>
        ${entity.text}
    </p>
    <c:if test="${not empty entity.link.url}">
        <a class="${linkStyle}" href="${entity.link.url}" title="${entity.link.alternateText}" ${markup.property(entity, "link")}>
            <c:choose>
                <c:when test="${not empty entity.link.linkText}">
                    ${entity.link.linkText}
                </c:when>
                <c:otherwise>
                    <tri:resource key="core.readMoreLinkText"/>
                </c:otherwise>
            </c:choose>
            <c:if test="${not fn:contains(linkStyle, 'btn')}">
                <i class="fa fa-chevron-right"></i>
            </c:if>
        </a>
    </c:if>
</div>
