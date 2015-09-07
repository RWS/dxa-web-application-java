<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="dxa" uri="http://www.sdl.com/tridion-dxa" %> 
<%@ taglib prefix="xpm" uri="http://www.sdl.com/tridion-xpm" %>
<jsp:useBean id="region" type="com.sdl.webapp.common.api.model.RegionModel" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>

<xpm:button region="${region}" />

<dxa:region parentRegion="${region}" name="Info"/>
<header id="page-header" class="navbar navbar-default">
    <div class="container-fluid page-border">
        <div class="row">
            <div class="col-xs-12" role="navigation">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                        <i class="fa fa-bars"></i>
                    </button>
                    <dxa:region parentRegion="${region}" name="Logo"/>
                </div>
                <dxa:regions parentRegion="${region}" exclude="Logo,Info" containerSize="12"/>
            </div>
        </div>
    </div>
</header>
