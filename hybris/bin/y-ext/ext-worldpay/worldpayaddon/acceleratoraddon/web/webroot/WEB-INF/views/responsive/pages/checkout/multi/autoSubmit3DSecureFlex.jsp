<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<html>
<head>
    <title>3D Secure Flex</title>
</head>
<body>

<div id="challengeUrl" data-challengeUrl="${challengeUrl}"></div>
<div id="challengeJWT" data-jwtChallenge="${jwt}"></div>

<iframe src="challengeIframe" height="${height}" width="${width}">
</iframe>

<form:form name="autoSubmitThreeDSecureFlex" method="POST" id="autoSubmitThreeDSecureFlex" action="${autoSubmitThreeDSecureFlexUrl}">
</form:form>

</body>
</html>
