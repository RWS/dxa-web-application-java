<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.ItemList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<c:set var="carouselId" value="carousel-${entity.id}"/>
<div id="${carouselId}" class="carousel slide" data-ride="carousel" data-interval="5000" ${markup.entity(entity)}>
    <ol class="carousel-indicators">
        <c:forEach var="indicator" varStatus="status" items="${entity.itemListElements}">
            <c:choose>
                <c:when test="${status.index == 0}">
                    <li data-target="#${carouselId}" data-slide-to="${status.index}" class="active"></li>
                </c:when>
                <c:otherwise>
                    <li data-target="#${carouselId}" data-slide-to="${status.index}"></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </ol>
    <div class="carousel-inner">
        <c:forEach var="carousel" varStatus="status" items="${entity.itemListElements}">
            <c:set var="item" value="${carousel}" scope="request"/>
            <c:choose>
                <c:when test="${status.index == 0}">
                    <div class="item active" ${markup.property(entity, "itemListElements", status.index)}>
                        <c:import url="/WEB-INF/Views/Core/Entity/Partials/Teaser-ImageOverlay.jsp"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="item" ${markup.property(entity, "itemListElements", status.index)}>
                        <c:import url="/WEB-INF/Views/Core/Entity/Partials/Teaser-ImageOverlay.jsp"/>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
    <a class="left carousel-control" href="#${carouselId}" data-slide="prev">
        <i class='fa fa-chevron-left carousel-icon-left'></i>
    </a>
    <a class="right carousel-control" href="#${carouselId}" data-slide="next">
        <i class='fa fa-chevron-right carousel-icon-right'></i>
    </a>
</div>
