<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request"/>
<jsp:useBean id="regionModel" type="com.sdl.tridion.referenceimpl.common.model.Region" scope="request"/>
<div class="region">
    <h2>Hero</h2>
    <c:forEach var="entity" items="${regionModel.entities}">
        <jsp:include page="/entity/${entity.id}"/>
    </c:forEach>
</div>
