<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>
		<title>Worldpay</title>
	</head>
	<body>
		<div class="prepend-top span-17 colborder" id="content">
			<div class="marginLeft" id="inner">
				<h2>Support email body:</h2>

				<div style="white-space: pre;">${body}</div>
			</div>
		</div>
		<div class="prepend-top span-6 last" id="sidebar">
			<button type="button" id="sendEmailSidebar" onclick="location.href='/worldpayhac/sendemail'" >Send Email</button>
			<c:if test="${send}" >
				<div>${message}</div>
			</c:if>
		</div>
	</body>
</html>

