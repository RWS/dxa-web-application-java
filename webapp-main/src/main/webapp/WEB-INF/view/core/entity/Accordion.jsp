<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.ItemList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<article class="rich-text" ${markup.entity(entityModel)}>
    <div class="content">
        <c:if test="${not empty entityModel.headline}">
            <h1 ${markup.property(entityModel, "headline")}>${entityModel.headline}</h1>
        </c:if>
        <c:if test="${entityModel.itemListElements.size() > 0}">
            <c:set var="accordionId" value="accordion-${tri:randomUUID()}"/>
            <div class="panel-group responsive-accordion" id="${accordionId}">
                <c:forEach var="element" items="${entityModel.itemListElements}" varStatus="status">
                    <c:set var="collapseId" value="collapse-${tri:randomUUID()}"/>
                    <div class="panel panel-default" ${markup.property(entityModel, "itemListElement")}>
                        <div class="panel-heading" data-toggle="collapse" data-target="#${collapseId}" data-parent="#${accordionId}">
                            <h4 class="panel-title" ${markup.property(element, "headline")}>${element.headline}</h4>
                        </div>
                        <div id="${collapseId}" class="panel-collapse collapse ${status.index == 0 ? 'in' : ''}">
                            <div class="panel-body">
                                <c:if test="${not empty element.text}">
                                    <div ${markup.property(element, "text")}>${element.text}</div>
                                </c:if>
                                <c:if test="${not empty element.media}">
                                    <figure ${markup.property(element, "media")}><%-- TODO: media --%></figure>
                                </c:if>
                                <c:if test="${not empty element.link.url}">
                                    <p ${markup.property(element, "link.linkText")}> <%-- TODO: This markup won't work --%>
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
