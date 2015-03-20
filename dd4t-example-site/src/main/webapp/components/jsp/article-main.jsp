<%@page import="java.util.Map"%>
<%@page import="org.dd4t.springmvc.constants.Constants"%>
<%@page import="org.dd4t.springmvc.siteedit.SiteEditService"%>
<%@ page 
    language="java" contentType="text/html; charset=UTF-8"
	import="org.dd4t.springmvc.view.model.*,
			org.dd4t.contentmodel.*,
			org.dd4t.contentmodel.impl.*"
	pageEncoding="UTF-8"%>
		
<%
	ComponentPresentation cp = (ComponentPresentation) request.getAttribute(Constants.COMPONENT_PRESENTATION_KEY);
	Map<String, Field> cont = cp.getComponent().getContent();
%>
	<h2>
		<%=SiteEditService.generateSiteEditFieldMarking(cont.get("title")) %>
		<%=cont.get("title").getValues().get(0) %>
	</h2>
	
	<p><strong>
		<%=SiteEditService.generateSiteEditFieldMarking(cont.get("introduction")) %>
		<%=cont.get("introduction").getValues().get(0) %>
	</strong></p>
	
	<div>
		<%=SiteEditService.generateSiteEditFieldMarking(cont.get("paragraph")) %>
		<%=cont.get("paragraph").getValues().get(0) %>
	</div>
	
	