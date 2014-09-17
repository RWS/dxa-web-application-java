<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.model.PageModel" scope="request"/>
<html>
<head>
    <title>Hello World</title>
</head>
<body>
<h1>Welcome</h1>
<p>Page title: <c:out value="${pageModel.title}"/></p>
</body>
</html>
