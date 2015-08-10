<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.ContentList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<div class="rich-text" ${markup.entity(entity)}>
    <c:if test="${not empty entity.headline}">
        <div class='page-header page-header-top'>
            <h1 class="h3" ${markup.property(entity, "headline")}>${entity.headline}</h1>
        </div>
    </c:if>
    <c:if test="${entity.itemListElements.size() > 0}">
        <div class="pull-right">${markup.formatMessage(markup.resource("core.showingItemsText"), entity.start + 1, entity.start + entity.itemListElements.size())}</div>
        <c:import url="/WEB-INF/Views/Core/Entity/Partials/Pager.jsp"/>
        <div class="list-group">
            <c:forEach var="item" items="${entity.itemListElements}">
                <c:if test="${not empty item.link.url}">
                    <a href="${item.link.url}" class="list-group-item">
                </c:if>
                <h4 class="list-group-item-heading">${item.headline}</h4>
                <c:if test="${not empty item.date}">
                    <time class="meta small">${markup.formatDateDiff(item.date)}</time>
                </c:if>
                <c:if test="${not empty item.text}">
                    <p class="list-group-item-text">${item.text}</p>
                </c:if>
                <c:if test="${not empty item.link.url}">
                    </a>
                </c:if>
            </c:forEach>
        </div>
        <c:import url="/WEB-INF/Views/Core/Entity/Partials/Pager.jsp"/>
    </c:if>
</div>
