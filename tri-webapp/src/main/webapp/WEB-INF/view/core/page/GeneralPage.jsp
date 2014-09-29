<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
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
<%@include file="../../shared/Header.jsp" %>
<%
    boolean hasLeftBar = pageModel.getRegions().containsKey("Left");
    int mainContainerSize = hasLeftBar ? 9 : 12;
%>
<main class="page-row page-row-expanded" role="main">
    <div class="container-fluid page-border">
        <tri:region name="Hero"/>
        <div class="row">
            <div class="col-sm-12 col-md-3"><tri:region name="Left"/></div>
            <div class="col-sm-12 col-md-<%= mainContainerSize %>"><tri:regions exclude="Hero,Left"/></div>
        </div>
</main>
<%@include file="../../shared/Footer.jsp" %>
<script src="system/assets/scripts/main.js"></script>
</body>
</html>
