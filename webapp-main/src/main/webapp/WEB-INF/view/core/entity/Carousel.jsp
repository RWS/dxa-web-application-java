<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ page import="java.util.UUID" %>
<jsp:useBean id="pageModel" type="com.sdl.webapp.common.api.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.ItemList" scope="request"/>

<c:set var="carouselId" value="${UUID.randomUUID}" />

<div id="carousel-${carouselId}" class="carousel slide" data-ride="carousel" data-interval="5000">
    <ol class="carousel-indicators">
        <c:forEach var="indicator" varStatus="indicatorStatus" items="${entityModel.itemListElements}">
            <c:choose>
                <c:if test="${indicatorStatus.count == 1}">
                    <li data-target="#carousel-@carouselId" data-slide-to="${indicatorStatus.count}" class=active"></li>
                </c:if>
                <c:otherwise>
                    <li data-target="#carousel-@carouselId" data-slide-to="${indicatorStatus.count}"></li>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </ol>

    <div class="carousel-inner">
        <c:forEach var="carousel" varStatus="carouselStatus" items="${entityModel.itemListElements}">
            <c:choose>
                <c:set var="carouselItem" value="${carousel}" scope="request" />

                <c:if test="${carouselStatus.count == 1}">
                    <div class="item active">
                        <!--c:import url="/WEB-INF/view/core/entity/partial-includes/Teaser-ImageOverlay.jsp" /-->
                    </div>
                </c:if>
                <c:otherwise>
                    <div class="item">
                        <!-- c:import url="/WEB-INF/view/core/entity/partial-includes/Teaser-ImageOverlay.jsp" /-->
                    </div>
                </c:otherwise>
            </c:choose>
        </c:forEach>
    </div>

    <a class="left carousel-control" href="#carousel-${carouselId}" data-slide="prev">
        <i class='fa fa-chevron-left carousel-icon-left'></i>
    </a>
    <a class="right carousel-control" href="#carousel-${carouselId}" data-slide="next">
        <i class='fa fa-chevron-right carousel-icon-right'></i>
    </a>
</div>
