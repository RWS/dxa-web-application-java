<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.ContentList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<c:set var="urlFormat" value="?start={0}&id=${entity.id}"/>
<ul class="pagination">
  <c:if test="${entity.start > 1}">
    <c:set var="link" value="${markup.formatMessage(urlFormat, entity.start - entity.pageSize)}"/>
    <li><a href="${link}"><i class="fa fa-angle-left"></i></a></li>
    <li><a href="${link}">${entity.currentPage - 1}</a></li>
  </c:if>
  <li class="active"><a href="${markup.formatMessage(urlFormat, entity.start)}">${entity.currentPage}</a></li>
  <c:if test="${entity.hasMore}">
    <c:set var="link" value="${markup.formatMessage(urlFormat, entity.start + entity.pageSize)}"/>
    <li><a href="${link}">${entity.currentPage + 1}</a></li>
    <li><a href="${link}"><i class="fa fa-angle-right"></i></a></li>
  </c:if>
</ul>
