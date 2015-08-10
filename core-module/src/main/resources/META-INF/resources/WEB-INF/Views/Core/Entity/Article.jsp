<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.Article" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<jsp:useBean id="screenWidth" type="com.sdl.webapp.common.api.ScreenWidth" scope="request"/>
<article class="rich-text" ${markup.entity(entity)}>
    <c:choose>
        <c:when test="${not empty entity.image and screenWidth != 'EXTRA_SMALL'}">
            <div class="hero" ${markup.property(entity, "image")}>
                <tri:media media="${entity.image}" aspect="3.3"/>
                <div class="overlay overlay-tl ribbon">
                    <h1 ${markup.property(entity, "headline")}>${entity.headline}</h1>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <h1 ${markup.property(entity, "headline")}>${entity.headline}</h1>
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty entity.date}">
        <div class="meta" ${markup.property(entity, "date")}>
            ${markup.formatDate(entity.date)}
        </div>
    </c:if>
    <div class="content">
        <c:forEach var="para" items="${entity.articleBody}" varStatus="status">
            <div ${markup.property(entity, "articleBody", status.index)}>
                <c:if test="${not empty para.subheading}">
                    <h3 ${markup.property(para, "subheading")}>${para.subheading}</h3>
                </c:if>
                <c:if test="${not empty para.content}">
                    <div ${markup.property(para, "content")}>${para.content}</div>
                </c:if>
                <c:if test="${not empty para.media}">
                    <figure ${markup.property(para, "media")}>
                        <tri:media media="${para.media}" widthFactor="100%"/>
                        <c:if test="${not empty para.caption}">
                            <figcaption ${markup.property(para, "caption")}>${para.caption}</figcaption>
                        </c:if>
                    </figure>
                </c:if>
            </div>
        </c:forEach>
    </div>
</article>
