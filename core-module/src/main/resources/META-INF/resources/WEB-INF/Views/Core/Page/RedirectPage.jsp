<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dxa" uri="http://www.sdl.com/tridion-dxa" %> 
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>

<jsp:useBean id="pageModel" type="com.sdl.webapp.common.api.model.PageModel" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>

<c:set var="redirectUrl" value="${pageModel.meta['externalLink']}"/>
<c:if test="${empty redirectUrl}">
     <c:set var="redirectUrl" value="${pageModel.meta['internalLink']}"/>
</c:if>
<c:choose>
  <c:when test="${not empty redirectUrl and empty param.edit}">
      <c:redirect url="${redirectUrl}"/>
  </c:when>
  <c:otherwise>
      <strong>
         <dxa:resource key="core.redirectEditorHintText"/>:${redirectUrl}
      </strong>
  </c:otherwise>
</c:choose>
