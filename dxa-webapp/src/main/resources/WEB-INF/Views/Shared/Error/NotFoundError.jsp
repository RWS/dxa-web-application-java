<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" %>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<!--[if lt IE 7]><html class="no-js lt-ie9 lt-ie8 lt-ie7"><![endif]-->
<!--[if IE 7]><html class="no-js lt-ie9 lt-ie8"><![endif]-->
<!--[if IE 8]><html class="no-js lt-ie9"><![endif]-->
<!--[if gt IE 8]><!--><html class="no-js"><!--<![endif]-->
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Not Found</title>
    <link rel="stylesheet" href="${markup.versionedContent('/assets/css/main.css')}" type="text/css"/>
    <script src="${markup.versionedContent('/assets/scripts/header.js')}"></script>
    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="${markup.versionedContent('/assets/scripts/ie.js')}"></script>
    <![endif]-->
</head>
<body>
<header id="page-header" class="page-row navbar navbar-default">
    <div class="container-fluid page-border">
        <div class="row">
            <div class="col-xs-12" role="navigation">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                        <i class="fa fa-bars"></i>
                    </button>
                </div>
                <div class="navbar-collapse collapse">
                    <ul class="nav navbar-nav main-nav">
                        <li class="">
                            <a href="${markup.url('/index')}">Home</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
</header>
<main class="page-row page-row-expanded" role="main">
    <div class="container-fluid page-border">
        <h1>Not Found</h1>

        <p>The page you were trying to access was not found on the server.</p>
    </div>
</main>
<script src="${markup.versionedContent('/assets/scripts/main.js')}"></script>
<%--
<script src="${markup.versionedContent('/assets/scripts/custom.js')}"></script>
<!-- discover-min.js is part of the SDL Tridion Context Engine -->
<script src="${markup.versionedContent('/assets/scripts/discover-min.js')}"></script>
--%>
</body>
</html>
