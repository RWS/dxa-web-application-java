<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dxa" uri="http://www.sdl.com/tridion-dxa" %> 
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>

<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.Download" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>

<div class="${entity.htmlClasses}" ${markup.entity(entity)}>
   <div class="download-list">
       <i class="fa ${entity.iconClass}"></i>
       <div>
           <a href="${entity.url}">${entity.fileName}</a>
           <small class="size" ${markup.property(entity, "fileSize")}>(${entity.getFriendlyFileSize()})</small>
           <c:if test="${not empty entity.description}">
               <small ${markup.property(entity, "description")}>${entity.description}</small>
           </c:if>
       </div>
   </div>
</div>