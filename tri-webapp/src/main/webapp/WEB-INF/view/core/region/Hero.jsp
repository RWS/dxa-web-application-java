<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<jsp:useBean id="regionModel" type="com.sdl.tridion.referenceimpl.common.model.Region" scope="request"/>
<div typeof="Region" resource="<%= regionModel.getViewName() %>">
    <tri:entities region="Hero"/>
</div>
