<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sdl.webapp.common.api.ScreenWidth" %>
<%@ page import="com.sdl.webapp.common.api.model.Entity" %>
<%@ page import="java.util.Iterator" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="region" type="com.sdl.webapp.common.api.model.Region" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<jsp:useBean id="screenWidth" type="com.sdl.webapp.common.api.ScreenWidth" scope="request"/>
<div ${markup.region(region)}>
<%
    final int cols = screenWidth == ScreenWidth.SMALL ? 2 : 3;
    final int rows = (int) Math.ceil(region.getEntities().size() / (double) cols);
    final Iterator<Entity> iterator = region.getEntities().values().iterator();

    for (int row = 0; row < rows; row++) {
        %><div class="row"><%
        for (int col = 0; col < cols && iterator.hasNext(); col++) {
            final Entity entity = iterator.next();
            %><div class="col-sm-6 col-md-4"><tri:entity region="3-Column" entityId="<%= entity.getId() %>"/></div><%
        }
        %></div><%
    }
%>
</div>
