<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="Generic" type="org.dd4t.test.web.models.Generic" scope="request"/>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
Heading field:
${Generic.heading}


<c:forEach var="embeddedField" items="${Generic.embedded}">
    ${embeddedField.testfieldOne}
    ${embeddedField.embeddableTwo.testfieldTwo}
</c:forEach>
