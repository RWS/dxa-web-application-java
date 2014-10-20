<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="pageModel" type="com.sdl.webapp.common.api.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.Article" scope="request"/>

<article class="rich-text">
    <c:choose>
        <c:when test="${not empty entityModel.image}">
            <div class="hero">
                <!-- TODO: @Html.Media(Model.Image, 3.3) -->

                <div class="overlay overlay-tl ribbon">
                    <h1>${entityModel.headline}</h1>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <h1>${entityModel.headline}</h1>
        </c:otherwise>
    </c:choose>

    <c:if test="${not empty entityModel.date}">
        <div class="meta">
            <fmt:formatDate value="${teaser.date}" pattern="D" />
        </div>
    </c:if>

    <div class="content">
        <c:if test="${not empty entityModel.articleBody}" >
            <c:forEach var="articleBody" items="${entityModel.articleBody}">
                <div>
                    <c:if test="${not empty articleBody.subheading}">
                        <h3>${articleBody.subheading}</h3>
                    </c:if>

                    <div>
                        ${articleBody.content}
                    </div>

                    <c:if test="${not empty articleBody.media}">
                        <figure>
                        <!-- TODO: @Html.Media(para.Media, "100%", 0) -->

                        <c:if test="${not empty articleBody.caption}">
                            <figcaption>
                                ${articleBody.caption}
                            </figcaption>
                        </c:if>
                        </figure>
                    </c:if>
                </div>
            </c:forEach>
        </c:if>
    </div>
</article>