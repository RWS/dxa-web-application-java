<%@ genericPage language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:useBean id="PageModel" type="org.dd4t.contentmodel.GenericPage" scope="request"/>
<html>
<head><title>Hello</title></head>
<body>
<h1><c:out value="${PageModel.title}" /></h1>

<c:forEach var="cp" items="${PageModel.componentPresentations}">
<p><c:out value="${cp.component.title}"/></p>
</c:forEach>

</body>
</html>
