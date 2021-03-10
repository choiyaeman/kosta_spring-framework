<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath }"/>

<header>
   <ul>
      <li><a href="${contextPath}/list">게시판</a></li>
      <li><a href="${contextPath}/write">글쓰기</a></li>
      <li><a href="#">메뉴2</a></li>
   </ul>
</header>