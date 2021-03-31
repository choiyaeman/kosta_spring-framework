<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>    
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body style="background-color: lightblue;">
<h1>/sample/admin page</h1>


<p>principal : <sec:authentication property="principal"/></p>
<%-- <p>MemberVO : <sec:authentication property="principal.member"/></p> --%>
<%-- <p>사용자이름 : <sec:authentication property="principal.member.userName"/></p> --%>
<h2>username :  <sec:authentication property="principal.username"/></h2>
<%-- <p>사용자 권한 리스트  : <sec:authentication property="principal.member.authList"/></p> --%>
<%-- <h3>관리자 화면</h3>--%>
<ul>
<li><a href="${pageContext.request.contextPath}/logout">내장된 로그아웃</a></li>
<li><a href="${pageContext.request.contextPath}/customLogout">사용자정의 로그아웃</a></li>
<li><a href="#">상품목록</a></li>
<li><a href="#">상품추가</a></li>
<li><a href="#">상품수정</a></li>
<li><a href="${pageContext.request.contextPath}">메인메뉴</a></li>
</ul>
</body>
</html>
