<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.ItemList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<article class="rich-text" ${markup.entity(entity)}>
    <div class="content">
        <c:if test="${not empty entity.headline}">
            <h1 ${markup.property(entity, "headline")}>${entity.headline}</h1>
        </c:if>
        <c:if test="${entity.itemListElements.size() > 0}">
            <c:set var="accordionId" value="${entity.id}"/>
            <div class="panel-group responsive-accordion" id="${accordionId}">
                <c:forEach var="element" items="${entity.itemListElements}" varStatus="status">
                    <c:set var="collapseId" value="${entity.id}_${status.index}"/>
                    <div class="panel panel-default" ${markup.property(entity, "itemListElement", status.index)}>
                        <div class="panel-heading" data-toggle="collapse" data-target="#${collapseId}" data-parent="#${accordionId}">
                            <h4 class="panel-title" ${markup.property(element, "headline")}>${element.headline}</h4>
                        </div>
                        <div id="${collapseId}" class="panel-collapse collapse ${status.index == 0 ? 'in' : ''}">
                            <div class="panel-body">
                                <c:if test="${not empty element.text}">
                                    <div ${markup.property(element, "text")}>${element.text}</div>
                                </c:if>
                                <c:if test="${not empty element.media}">
                                    <figure ${markup.property(element, "media")}>
                                        <tri:media media="${element.media}"/>
                                    </figure>
                                </c:if>
                                <c:if test="${not empty element.link.url}">
                                    <p ${markup.property(element.link, "linkText")}>
                                        <tri:link link="${element.link}" cssClass="btn btn-primary"/>
                                    </p>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>
    </div>
</article>
