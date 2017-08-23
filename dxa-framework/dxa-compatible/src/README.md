Thymeleaf is a view language for java (see http://www.thymeleaf.org/).

This library offers support for Experience Manager in Thymeleaf  views.

Usage:

<xpm:page src="${page}"/>

Place this somewhere near the bottom of the HTML page. Generates the <script> tag needed for XPM. Note that the DD4T Page object must be on the request (in this example, it is called 'page' but that can be different per implementation).

<xpm:componentpresentation src="${entity}"/> 

Place this tag directly inside the enclosing element of the component presentation. Note that the DD4T entity (= ViewModel) object must be on the request (in this example, it is called 'entity' but that can be different per implementation).

<xpm:field src="${entity}" fieldname="somefieldname"/>

Place this tag directly inside the enclosing element of the field. Note that the DD4T entity (= ViewModel) object must be on the request (in this example, it is called 'entity' but that can be different per implementation).
