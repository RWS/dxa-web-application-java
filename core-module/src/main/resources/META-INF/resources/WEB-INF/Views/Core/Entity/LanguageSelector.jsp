<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sdl.webapp.common.api.localization.SiteLocalization" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.Configuration" scope="request"/>
<jsp:useBean id="pageId" type="java.lang.String" scope="request"/>
<jsp:useBean id="localization" type="com.sdl.webapp.common.api.localization.Localization" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<%
    final List<SiteLocalization> siteLocalizations = localization.getSiteLocalizations();
    final List<SiteLocalization> filteredLocalizations = new ArrayList<>();
    if (siteLocalizations.size() > 1) {
        final Set<String> excludedLocalizations = new HashSet<>();
        if (entity.getSettings().containsKey("suppressLocalizations")) {
            for (String s : entity.getSettings().get("suppressLocalizations").split(",")) {
                excludedLocalizations.add("/" + s.trim());
            }
        }

        for (SiteLocalization loc : siteLocalizations) {
            if (!excludedLocalizations.contains(loc.getPath())) {
                filteredLocalizations.add(loc);
            }
        }
    }

    request.setAttribute("filteredLocalizations", filteredLocalizations);
%>
<c:set var="defaultItem" value="${entity.settings['defaultContentLink']}"/>
<c:if test="${not empty filteredLocalizations and filteredLocalizations.size() > 1}">
    <div ${markup.entity(entity)}>
        <select class="selectpicker" data-width="auto">
            <c:forEach var="loc" items="${filteredLocalizations}">
                <c:set var="params" value="?localizationId=${loc.id}&defaultPath=${loc.path}${not empty defaultItem ? ('&defaultItem='.concat(defaultItem)) : ''}"/>
                <c:set var="link" value="${localization.localizePath('/resolve/'.concat(pageId).concat(params))}"/>
                <option value="${loc.id}" data-href="${link}" ${loc.id.equals(localization.id) ? "selected" : ""}>
                    ${loc.language}
                </option>
            </c:forEach>
        </select>
    </div>
</c:if>
