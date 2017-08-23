<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="pageModel" type="org.dd4t.contentmodel.Page" scope="request"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dd4t" uri="http://www.dd4t.org/tags/2.0" %>
<jsp:useBean id="generic" type="org.dd4t.test.web.models.Generic" scope="request"/>

<c:if test="${!empty generic.heading}">
    <h1>${generic.heading}</h1>
</c:if>
<c:if test="${!empty generic.events}">
    <c:forEach items="${generic.events}" var="event">
        <h2><c:out value="${event.title}"/></h2>
    </c:forEach>
</c:if>

<smarttarget:query siteEditTagName="span" publication="tcm:0-72-1">
    <smarttarget:promotions region="Homepage Banners" var="promotion"
                            maxItems="1">
        <smarttarget:itemTemplate>
            <smarttarget:promotionalItems>
                <smarttarget:itemTemplate>
                    <my:dynamicComponentPresentation component="${item.componentUri}" view="smarttarget-content-item"/>
                </smarttarget:itemTemplate>
            </smarttarget:promotionalItems>
        </smarttarget:itemTemplate>
    </smarttarget:promotions>
</smarttarget:query>