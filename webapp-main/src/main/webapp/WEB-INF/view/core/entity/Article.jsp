<%@ page import="com.sdl.webapp.main.markup.Markup" %>
<%@ page import="javax.servlet.jsp.jstl.core.LoopTagStatus" %>
<%@ page import="com.sdl.webapp.common.api.model.entity.Paragraph" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.Article" scope="request"/>
<jsp:useBean id="screenWidth" type="com.sdl.webapp.common.api.ScreenWidth" scope="request"/>
<article class="rich-text" <%= Markup.entity(entityModel) %>>
    <c:choose>
        <c:when test="${not empty entityModel.image and screenWidth != 'EXTRA_SMALL'}">
            <div class="hero" <%= Markup.property(entityModel, "image") %>>
                <tri:image url="${entityModel.image.url}" alt="${entityModel.image.alternateText}" aspect="3.3"/>
                <div class="overlay overlay-tl ribbon">
                    <h1 <%= Markup.property(entityModel, "headline") %>>${entityModel.headline}</h1>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <h1 <%= Markup.property(entityModel, "headline") %>>${entityModel.headline}</h1>
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty entityModel.date}">
        <div class="meta" <%= Markup.property(entityModel, "date") %>>
            <fmt:formatDate value="${entityModel.date}" dateStyle="LONG"/> <%-- TODO: Should use locale from localization instead of system default locale --%>
        </div>
    </c:if>
    <div class="content">
        <c:forEach var="para" items="${entityModel.articleBody}" varStatus="status">
            <%
                final LoopTagStatus status = ((LoopTagStatus) pageContext.getAttribute("status"));
                final int index = status.getIndex();
                final Paragraph para = (Paragraph) status.getCurrent();
            %>
            <div <%= Markup.property(entityModel, "articleBody", index) %>>
                <c:if test="${not empty para.subheading}">
                    <h3 <%= Markup.property(para, "subheading") %>>${para.subheading}</h3>
                </c:if>
                <c:if test="${not empty para.content}">
                    <div <%= Markup.property(para, "content") %>>${para.content}</div>
                </c:if>
                <c:if test="${not empty para.media}">
                    <figure <%= Markup.property(para, "media") %>>
                        <tri:image url="${para.media.url}" widthFactor="100%"/>
                        <c:if test="${not empty para.caption}">
                            <figcaption <%= Markup.property(para, "caption") %>>${para.caption}</figcaption>
                        </c:if>
                    </figure>
                </c:if>
            </div>
        </c:forEach>
    </div>
</article>
