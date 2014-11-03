<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.TagLinkList" scope="request"/>
<div class="icon-list">
    <c:forEach var="link" items="${entityModel.links}">
        <a href="${link.url}" class="fa-stack fa-lg" title="<tri:resource key="core.visitUsSocialLinkTitle" />">
            <i class="fa fa-circle fa-stack-2x"></i>
            <i class="fa fa-${link.tag.key} fa-stack-1x"></i>
        </a>
    </c:forEach>
</div>
