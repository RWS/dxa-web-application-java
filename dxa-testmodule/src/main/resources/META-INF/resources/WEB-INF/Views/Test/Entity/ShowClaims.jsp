<%@ page import="com.sdl.webapp.common.impl.contextengine.DeviceClaims" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="contextengine" type="com.sdl.webapp.common.api.contextengine.ContextEngine" scope="request"/>

<%
    DeviceClaims claims = contextengine.getClaims(DeviceClaims.class);
    claims.getDisplayWidth();
%>
<article class="rich-text ${entity.htmlClasses}" ${markup.entity(entity)}>
    Model:<%=claims.getModel()%><br/>
    Vendor: <%=claims.getVendor()%><br/>

</article>

