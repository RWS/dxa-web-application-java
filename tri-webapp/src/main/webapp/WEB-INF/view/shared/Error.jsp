<%@ page import="java.io.StringWriter" %>
<%@ page import="java.io.PrintWriter" %>
<%@ page isErrorPage="true" %>
<div style="border: 5px solid red;">
<p>Error</p>
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
