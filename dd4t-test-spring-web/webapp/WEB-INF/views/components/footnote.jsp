<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="footnote" type="org.dd4t.test.web.models.GenericTextField" scope="request"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:forEach var="note" items="${footnote.textFields}">
    <p><c:out value="${note}"/></p>
</c:forEach>
