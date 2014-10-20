<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.tridion.referenceimpl.common.model.entity.ItemList" scope="request"/>

<article class="rich-text">
    <div class="content">
        <c:if test="${not empty entityModel.headline}">
            <h1>${entityModel.headline}</h1>
        </c:if>

    @if (Model.ItemListElements.Count > 0)
    {
        <c:set var="accordionId" value="${NewGuid}" />

        <div class="panel-group responsive-accordion" id="@accordionId">
            <c:forEach var="teaser" items="${entityModel.itemListElements}">
                <c:set var="collapseId" value="${NewGuid}" />

                <div class="panel panel-default">
                    <div class="panel-heading" data-toggle="collapse" data-target="#${collapseId}" data-parent="#${accordionId}">
                        <h4 class="panel-title">${teaser.headline}</h4>
                    </div>

                    <div id="${collapseId}" class="panel-collapse collapse @(i==1?"in":"")">
                        <div class="panel-body">
                            <c:if test="${not empty teaser.text}">
                                <div>
                                    ${teaser.text}
                                </div>
                            </c:if>

                            <c:if test="${not empty teaser.media}">
                                <figure>
                                    <!-- Todo: @Html.Media(element.Media) -->
                                </figure>
                            </c:if>

                            <c:if test="${not empty teaser.link.url}">
                                <p>
                                    <a href="${teaser.link.url}" title="${teaser.link.alternateText}" class="btn btn-primary">
                                        <c:choose>
                                            <c:when test="${not empty teaser.link.linkText}">
                                                ${entityModel.link.linkText}
                                            </c:when>
                                            <c:otherwise>
                                                <tri:resource key="core.readMoreLinkText" />
                                            </c:otherwise>
                                        </c:choose>
                                    </a>
                                </p>
                            </c:if>
                        </div>
                    </div>
            </c:forEach>
        </div>
    </div>
</article>