<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sdl.webapp.common.api.ScreenWidth" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="regionModel" type="com.sdl.webapp.common.api.model.Region" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<jsp:useBean id="screenWidth" type="com.sdl.webapp.common.api.ScreenWidth" scope="request"/>
<div ${markup.region(regionModel)}>
    <xpm:region region="${regionModel}"/>
<%
    int entityCount = regionModel.getEntities().size();

    int cols = screenWidth == ScreenWidth.SMALL ? 2 : 3;
    int rows = (int) Math.ceil(entityCount / (double) cols);

    for (int i = 0; i < rows; i++) {
        %><div class="row"><%
            for (int j = 0; j < cols && (cols * i + j < entityCount); j++) {
                %><div class="col-sm-6 col-md-4"><tri:entity region="3-Column" index="<%= cols * i + j %>"/></div><%
            }
        %></div><%
    }
%>
</div>
