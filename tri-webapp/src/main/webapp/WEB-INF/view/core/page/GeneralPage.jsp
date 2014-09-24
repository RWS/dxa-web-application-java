<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sdl.tridion.referenceimpl.common.model.Region" %>
<%@ page import="com.sdl.tridion.referenceimpl.controller.core.JspBeanNames" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request"/>
<!--[if lt IE 7]><html class="no-js lt-ie9 lt-ie8 lt-ie7"><![endif]-->
<!--[if IE 7]><html class="no-js lt-ie9 lt-ie8"><![endif]-->
<!--[if IE 8]><html class="no-js lt-ie9"><![endif]-->
<!--[if gt IE 8]><!--><html class="no-js"><!--<![endif]-->
<html>
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Hello World</title>
    <link rel="stylesheet" href="system/assets/css/main.css" type="text/css"/>
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="system/assets/scripts/ie.js"></script>
    <![endif]-->
</head>
<body>
<%@include file="../../shared/Header.jsp"%>
<main class="page-row page-row-expanded" role="main">
    <div class="container-fluid page-border">
        <%
            boolean hasLeftBar = pageModel.getRegions().containsKey("Left");
            int mainContainerSize = hasLeftBar ? 9 : 12;

            if (pageModel.getRegions().containsKey("Hero")) {
                request.setAttribute(JspBeanNames.REGION_MODEL, pageModel.getRegions().get("Hero"));
                pageContext.include("/region");
            }
        %>
    <div class="row">
        <%
            if (hasLeftBar) {
                %><div class="col-sm-12 col-md-3"><%
                    if (pageModel.getRegions().containsKey("Left")) {
                        request.setAttribute(JspBeanNames.REGION_MODEL, pageModel.getRegions().get("Left"));
                        pageContext.include("/region");
                    }
                %></div><%
            }
        %>
        <div class="col-sm-12 col-md-<%= mainContainerSize %>">
            <%
                for (Region region : pageModel.getRegions().values()) {
                    String viewName = region.getViewName();
                    if (!viewName.equals("Hero") && !viewName.equals("Left")) {
                        request.setAttribute(JspBeanNames.REGION_MODEL, pageModel.getRegions().get(viewName));
                        pageContext.include("/region");
                    }
                }
            %>
        </div>
    </div>
</main>
<%@include file="../../shared/Footer.jsp"%>
<script src="system/assets/scripts/main.js"></script>
</body>
</html>
