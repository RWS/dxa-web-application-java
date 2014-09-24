<%@ page import="com.sdl.tridion.referenceimpl.common.model.Entity" %>
<%@ page import="com.sdl.tridion.referenceimpl.controller.core.JspBeanNames" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="regionModel" type="com.sdl.tridion.referenceimpl.common.model.Region" scope="request"/>
<div typeof="Region" resource="<%= regionModel.getViewName() %>">
<%
    for (Entity entity : regionModel.getEntities()) {
        request.setAttribute(JspBeanNames.ENTITY_MODEL, entity);
        pageContext.include("/entity");
    }
%>
</div>
