<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath }"/>
<c:set var="board" value="${requestScope.board}"/>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>list.jsp</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script> 
<script>
</script>
<style>
*{
  box-sizing: border-box;
}
</style>
</head>
<body>
<%@include file="/WEB-INF/views/header.jsp" %>
<section>
  <form method="post" action="${contextPath}/write">
      글제목: <input type="text" name="board_title"><br>
      작성자 : <input type="text" name="board_writer"><br>
      비밀번호:<input type="password" name="board_pwd" required><br>
   <input type="submit" value="글쓰기"> 
  </form>
</section>
<%@include file="/WEB-INF/views/footer.jsp" %>
</body>
</html>