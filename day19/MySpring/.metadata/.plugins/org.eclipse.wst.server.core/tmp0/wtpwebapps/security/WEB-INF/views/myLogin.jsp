<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>myLogin.jsp</title>
</head>
<body>
<h2>${requestScope.error}</h2>
<h2>${requestScope.logout}</h2>
<form method="post" action="${pageContext.request.contextPath}/login">
	username : <input type="text" name="username">
	password : <input type="password" name="password">
	<input name="${_csrf.parameterName}" 
	       type="hidden" 
	       value="${_csrf.token}">
	<input type="submit">
</form>
</body>
</html>