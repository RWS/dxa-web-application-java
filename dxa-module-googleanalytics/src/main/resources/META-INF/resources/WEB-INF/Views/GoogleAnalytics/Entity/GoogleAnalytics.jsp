<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<jsp:useBean id="entity" type="com.sdl.dxa.modules.googleanalytics.model.GoogleAnalyticsConfiguration" scope="request"/>
<jsp:useBean id="markup" type="com.sdl.webapp.common.markup.Markup" scope="request"/>
<script>
    (function (b, o, i, l, e, r) {
        b.GoogleAnalyticsObject = l; b[l] || (b[l] =
                function () { (b[l].q = b[l].q || []).push(arguments); }); b[l].l = +new Date;
        e = o.createElement(i); r = o.getElementsByTagName(i)[0];
        e.src = '//www.google-analytics.com/analytics.js';
        r.parentNode.insertBefore(e, r);
    }(window, document, 'script', 'ga'));
    ga('create', '${entity.siteKey}'); ga('send', 'pageview');
</script>
