<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:useBean id="entity" type="com.sdl.webapp.common.api.model.entity.RedirectEntity" scope="request"/>
<script>window.location.replace("${entity.url}");</script>