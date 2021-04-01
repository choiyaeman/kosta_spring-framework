<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>
	Hello world!  
</h1>

<P>  The time on the server is ${serverTime}. </P>
<a href="${pageContext.request.contextPath}/sample/member">고객</a>
<a href="${pageContext.request.contextPath}/sample/admin">관리자</a>
<a href="${pageContext.request.contextPath}/sample/all">누구나</a>

</body>
</html>
