<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="footnotes" type="org.dd4t.test.web.models.GenericTextField" scope="request"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:forEach var="footNote" items="${footnotes.textFields}">
    <p><c:out value="${footNote}"/></p>
</c:forEach>
