<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.tridion.referenceimpl.common.model.entity.Teaser" scope="request"/>

<div>
    <c:if test="${not empty entityModel.headline}">
        <h3>
            <c:choose>
                <c:when test="${not empty entityModel.link}">
                    <a href="${entityModel.link}">
                        ${entityModel.headline}
                    </a>
                </c:when>
                <c:otherwise>
                    ${entityModel.headline}
                </c:otherwise>
            </c:choose>
        </h3>
    </c:if>

    <c:if test="${not empty entityModel.location}">
        <c:set var="location" value="${entityModel.location}" scope="request" />

        <c:import url="/WEB-INF/view/core/entity/partial-includes/GoogleStaticMap.jsp">
            <c:param name="height" value="100%" />
        </c:import>
    </c:if>
</div>
