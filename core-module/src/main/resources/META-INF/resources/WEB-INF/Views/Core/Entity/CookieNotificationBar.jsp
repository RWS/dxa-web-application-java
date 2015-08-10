<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.Notification" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<div id="cookie" class="container-fluid page-border" ${markup.entity(entity)}>
    <div class="row">
        <div class="col-sm-9">
            <div class="h4" ${markup.property(entity, "headline")}>${entity.headline}</div>
            <p ${markup.property(entity, "text")}>${entity.text}</p>
        </div>
        <div class="col-sm-3">
            <ul class="nav nav-cookie">
                <li ${markup.property(entity, "continue_")}>
                    <a id="cookie-hide" href="#"><i class="fa fa-check-circle"></i> ${entity.continue_}</a>
                </li>
                <c:if test="${not empty entity.link.url and not empty entity.link.linkText}">
                    <li ${markup.property(entity.link, "linkText")}>
                        <a href="${entity.link.url}"><i class="fa fa-question-circle"></i> ${entity.link.linkText}</a>
                    </li>
                </c:if>
            </ul>
        </div>
    </div>
</div>
