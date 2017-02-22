<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.ExceptionEntity" scope="request"/>
<jsp:useBean id="webRequestContext" type="com.sdl.webapp.common.api.WebRequestContext" scope="request"/>

<div>
    <jsp:include page="../Error/SectionError.jsp"/>
    <c:if test="${webRequestContext.developerMode && entity != null}">
        <c:out value="${entity.toHtmlElement()}"/>
    </c:if>
</div>
