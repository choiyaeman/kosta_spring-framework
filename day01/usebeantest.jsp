<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>usebeantest.jsp</title>
</head>
<body>
<jsp:useBean id="c" class="com.my.vo.Customer" scope="request"></jsp:useBean>
<jsp:setProperty property="pwd" name="c" value="p1"/>
<jsp:getProperty property="pwd" name="c"/>

<%--
Customer c = request.getAttribute("c");
if(c == null){
	c = new Customer();
	request.setAttribute("c", c);
}
c.setPwd("p1");
out.print(c.getPwd());
 --%>
</body>
</html>