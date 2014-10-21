<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!-- jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request" -->
<!-- jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.Location" scope="request" -->

<div id="1" class="static-map" style="height:${param.height}">
    <ol>
        <li>${location.id}</li>
        <li>${location.viewName}</li>
        <li>${location.latitude}</li>
        <li>${location.longitude}</li>
        <li>${location.query}</li>
    </ol>

    <img src="//maps.googleapis.com/maps/api/staticmap" alt="">
</div>
