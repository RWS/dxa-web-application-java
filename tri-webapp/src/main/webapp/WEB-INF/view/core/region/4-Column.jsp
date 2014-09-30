<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sdl.tridion.referenceimpl.controller.core.ViewAttributeNames" %>
<%@ page import="com.sdl.tridion.referenceimpl.common.config.ScreenWidth" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request"/>
<jsp:useBean id="regionModel" type="com.sdl.tridion.referenceimpl.common.model.Region" scope="request"/>
<div typeof="Region" resource="<%= regionModel.getViewName() %>">
<%
    int entityCount = regionModel.getEntities().size();

    int cols = request.getAttribute(ViewAttributeNames.SCREEN_WIDTH) == ScreenWidth.SMALL ? 2 : 4;
    int rows = (int) Math.ceil(entityCount / (double) cols);

    for (int i = 0; i < rows; i++) {
        %><div class="row"><%
            for (int j = 0; j < cols && (cols * i + j < entityCount); j++) {
                String entityId = regionModel.getEntities().get(cols * i + j).getId();
                %><div class="col-sm-6 col-md-3"><tri:entity id="<%= entityId %>"/></div><%
            }
        %></div><%
    }
%>
</div>
