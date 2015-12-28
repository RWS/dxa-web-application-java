<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="pageModel" type="org.dd4t.contentmodel.Page" scope="request"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dd4t" uri="http://www.dd4t.org/tags/2.0" %>
<html>
<head>
    <title>${pageModel.title}</title>
</head>
<body>
<h1>Hello world!</h1>
<h2>The Title of the page is: ${pageModel.title}</h2>

<p>There are ${pageModel.componentPresentations.size()} Component Presentations on this page:</p>

<c:forEach var="componentPresentation" items="${pageModel.componentPresentations}">
    <p>${componentPresentation.component.title}</p>
</c:forEach>
<dd4t:componentpresentations/>

</body>
</html>
