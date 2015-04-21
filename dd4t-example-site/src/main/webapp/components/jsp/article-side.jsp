<%@page import="org.dd4t.springmvc.constants.Constants"%>
<%@page import="java.util.Map"%>
<%@page import="org.dd4t.springmvc.siteedit.SiteEditService"%>
<%@page import="org.dd4t.springmvc.util.ApplicationContextProvider"%>
<%@page import="org.dd4t.core.resolvers.LinkResolver"%>
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
	<h3>
		<%=SiteEditService.generateSiteEditFieldMarking(cont.get("title")) %>		
		<%=cont.get("title").getValues().get(0) %>
	</h3>
	
	<p><strong>
		<%=SiteEditService.generateSiteEditFieldMarking(cont.get("introduction")) %>
		<%=cont.get("introduction").getValues().get(0) %>
	</strong></p>
	
	<%
		LinkResolver resolver = (LinkResolver) ApplicationContextProvider.getBean("LinkResolver");
		String url = resolver.resolve(cp);
	%>
	
	<a href="<%=url %>">Read more</a>