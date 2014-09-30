<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="regionModel" type="com.sdl.tridion.referenceimpl.common.model.Region" scope="request"/>
<div typeof="Region" resource="2-Column">
<%
    int entityCount = regionModel.getEntities().size();

    int rows = (int) Math.ceil(entityCount / 2.0);

    for (int i = 0; i < rows; i++) {
        %><div class="row"><%
            for (int j = 0; j < 2 && (2 * i + j < entityCount); j++) {
                %><div class="col-sm-6"><tri:entity region="<%= regionModel.getName() %>" index="<%= 2 * i + j %>"/></div><%
            }
        %></div><%
    }
%>
</div>
