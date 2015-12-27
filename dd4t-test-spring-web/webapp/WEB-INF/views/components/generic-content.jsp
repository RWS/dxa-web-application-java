<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:useBean id="Generic" type="org.dd4t.test.web.models.Generic" scope="request" />

Heading field:
${Generic.heading}

${Generic.embedded.content.fieldkey}

Meh.
