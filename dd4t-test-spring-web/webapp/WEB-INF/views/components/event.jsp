<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="pageModel" type="org.dd4t.contentmodel.Page" scope="request"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dd4t" uri="http://www.dd4t.org/tags/2.0" %>
<jsp:useBean id="Event" type="org.dd4t.test.web.models.Event" scope="request"/>

<c:if test="${!empty Event.title}">
    <h1>${Event.title}</h1>
</c:if>

<c:if test="${!empty Event.color}">
    <c:forEach items="${Event.color}" var="colorKeyword">
        <c:out value="${colorKeyword.title}" />
    </c:forEach>
</c:if>