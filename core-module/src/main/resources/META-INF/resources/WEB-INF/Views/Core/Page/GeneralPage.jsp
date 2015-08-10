<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="pageModel" type="com.sdl.webapp.common.api.model.Page" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<!--[if lt IE 7]><html class="no-js lt-ie9 lt-ie8 lt-ie7"><![endif]-->
<!--[if IE 7]><html class="no-js lt-ie9 lt-ie8"><![endif]-->
<!--[if IE 8]><html class="no-js lt-ie9"><![endif]-->
<!--[if gt IE 8]><!--><html class="no-js"><!--<![endif]-->
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${pageModel.title}</title>
    <c:forEach var="meta" items="${pageModel.meta.entrySet()}">
        <meta name="${meta.key}" content="${meta.value}">
    </c:forEach>
    <link rel="stylesheet" href="${markup.versionedContent('/assets/css/main.css')}" type="text/css"/>
    <tri:pluggableMarkup label="css"/>
    <script src="${markup.versionedContent('/assets/scripts/header.js')}"></script>
    <tri:pluggableMarkup label="top-js"/>
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="${markup.versionedContent('/assets/scripts/ie.js')}"></script>
    <![endif]-->
</head>
<body>
<tri:page name="Header" viewName="Shared/Header"/>
<main class="page-row page-row-expanded" role="main">
    <div class="container-fluid page-border">
        <%
            boolean hasLeftBar = pageModel.getIncludes().containsKey("Left Navigation") ||
                    pageModel.getRegions().containsKey("Left");
            int mainContainerSize = hasLeftBar ? 9 : 12;
        %>
        <tri:region name="Hero"/>
        <tri:page name="Content Tools"/>
        <div class="row">
            <% if (hasLeftBar) { %>
                <div class="col-sm-12 col-md-3">
                    <tri:page name="Left Navigation"/>
                    <tri:region name="Left"/>
                </div>
            <% } %>
            <div class="col-sm-12 col-md-<%= mainContainerSize %>"><tri:regions exclude="Hero,Left"/></div>
        </div>
    </div>
</main>
<tri:page name="Footer" viewName="Shared/Footer"/>
<script src="${markup.versionedContent('/assets/scripts/main.js')}"></script>
<xpm:if-enabled><script src="${markup.versionedContent('/assets/scripts/xpm.js')}"></script></xpm:if-enabled>
<tri:pluggableMarkup label="bottom-js"/>
<xpm:page page="${pageModel}"/>
</body>
</html>
