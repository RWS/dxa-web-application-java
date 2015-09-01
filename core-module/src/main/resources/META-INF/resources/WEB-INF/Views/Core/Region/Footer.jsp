<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="dxa" uri="http://www.sdl.com/tridion-dxa" %> 
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="region" type="com.sdl.webapp.common.api.model.RegionModel" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
    
<xpm:button region="${region}" />
    
<footer id="page-footer" class="page-row">
    <div class="container-fluid page-border">
        <dxa:regions parentRegion="${region}" exclude="Links" containerSize="12"/>
        <hr/>
        <dxa:region parentRegion="${region}"  name="Links"/>
    </div>
</footer>