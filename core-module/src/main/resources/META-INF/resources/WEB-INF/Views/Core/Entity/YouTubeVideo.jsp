<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dxa" uri="http://www.sdl.com/tridion-dxa" %> 
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.YouTubeVideo" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<jsp:useBean id="mediaHelper" type="com.sdl.webapp.common.api.MediaHelper" scope="request"/>

<% pageContext.setAttribute("uuid", java.util.UUID.randomUUID().toString().replaceAll("-", "")); %>
    <c:choose>
    <c:when test="${entity.isEmbedded}">
	    <div class="embed-video ${entity.HtmlClasses}">
	        <img src="${mediaHelper.getResponsiveImageUrl(entity.url, "100%", 0.0, 0)}" alt="${entity.headline}">
	        <button type="button" data-video="${entity.youTubeId}">
	            <i class="fa fa-play-circle"></i>
	        </button>
	    </div>
    </c:when>
    <c:otherwise>
	    <div class="video ${entity.HtmlClasses}" ${markup.entity(entity)}>
            <c:if test="${not empty entity.headline}">
		        <h3 ${markup.property(entity, "headline")}>${entity.headline}</h3>
		    </c:if>
	        <c:choose>
			    <c:when test="${not empty entity.url}">
		            <div class="embed-video">
		                <img src="${mediaHelper.getResponsiveImageUrl(entity.url, "100%", 0.0, 0)}" alt="${entity.headline}">
		                <button type="button" data-video="${entity.youTubeId}">
		                    <i class="fa fa-play-circle"></i>
		                </button>
		            </div>
		         </c:when>
	    		<c:otherwise>
		            <iframe src="https://www.youtube.com/embed/${entity.youTubeId}?version=3&enablejsapi=1" id="video${uuid}"></iframe>
	            </c:otherwise>
	        </c:choose>
	    </div>
    </c:otherwise>
    </c:choose>
</div>
