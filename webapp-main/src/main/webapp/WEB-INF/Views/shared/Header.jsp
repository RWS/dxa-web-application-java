<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="tri" uri="http://www.sdl.com/tridion-reference-impl" %>
<tri:region name="Info"/>
<header id="page-header" class="navbar navbar-default">
    <div class="container-fluid page-border">
        <div class="row">
            <div class="col-xs-12" role="navigation">
                <div class="navbar-header">
                    <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
                        <i class="fa fa-bars"></i>
                    </button>
                    <tri:region name="Logo"/>
                </div>
                <tri:regions exclude="Logo,Info"/>
            </div>
        </div>
    </div>
</header>
