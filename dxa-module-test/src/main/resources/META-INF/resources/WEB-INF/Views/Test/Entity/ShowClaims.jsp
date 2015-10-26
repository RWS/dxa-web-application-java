<%@ page import="com.sdl.webapp.common.impl.contextengine.DeviceClaims" %>
<%@ page import="org.springframework.beans.BeanUtils" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.beans.PropertyDescriptor" %>
<%@ page import="com.sdl.webapp.common.impl.contextengine.BrowserClaims" %>
<%@ page import="com.sdl.webapp.common.impl.contextengine.OperatingSystemClaims" %>
<%@ page import="java.util.Arrays" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="contextengine" type="com.sdl.webapp.common.api.contextengine.ContextEngine" scope="request"/>

<article class="rich-text ${entity.htmlClasses}" ${markup.entity(entity)}>
<%
    DeviceClaims claims = contextengine.getClaims(DeviceClaims.class);
    BrowserClaims browserclaims = contextengine.getClaims(BrowserClaims.class);
    OperatingSystemClaims operatingSystemClaims = contextengine.getClaims(OperatingSystemClaims.class);
%>
    <h2>Device claims</h2>
    getDisplayWidth : <%=claims.getDisplayWidth()%><br/>
    getDisplayHeight : <%=claims.getDisplayHeight()%><br/>
    getIsMobile : <%=claims.getIsMobile()%><br/>
    getIsRobot : <%=claims.getIsRobot()%><br/>
    getIsTablet : <%=claims.getIsTablet()%><br/>
    getModel : <%=claims.getModel()%><br/>
    getPixelDensity : <%=claims.getPixelDensity()%><br/>
    getPixelRatio : <%=claims.getPixelRatio()%><br/>
    getVariant : <%=claims.getVariant()%><br/>
    getVendor : <%=claims.getVendor()%><br/>
    getVersion : <%=claims.getVersion()%><br/>

    <h2>Browser claims</h2>
    getVariant : <%=browserclaims.getVariant()%><br/>
    getDisplayWidth : <%=browserclaims.getDisplayWidth()%><br/>
    getCookieSupport : <%=browserclaims.getCookieSupport()%><br/>
    getCssVersion : <%=browserclaims.getCssVersion()%><br/>
    getDisplayColorDepth : <%=browserclaims.getDisplayColorDepth()%><br/>
    getDisplayHeight : <%=browserclaims.getDisplayHeight()%><br/>
    getImageFormatSupport : <%=Arrays.toString(browserclaims.getImageFormatSupport())%><br/>
    getInputDevices : <%=Arrays.toString(browserclaims.getInputDevices())%><br/>
    getInputModeSupport : <%=Arrays.toString(browserclaims.getInputModeSupport())%><br/>
    getJsVersion : <%=browserclaims.getJsVersion()%><br/>
    getMarkupSupport : <%=Arrays.toString(browserclaims.getMarkupSupport())%><br/>
    getModel : <%=browserclaims.getModel()%><br/>
    getPreferredHtmlContentType : <%=browserclaims.getPreferredHtmlContentType()%><br/>
    getScriptSupport : <%=Arrays.toString(browserclaims.getScriptSupport())%><br/>
    getStylesheetSupport : <%=Arrays.toString(browserclaims.getStylesheetSupport())%><br/>
    getVendor : <%=browserclaims.getVendor()%><br/>
    getVersion : <%=browserclaims.getVersion()%><br/>

    <h2>OperatingSystem claims</h2>
    getVendor : <%=operatingSystemClaims.getVendor()%><br/>
    getVersion : <%=operatingSystemClaims.getVersion()%><br/>
    getModel : <%=operatingSystemClaims.getModel()%><br/>
    getVariant : <%=operatingSystemClaims.getVariant()%><br/>

</article>

