<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.YouTubeVideo" scope="request"/>
<div class="video">
    <c:if test="${not empty entityModel.headline}">
        <h3>${entityModel.headline}</h3>
    </c:if>
    <%-- TODO: Gepruts met containerSize --%>
    <tri:youTubeVideo youTubeId="${entityModel.youTubeId}" url="${entityModel.url}" headline="${entityModel.headline}" widthFactor="100%"/>
</div>
