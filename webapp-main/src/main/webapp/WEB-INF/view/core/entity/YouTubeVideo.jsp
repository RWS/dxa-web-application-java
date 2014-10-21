<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.UUID" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="pageModel" type="com.sdl.webapp.common.api.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.YouTubeVideo" scope="request"/>

<div class="video">
    <c:if test="${not empty entityModel.headline}">
        <h3>${entityModel.headline}</h3>
    </c:if>

    <iframe src="https://www.youtube.com/embed/${entityModel.youTubeId}?version=3&enablejsapi=1"
            frameborder="0" allowfullscreen="true" id="<%= UUID.randomUUID().toString() %>"></iframe>
</div>
