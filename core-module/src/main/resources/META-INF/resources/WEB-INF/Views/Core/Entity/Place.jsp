<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.Place" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<jsp:useBean id="screenWidth" type="com.sdl.webapp.common.api.ScreenWidth" scope="request"/>
<c:set var="width" value="${not empty entity.location ? 6 : 12}"/>
<c:set var="daddr" value="${not empty entity.location ? (not empty entity.location.query ? entity.location.query : (entity.location.latitude + ',' + entity.location.longitude)) : ''}"/>
<div ${markup.entity(entity)}>
    <c:choose>
        <c:when test="${not empty entity.image and screenWidth != 'EXTRA_SMALL'}">
            <div class="hero" ${markup.property(entity, "image")}>
                <tri:media media="${entity.image}" aspect="3.3"/>
                <div class="overlay overlay-tl ribbon">
                    <h1 ${markup.property(entity, "name")}>${entity.name}</h1>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${not empty entity.name}">
                <h1 ${markup.property(entity, "name")}>${entity.name}</h1>
            </c:if>
        </c:otherwise>
    </c:choose>
    <div class="container-fluid">
        <div id="location-tile" class="row">
            <c:if test="${not empty entity.location}">
                <div class="col-sm-6">
                    <tri:googlemap latitude="${entity.location.latitude}" longitude="${entity.location.longitude}"
                                   markerName="${entity.name}" mapHeight="300"/>
                </div>
            </c:if>
            <div class="col-sm-${width}">
                <div class="tile">
                    <div class="vcard">
                        <c:if test="${not empty entity.address}">
                            <h4><i class="fa fa-map-marker"></i> ${markup.resource("core.addressHeadingText")}</h4>
                            <div class="adr" ${markup.property(entity, "address")}>
                                ${entity.address}
                            </div>
                            <c:if test="${not empty daddr}">
                                <a class="popup-iframe popup-mobile-ignore" href="//maps.google.com/maps?saddr=My+Location&amp;daddr=${daddr}">${markup.resource("core.directionsLinkText")}</a><br />
                                <a class="popup-iframe popup-mobile-ignore" href="//maps.google.com/maps?q=${daddr}">${markup.resource("core.largeMapLinkText")}</a>
                            </c:if>
                        </c:if>
                        <c:if test="${not empty entity.telephone or not empty entity.faxNumber or not empty entity.email}">
                            <h4><i class="fa fa-envelope"></i> ${markup.resource("core.placeContactHeadingText")}</h4>
                            <div class="h-card">
                                <c:if test="${not empty entity.telephone}">
                                    <p>${markup.resource("core.teleponeCaptionText")} <a class="tel" href="tel:${entity.telephone}" ${markup.property(entity, "telephone")}>${entity.telephone}</a></p>
                                </c:if>
                                <c:if test="${not empty entity.telephone}">
                                    <p>${markup.resource("core.faxCaptionText")} <a class="fax" href="fax:${entity.faxNumber}" ${markup.property(entity, "faxNumber")}>${entity.faxNumber}</a></p>
                                </c:if>
                                <c:if test="${not empty entity.telephone}">
                                    <p>${markup.resource("core.emailCaptionText")} <a class="email" href="mailto:${entity.email}" ${markup.property(entity, "email")}>${entity.email}</a></p>
                                </c:if>
                            </div>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
