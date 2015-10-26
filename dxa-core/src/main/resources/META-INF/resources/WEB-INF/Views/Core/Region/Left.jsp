<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="dxa" uri="http://www.sdl.com/tridion-dxa" %>
<jsp:useBean id="region" type="com.sdl.webapp.common.api.model.RegionModel" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>

<div ${markup.region(region)}>
    <dxa:entities region="Left"/>
</div>
