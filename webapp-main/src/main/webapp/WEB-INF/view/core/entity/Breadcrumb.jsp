<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.sdl.webapp.common.api.model.entity.Link" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.NavigationLinks" scope="request"/>
<%
    final List<Link> links = entityModel.getItems();
    if (links != null && !links.isEmpty()) {
        int start = links.size() > 5 ? links.size() - 4 : 1;

        %><ol class="breadcrumb"><%
        %><li><a href="<%= links.get(0).getUrl() %>"><i class="fa fa-home"><span class="sr-only"><%= links.get(0).getLinkText() %></span></i></a></li><%
        if (start > 1) {
            %><li>...</li><%
        }
        for (int i = start; i < links.size() - 1; i++) {
            %><li><a href="<%= links.get(i).getUrl() %>"><%= links.get(i).getLinkText() %></a></li><%
        }
        if (links.size() > 1) {
            %><li class="active"><%= links.get(links.size() - 1).getLinkText() %></li><%
        }
        %></ol><%
    }
%>
