<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="Generic" type="org.dd4t.test.web.models.Generic" scope="request"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="xpm" uri="http://www.dd4t.org/tags/2.0/xpm" %>
<xpm:view model="${Generic}" />
Heading field:
<xpm:editable model="${Generic}" field="heading">${Generic.heading}</xpm:editable>




<c:forEach var="embeddedField" items="${Generic.embedded}">
    <xpm:editable model="${embeddedField}" field="testfieldOne">${embeddedField.testfieldOne}</xpm:editable>
    ${embeddedField.embeddableTwo.testfieldTwo}
</c:forEach>
