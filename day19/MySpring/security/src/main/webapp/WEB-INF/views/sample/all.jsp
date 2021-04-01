<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>    
 <c:set var="contextPath" value="${pageContext.request.contextPath }" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<!-- all or member or admin -->
<h1>/sample/all. page</h1>
<ul>
 <sec:authorize access="isAnonymous()">
<li><a href="${pageContext.request.contextPath}/login">내장된 로그인</a></li>
<li><a href="${pageContext.request.contextPath}/myLogin">사용자정의 로그인</a></li>
<li><a href="#">가입</a></li>
</sec:authorize>

<sec:authorize access="isAuthenticated()">
<h2>username : <sec:authentication property="principal.username"/></h2>
<li><a href="${pageContext.request.contextPath}/logout">내장된 로그아웃</a></li>
<li><a href="${pageContext.request.contextPath}/myLogout">사용자정의 로그아웃</a></li>
</sec:authorize>
<li><a href="#">상품목록</a></li>
<li><a href="${pageContext.request.contextPath}/">메인메뉴</a></li>
</ul>
</body>
</html>