<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.google.common.base.Strings" %>
<%@ page import="com.sdl.tridion.referenceimpl.common.model.entity.Teaser" %>
<%@ page import="java.util.UUID" %>
<%@ page import="com.sdl.tridion.referenceimpl.common.model.entity.Link" %>
<%@ page import="com.sdl.tridion.referenceimpl.common.model.entity.MediaItem" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="pageModel" type="com.sdl.tridion.referenceimpl.common.model.Page" scope="request"/>
<jsp:useBean id="entityModel" type="com.sdl.tridion.referenceimpl.common.model.entity.ItemList" scope="request"/>
<%
    String carouselId = UUID.randomUUID().toString();
%>
<div id="carousel-<%= carouselId %>" class="carousel slide" data-ride="carousel" data-interval="5000">
    <ol class="carousel-indicators">
        <%
            int count = entityModel.getItemListElements().size();
            for (int i = 0; i < count; i++) {
                out.write("<li");
                out.write(" data-target=\"#carousel-" + carouselId + "\"");
                out.write(" data-slide-to=\"" + i + "\"");
                if (i == 0) { out.write(" class=\"active\""); }
                out.write("></li>");
            }
        %>
    </ol>
    <div class="carousel-inner">
        <%
            int i = 0;
            for (Teaser teaser : entityModel.getItemListElements()) {
                out.write("<div class=\"item");
                if (i == 0) { out.write(" active"); }
                out.write("\">");

                // See Teaser-ImageOverlay.cshtml in .NET version
                out.write("<div>");
                final MediaItem media = teaser.getMedia();
                if (media != null) {
                    out.write("<span>");
                    // TODO: See HtmlHelperExtensions.Media (C# version)
                    out.write("<img src=\"" + media.getUrl() + "\" width=\"100%\" data-aspect=\"3.3\">");
                    out.write("</span>");
                }

                final String headline = teaser.getHeadline();
                final String text = teaser.getText();
                if (!Strings.isNullOrEmpty(headline) || !Strings.isNullOrEmpty(text)) {
                    out.write("<div class=\"overlay overlay-tl ribbon\">");
                    if (!Strings.isNullOrEmpty(headline)) {
                        out.write("<h2>" + headline + "</h2>");
                    }
                    if (!Strings.isNullOrEmpty(text)) {
                        out.write("<div>" + text.replaceAll("\\. ", "<br/>") + "</div>");
                    }
                    out.write("</div>");
                }

                final Link link = teaser.getLink();
                if (link != null && !Strings.isNullOrEmpty(link.getLinkText())) {
                    out.write("<div class=\"carousel-caption\">");
                    out.write("<p><a href=\"" + link.getUrl() + "\" title=\"" +
                            link.getAlternateText() + "\" class=\"btn btn-primary\">" +
                            link.getLinkText() + "</a></p>");
                    out.write("</div>");
                }
                out.write("</div>");

                out.write("</div>");
                i++;
            }
        %>
    </div>
    <!-- Controls -->
    <a class="left carousel-control" href="#carousel-<%= carouselId %>" data-slide="prev">
        <i class='fa fa-chevron-left carousel-icon-left'></i>
    </a>
    <a class="right carousel-control" href="#carousel-<%= carouselId %>" data-slide="next">
        <i class='fa fa-chevron-right carousel-icon-right'></i>
    </a>
</div>
