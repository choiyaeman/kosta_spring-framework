<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%

Exception e = (Exception)request.getAttribute("e");
if(e == null){
	return;
}
%>
<script>
alert('<%=e.getMessage()%>');
</script>