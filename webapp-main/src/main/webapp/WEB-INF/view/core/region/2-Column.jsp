<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="region" type="com.sdl.webapp.common.api.model.Region" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<div ${markup.region(region)}>
    <xpm:region region="${region}"/>
<%
    int entityCount = region.getEntities().size();

    int rows = (int) Math.ceil(entityCount / 2.0);

    for (int i = 0; i < rows; i++) {
        %><div class="row"><%
            for (int j = 0; j < 2 && (2 * i + j < entityCount); j++) {
                %><div class="col-sm-6"><tri:entity region="2-Column" index="<%= 2 * i + j %>"/></div><%
            }
        %></div><%
    }
%>
</div>
