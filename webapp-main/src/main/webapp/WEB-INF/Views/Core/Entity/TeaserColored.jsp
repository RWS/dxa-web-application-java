<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="formatOptions" class="java.util.HashMap" scope="request"/>
<c:set target="${formatOptions}" property="style" value="teaser-coloured"/>
<c:import url="/WEB-INF/Views/Core/Entity/Partials/Teaser.jsp"/>
