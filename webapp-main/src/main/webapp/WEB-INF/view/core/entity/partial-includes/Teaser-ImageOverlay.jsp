<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>

<div>
    <c:choose>
        <c:if test="${not empty carouselItem.media}" >
            <span><tri:image url="${carouselItem.media.url}" widthFactor="100%" /></span>
        </c:if>
        <c:otherwise>
            <img src="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7" width="100%">
        </c:otherwise>
    </c:choose>

    <c:if test="${not empty carouselItem.headline or not empty carouselItem.text}" >
        <div class="overlay overlay-tl ribbon">
            <c:if test="${not empty carouselItem.headline}">
                <h2>${carouselItem.headline}</h2>
            </c:if>

            <c:if test="${not empty carousel.text}">
                <div>${carousel.text}</div>
            </c:if>
        </div>
    </c:if>

    <c:if test="${not empty carouselItem.link.linkText}">
        <div class="carousel-caption">
            <p>
                <a href="${carouselItem.link.url}" title="${carouselItem.link.alternateText}" class="btn btn-primary">
                    ${carouselItem.link.linkText}
                </a>
            </p>
        </div>
    </c:if>
</div>