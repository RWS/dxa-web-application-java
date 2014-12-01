<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.Configuration" scope="request"/>
<jsp:useBean id="pageId" type="java.lang.String" scope="request"/>
<jsp:useBean id="localization" type="com.sdl.webapp.common.api.localization.Localization" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<c:set var="siteLocalizations" value="${localization.siteLocalizations}"/>
<c:set var="defaultItem" value="${entity.settings['defaultContentLink']}"/>
<c:if test="${not empty siteLocalizations and siteLocalizations.size() > 1}">
    <div ${markup.entity(entity)}>
        <xpm:entity entity="${entity}"/>
        <select class="selectpicker" data-width="auto">
            <c:forEach var="loc" items="${siteLocalizations}">
                <c:set var="params" value="?localizationId=${loc.id}&defaultPath=${loc.path}${not empty defaultItem ? ('&defaultItem='.concat(defaultItem)) : ''}"/>
                <c:set var="link" value="${localization.localizePath('/resolve/'.concat(pageId).concat(params))}"/>
                <option value="${loc.id}" data-href="${link}" ${loc.id.equals(localization.id) ? "selected" : ""}>
                    ${loc.language}
                </option>
            </c:forEach>
        </select>
    </div>
</c:if>
