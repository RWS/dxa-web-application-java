<%@page import="org.dd4t.contentmodel.ComponentPresentation"%>
<%@page import="org.dd4t.springmvc.util.ApplicationContextProvider"%>
<%@page import="org.dd4t.core.resolvers.LinkResolver"%>
<%@page import="java.util.List"%>
<%@page import="org.dd4t.springmvc.apps.listings.NewsList"%>
<%@page import="org.dd4t.springmvc.constants.Constants"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%
	ComponentPresentation comp = (ComponentPresentation) request.getAttribute(Constants.COMPONENT_PRESENTATION_KEY);

	String title = (String) comp.getComponent().getContent().get("title").getValues().get(0);

	List<ComponentPresentation> newsitems = (List<ComponentPresentation>) request.getAttribute(NewsList.NEWSLIST_COMPS_KEY);
	
	LinkResolver resolver = (LinkResolver) ApplicationContextProvider.getBean("LinkResolver");
%>
<h3><%=title %></h3>

<ul>
<%

	for(ComponentPresentation newsItem : newsitems){
		String newsTitle = (String) newsItem.getComponent().getContent().get("title").getValues().get(0);		
		String newsUrl = resolver.resolve(newsItem);
%>		
		<li><a href="<%=newsUrl%>"><%=newsTitle %></a></li>
<%		
	}
%>
</ul>