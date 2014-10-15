<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page isErrorPage="true" %>
<div style="background-color: #ff9999; border: 5px solid red; padding: 5px;">
<p>Error: <%= exception.getMessage() %></p>
<!--
<%
final StringWriter sw = new StringWriter();
final PrintWriter pw = new PrintWriter(sw);
exception.printStackTrace(pw);
pw.flush();
out.print(sw.toString());
%>
-->
</div>
