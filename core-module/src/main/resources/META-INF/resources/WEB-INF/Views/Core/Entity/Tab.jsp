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
        <c:if test="${not entity.itemListElements.isEmpty()}">
            <c:set var="panelId" value="${entity.id}"/>
            <div class="panel panel-default">
                <div class="panel-body tab-container">
                    <%-- Tab dropdown --%>
                    <div class="dropdown visible-xs">
                        <select class="tab-group form-control" data-toggle="tab">
                            <c:forEach var="element" items="${entity.itemListElements}" varStatus="status">
                                <c:set var="ident" value="tab${panelId}_${status.index}"/>
                                <option value="#${ident}" data-toggle="tab">${element.headline}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <%-- Tab tips --%>
                    <ul class="tab-group nav nav-tabs hidden-xs">
                        <c:forEach var="element" items="${entity.itemListElements}" varStatus="status">
                            <li class="${status.index == 0 ? 'active' : ''}">
                                <c:set var="ident" value="tab${panelId}_${status.index}"/>
                                <a href="#${ident}" data-toggle="tab" ${markup.property(element, "headline")}>${element.headline}</a>
                            </li>
                        </c:forEach>
                    </ul>
                    <%-- Tab panes --%>
                    <div class="tab-content">
                        <c:forEach var="element" items="${entity.itemListElements}" varStatus="status">
                            <c:set var="ident" value="tab${panelId}_${status.index}"/>
                            <div class="tab-pane ${status.index == 0 ? 'active' : ''}" id="${ident}">
                                <div ${markup.property(element, "text")}>${element.text}</div>
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
                        </c:forEach>
                    </div>
                </div>
            </div>
        </c:if>
    </div>
</article>
