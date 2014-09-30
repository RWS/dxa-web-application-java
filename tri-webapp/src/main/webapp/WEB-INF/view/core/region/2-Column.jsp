<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request"/>
<jsp:useBean id="regionModel" type="com.sdl.tridion.referenceimpl.common.model.Region" scope="request"/>
<div typeof="Region" resource="<%= regionModel.getViewName() %>">
<%
    int entityCount = regionModel.getEntities().size();

    int rows = (int) Math.ceil(entityCount / 2.0);

    for (int i = 0; i < rows; i++) {
        %><div class="row"><%
            for (int j = 0; j < 2 && (2 * i + j < entityCount); j++) {
                String entityId = regionModel.getEntities().get(2 * i + j).getId();
                %><div class="col-sm-6"><tri:entity id="<%= entityId %>"/></div><%
            }
        %></div><%
    }
%>
</div>
