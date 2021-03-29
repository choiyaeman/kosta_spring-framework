<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>list.jsp</title>
</head>
<body>
<%@include file="/WEB-INF/views/header.jsp" %>
<section>
${requestScope.exception.message}

</section>
<%@include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>