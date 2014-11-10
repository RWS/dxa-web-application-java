<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="entityModel" type="com.sdl.webapp.common.api.model.entity.TagLinkList" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.main.markup.Markup" scope="request"/>
<div class="icon-list" ${markup.entity(entityModel)}>
    <c:forEach var="link" items="${entityModel.links}">
        <a href="${link.url}" class="fa-stack fa-lg"
           title="<tri:resource key="core.visitUsSocialLinkTitle" arg1="${link.tag.displayText}"/>"
           ${markup.property(entityModel, "links")}>
            <i class="fa fa-circle fa-stack-2x"></i>
            <i class="fa fa-${link.tag.key} fa-stack-1x"></i>
        </a>
    </c:forEach>
</div>
