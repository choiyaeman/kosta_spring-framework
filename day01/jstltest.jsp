<%@page import="java.util.ArrayList"%>
<%@page import="com.my.vo.Product"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>jstltest.jsp</title>
</head>
<body>
<%--c:set 변수 선언용 JSTL태그 --%>
<c:set var="num" value="123"></c:set>
<%--c:if 조건문용 JSTL태그 --%>
<c:if test="${num%2==0}">
짝수입니다
</c:if>

<c:choose>
<c:when test="${num%2==0}">
짝수입니다
</c:when>
<c:otherwise>
홀수입니다
</c:otherwise>
</c:choose>

<hr>
<%--요청전달데이터opt값이 add인경우는 '가입작업을 선택했습니다'를 출력
                     findAll인 경우는 '조회작업을 선택했습니다'를 출력
                                          없는 경우(null이거나 ""인 경우)는 '작업을 선택하세요를 출력'하시오.
    http://localhost:888/myback/jstltest.jsp?opt=add
    http://localhost:888/myback/jstltest.jsp?opt=findAll
    http://localhost:888/myback/jstltest.jsp
 --%>
<c:set var="optValue" value="${param.opt}"/>
<c:choose>
  <c:when test="${optValue == 'add'}">가입작업을 선택했습니다</c:when>
  <c:when test="${optValue == 'findAll'}">조회작업을 선택했습니다</c:when>
  <c:when test="${empty optValue}">작업을 선택하세요</c:when>
  <c:otherwise>그외의 작업을 선택했습니다</c:otherwise>
</c:choose>

<hr>
<%--c:forEach 반복문용 JSTL태그 --%>
<c:forEach begin="10" end="20" step="2" var="i">
${i}&nbsp;&nbsp;
</c:forEach>
<hr>

<% //servlet에서 아래작업수행후 forward된 경우
List<Product> list = new ArrayList<>();
list.add( new Product("c0001", "아메리카노", 1000));
list.add( new Product("c0002", "아이스아메리카노", 1000));
list.add( new Product("c0003", "라테", 1500));
request.setAttribute("list", list);
%>
<c:forEach items="${requestScope.list}" var="p">
  ${p.prod_no} : ${p.prod_name} : ${p.prod_price}:
  <fmt:formatNumber pattern="#,##0">${p.prod_price}</fmt:formatNumber>
  <br>
</c:forEach>

<%--
List<Product> list = request.getAttribute("list");
DecimalFormat df = new DecimalFormat("9,990");
for(product p: list){
    df.format(p.getProd_price());
}
 --%>

<c:set var="str" value="최예만자바JSPHTMLELJSTLSPRING"/>
<c:set var="str1" value="오라클"/>
<c:if test="${fn:contains(str, str1)}">
</c:if>

</body>
</html>