<!DOCTYPE html>
    <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="dxa" uri="http://www.sdl.com/tridion-dxa" %>
    <%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>

    <jsp:useBean id="pageModel" type="com.sdl.webapp.common.api.model.PageModel" scope="request"/>
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
        <dxa:pluggableMarkup label="css"/>
        <script src="${markup.versionedContent('/assets/scripts/header.js')}"></script>
        <dxa:pluggableMarkup label="top-js"/>
        <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
        <script src="${markup.versionedContent('/assets/scripts/ie.js')}"></script>
        <![endif]-->
    </head>
    <body>
        <dxa:region name="Header" />
        <main class="page-row page-row-expanded ${pageModel.htmlClasses}" role="main">
            <div class="container-fluid page-border">
                <%
                boolean hasLeftBar = pageModel.getRegions().containsKey("Left-Navigation") ||
                pageModel.getRegions().containsKey("Left");
                int mainContainerSize = hasLeftBar ? 9 : 12;
                %>
                <dxa:region name="Hero"/>
                <dxa:region name="Content-Tools"/>
                <div class="row">
                    <% if (hasLeftBar)
                    {
                    %>
                        <div class="col-sm-12 col-md-3">
                            <dxa:region name="Left-Navigation" containerSize="3"/>
                            <dxa:region name="Left"  containerSize="3"/>
                        </div>
                     <%
                     }
                     %>
                    <div class="col-sm-12 col-md-<%= mainContainerSize %>">
                        <dxa:regions exclude="Hero,Left,Left-Navigation,Header,Footer,Content-Tools" containerSize="${mainContainerSize}"/>
                    </div>
                </div>
            </div>
        </main>
        <dxa:region name="Footer" />
        <script src="${markup.versionedContent('/assets/scripts/main.js')}"></script>
        <xpm:if-enabled>
            <script src="${markup.versionedContent('/assets/scripts/xpm.js')}"></script>
        </xpm:if-enabled>
        <dxa:pluggableMarkup label="bottom-js"/>
        <xpm:page page="${pageModel}"/>
    </body>
</html>
