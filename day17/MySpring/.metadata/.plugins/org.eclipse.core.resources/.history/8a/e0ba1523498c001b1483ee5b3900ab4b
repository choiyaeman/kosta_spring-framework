<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    isErrorPage = "true" %>
<%
System.out.println("예외내용:" + exception.getMessage());
%>    
<%

Exception e = (Exception)request.getAttribute("e");
if(e == null){
	return;
}
%>
<script>
alert('<%=e.getMessage()%>');
</script>