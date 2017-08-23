<%@page import="org.dd4t.contentmodel.Page"%>
<%@page import="org.dd4t.springmvc.constants.Constants"%>
<%@page import="org.dd4t.springmvc.siteedit.SiteEditService"%>
<%@ page 
	language="java" 
	contentType="text/html; charset=UTF-8"
	import="org.dd4t.springmvc.view.model.*"
	pageEncoding="UTF-8"
%>

	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%
	ComponentViews contentModel = (ComponentViews) request.getAttribute(Constants.CONTENT_MODEL_KEY);
	Page pageModel = (Page) request.getAttribute(Constants.PAGE_MODEL_KEY);
%>
	<title><%=pageModel.getTitle() %></title>
</head>
<body>
<table>
	<tr>
		<td align="center" colspan="3">Your header here <br /> Your top navigation here </td>
	</tr>
	<tr valign="top">
		<td width="25%">Your left navigation here </td>
		<td width="50%">
			<h1><%=pageModel.getTitle() %></h1> 
		<%
			if(contentModel.getRegions().containsKey("main")){
			    for(String view : contentModel.getRegions().get("main").getComponentViews()){         
				       out.println(view);
				    }
			}
		%>
		</td>
		<td width="25%">		
		<%
			if(contentModel.getRegions().containsKey("side")){
			    for(String view : contentModel.getRegions().get("side").getComponentViews()){    
				       out.println(view);
				    }
			}
		%>
		</td>
	</tr>
	<tr>
		<td align="center" colspan="3">Your footer here </td>
	</tr>	
</table>
<%=SiteEditService.generateSiteEditPageTag(pageModel)%>
</body>
</html>