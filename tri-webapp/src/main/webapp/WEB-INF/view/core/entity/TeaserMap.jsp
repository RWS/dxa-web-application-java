<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.tridion.referenceimpl.common.model.entity.Teaser" scope="request"/>
<div>
<%
    String headline = entityModel.getHeadline();
    if (headline != null && !headline.isEmpty()) {
        out.print("<h3>" + headline + "</h3>");
    }
%>
    <%-- TODO --%>
</div>
