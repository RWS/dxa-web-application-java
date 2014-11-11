<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.Article" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<jsp:useBean id="screenWidth" type="com.sdl.webapp.common.api.ScreenWidth" scope="request"/>
<article class="rich-text" ${markup.entity(entityModel)}>
    <xpm:entity entity="${entityModel}"/>
    <c:choose>
        <c:when test="${not empty entityModel.image and screenWidth != 'EXTRA_SMALL'}">
            <div class="hero" ${markup.property(entityModel, "image")}>
                <xpm:property entity="${entityModel}" property="image"/>
                <tri:image url="${entityModel.image.url}" alt="${entityModel.image.alternateText}" aspect="3.3"/>
                <div class="overlay overlay-tl ribbon">
                    <h1 ${markup.property(entityModel, "headline")}><xpm:property entity="${entityModel}" property="headline"/>${entityModel.headline}</h1>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <h1 ${markup.property(entityModel, "headline")}><xpm:property entity="${entityModel}" property="headline"/>${entityModel.headline}</h1>
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty entityModel.date}">
        <div class="meta" ${markup.property(entityModel, "date")}>
            <xpm:property entity="${entityModel}" property="date"/>
            ${tri:formatDateTime(entityModel.date, "d MMM yyyy")}
        </div>
    </c:if>
    <div class="content">
        <c:forEach var="para" items="${entityModel.articleBody}" varStatus="status">
            <div ${markup.property(entityModel, "articleBody")}>
                <xpm:property entity="${entityModel}" property="articleBody" index="${status.index}"/>
                <c:if test="${not empty para.subheading}">
                    <h3 ${markup.property(para, "subheading")}><xpm:property entity="${para}" property="subheading"/>${para.subheading}</h3>
                </c:if>
                <c:if test="${not empty para.content}">
                    <div ${markup.property(para, "content")}><xpm:property entity="${para}" property="content"/>${para.content}</div>
                </c:if>
                <c:if test="${not empty para.media}">
                    <figure ${markup.property(para, "media")}>
                        <xpm:property entity="${para}" property="media"/>
                        <tri:image url="${para.media.url}" widthFactor="100%"/>
                        <c:if test="${not empty para.caption}">
                            <figcaption ${markup.property(para, "caption")}><xpm:property entity="${para}" property="caption"/>${para.caption}</figcaption>
                        </c:if>
                    </figure>
                </c:if>
            </div>
        </c:forEach>
    </div>
</article>
